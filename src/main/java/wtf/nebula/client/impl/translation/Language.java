package wtf.nebula.client.impl.translation;

public enum Language {
    AUTO("auto"),

    AFRIKAANS("af"),
    ALBANIAN("sq"),
    AMHARIC("am"),
    ARABIC("ar"),
    ARMENIAN("hy"),
    AZERBAIJANI("az"),
    BASQUE("eu"),
    BELARUSIAN("be"),
    BENGALI("bn"),
    BOSNIAN("bs"),
    BULGARIAN("bg"),
    CATALAN("ca"),
    CEBUANO("ceb"),
    CHICHEWA("ny"),
    CHINESE_SIMPLIFIED("zh-cn"),
    CHINESE_TRADITIONAL("zh-tw"),
    CORSICAN("co"),
    CROATIAN("hr"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    ESPERANTO("eo"),
    ESTONIAN("et"),
    FILIPINO("tl"),
    FINNISH("fi"),
    FRENCH("fr"),
    FRISIAN("fy"),
    GALICIAN("gl"),
    GEORGIAN("ka"),
    GERMAN("de"),
    GREEK("el"),
    GUJARATI("gu"),
    HATIAN_CREOLE("ht"),
    HAUSA("ha"),
    HAWAIIAN("haw"),
    HEBREW_IW("iw"),
    HEBREW_HE("he"),
    HINDI("hi"),
    HMONG("hm"),
    HUNGARIAN("hu"),
    ICELANDIC("is"),
    IGBO("ig"),
    INDONESIAN("id"),
    IRISH("ga"),
    ITALIAN("it"),
    JAPANESE("ja"),
    JAVANESE("jw"),
    KANNADA("kn"),
    KAZAKH("kk"),
    KHMER("km"),
    KOREAN("ko"),
    KURDISH_KURMANJI("ku"),
    KYRGYZ("ky"),
    LAO("lo"),
    LATIN("la"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    LUXEMBOURGISH("lb"),
    MACEDONIAN("mk"),
    MALAGASY("mg"),
    MALAY("ms"),
    MALAYALAM("ml"),
    MALTESE("mt"),
    MAORI("mi"),
    MARATHI("mr"),
    MONGOLIAN("mn"),
    MYANMAR_BURMESE("my"),
    NEPALI("ne"),
    NORWEGIAN("no"),
    ODIA("or"),
    PASHTO("ps"),
    PERSIAN("fa"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    PUNJABI("pa"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    SAMOAN("sm"),
    SCOTS_GAELIC("gd"),
    SERBIAN("sr"),
    SESOTHO("st"),
    SHONA("sn"),
    SINDHI("sd"),
    SINHALA("si"),
    SLOVAK("sk"),
    SLOVENIAN("sl"),
    SOMALI("so"),
    SPANISH("es"),
    SUDANESE("su"),
    SWAHILI("sw"),
    SWEDISH("sv"),
    TAJIK("tg"),
    TAMIL("ta"),
    TELUGU("te"),
    THAI("th"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    URDU("ur"),
    UYGHUR("ug"),
    UZBEK("uz"),
    VIETNAMESE("vi"),
    WELSH("cy"),
    XHOSA("xh"),
    YIDDISH("yi"),
    YORUBA("yo"),
    ZULU("zu");

    private final String locale;

    Language(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }

    public static Language fromLocale(String in) {
        for (Language lang : values()) {
            if (lang.getLocale().equals(in)) {
                return lang;
            }
        }

        return null;
    }
}
