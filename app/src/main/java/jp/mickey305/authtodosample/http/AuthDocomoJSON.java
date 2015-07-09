package jp.mickey305.authtodosample.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

import jp.mickey305.authtodosample.util.JSONPicker;

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
    public ArrayList<String> getValues() throws JSONException {
        ArrayList<String> _tmp;
        final JSONPicker _picker = new JSONPicker(getJsonObject());
        _tmp = _picker.getValues(getKeys());
        return _tmp;
    }
    public boolean isExistAllKeys() {
        final JSONPicker _picker = new JSONPicker(getJsonObject());
        return _picker.isExistAllKeys(getKeys());
    }

    public String getValue(String key, int index) throws JSONException {
        String _value;
        ArrayList<String> _list;
        final JSONPicker _picker = new JSONPicker(getJsonObject());

        _list = _picker.getValues(key);

        try {
            _value = _list.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw(new JSONException(TAG+": IndexOutOfBoundsException"));
        }

        return _value;
    }
    public boolean isExistKey(String key) {
        return new JSONPicker(getJsonObject()).isExistKey(key);
    }
}
