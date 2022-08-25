package optifine.xdelta;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;

public class Delta
{
    public static final int S = 16;
    public static final boolean debug = false;
    public static final int buff_size = 1024;

    public static void computeDelta(SeekableSource source, InputStream targetIS, int targetLength, DiffWriter output) throws IOException, DeltaException
    {
        int sourceLength = (int)source.length();
        Checksum checksum = new Checksum();
        checksum.generateChecksums((InputStream)(new SeekableSourceInputStream(source)), sourceLength);
        source.seek(0L);
        PushbackInputStream target = new PushbackInputStream(new BufferedInputStream(targetIS), 1024);
        boolean done = false;
        byte[] buf = new byte[16];
        long hashf = 0L;
        byte[] b = new byte[1];
        byte[] sourcebyte = new byte[16];
        int bytesRead;
        int targetidx;

        if (targetLength > 16 && sourceLength > 16)
        {
            bytesRead = target.read(buf, 0, 16);
            targetidx = bytesRead;
            hashf = Checksum.queryChecksum(buf, 16);
            long alternativehashf = hashf;
            boolean sourceOutofBytes = false;
            label124:

            while (!done)
            {
                int index = checksum.findChecksumIndex(hashf);

                if (index != -1)
                {
                    boolean ix = true;
                    int offset = index * 16;
                    int length = 15;
                    source.seek((long)offset);

                    if (!sourceOutofBytes && source.read(sourcebyte, 0, 16) != -1)
                    {
                        for (int start = 0; start < 16; ++start)
                        {
                            if (sourcebyte[start] != buf[start])
                            {
                                ix = false;
                            }
                        }
                    }
                    else
                    {
                        sourceOutofBytes = true;
                    }

                    if (ix & !sourceOutofBytes)
                    {
                        long var34 = System.currentTimeMillis();
                        boolean ok = true;
                        byte[] sourceBuff = new byte[1024];
                        byte[] targetBuff = new byte[1024];
                        boolean source_idx = false;
                        boolean target_idx = false;
                        boolean tCount = false;

                        while (true)
                        {
                            int var35 = source.read(sourceBuff, 0, 1024);
                            int remaining;

                            if (var35 == -1)
                            {
                                sourceOutofBytes = true;
                            }
                            else
                            {
                                int var36 = target.read(targetBuff, 0, var35);

                                if (var36 != -1)
                                {
                                    remaining = Math.min(var35, var36);
                                    int readStatus = 0;

                                    do
                                    {
                                        ++targetidx;
                                        ++length;
                                        ok = sourceBuff[readStatus] == targetBuff[readStatus];
                                        ++readStatus;

                                        if (!ok)
                                        {
                                            b[0] = targetBuff[readStatus - 1];

                                            if (var36 != -1)
                                            {
                                                target.unread(targetBuff, readStatus, var36 - readStatus);
                                            }
                                        }
                                    }
                                    while (readStatus < remaining && ok);

                                    b[0] = targetBuff[readStatus - 1];

                                    if (ok && targetLength - targetidx > 0)
                                    {
                                        continue;
                                    }
                                }
                            }

                            output.addCopy(offset, length);

                            if (targetLength - targetidx > 15)
                            {
                                buf[0] = b[0];
                                target.read(buf, 1, 15);
                                targetidx += 15;
                                alternativehashf = hashf = Checksum.queryChecksum(buf, 16);
                                continue label124;
                            }

                            buf[0] = b[0];
                            remaining = targetLength - targetidx;
                            target.read(buf, 1, remaining);
                            targetidx += remaining;

                            for (int ix1 = 0; ix1 < remaining + 1; ++ix1)
                            {
                                output.addData(buf[ix1]);
                            }

                            done = true;
                            continue label124;
                        }
                    }
                }

                int var33;

                if (targetLength - targetidx > 0)
                {
                    target.read(b, 0, 1);
                    ++targetidx;
                    output.addData(buf[0]);
                    alternativehashf = Checksum.incrementChecksum(alternativehashf, buf[0], b[0]);

                    for (var33 = 0; var33 < 15; ++var33)
                    {
                        buf[var33] = buf[var33 + 1];
                    }

                    buf[15] = b[0];
                    hashf = Checksum.queryChecksum(buf, 16);
                }
                else
                {
                    for (var33 = 0; var33 < 16; ++var33)
                    {
                        output.addData(buf[var33]);
                    }

                    done = true;
                }
            }
        }
        else
        {
            while ((bytesRead = target.read(buf)) >= 0)
            {
                for (targetidx = 0; targetidx < bytesRead; ++targetidx)
                {
                    output.addData(buf[targetidx]);
                }
            }
        }
    }

    public static void computeDelta(byte[] source, InputStream targetIS, int targetLength, DiffWriter output) throws IOException, DeltaException
    {
        computeDelta((SeekableSource)(new ByteArraySeekableSource(source)), targetIS, targetLength, output);
    }

    public static void computeDelta(File sourceFile, File targetFile, DiffWriter output) throws IOException, DeltaException
    {
        int targetLength = (int)targetFile.length();
        RandomAccessFileSeekableSource source = new RandomAccessFileSeekableSource(new RandomAccessFile(sourceFile, "r"));
        FileInputStream targetIS = new FileInputStream(targetFile);

        try
        {
            computeDelta((SeekableSource)source, targetIS, targetLength, output);
        }
        catch (IOException var11)
        {
            throw var11;
        }
        catch (DeltaException var12)
        {
            throw var12;
        }
        finally
        {
            output.flush();
            source.close();
            targetIS.close();
            output.close();
        }
    }

    public static void main(String[] argv)
    {
        new Delta();

        if (argv.length != 3)
        {
            System.err.println("usage Delta [-d] source target [output]");
            System.err.println("either -d or an output filename must be specified.");
            System.err.println("aborting..");
        }
        else
        {
            try
            {
                Object e = null;
                File sourceFile = null;
                File targetFile = null;

                if (argv[0].equals("-d"))
                {
                    sourceFile = new File(argv[1]);
                    targetFile = new File(argv[2]);
                    e = new DebugDiffWriter();
                }
                else
                {
                    sourceFile = new File(argv[0]);
                    targetFile = new File(argv[1]);
                    e = new GDiffWriter(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(argv[2])))));
                }

                if (sourceFile.length() > 2147483647L || targetFile.length() > 2147483647L)
                {
                    System.err.println("source or target is too large, max length is 2147483647");
                    System.err.println("aborting..");
                    return;
                }

                computeDelta(sourceFile, targetFile, (DiffWriter)e);
                ((DiffWriter)e).flush();
                ((DiffWriter)e).close();
            }
            catch (Exception var5)
            {
                System.err.println("error while generating delta: " + var5);
            }
        }
    }
}
