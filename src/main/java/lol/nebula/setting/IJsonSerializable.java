package lol.nebula.setting;

import com.google.gson.JsonObject;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public interface IJsonSerializable {

    /**
     * Loads data from a parsed json object
     * @param object the passed json object
     */
    void fromJson(JsonObject object);

    /**
     * A json representation of this object's properties
     * @return the json representation of this object's properties
     */
    JsonObject toJson();
}
