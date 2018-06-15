package com.thetigerparty.argodflib.HelperClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fredtsao on 1/14/17.
 */

public class JsonArrayHelper {
    public static List<JSONObject> toList(final JSONArray array) {
        final int length = array.length();
        final ArrayList<JSONObject> result = new ArrayList<JSONObject>(length);
        for (int i = 0; i < length; i++) {
            final JSONObject json = array.optJSONObject(i);
            if (json != null) {
                result.add(json);
            }
        }

        return result;
    }

    public static JSONArray remove(int index, final JSONArray array) {
        final List<JSONObject> json_list = toList(array);
        json_list.remove(index);

        final JSONArray result = new JSONArray();
        for (final JSONObject jsonObject : json_list) {
            result.put(jsonObject);
        }

        return result;
    }

    public static JSONArray pop(final JSONArray array) {
        final List<JSONObject> json_list = toList(array);
        json_list.remove(json_list.size() - 1);

        final JSONArray result = new JSONArray();
        for (final JSONObject jsonObject : json_list) {
            result.put(jsonObject);
        }
        return result;
    }
}
