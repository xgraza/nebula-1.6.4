package optifine;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class Utils
{
    public static final String MAC_OS_HOME_PREFIX = "Library/Application Support";
    private static final char[] hexTable = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static int[] $SWITCH_TABLE$optifine$Utils$OS;

    public static File getWorkingDirectory()
    {
        return getWorkingDirectory("minecraft");
    }

    public static File getWorkingDirectory(String applicationName)
    {
        String userHome = System.getProperty("user.home", ".");
        File workingDirectory = null;

        switch ($SWITCH_TABLE$optifine$Utils$OS()[getPlatform().ordinal()])
        {
            case 1:
            case 2:
                workingDirectory = new File(userHome, '.' + applicationName + '/');
                break;

            case 3:
                String applicationData = System.getenv("APPDATA");

                if (applicationData != null)
                {
                    workingDirectory = new File(applicationData, "." + applicationName + '/');
                }
                else
                {
                    workingDirectory = new File(userHome, '.' + applicationName + '/');
                }

                break;

            case 4:
                workingDirectory = new File(userHome, "Library/Application Support/" + applicationName);
                break;

            default:
                workingDirectory = new File(userHome, applicationName + '/');
        }

        if (!workingDirectory.exists() && !workingDirectory.mkdirs())
        {
            throw new RuntimeException("The working directory could not be created: " + workingDirectory);
        }
        else
        {
            return workingDirectory;
        }
    }

    public static Utils.OS getPlatform()
    {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win") ? Utils.OS.WINDOWS : (osName.contains("mac") ? Utils.OS.MACOS : (osName.contains("solaris") ? Utils.OS.SOLARIS : (osName.contains("sunos") ? Utils.OS.SOLARIS : (osName.contains("linux") ? Utils.OS.LINUX : (osName.contains("unix") ? Utils.OS.LINUX : Utils.OS.UNKNOWN)))));
    }

    public static int find(byte[] buf, byte[] pattern)
    {
        return find(buf, 0, pattern);
    }

    public static int find(byte[] buf, int index, byte[] pattern)
    {
        int i = index;

        while (i < buf.length - pattern.length)
        {
            boolean found = true;
            int pos = 0;

            while (true)
            {
                if (pos < pattern.length)
                {
                    if (pattern[pos] == buf[i + pos])
                    {
                        ++pos;
                        continue;
                    }

                    found = false;
                }

                if (found)
                {
                    return i;
                }

                ++i;
                break;
            }
        }

        return -1;
    }

    public static byte[] readAll(InputStream is) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];

        while (true)
        {
            int bytes = is.read(buf);

            if (bytes < 0)
            {
                is.close();
                byte[] bytes1 = baos.toByteArray();
                return bytes1;
            }

            baos.write(buf, 0, bytes);
        }
    }

    public static void dbg(String str)
    {
        System.out.println(str);
    }

    public static String[] tokenize(String str, String delim)
    {
        ArrayList list = new ArrayList();
        StringTokenizer tok = new StringTokenizer(str, delim);

        while (tok.hasMoreTokens())
        {
            String tokens = tok.nextToken();
            list.add(tokens);
        }

        String[] tokens1 = (String[])list.toArray(new String[list.size()]);
        return tokens1;
    }

    public static String getExceptionStackTrace(Throwable e)
    {
        StringWriter swr = new StringWriter();
        PrintWriter pwr = new PrintWriter(swr);
        e.printStackTrace(pwr);
        pwr.close();

        try
        {
            swr.close();
        }
        catch (IOException var4)
        {
            ;
        }

        return swr.getBuffer().toString();
    }

    public static void copyFile(File fileSrc, File fileDest) throws IOException
    {
        if (!fileSrc.getCanonicalPath().equals(fileDest.getCanonicalPath()))
        {
            FileInputStream fin = new FileInputStream(fileSrc);
            FileOutputStream fout = new FileOutputStream(fileDest);
            copyAll(fin, fout);
            fout.flush();
            fin.close();
            fout.close();
        }
    }

    public static void copyAll(InputStream is, OutputStream os) throws IOException
    {
        byte[] buf = new byte[1024];

        while (true)
        {
            int len = is.read(buf);

            if (len < 0)
            {
                return;
            }

            os.write(buf, 0, len);
        }
    }

    public static void showMessage(String msg)
    {
        JOptionPane.showMessageDialog((Component)null, msg, "OptiFine", 1);
    }

    public static void showErrorMessage(String msg)
    {
        JOptionPane.showMessageDialog((Component)null, msg, "Error", 0);
    }

    public static String readFile(File file) throws IOException
    {
        return readFile(file, "ASCII");
    }

    public static String readFile(File file, String encoding) throws IOException
    {
        FileInputStream fin = new FileInputStream(file);
        return readText(fin, encoding);
    }

    public static String readText(InputStream in, String encoding) throws IOException
    {
        InputStreamReader inr = new InputStreamReader(in, encoding);
        BufferedReader br = new BufferedReader(inr);
        StringBuffer sb = new StringBuffer();

        while (true)
        {
            String line = br.readLine();

            if (line == null)
            {
                br.close();
                inr.close();
                in.close();
                return sb.toString();
            }

            sb.append(line);
            sb.append("\n");
        }
    }

    public static String[] readLines(InputStream in, String encoding) throws IOException
    {
        String str = readText(in, encoding);
        String[] strs = tokenize(str, "\n\r");
        return strs;
    }

    public static void centerWindow(Component c, Component par)
    {
        if (c != null)
        {
            Rectangle rect = c.getBounds();
            Rectangle parRect;

            if (par != null && par.isVisible())
            {
                parRect = par.getBounds();
            }
            else
            {
                Dimension newX = Toolkit.getDefaultToolkit().getScreenSize();
                parRect = new Rectangle(0, 0, newX.width, newX.height);
            }

            int newX1 = parRect.x + (parRect.width - rect.width) / 2;
            int newY = parRect.y + (parRect.height - rect.height) / 2;

            if (newX1 < 0)
            {
                newX1 = 0;
            }

            if (newY < 0)
            {
                newY = 0;
            }

            c.setBounds(newX1, newY, rect.width, rect.height);
        }
    }

    public static String byteArrayToHexString(byte[] bytes)
    {
        if (bytes == null)
        {
            return "";
        }
        else
        {
            StringBuffer buf = new StringBuffer();

            for (int i = 0; i < bytes.length; ++i)
            {
                byte b = bytes[i];
                buf.append(hexTable[b >> 4 & 15]);
                buf.append(hexTable[b & 15]);
            }

            return buf.toString();
        }
    }

    public static String arrayToCommaSeparatedString(Object[] arr)
    {
        if (arr == null)
        {
            return "";
        }
        else
        {
            StringBuffer buf = new StringBuffer();

            for (int i = 0; i < arr.length; ++i)
            {
                Object val = arr[i];

                if (i > 0)
                {
                    buf.append(", ");
                }

                if (val == null)
                {
                    buf.append("null");
                }
                else if (val.getClass().isArray())
                {
                    buf.append("[");

                    if (val instanceof Object[])
                    {
                        Object[] ai = (Object[])val;
                        buf.append(arrayToCommaSeparatedString(ai));
                    }
                    else
                    {
                        for (int var5 = 0; var5 < Array.getLength(val); ++var5)
                        {
                            if (var5 > 0)
                            {
                                buf.append(", ");
                            }

                            buf.append(Array.get(val, var5));
                        }
                    }

                    buf.append("]");
                }
                else
                {
                    buf.append(arr[i]);
                }
            }

            return buf.toString();
        }
    }

    public static String removePrefix(String str, String prefix)
    {
        if (str != null && prefix != null)
        {
            if (str.startsWith(prefix))
            {
                str = str.substring(prefix.length());
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String ensurePrefix(String str, String prefix)
    {
        if (str != null && prefix != null)
        {
            if (!str.startsWith(prefix))
            {
                str = prefix + str;
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static boolean equals(Object o1, Object o2)
    {
        return o1 == o2 ? true : (o1 == null ? false : o1.equals(o2));
    }

    static int[] $SWITCH_TABLE$optifine$Utils$OS()
    {
        if ($SWITCH_TABLE$optifine$Utils$OS != null)
        {
            return $SWITCH_TABLE$optifine$Utils$OS;
        }
        else
        {
            int[] var0 = new int[Utils.OS.values().length];

            try
            {
                var0[Utils.OS.LINUX.ordinal()] = 1;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try
            {
                var0[Utils.OS.MACOS.ordinal()] = 4;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                var0[Utils.OS.SOLARIS.ordinal()] = 2;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                var0[Utils.OS.UNKNOWN.ordinal()] = 5;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                var0[Utils.OS.WINDOWS.ordinal()] = 3;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }

            $SWITCH_TABLE$optifine$Utils$OS = var0;
            return var0;
        }
    }

    public static enum OS
    {
        LINUX("LINUX", 0),
        SOLARIS("SOLARIS", 1),
        WINDOWS("WINDOWS", 2),
        MACOS("MACOS", 3),
        UNKNOWN("UNKNOWN", 4);

        private static final Utils.OS[] ENUM$VALUES = new Utils.OS[]{LINUX, SOLARIS, WINDOWS, MACOS, UNKNOWN};

        private OS(String var1, int var2) {}
    }
}
