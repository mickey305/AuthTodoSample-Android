package jp.mickey305.authtodosample.http;

import com.mickey305.androidCommons.analysis.json.JSONPicker;
import com.mickey305.androidCommons.analysis.json.JSONToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class AuthDocomoJSON {
    public static final String TAG = AuthDocomoJSON.class.getSimpleName();
    private JSONObject jsonObject;
    private final LinkedList<String> keys = new LinkedList<>();

    public AuthDocomoJSON(JSONObject jsonObject) {
        setJsonObject(jsonObject);
    }

    private LinkedList<String> getKeys() { return keys; }

    private JSONObject getJsonObject() { return jsonObject; }

    public void setJsonObject(JSONObject jsonObject) { this.jsonObject = jsonObject; }

    public void addKey(String key) { getKeys().add(key); }

    public void clearKey() { getKeys().clear(); }

    public String removeKey() { return getKeys().remove(); }

    @SuppressWarnings("unchecked")
    public String getValue(String key, int index) throws JSONException {
        String _value;
        LinkedList<JSONToken> _list;
        final JSONPicker _picker = new JSONPicker(getJsonObject());

        _list = _picker.getValues(key);

        try {
            _value = _list.get(index).getValue().to_s();
        } catch (IndexOutOfBoundsException e) {
            throw(new JSONException(TAG+": IndexOutOfBoundsException"));
        }

        return _value;
    }

    @SuppressWarnings("unchecked")
    public boolean isExistKey(String key) {
        return new JSONPicker(getJsonObject()).isExistKey(key);
    }
}
