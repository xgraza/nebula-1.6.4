package wtf.nebula.client.impl.translation;

import wtf.nebula.client.utils.io.FileUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A lot of this code is going to be based off of Bush's translator
 * <p>
 * Here is <a href="https://github.com/therealbush/translator">Bush's Github Repo</a>
 */
public class TranslationAPI {

    private static final String GOOGLE_TRANSLATE_URL = "https://translate.googleapis.com/translate_a/single";

    public static SearchThread getResult(Language src, Language target, String content) {

        SearchThread thread = new SearchThread(src, target, content);
        thread.start();

        return thread;
    }

    public static String queryString(String[]... params) {
        return Arrays.stream(params).map((arr) -> {
            String key = arr[0];

            String value;
            try {
                value = arr[1];
            } catch (IndexOutOfBoundsException e) {
                value = key;
            }

            return encodeUri(key) + "=" + value;
        }).collect(Collectors.joining("&"));
    }

    public static String encodeUri(String t) {
        try {
            return URLEncoder.encode(t, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return t;
        }
    }

    public static class SearchThread extends Thread {
        public Translation translation;

        private final Language src, target;
        private final String content;

        public SearchThread(Language src, Language target, String content) {
            this.src = src;
            this.target = target;
            this.content = content;
        }

        @Override
        public void run() {
            try {
                String url = GOOGLE_TRANSLATE_URL + "?" + queryString(new String[][]{
                        {"client", "gtx"},
                        {"dt", "at"},
                        {"dt", "bd"},
                        {"dt", "ex"},
                        {"dt", "ld"},
                        {"dt", "md"},
                        {"dt", "qca"},
                        {"dt", "rw"},
                        {"dt", "rm"},
                        {"dt", "ss"},
                        {"dt", "t"},
                        {"ie", "UTF-8"},
                        {"oe", "UTF-8"},
                        {"otf", "1"},
                        {"ssel", "0"},
                        {"tsel", "0"},
                        {"tk", "bushissocool"},
                        {"sl", src.getLocale()},
                        {"tl", target.getLocale()},
                        {"hl", target.getLocale()},
                        {"q", encodeUri(content)}
                });

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                connection.connect();

                int code = connection.getResponseCode();
                if (code != 200) {
                    return;
                }

                translation = Translation.fromString(content, FileUtils.read(connection.getInputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
