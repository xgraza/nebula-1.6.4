package optifine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import optifine.xdelta.Delta;
import optifine.xdelta.DeltaException;
import optifine.xdelta.GDiffWriter;

public class Differ
{
    public static void main(String[] args) throws Exception
    {
        if (args.length < 3)
        {
            Utils.dbg("Usage: Differ <base.jar> <mod.jar> <diff.jar>");
        }
        else
        {
            File baseFile = new File(args[0]);
            File modFile = new File(args[1]);
            File diffFile = new File(args[2]);

            if (baseFile.getName().equals("AUTO"))
            {
                baseFile = detectBaseFile(modFile);
            }

            if (baseFile.exists() && baseFile.isFile())
            {
                if (modFile.exists() && modFile.isFile())
                {
                    process(baseFile, modFile, diffFile);
                }
                else
                {
                    throw new IOException("Mod file not found: " + modFile);
                }
            }
            else
            {
                throw new IOException("Base file not found: " + baseFile);
            }
        }
    }

    private static void process(File baseFile, File modFile, File diffFile) throws IOException, DeltaException, NoSuchAlgorithmException
    {
        ZipFile modZip = new ZipFile(modFile);
        Map cfgMap = Patcher.getConfigurationMap(modZip);
        Pattern[] patterns = Patcher.getConfigurationPatterns(cfgMap);
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(diffFile));
        ZipFile baseZip = new ZipFile(baseFile);
        Enumeration modZipEntries = modZip.entries();

        while (modZipEntries.hasMoreElements())
        {
            ZipEntry modZipEntry = (ZipEntry)modZipEntries.nextElement();
            InputStream in = modZip.getInputStream(modZipEntry);
            byte[] bytes = Utils.readAll(in);
            String name = modZipEntry.getName();
            byte[] bytesDiff = makeDiff(name, bytes, patterns, cfgMap, baseZip);
            ZipEntry zipEntrySame;

            if (bytesDiff != bytes)
            {
                zipEntrySame = new ZipEntry("patch/" + name + ".xdelta");
                zipOut.putNextEntry(zipEntrySame);
                zipOut.write(bytesDiff);
                zipOut.closeEntry();
                Utils.dbg("Delta: " + zipEntrySame.getName());
                byte[] md5 = HashUtils.getHashMd5(bytes);
                String md5Str = HashUtils.toHexString(md5);
                byte[] bytesMd5Str = md5Str.getBytes("ASCII");
                ZipEntry zipEntryMd5 = new ZipEntry("patch/" + name + ".md5");
                zipOut.putNextEntry(zipEntryMd5);
                zipOut.write(bytesMd5Str);
                zipOut.closeEntry();
            }
            else
            {
                zipEntrySame = new ZipEntry(name);
                zipOut.putNextEntry(zipEntrySame);
                zipOut.write(bytes);
                zipOut.closeEntry();
                Utils.dbg("Same: " + zipEntrySame.getName());
            }
        }

        zipOut.close();
    }

    public static byte[] makeDiff(String name, byte[] bytesMod, Pattern[] patterns, Map<String, String> cfgMap, ZipFile zipBase) throws IOException, DeltaException
    {
        String baseName = Patcher.getPatchBase(name, patterns, cfgMap);

        if (baseName == null)
        {
            return bytesMod;
        }
        else
        {
            ZipEntry baseEntry = zipBase.getEntry(baseName);

            if (baseEntry == null)
            {
                throw new IOException("Base entry not found: " + baseName + " in: " + zipBase.getName());
            }
            else
            {
                InputStream baseIn = zipBase.getInputStream(baseEntry);
                byte[] baseBytes = Utils.readAll(baseIn);
                ByteArrayInputStream baisTarget = new ByteArrayInputStream(bytesMod);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                GDiffWriter diffWriter = new GDiffWriter(new DataOutputStream(outputStream));
                Delta.computeDelta(baseBytes, baisTarget, bytesMod.length, diffWriter);
                diffWriter.close();
                return outputStream.toByteArray();
            }
        }
    }

    public static File detectBaseFile(File modFile) throws IOException
    {
        ZipFile modZip = new ZipFile(modFile);
        String ofVer = Installer.getOptiFineVersion(modZip);

        if (ofVer == null)
        {
            throw new IOException("Version not found");
        }
        else
        {
            modZip.close();
            String mcVer = Installer.getMinecraftVersionFromOfVersion(ofVer);

            if (mcVer == null)
            {
                throw new IOException("Version not found");
            }
            else
            {
                File dirMc = Utils.getWorkingDirectory();
                File baseFile = new File(dirMc, "versions/" + mcVer + "/" + mcVer + ".jar");
                return baseFile;
            }
        }
    }
}
