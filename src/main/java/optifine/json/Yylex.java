package optifine.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

class Yylex
{
    public static final int YYEOF = -1;
    private static final int ZZ_BUFFERSIZE = 16384;
    public static final int YYINITIAL = 0;
    public static final int STRING_BEGIN = 2;
    private static final int[] ZZ_LEXSTATE = new int[] {0, 0, 1, 1};
    private static final String ZZ_CMAP_PACKED = "\t\u0000\u0001\u0007\u0001\u0007\u0002\u0000\u0001\u0007\u0012\u0000\u0001\u0007\u0001\u0000\u0001\t\b\u0000\u0001\u0006\u0001\u0019\u0001\u0002\u0001\u0004\u0001\n\n\u0003\u0001\u001a\u0006\u0000\u0004\u0001\u0001\u0005\u0001\u0001\u0014\u0000\u0001\u0017\u0001\b\u0001\u0018\u0003\u0000\u0001\u0012\u0001\u000b\u0002\u0001\u0001\u0011\u0001\f\u0005\u0000\u0001\u0013\u0001\u0000\u0001\r\u0003\u0000\u0001\u000e\u0001\u0014\u0001\u000f\u0001\u0010\u0005\u0000\u0001\u0015\u0001\u0000\u0001\u0016\uff82\u0000";
    private static final char[] ZZ_CMAP = zzUnpackCMap("\t\u0000\u0001\u0007\u0001\u0007\u0002\u0000\u0001\u0007\u0012\u0000\u0001\u0007\u0001\u0000\u0001\t\b\u0000\u0001\u0006\u0001\u0019\u0001\u0002\u0001\u0004\u0001\n\n\u0003\u0001\u001a\u0006\u0000\u0004\u0001\u0001\u0005\u0001\u0001\u0014\u0000\u0001\u0017\u0001\b\u0001\u0018\u0003\u0000\u0001\u0012\u0001\u000b\u0002\u0001\u0001\u0011\u0001\f\u0005\u0000\u0001\u0013\u0001\u0000\u0001\r\u0003\u0000\u0001\u000e\u0001\u0014\u0001\u000f\u0001\u0010\u0005\u0000\u0001\u0015\u0001\u0000\u0001\u0016\uff82\u0000");
    private static final int[] ZZ_ACTION = zzUnpackAction();
    private static final String ZZ_ACTION_PACKED_0 = "\u0002\u0000\u0002\u0001\u0001\u0002\u0001\u0003\u0001\u0004\u0003\u0001\u0001\u0005\u0001\u0006\u0001\u0007\u0001\b\u0001\t\u0001\n\u0001\u000b\u0001\f\u0001\r\u0005\u0000\u0001\f\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0001\u0014\u0001\u0000\u0001\u0015\u0001\u0000\u0001\u0015\u0004\u0000\u0001\u0016\u0001\u0017\u0002\u0000\u0001\u0018";
    private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
    private static final String ZZ_ROWMAP_PACKED_0 = "\u0000\u0000\u0000\u001b\u00006\u0000Q\u0000l\u0000\u0087\u00006\u0000\u00a2\u0000\u00bd\u0000\u00d8\u00006\u00006\u00006\u00006\u00006\u00006\u0000\u00f3\u0000\u010e\u00006\u0000\u0129\u0000\u0144\u0000\u015f\u0000\u017a\u0000\u0195\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u0000\u01b0\u0000\u01cb\u0000\u01e6\u0000\u01e6\u0000\u0201\u0000\u021c\u0000\u0237\u0000\u0252\u00006\u00006\u0000\u026d\u0000\u0288\u00006";
    private static final int[] ZZ_TRANS = new int[] {2, 2, 3, 4, 2, 2, 2, 5, 2, 6, 2, 2, 7, 8, 2, 9, 2, 2, 2, 2, 2, 10, 11, 12, 13, 14, 15, 16, 16, 16, 16, 16, 16, 16, 16, 17, 18, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 19, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 21, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 16, 16, 16, 16, 16, 16, 16, 16, -1, -1, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, -1, -1, -1, -1, -1, -1, -1, -1, 24, 25, 26, 27, 28, 29, 30, 31, 32, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 33, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 34, 35, -1, -1, 34, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 37, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 38, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 39, -1, 39, -1, 39, -1, -1, -1, -1, -1, 39, 39, -1, -1, -1, -1, 39, 39, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 33, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 35, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 38, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 40, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 42, -1, 42, -1, 42, -1, -1, -1, -1, -1, 42, 42, -1, -1, -1, -1, 42, 42, -1, -1, -1, -1, -1, -1, -1, -1, -1, 43, -1, 43, -1, 43, -1, -1, -1, -1, -1, 43, 43, -1, -1, -1, -1, 43, 43, -1, -1, -1, -1, -1, -1, -1, -1, -1, 44, -1, 44, -1, 44, -1, -1, -1, -1, -1, 44, 44, -1, -1, -1, -1, 44, 44, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;
    private static final String[] ZZ_ERROR_MSG = new String[] {"Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large"};
    private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\u0002\u0000\u0001\t\u0003\u0001\u0001\t\u0003\u0001\u0006\t\u0002\u0001\u0001\t\u0005\u0000\b\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0004\u0000\u0002\t\u0002\u0000\u0001\t";
    private Reader zzReader;
    private int zzState;
    private int zzLexicalState;
    private char[] zzBuffer;
    private int zzMarkedPos;
    private int zzCurrentPos;
    private int zzStartRead;
    private int zzEndRead;
    private int yyline;
    private int yychar;
    private int yycolumn;
    private boolean zzAtBOL;
    private boolean zzAtEOF;
    private StringBuffer sb;

    private static int[] zzUnpackAction()
    {
        int[] result = new int[45];
        byte offset = 0;
        zzUnpackAction("\u0002\u0000\u0002\u0001\u0001\u0002\u0001\u0003\u0001\u0004\u0003\u0001\u0001\u0005\u0001\u0006\u0001\u0007\u0001\b\u0001\t\u0001\n\u0001\u000b\u0001\f\u0001\r\u0005\u0000\u0001\f\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0001\u0014\u0001\u0000\u0001\u0015\u0001\u0000\u0001\u0015\u0004\u0000\u0001\u0016\u0001\u0017\u0002\u0000\u0001\u0018", offset, result);
        return result;
    }

    private static int zzUnpackAction(String packed, int offset, int[] result)
    {
        int i = 0;
        int j = offset;
        int l = packed.length();

        while (i < l)
        {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);

            while (true)
            {
                result[j++] = value;
                --count;

                if (count <= 0)
                {
                    break;
                }
            }
        }

        return j;
    }

    private static int[] zzUnpackRowMap()
    {
        int[] result = new int[45];
        byte offset = 0;
        zzUnpackRowMap("\u0000\u0000\u0000\u001b\u00006\u0000Q\u0000l\u0000\u0087\u00006\u0000\u00a2\u0000\u00bd\u0000\u00d8\u00006\u00006\u00006\u00006\u00006\u00006\u0000\u00f3\u0000\u010e\u00006\u0000\u0129\u0000\u0144\u0000\u015f\u0000\u017a\u0000\u0195\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u0000\u01b0\u0000\u01cb\u0000\u01e6\u0000\u01e6\u0000\u0201\u0000\u021c\u0000\u0237\u0000\u0252\u00006\u00006\u0000\u026d\u0000\u0288\u00006", offset, result);
        return result;
    }

    private static int zzUnpackRowMap(String packed, int offset, int[] result)
    {
        int i = 0;
        int j = offset;
        int high;

        for (int l = packed.length(); i < l; result[j++] = high | packed.charAt(i++))
        {
            high = packed.charAt(i++) << 16;
        }

        return j;
    }

    private static int[] zzUnpackAttribute()
    {
        int[] result = new int[45];
        byte offset = 0;
        zzUnpackAttribute("\u0002\u0000\u0001\t\u0003\u0001\u0001\t\u0003\u0001\u0006\t\u0002\u0001\u0001\t\u0005\u0000\b\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0004\u0000\u0002\t\u0002\u0000\u0001\t", offset, result);
        return result;
    }

    private static int zzUnpackAttribute(String packed, int offset, int[] result)
    {
        int i = 0;
        int j = offset;
        int l = packed.length();

        while (i < l)
        {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);

            while (true)
            {
                result[j++] = value;
                --count;

                if (count <= 0)
                {
                    break;
                }
            }
        }

        return j;
    }

    int getPosition()
    {
        return this.yychar;
    }

    Yylex(Reader in)
    {
        this.zzLexicalState = 0;
        this.zzBuffer = new char[16384];
        this.zzAtBOL = true;
        this.sb = new StringBuffer();
        this.zzReader = in;
    }

    Yylex(InputStream in)
    {
        this((Reader)(new InputStreamReader(in)));
    }

    private static char[] zzUnpackCMap(String packed)
    {
        char[] map = new char[65536];
        int i = 0;
        int j = 0;

        while (i < 90)
        {
            int count = packed.charAt(i++);
            char value = packed.charAt(i++);

            while (true)
            {
                map[j++] = value;
                --count;

                if (count <= 0)
                {
                    break;
                }
            }
        }

        return map;
    }

    private boolean zzRefill() throws IOException
    {
        if (this.zzStartRead > 0)
        {
            System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
            this.zzEndRead -= this.zzStartRead;
            this.zzCurrentPos -= this.zzStartRead;
            this.zzMarkedPos -= this.zzStartRead;
            this.zzStartRead = 0;
        }

        if (this.zzCurrentPos >= this.zzBuffer.length)
        {
            char[] numRead = new char[this.zzCurrentPos * 2];
            System.arraycopy(this.zzBuffer, 0, numRead, 0, this.zzBuffer.length);
            this.zzBuffer = numRead;
        }

        int numRead1 = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);

        if (numRead1 > 0)
        {
            this.zzEndRead += numRead1;
            return false;
        }
        else if (numRead1 == 0)
        {
            int c = this.zzReader.read();

            if (c == -1)
            {
                return true;
            }
            else
            {
                this.zzBuffer[this.zzEndRead++] = (char)c;
                return false;
            }
        }
        else
        {
            return true;
        }
    }

    public final void yyclose() throws IOException
    {
        this.zzAtEOF = true;
        this.zzEndRead = this.zzStartRead;

        if (this.zzReader != null)
        {
            this.zzReader.close();
        }
    }

    public final void yyreset(Reader reader)
    {
        this.zzReader = reader;
        this.zzAtBOL = true;
        this.zzAtEOF = false;
        this.zzEndRead = this.zzStartRead = 0;
        this.zzCurrentPos = this.zzMarkedPos = 0;
        this.yyline = this.yychar = this.yycolumn = 0;
        this.zzLexicalState = 0;
    }

    public final int yystate()
    {
        return this.zzLexicalState;
    }

    public final void yybegin(int newState)
    {
        this.zzLexicalState = newState;
    }

    public final String yytext()
    {
        return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }

    public final char yycharat(int pos)
    {
        return this.zzBuffer[this.zzStartRead + pos];
    }

    public final int yylength()
    {
        return this.zzMarkedPos - this.zzStartRead;
    }

    private void zzScanError(int errorCode)
    {
        String message;

        try
        {
            message = ZZ_ERROR_MSG[errorCode];
        }
        catch (ArrayIndexOutOfBoundsException var4)
        {
            message = ZZ_ERROR_MSG[0];
        }

        throw new Error(message);
    }

    public void yypushback(int number)
    {
        if (number > this.yylength())
        {
            this.zzScanError(2);
        }

        this.zzMarkedPos -= number;
    }

    public Yytoken yylex() throws IOException, ParseException
    {
        int zzEndReadL = this.zzEndRead;
        char[] zzBufferL = this.zzBuffer;
        char[] zzCMapL = ZZ_CMAP;
        int[] zzTransL = ZZ_TRANS;
        int[] zzRowMapL = ZZ_ROWMAP;
        int[] zzAttrL = ZZ_ATTRIBUTE;

        while (true)
        {
            int zzMarkedPosL = this.zzMarkedPos;
            this.yychar += zzMarkedPosL - this.zzStartRead;
            int zzAction = -1;
            int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
            this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
            int zzInput;
            int var14;

            while (true)
            {
                if (zzCurrentPosL < zzEndReadL)
                {
                    zzInput = zzBufferL[zzCurrentPosL++];
                }
                else
                {
                    if (this.zzAtEOF)
                    {
                        zzInput = -1;
                        break;
                    }

                    this.zzCurrentPos = zzCurrentPosL;
                    this.zzMarkedPos = zzMarkedPosL;
                    boolean val = this.zzRefill();
                    zzCurrentPosL = this.zzCurrentPos;
                    zzMarkedPosL = this.zzMarkedPos;
                    zzBufferL = this.zzBuffer;
                    zzEndReadL = this.zzEndRead;

                    if (val)
                    {
                        zzInput = -1;
                        break;
                    }

                    zzInput = zzBufferL[zzCurrentPosL++];
                }

                var14 = zzTransL[zzRowMapL[this.zzState] + zzCMapL[zzInput]];

                if (var14 != -1)
                {
                    this.zzState = var14;
                    int zzAttributes = zzAttrL[this.zzState];

                    if ((zzAttributes & 1) != 1)
                    {
                        continue;
                    }

                    zzAction = this.zzState;
                    zzMarkedPosL = zzCurrentPosL;

                    if ((zzAttributes & 8) != 8)
                    {
                        continue;
                    }
                }

                break;
            }

            this.zzMarkedPos = zzMarkedPosL;

            switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
            {
                case 1:
                    throw new ParseException(this.yychar, 0, new Character(this.yycharat(0)));

                case 2:
                    Long var17 = Long.valueOf(this.yytext());
                    return new Yytoken(0, var17);

                case 3:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                    break;

                case 4:
                    this.sb.delete(0, this.sb.length());
                    this.yybegin(2);
                    break;

                case 5:
                    return new Yytoken(1, (Object)null);

                case 6:
                    return new Yytoken(2, (Object)null);

                case 7:
                    return new Yytoken(3, (Object)null);

                case 8:
                    return new Yytoken(4, (Object)null);

                case 9:
                    return new Yytoken(5, (Object)null);

                case 10:
                    return new Yytoken(6, (Object)null);

                case 11:
                    this.sb.append(this.yytext());
                    break;

                case 12:
                    this.sb.append('\\');
                    break;

                case 13:
                    this.yybegin(0);
                    return new Yytoken(0, this.sb.toString());

                case 14:
                    this.sb.append('\"');
                    break;

                case 15:
                    this.sb.append('/');
                    break;

                case 16:
                    this.sb.append('\b');
                    break;

                case 17:
                    this.sb.append('\f');
                    break;

                case 18:
                    this.sb.append('\n');
                    break;

                case 19:
                    this.sb.append('\r');
                    break;

                case 20:
                    this.sb.append('\t');
                    break;

                case 21:
                    Double var16 = Double.valueOf(this.yytext());
                    return new Yytoken(0, var16);

                case 22:
                    return new Yytoken(0, (Object)null);

                case 23:
                    Boolean var15 = Boolean.valueOf(this.yytext());
                    return new Yytoken(0, var15);

                case 24:
                    try
                    {
                        var14 = Integer.parseInt(this.yytext().substring(2), 16);
                        this.sb.append((char)var14);
                        break;
                    }
                    catch (Exception var13)
                    {
                        throw new ParseException(this.yychar, 2, var13);
                    }

                default:
                    if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos)
                    {
                        this.zzAtEOF = true;
                        return null;
                    }

                    this.zzScanError(1);
            }
        }
    }
}
