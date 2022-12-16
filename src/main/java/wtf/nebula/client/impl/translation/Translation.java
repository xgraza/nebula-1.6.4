package wtf.nebula.client.impl.translation;

import com.google.gson.*;

public class Translation {

    private final String inputText, translatedText;
    private final Language sourceLanguage;

    public Translation(String inputText, String translatedText, Language sourceLanguage) {
        this.inputText = inputText;
        this.translatedText = translatedText;
        this.sourceLanguage = sourceLanguage;
    }

    public String getInputText() {
        return inputText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    public static Translation fromString(String inputText, String s) {

        try {
            JsonArray obj = JsonParser.parseString(s).getAsJsonArray();

            StringBuilder translated = new StringBuilder();

            JsonArray arr = obj.get(0).getAsJsonArray();
            for (JsonElement element : arr) {
                if (element.isJsonArray()) {
                    JsonArray a = element.getAsJsonArray();

                    JsonElement e = a.get(0);
                    if (e.isJsonPrimitive()) {
                        JsonPrimitive primitive = e.getAsJsonPrimitive();
                        if (primitive.isString()) {
                            translated.append(primitive.getAsString().replaceAll("\n", "")).append(" ");
                        }
                    }
                }
            }

            Language sourceLanguage = Language.fromLocale(obj.get(2).getAsString());

            return new Translation(inputText, translated.toString(), sourceLanguage);
        } catch (Exception ignored) {
            return null;
        }
    }
}
