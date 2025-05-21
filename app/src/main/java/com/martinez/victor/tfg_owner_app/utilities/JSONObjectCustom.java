package com.martinez.victor.tfg_owner_app.utilities;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONObjectCustom extends JSONObject {
    @NonNull
    @Override
    public JSONObject put(@NonNull String key, Object object) throws JSONException {
        if (object instanceof String[]) {
            String[] values = (String[]) object;
            JSONArray array = new JSONArray();
            for (String value: values) {
                array.put(value);
            }
            return super.put(key, array);
        }
        if (object instanceof ArrayList) {
            ArrayList<?> values = (ArrayList<?>) object;
            boolean hasToJson = true;
            for (Object value : values) {
                if (!(value instanceof IToJson)) {
                    hasToJson = false;
                    break;
                }
            }
            if (hasToJson) {
                JSONArray array = new JSONArray();
                for (Object value: values) {
                    array.put(((IToJson) value).toJson());
                }
                return super.put(key, array);
            }
        }

        return super.put(key, object == null ? JSONObject.NULL : object);
    }
}
