package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import javax.crypto.SecretKey;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import us.nebula.api.listener.EventBus;
import us.nebula.impl.event.network.EventPacket;

public class NetworkManager extends SimpleChannelInboundHandler<Packet>
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
    public static final Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);

    public static final AttributeKey<EnumConnectionState> ATTRIBUTE_CONNECTION_STATE = new AttributeKey<>("protocol");
    public static final AttributeKey<BiMap<Integer, Class<? extends Packet>>> ATTRIBUTE_RECEIVABLE = new AttributeKey<>("receivable_packets");
    public static final AttributeKey<BiMap<Integer, Class<? extends Packet>>> ATTRIBUTE_SENDABLE = new AttributeKey<>("sendable_packets");

    public static final NioEventLoopGroup eventLoops = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());

    /**
     * Whether this NetworkManager deals with the client or server side of the connection
     */
    private final boolean isClientSide;

    /**
     * The queue for received, unprioritized packets that will be processed at the earliest opportunity
     */
    private final Queue<Packet> receivedPacketsQueue = Queues.newConcurrentLinkedQueue();

    /** The queue for packets that require transmission */
    private final Queue<InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();

    /** The active channel */
    private Channel channel;

    /** The address of the remote party */
    private SocketAddress socketAddress;

    /** The INetHandler instance responsible for processing received packets */
    private INetHandler netHandler;

    /**
     * The current connection state, being one of: HANDSHAKING, PLAY, STATUS, LOGIN
     */
    private EnumConnectionState connectionState;

    /** A String indicating why the network has shutdown. */
    private IChatComponent terminationReason;

    public NetworkManager(boolean clientSide)
    {
        this.isClientSide = clientSide;
    }

    public void channelActive(ChannelHandlerContext activeChannelCtx) throws Exception
    {
        super.channelActive(activeChannelCtx);
        this.channel = activeChannelCtx.channel();
        this.socketAddress = this.channel.remoteAddress();
        this.setConnectionState(EnumConnectionState.HANDSHAKING);
    }

    /**
     * Sets the new connection state and registers which packets this channel may send and receive
     */
    public void setConnectionState(EnumConnectionState state)
    {
        this.connectionState = this.channel.attr(ATTRIBUTE_CONNECTION_STATE).getAndSet(state);
        this.channel.attr(ATTRIBUTE_RECEIVABLE).set(state.getReceivablePackets(this.isClientSide));
        this.channel.attr(ATTRIBUTE_SENDABLE).set(state.getSendablePackets(this.isClientSide));
        this.channel.config().setAutoRead(true);
        LOGGER.debug("Enabled auto read");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable)
    {
        this.closeChannel(new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + throwable));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet)
    {
        if (this.channel.isOpen())
        {
            if (EventBus.dispatch(new EventPacket.Inbound(netHandler, packet)))
            {
                return;
            }
            if (packet.hasPriority())
            {
                packet.processPacket(this.netHandler);
            }
            else
            {
                this.receivedPacketsQueue.add(packet);
            }
        }
    }

    /**
     * Sets the NetHandler for this NetworkManager, no checks are made if this handler is suitable for the particular
     * connection state (protocol)
     */
    public void setNetHandler(final INetHandler netHandler)
    {
        Validate.notNull(netHandler, "packetListener");
        LOGGER.debug("Set listener of {} to {}", this, netHandler);
        this.netHandler = netHandler;
    }

    /**
     * Will flush the outbound queue and dispatch the supplied Packet if the channel is ready, otherwise it adds the
     * packet to the outbound queue and registers the GenericFutureListener to fire after transmission
     */
    public void scheduleOutboundPacket(Packet packet, GenericFutureListener ... futureListeners)
    {
        if (this.channel != null && this.channel.isOpen())
        {
            this.flushOutboundQueue();
            this.dispatchPacket(packet, futureListeners);
        }
        else
        {
            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(packet, futureListeners));
        }
    }

    /**
     * Will commit the packet to the channel. If the current thread 'owns' the channel it will write and flush the
     * packet, otherwise it will add a task for the channel eventloop thread to do that.
     */
    public void dispatchPacket(final Packet packet, final GenericFutureListener[] p_150732_2_)
    {
        if (EventBus.dispatch(new EventPacket.Outbound(packet)))
        {
            return;
        }
        sendPacketInstantly(packet, p_150732_2_);
    }

    public void sendPacketInstantly(final Packet packet, final GenericFutureListener... futureListeners)
    {
        final EnumConnectionState packetState = EnumConnectionState.getStateForPacket(packet);
        final EnumConnectionState currentState = this.channel.attr(ATTRIBUTE_CONNECTION_STATE).get();

        if (currentState != packetState)
        {
            LOGGER.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }

        if (this.channel.eventLoop().inEventLoop())
        {
            if (packetState != currentState)
            {
                this.setConnectionState(packetState);
            }

            this.channel.writeAndFlush(packet)
                    .addListeners(futureListeners)
                    .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
        else
        {
            this.channel.eventLoop().execute(() ->
            {
                if (packetState != currentState)
                {
                    NetworkManager.this.setConnectionState(packetState);
                }

                NetworkManager.this.channel.writeAndFlush(packet)
                        .addListeners(futureListeners)
                        .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            });
        }
    }

    /**
     * Will iterate through the outboundPacketQueue and dispatch all Packets
     */
    private void flushOutboundQueue()
    {
        if (this.channel != null && this.channel.isOpen())
        {
            while (!this.outboundPacketsQueue.isEmpty())
            {
                NetworkManager.InboundHandlerTuplePacketListener var1 = this.outboundPacketsQueue.poll();
                if (var1 == null)
                {
                    break;
                }
                this.dispatchPacket(var1.packet, var1.futureListeners);
            }
        }
    }

    /**
     * Checks timeouts and processes all packets received
     */
    public void processReceivedPackets()
    {
        this.flushOutboundQueue();
        EnumConnectionState state = this.channel.attr(ATTRIBUTE_CONNECTION_STATE).get();

        if (this.connectionState != state)
        {
            if (this.connectionState != null)
            {
                this.netHandler.onConnectionStateTransition(this.connectionState, state);
            }

            this.connectionState = state;
        }

        if (this.netHandler != null)
        {
            for (int packets = 1000; !this.receivedPacketsQueue.isEmpty() && packets >= 0; --packets)
            {
                final Packet packet = this.receivedPacketsQueue.poll();
                if (packet == null)
                {
                    break;
                }
                packet.processPacket(this.netHandler);
            }

            this.netHandler.onNetworkTick();
        }

        this.channel.flush();
    }

    /**
     * Return the InetSocketAddress of the remote endpoint
     */
    public SocketAddress getSocketAddress()
    {
        return this.socketAddress;
    }

    /**
     * Closes the channel, the parameter can be used for an exit message (not certain how it gets sent)
     */
    public void closeChannel(IChatComponent chatComponent)
    {
        if (this.channel.isOpen())
        {
            this.channel.close();
            this.terminationReason = chatComponent;
        }
    }

    /**
     * True if this NetworkManager uses a memory connection (single player game). False may imply both an active TCP
     * connection or simply no active connection at all
     */
    public boolean isLocalChannel()
    {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    /**
     * Prepares a clientside NetworkManager: establishes a connection to the address and port supplied and configures
     * the channel pipeline. Returns the newly created instance.
     */
    public static NetworkManager provideLanClient(final InetAddress address, final int port)
    {
        final NetworkManager networkManager = new NetworkManager(true);
        (new Bootstrap()).group(eventLoops).handler(new ChannelInitializer()
        {
            @Override
            protected void initChannel(final Channel channel)
            {
                try
                {
                    channel.config().setOption(ChannelOption.IP_TOS, 24);
                }
                catch (ChannelException ignored)
                {
                }

                try
                {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (ChannelException ignored)
                {
                }

                channel.pipeline()
                        .addLast("timeout", new ReadTimeoutHandler(20))
                        .addLast("splitter", new MessageDeserializer2())
                        .addLast("decoder", new MessageDeserializer())
                        .addLast("prepender", new MessageSerializer2())
                        .addLast("encoder", new MessageSerializer())
                        .addLast("packet_handler", networkManager);
            }
        }).channel(NioSocketChannel.class)
                .connect(address, port)
                .syncUninterruptibly();
        return networkManager;
    }

    /**
     * Prepares a clientside NetworkManager: establishes a connection to the socket supplied and configures the channel
     * pipeline. Returns the newly created instance.
     */
    public static NetworkManager provideLocalClient(final SocketAddress address)
    {
        final NetworkManager networkManager = new NetworkManager(true);
        (new Bootstrap()).group(eventLoops).handler(new ChannelInitializer()
        {
            @Override
            protected void initChannel(final Channel channel)
            {
                try
                {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (ChannelException ignored)
                {
                }
                channel.pipeline().addLast("packet_handler", networkManager);
            }
        }).channel(LocalChannel.class)
                .connect(address)
                .syncUninterruptibly();
        return networkManager;
    }

    /**
     * Adds an encoder+decoder to the channel pipeline. The parameter is the secret key used for encrypted communication
     */
    public void enableEncryption(SecretKey p_150727_1_)
    {
        this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.func_151229_a(2, p_150727_1_)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.func_151229_a(1, p_150727_1_)));
    }

    /**
     * Returns true if this NetworkManager has an active channel, false otherwise
     */
    public boolean isChannelOpen()
    {
        return this.channel != null && this.channel.isOpen();
    }

    /**
     * Gets the current handler for processing packets
     */
    public INetHandler getNetHandler()
    {
        return this.netHandler;
    }

    /**
     * If this channel is closed, returns the exit message, null otherwise.
     */
    public IChatComponent getExitMessage()
    {
        return this.terminationReason;
    }

    /**
     * Switches the channel to manual reading modus
     */
    public void disableAutoRead()
    {
        this.channel.config().setAutoRead(false);
    }

    static class InboundHandlerTuplePacketListener
    {
        private final Packet packet;
        private final GenericFutureListener[] futureListeners;

        public InboundHandlerTuplePacketListener(Packet packet, GenericFutureListener ... futureListeners)
        {
            this.packet = packet;
            this.futureListeners = futureListeners;
        }
    }
}
