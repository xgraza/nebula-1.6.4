package optifine.xdelta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class GDiffPatcher
{
    public GDiffPatcher(File sourceFile, File patchFile, File outputFile) throws IOException, PatchException
    {
        RandomAccessFileSeekableSource source = new RandomAccessFileSeekableSource(new RandomAccessFile(sourceFile, "r"));
        FileInputStream patch = new FileInputStream(patchFile);
        FileOutputStream output = new FileOutputStream(outputFile);

        try
        {
            runPatch(source, patch, output);
        }
        catch (IOException var12)
        {
            throw var12;
        }
        catch (PatchException var13)
        {
            throw var13;
        }
        finally
        {
            source.close();
            patch.close();
            output.close();
        }
    }

    public GDiffPatcher(byte[] source, InputStream patch, OutputStream output) throws IOException, PatchException
    {
        this((SeekableSource)(new ByteArraySeekableSource(source)), patch, output);
    }

    public GDiffPatcher(SeekableSource source, InputStream patch, OutputStream out) throws IOException, PatchException
    {
        runPatch(source, patch, out);
    }

    private static void runPatch(SeekableSource source, InputStream patch, OutputStream out) throws IOException, PatchException
    {
        DataOutputStream outOS = new DataOutputStream(out);
        DataInputStream patchIS = new DataInputStream(patch);

        try
        {
            byte[] e = new byte[256];
            byte i = 0;

            if (patchIS.readUnsignedByte() == 209 && patchIS.readUnsignedByte() == 255 && patchIS.readUnsignedByte() == 209 && patchIS.readUnsignedByte() == 255 && patchIS.readUnsignedByte() == 4)
            {
                int i1 = i + 5;

                while (patchIS.available() > 0)
                {
                    int command = patchIS.readUnsignedByte();
                    boolean length = false;
                    boolean offset = false;
                    int length1;
                    int offset1;

                    switch (command)
                    {
                        case 0:
                            break;

                        case 1:
                            append(1, patchIS, outOS);
                            i1 += 2;
                            break;

                        case 2:
                            append(2, patchIS, outOS);
                            i1 += 3;
                            break;

                        case 246:
                            append(246, patchIS, outOS);
                            i1 += 247;
                            break;

                        case 247:
                            length1 = patchIS.readUnsignedShort();
                            append(length1, patchIS, outOS);
                            i1 += length1 + 3;
                            break;

                        case 248:
                            length1 = patchIS.readInt();
                            append(length1, patchIS, outOS);
                            i1 += length1 + 5;
                            break;

                        case 249:
                            offset1 = patchIS.readUnsignedShort();
                            length1 = patchIS.readUnsignedByte();
                            copy((long)offset1, length1, source, outOS);
                            i1 += 4;
                            break;

                        case 250:
                            offset1 = patchIS.readUnsignedShort();
                            length1 = patchIS.readUnsignedShort();
                            copy((long)offset1, length1, source, outOS);
                            i1 += 5;
                            break;

                        case 251:
                            offset1 = patchIS.readUnsignedShort();
                            length1 = patchIS.readInt();
                            copy((long)offset1, length1, source, outOS);
                            i1 += 7;
                            break;

                        case 252:
                            offset1 = patchIS.readInt();
                            length1 = patchIS.readUnsignedByte();
                            copy((long)offset1, length1, source, outOS);
                            i1 += 8;
                            break;

                        case 253:
                            offset1 = patchIS.readInt();
                            length1 = patchIS.readUnsignedShort();
                            copy((long)offset1, length1, source, outOS);
                            i1 += 7;
                            break;

                        case 254:
                            offset1 = patchIS.readInt();
                            length1 = patchIS.readInt();
                            copy((long)offset1, length1, source, outOS);
                            i1 += 9;
                            break;

                        case 255:
                            long loffset = patchIS.readLong();
                            length1 = patchIS.readInt();
                            copy(loffset, length1, source, outOS);
                            i1 += 13;
                            break;

                        default:
                            append(command, patchIS, outOS);
                            i1 += command + 1;
                    }
                }
            }
            else
            {
                System.err.println("magic string not found, aborting!");
            }
        }
        catch (PatchException var15)
        {
            throw var15;
        }
        finally
        {
            outOS.flush();
        }
    }

    protected static void copy(long offset, int length, SeekableSource source, OutputStream output) throws IOException, PatchException
    {
        if (offset + (long)length > source.length())
        {
            throw new PatchException("truncated source file, aborting");
        }
        else
        {
            byte[] buf = new byte[256];
            source.seek(offset);

            while (length > 0)
            {
                int len = length > 256 ? 256 : length;
                int res = source.read(buf, 0, len);
                output.write(buf, 0, res);
                length -= res;
            }
        }
    }

    protected static void append(int length, InputStream patch, OutputStream output) throws IOException
    {
        int res;

        for (byte[] buf = new byte[256]; length > 0; length -= res)
        {
            int len = length > 256 ? 256 : length;
            res = patch.read(buf, 0, len);
            output.write(buf, 0, res);
        }
    }

    public static void main(String[] argv)
    {
        if (argv.length != 3)
        {
            System.err.println("usage GDiffPatch source patch output");
            System.err.println("aborting..");
        }
        else
        {
            try
            {
                File ioe = new File(argv[0]);
                File patchFile = new File(argv[1]);
                File outputFile = new File(argv[2]);

                if (ioe.length() > 2147483647L || patchFile.length() > 2147483647L)
                {
                    System.err.println("source or patch is too large, max length is 2147483647");
                    System.err.println("aborting..");
                    return;
                }

                new GDiffPatcher(ioe, patchFile, outputFile);
                System.out.println("finished patching file");
            }
            catch (Exception var5)
            {
                System.err.println("error while patching: " + var5);
            }
        }
    }
}
