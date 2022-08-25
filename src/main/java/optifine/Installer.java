package optifine;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import optifine.json.JSONArray;
import optifine.json.JSONObject;
import optifine.json.JSONParser;
import optifine.json.JSONWriter;
import optifine.json.ParseException;

public class Installer
{
    public static void main(String[] args)
    {
        try
        {
            File e = Utils.getWorkingDirectory();
            doInstall(e);
        }
        catch (Exception var8)
        {
            String msg = var8.getMessage();

            if (msg != null && msg.equals("QUIET"))
            {
                return;
            }

            var8.printStackTrace();
            String str = Utils.getExceptionStackTrace(var8);
            str = str.replace("\t", "  ");
            JTextArea textArea = new JTextArea(str);
            textArea.setEditable(false);
            Font f = textArea.getFont();
            Font f2 = new Font("Monospaced", f.getStyle(), f.getSize());
            textArea.setFont(f2);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog((Component)null, scrollPane, "Error", 0);
        }
    }

    public static void doInstall(File dirMc) throws Exception
    {
        Utils.dbg("Dir minecraft: " + dirMc);
        File dirMcLib = new File(dirMc, "libraries");
        Utils.dbg("Dir libraries: " + dirMcLib);
        File dirMcVers = new File(dirMc, "versions");
        Utils.dbg("Dir versions: " + dirMcVers);
        String ofVer = getOptiFineVersion();
        Utils.dbg("OptiFine Version: " + ofVer);
        String[] ofVers = Utils.tokenize(ofVer, "_");
        String mcVer = ofVers[1];
        Utils.dbg("Minecraft Version: " + mcVer);
        String ofEd = getOptiFineEdition(ofVers);
        Utils.dbg("OptiFine Edition: " + ofEd);
        String mcVerOf = mcVer + "-OptiFine_" + ofEd;
        Utils.dbg("Minecraft_OptiFine Version: " + mcVerOf);
        copyMinecraftVersion(mcVer, mcVerOf, dirMcVers);
        installOptiFineLibrary(mcVer, ofEd, dirMcLib, false);
        updateJson(dirMcVers, mcVerOf, dirMcLib, mcVer, ofEd);
        updateLauncherJson(dirMc, mcVerOf);
    }

    public static boolean doExtract(File dirMc) throws Exception
    {
        Utils.dbg("Dir minecraft: " + dirMc);
        File dirMcLib = new File(dirMc, "libraries");
        Utils.dbg("Dir libraries: " + dirMcLib);
        File dirMcVers = new File(dirMc, "versions");
        Utils.dbg("Dir versions: " + dirMcVers);
        String ofVer = getOptiFineVersion();
        Utils.dbg("OptiFine Version: " + ofVer);
        String[] ofVers = Utils.tokenize(ofVer, "_");
        String mcVer = ofVers[1];
        Utils.dbg("Minecraft Version: " + mcVer);
        String ofEd = getOptiFineEdition(ofVers);
        Utils.dbg("OptiFine Edition: " + ofEd);
        String mcVerOf = mcVer + "-OptiFine_" + ofEd;
        Utils.dbg("Minecraft_OptiFine Version: " + mcVerOf);
        return installOptiFineLibrary(mcVer, ofEd, dirMcLib, true);
    }

    private static void updateLauncherJson(File dirMc, String mcVerOf) throws IOException, ParseException
    {
        File fileJson = new File(dirMc, "launcher_profiles.json");

        if (fileJson.exists() && fileJson.isFile())
        {
            String json = Utils.readFile(fileJson, "UTF-8");
            JSONParser jp = new JSONParser();
            JSONObject root = (JSONObject)jp.parse(json);
            JSONObject profiles = (JSONObject)root.get("profiles");
            JSONObject prof = (JSONObject)profiles.get("OptiFine");

            if (prof == null)
            {
                prof = new JSONObject();
                prof.put("name", "OptiFine");
                profiles.put("OptiFine", prof);
            }

            prof.put("lastVersionId", mcVerOf);
            root.put("selectedProfile", "OptiFine");
            FileOutputStream fosJson = new FileOutputStream(fileJson);
            OutputStreamWriter oswJson = new OutputStreamWriter(fosJson, "UTF-8");
            JSONWriter jw = new JSONWriter(oswJson);
            jw.writeObject(root);
            oswJson.flush();
            oswJson.close();
        }
        else
        {
            Utils.showErrorMessage("File not found: " + fileJson);
            throw new RuntimeException("QUIET");
        }
    }

    private static void updateJson(File dirMcVers, String mcVerOf, File dirMcLib, String mcVer, String ofEd) throws IOException, ParseException
    {
        File dirMcVersOf = new File(dirMcVers, mcVerOf);
        File fileJson = new File(dirMcVersOf, mcVerOf + ".json");
        String json = Utils.readFile(fileJson, "UTF-8");
        JSONParser jp = new JSONParser();
        JSONObject root = (JSONObject)jp.parse(json);
        root.put("id", mcVerOf);
        JSONArray libs = (JSONArray)root.get("libraries");
        root.put("inheritsFrom", mcVer);
        libs = new JSONArray();
        root.put("libraries", libs);
        String mainClass = (String)root.get("mainClass");

        if (!mainClass.startsWith("net.minecraft.launchwrapper."))
        {
            mainClass = "net.minecraft.launchwrapper.Launch";
            root.put("mainClass", mainClass);
            String libOf = (String)root.get("minecraftArguments");
            libOf = libOf + "  --tweakClass optifine.OptiFineTweaker";
            root.put("minecraftArguments", libOf);
            JSONObject fosJson = new JSONObject();
            fosJson.put("name", "net.minecraft:launchwrapper:1.12");
            libs.add(0, fosJson);
        }

        JSONObject libOf1 = new JSONObject();
        libOf1.put("name", "optifine:OptiFine:" + mcVer + "_" + ofEd);
        libs.add(0, libOf1);
        FileOutputStream fosJson1 = new FileOutputStream(fileJson);
        OutputStreamWriter oswJson = new OutputStreamWriter(fosJson1, "UTF-8");
        JSONWriter jw = new JSONWriter(oswJson);
        jw.writeObject(root);
        oswJson.flush();
        oswJson.close();
    }

    public static String getOptiFineEdition(String[] ofVers)
    {
        if (ofVers.length <= 2)
        {
            return "";
        }
        else
        {
            String ofEd = "";

            for (int i = 2; i < ofVers.length; ++i)
            {
                if (i > 2)
                {
                    ofEd = ofEd + "_";
                }

                ofEd = ofEd + ofVers[i];
            }

            return ofEd;
        }
    }

    private static boolean installOptiFineLibrary(String mcVer, String ofEd, File dirMcLib, boolean selectTarget) throws Exception
    {
        File fileSrc = getOptiFineZipFile();
        File dirDest = new File(dirMcLib, "optifine/OptiFine/" + mcVer + "_" + ofEd);
        File fileDest = new File(dirDest, "OptiFine-" + mcVer + "_" + ofEd + ".jar");

        if (selectTarget)
        {
            fileDest = new File(fileSrc.getParentFile(), "OptiFine_" + mcVer + "_" + ofEd + "_MOD.jar");
            JFileChooser dirMc = new JFileChooser(fileDest.getParentFile());
            dirMc.setSelectedFile(fileDest);
            int fileBase = dirMc.showSaveDialog((Component)null);

            if (fileBase != 0)
            {
                return false;
            }

            fileDest = dirMc.getSelectedFile();

            if (fileDest.exists())
            {
                JOptionPane.setDefaultLocale(Locale.ENGLISH);
                int ret2 = JOptionPane.showConfirmDialog((Component)null, "The file \"" + fileDest.getName() + "\" already exists.\nDo you want to overwrite it?", "Save", 1);

                if (ret2 != 0)
                {
                    return false;
                }
            }
        }

        if (fileDest.equals(fileSrc))
        {
            JOptionPane.showMessageDialog((Component)null, "Source and target file are the same.", "Save", 0);
            return false;
        }
        else
        {
            Utils.dbg("Source: " + fileSrc);
            Utils.dbg("Dest: " + fileDest);
            File dirMc1 = dirMcLib.getParentFile();
            File fileBase1 = new File(dirMc1, "versions/" + mcVer + "/" + mcVer + ".jar");

            if (!fileBase1.exists())
            {
                showMessageVersionNotFound(mcVer);
                throw new RuntimeException("QUIET");
            }
            else
            {
                if (fileDest.getParentFile() != null)
                {
                    fileDest.getParentFile().mkdirs();
                }

                Patcher.process(fileBase1, fileSrc, fileDest);
                return true;
            }
        }
    }

    public static File getOptiFineZipFile() throws Exception
    {
        URL url = Installer.class.getProtectionDomain().getCodeSource().getLocation();
        Utils.dbg("URL: " + url);
        URI uri = url.toURI();
        File fileZip = new File(uri);
        return fileZip;
    }

    public static boolean isPatchFile() throws Exception
    {
        File fileZip = getOptiFineZipFile();
        ZipFile zipFile = new ZipFile(fileZip);

        try
        {
            Enumeration entries = zipFile.entries();
            ZipEntry zipEntry;

            do
            {
                if (!entries.hasMoreElements())
                {
                    return false;
                }

                zipEntry = (ZipEntry)entries.nextElement();
            }
            while (!zipEntry.getName().startsWith("patch/"));
        }
        finally
        {
            zipFile.close();
        }

        return true;
    }

    private static void copyMinecraftVersion(String mcVer, String mcVerOf, File dirMcVer) throws IOException
    {
        File dirVerMc = new File(dirMcVer, mcVer);

        if (!dirVerMc.exists())
        {
            showMessageVersionNotFound(mcVer);
            throw new RuntimeException("QUIET");
        }
        else
        {
            File dirVerMcOf = new File(dirMcVer, mcVerOf);
            dirVerMcOf.mkdirs();
            Utils.dbg("Dir version MC: " + dirVerMc);
            Utils.dbg("Dir version MC-OF: " + dirVerMcOf);
            File fileJarMc = new File(dirVerMc, mcVer + ".jar");
            File fileJarMcOf = new File(dirVerMcOf, mcVerOf + ".jar");

            if (!fileJarMc.exists())
            {
                showMessageVersionNotFound(mcVer);
                throw new RuntimeException("QUIET");
            }
            else
            {
                Utils.copyFile(fileJarMc, fileJarMcOf);
                File fileJsonMc = new File(dirVerMc, mcVer + ".json");
                File fileJsonMcOf = new File(dirVerMcOf, mcVerOf + ".json");
                Utils.copyFile(fileJsonMc, fileJsonMcOf);
            }
        }
    }

    private static void showMessageVersionNotFound(String mcVer)
    {
        Utils.showErrorMessage("Minecraft version " + mcVer + " not found.\nYou need to start the version " + mcVer + " manually once.");
    }

    public static String getOptiFineVersion() throws IOException
    {
        InputStream in = Installer.class.getResourceAsStream("/Config.class");

        if (in == null)
        {
            in = Installer.class.getResourceAsStream("/VersionThread.class");
        }

        return getOptiFineVersion(in);
    }

    public static String getOptiFineVersion(ZipFile zipFile) throws IOException
    {
        ZipEntry zipEntry = zipFile.getEntry("Config.class");

        if (zipEntry == null)
        {
            zipEntry = zipFile.getEntry("VersionThread.class");
        }

        if (zipEntry == null)
        {
            return null;
        }
        else
        {
            InputStream in = zipFile.getInputStream(zipEntry);
            String ofVer = getOptiFineVersion(in);
            in.close();
            return ofVer;
        }
    }

    public static String getOptiFineVersion(InputStream in) throws IOException
    {
        byte[] bytes = Utils.readAll(in);
        byte[] pattern = "OptiFine_".getBytes("ASCII");
        int pos = Utils.find(bytes, pattern);

        if (pos < 0)
        {
            return null;
        }
        else
        {
            int startPos = pos;

            for (pos = pos; pos < bytes.length; ++pos)
            {
                byte endPos = bytes[pos];

                if (endPos < 32 || endPos > 122)
                {
                    break;
                }
            }

            String ver = new String(bytes, startPos, pos - startPos, "ASCII");
            return ver;
        }
    }

    public static String getMinecraftVersionFromOfVersion(String ofVer)
    {
        if (ofVer == null)
        {
            return null;
        }
        else
        {
            String[] ofVers = Utils.tokenize(ofVer, "_");

            if (ofVers.length < 2)
            {
                return null;
            }
            else
            {
                String mcVer = ofVers[1];
                return mcVer;
            }
        }
    }
}
