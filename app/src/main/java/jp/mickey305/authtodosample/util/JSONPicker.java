/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 misaki_35
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jp.mickey305.authtodosample.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by misaki_35 on 15/07/04.
 * JSONPicker is the Utility Class to search the JSONObject Information.
 */
public class JSONPicker {
    public static final String TAG = JSONPicker.class.getSimpleName();

    private JSONObject json;
    private String[] escapeSequenceCodeList;

    /**
     *
     * @param json is JSONObject(e.g. Volley - Response.Listener<JSONObject>)
     */
    public JSONPicker(JSONObject json) {
        setJsonObject(json);

        // Create Escape-Sequence List
        final String[] codeList = {
                "\n", "\r", "\b", "\f", "\t", "\0"
        };
        setEscapeSequenceCodeList(codeList);
    }
    /**
     *
     * @param json is JSONObject(e.g. Volley - Response.Listener<JSONObject>)
     * @param escapeSequenceCodeList is JSON Escape Code
     */
    public JSONPicker(JSONObject json, String[] escapeSequenceCodeList) {
        setJsonObject(json);
        if(escapeSequenceCodeList != null) {
            setEscapeSequenceCodeList(escapeSequenceCodeList);
        }
    }

    //______________________________________________________________________________________________
    private class JsonIndex {
        private String key;
        private int valueStartPosition;
        private int keyLayerLevel;

        public JsonIndex(String key, int valueStartPosition, int keyLayerLevel) {
            setKey(key);
            setValueStartPosition(valueStartPosition);
            setKeyLayerLevel(keyLayerLevel);
        }

        public void setKey(String key) { this.key = key; }
        public void setValueStartPosition(int valueStartPosition) {
            this.valueStartPosition = valueStartPosition;
        }
        public void setKeyLayerLevel(int keyLayerLevel) {
            this.keyLayerLevel = keyLayerLevel;
        }
        public String getKey() { return key; }
        public int getValueStartPosition() { return valueStartPosition; }
        public int getKeyLayerLevel() { return keyLayerLevel; }
    }

    //______________________________________________________________________________________________
    /**
     * setter method
     * @param json is JSONObject(e.g. Volley - Response.Listener<JSONObject>)
     */
    public void setJsonObject(JSONObject json) { this.json = json; }
    /**
     *
     * @param key is String: JsonObject key
     * @return ArrayList is array searched for info of input-key
     * @throws JSONException
     */
    public ArrayList<String> getValues(String key) throws JSONException {
        ArrayList<String> _values = getValues(key, JSONtoString(getJson()));
        if(_values.isEmpty()) { throw new JSONException(TAG+": value is empty"); }
        return _values;
    }
    /**
     * e.g.
     * registered json ----------------------------------------------------
     * [{
     *  "name" : { "first" : "taro", "last" : "suzuki" },
     *  "mail" : "taro@example.jp",
     *  "todoList" : { "work" : "report", "limit" : "1994/05/13" }
     * },
     * {
     *  "name" : { "first" : "satoshi", "last" : "maeda" },
     *  "mail" : "satoshi0612@example.jp",
     *  "todoList" : { "work" : "test", "limit" : "2015/06/12" }
     * },
     * {
     *  "name" : { "first" : "hanako", "last" : "tanaka" },
     *  "mail" : "hanako_teacher@example.jp",
     *  "birthday" : "1985/02/13",
     *  "todoList" : { "work" : "book the hotel", "limit" : "2005/03/31" }
     * }]
     *
     * called method ------------------------------------------------------
     * input -->
     *  keyQueue.add("name");
     *  keyQueue.add("last");
     *  OR
     *  keyQueue.add("last");
     * output -->
     *  suzuki
     *  maeda
     *  tanaka as ArrayList<String>
     *
     * input -->
     *  keyQueue.add("last");
     *  keyQueue.add("name");
     * output -->
     *  ArrayList is Empty(IndexOutOfBoundsException Occurred)
     *
     * input -->
     *  keyQueue.add("birthday");
     * output -->
     *  1985/02/13 as ArrayList<String>
     *
     * input -->
     *  keyQueue.add("name");
     * output -->
     *  { "first" : "taro", "last" : "suzuki" }
     *  { "first" : "satoshi", "last" : "maeda" }
     *  { "first" : "hanako", "last" : "tanaka" } as ArrayList<String>
     *
     * @param keyQueue is queue
     * @return ArrayList (String array)
     * @throws JSONException
     */
    public ArrayList<String> getValues(LinkedList<String> keyQueue) throws JSONException {
        ArrayList<String> _valueList = getValues(keyQueue.remove());
        int _size = _valueList.size();
        while(!keyQueue.isEmpty()) {
            String key = keyQueue.remove();
            ArrayList<String> tmpValueList = new ArrayList<>();
            for(int i=0; i < _size; i++) {
                try {
                    tmpValueList.addAll(getValues(key, _valueList.remove(0)));
                } catch (IndexOutOfBoundsException e) {
                    tmpValueList = new ArrayList<>();
                    // Log.e(TAG, e.toString());
                }
            }
            _valueList = tmpValueList;
            _size = _valueList.size();
        }
        if(_valueList.isEmpty()) { throw new JSONException(TAG+": value is empty"); }
        return _valueList;
    }
    /**
     * check the existence of ALL KEYs
     * @param keys is queue
     * @return true or false
     */
    public boolean isExistAllKeys(LinkedList<String> keys) {
        final int num = keys.size();
        int count = 0;
        for(String key: keys) {
            if(isExistKey(key)) { count++; }
        }
        return count == num;
    }
    /**
     * check the existence of KEY
     * @param key is String: JsonObject key
     * @return true or false
     */
    public boolean isExistKey(String key) {
        final ArrayList<JsonIndex> _jsonIndexList = createJsonIndexList(JSONtoString(getJson()));
        for(JsonIndex jsonIndex: _jsonIndexList) {
            if(jsonIndex.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }
    /**
     *
     * @param key is String: JsonObject key
     * @return ArrayList (Integer array)
     * @throws JSONException
     */
    public ArrayList<Integer> getKeyLayerLevel(String key) throws JSONException {
        final ArrayList<Integer> _keyLayerList = new ArrayList<>();
        final ArrayList<JsonIndex> _jsonIndexList = createJsonIndexList(JSONtoString(getJson()));
        for(JsonIndex jsonIndex: _jsonIndexList) {
            if(jsonIndex.getKey().equals(key)) {
                _keyLayerList.add(jsonIndex.getKeyLayerLevel());
            }
        }
        if(_keyLayerList.isEmpty()) { throw new JSONException(TAG+": key not found"); }
        return _keyLayerList;
    }
    /**
     * get ALL KEYS in the JSONObject
     * @return ArrayList (String array)
     * @throws JSONException
     */
    public ArrayList<String> getAllKeys() throws JSONException {
        final ArrayList<String> _keyList = new ArrayList<>();
        final ArrayList<JsonIndex> _jsonIndexList = createJsonIndexList(JSONtoString(getJson()));
        for(JsonIndex jsonIndex: _jsonIndexList) {
            _keyList.add(jsonIndex.getKey());
        }
        if(_keyList.isEmpty()) { throw new JSONException(TAG+": keys not found"); }
        return _keyList;
    }
    /**
     * get ALL LayerLevels in the JSONObject
     * @return ArrayList (Integer array)
     * @throws JSONException
     */
    public ArrayList<Integer> getAllKeyLayerLevels() throws JSONException {
        final ArrayList<Integer> _keyLayerList = new ArrayList<>();
        final ArrayList<JsonIndex> _jsonIndexList = createJsonIndexList(JSONtoString(getJson()));
        for(JsonIndex jsonIndex: _jsonIndexList) {
            _keyLayerList.add(jsonIndex.getKeyLayerLevel());
        }
        if(_keyLayerList.isEmpty()) { throw new JSONException(TAG+": layerLevelList not found"); }
        return _keyLayerList;
    }
    /**
     *
     * @return HashMap (String: key, Integer: keyLayerLevel)
     * @throws JSONException
     */
    public Map<String, Integer> getAllKeysAndLayerLevels() throws JSONException {
        final Map<String, Integer> _hash = new HashMap<>();
        final ArrayList<JsonIndex> _jsonIndexList = createJsonIndexList(JSONtoString(getJson()));
        for(JsonIndex jsonIndex: _jsonIndexList) {
            _hash.put(jsonIndex.getKey(), jsonIndex.getKeyLayerLevel());
        }
        if(_hash.isEmpty()) { throw new JSONException(TAG+": hash is empty"); }
        return _hash;
    }

    /**
     *
     * @param key is String: JsonObject key
     * @param jsonString is JSONString
     * @return ArrayList (String)
     */
    private ArrayList<String> getValues(String key, String jsonString) {
        final ArrayList<String> _valueList = new ArrayList<>();

        // All Keys and CursorPoints List
        final ArrayList<JsonIndex> _jsonIndexList = createJsonIndexList(jsonString);

        // Values-List of Input-Key
        for(JsonIndex jsonIndex : _jsonIndexList) {
            if(jsonIndex.getKey().equals(key)) {
                _valueList.add(getValue(jsonString, jsonIndex.getValueStartPosition()));
            }
        }

        return _valueList;
    }
    /**
     * Create List All Keys and Values position INSERTED
     * @param jsonString is JSONString
     * @return ArrayList
     */
    private ArrayList<JsonIndex> createJsonIndexList(String jsonString) {
        final ArrayList<JsonIndex> _jsonIndexList = new ArrayList<>();
        String _key = "";
        String _notKey = "";
        boolean _isKeyValue = false;
        int _layerLevel = 0;

        for(int cursor=0; cursor < jsonString.length(); cursor++) {
            char currentChar = jsonString.charAt(cursor);
            if(currentChar != '"') {
                if(_isKeyValue) { _key += currentChar; }
                else { _notKey += currentChar; }
            } else {
                if(!isEmptyString(_key)) {
                    final int nextCursor = cursor +1;
                    final char nextChar = jsonString.charAt(nextCursor);
                    int valueStartCursor = nextChar == ':' ? nextCursor +1: nextCursor;
                    _jsonIndexList.add(new JsonIndex(_key, valueStartCursor, _layerLevel));
                }
                if(!isEmptyString(_notKey)) {
                    _layerLevel += getStartedTreeCharNum(_notKey) - getFinishedTreeCharNum(_notKey);
                }
                _key = "";
                _notKey = "";
                _isKeyValue = !_isKeyValue;
            }
        }
        return _jsonIndexList;
    }
    /**
     *
     * @param str is space-string of keys
     * @return count (start-character number)
     */
    private int getStartedTreeCharNum(String str) {
        int counter = 0;
        for(int cursor=0; cursor < str.length(); cursor++) {
            char ch = str.charAt(cursor);
            switch(ch) {
            case '{':
            case '[':
                counter++;
                break;
            default:
                break;
            }
        }
        return counter;
    }
    /**
     *
     * @param str is space-string of keys
     * @return count (finished-character number)
     */
    private int getFinishedTreeCharNum(String str) {
        int counter = 0;
        for(int cursor=0; cursor < str.length(); cursor++) {
            char ch = str.charAt(cursor);
            switch(ch) {
            case '}':
            case ']':
                counter++;
                break;
            default:
                break;
            }
        }
        return counter;
    }
    private JSONObject getJson() { return this.json; }
    private boolean isEmptyString(String s) { return s == null || s.isEmpty(); }
    /**
     * Returned Value-Style is {*}, [*] or *
     * @param jsonString is JSONString
     * @param startPosition is position in witch Value start on JSONString
     * @return JSON Value
     */
    private String getValue(String jsonString, int startPosition) {
        String _value = "";
        boolean _isInserted = false;
        boolean _isSearchFinished = false;
        final ArrayList<Character> _listSeparateChar = new ArrayList<>();
        for(int cursor=startPosition; cursor < jsonString.length(); cursor++) {
            char currentChar = jsonString.charAt(cursor);
            if(!_isInserted) {
                switch(currentChar) {
                case '[':
                case '{':
                    _value += currentChar;
                case '"':
                case ',':
                    _listSeparateChar.add(currentChar);
                    _isInserted = true;
                    break;
                default: break;
                }
            } else {
                final char startTypeChar = _listSeparateChar.get(0);
                final char[] startedCharArray  = {'[', '{'};
                final char[] finishedCharArray = {']', '}'};

                // JSONArray or JSONObject
                for(int i=0; i < startedCharArray.length; i++) {
                    if(startTypeChar == startedCharArray[i]) {
                        if(currentChar == startedCharArray[i]) {
                            _listSeparateChar.add(currentChar);
                        } else if(currentChar == finishedCharArray[i]) {
                            if(_listSeparateChar.size() == 1)
                            { _isSearchFinished = true; }
                            else
                            { _listSeparateChar.remove(0); }
                        }
                        _value += currentChar;
                    }
                }
                // Value(Text)
                if(startTypeChar == '"') {
                    if(currentChar == '"')
                    { _isSearchFinished = true; }
                    else
                    { _value += currentChar; }
                }
                // Value(about Number)
                if(startTypeChar == ',') {
                    _isSearchFinished = true;
                    int backedPosition = getPositionBackedOnColon(jsonString, cursor -1);
                    _value = getValueBeforeComma(jsonString, backedPosition);
                }
            }
            if(_isSearchFinished) { break; }
        }
        return _value;
    }
    /**
     *
     * @param jsonString is JSONString
     * @param nowPosition is CurrentCursor
     * @return int (Colon-Character position)
     */
    private int getPositionBackedOnColon(String jsonString, int nowPosition) {
        int backedPoint=nowPosition;
        for(int cursor=nowPosition; cursor > 0; cursor--) {
            char ch = jsonString.charAt(cursor);
            backedPoint = cursor;
            if(ch == ':') { break; }
        }
        return backedPoint;
    }
    /**
     *
     * @param jsonString is JSONString
     * @param startPosition is position in witch Value start on JSONString
     * @return value (about Number String)
     */
    private String getValueBeforeComma(String jsonString, int startPosition) {
        String _value = "";
        boolean isEnd = false;
        for(int cursor=startPosition; cursor < jsonString.length(); cursor++) {
            char currentChar = jsonString.charAt(cursor);
            switch(currentChar) {
            case ':':
            case ' ':
            case '　':
                break;
            case ',':
                isEnd = true;
                break;
            default:
                _value += currentChar;
                break;
            }
            if(isEnd) { break; }
        }
        return _value;
    }
    /**
     * Escape String
     * @param jsonObject is is JSONObject(e.g. Volley - Response.Listener<JSONObject>)
     * @return JSONString
     */
    private String JSONtoString(JSONObject jsonObject) {
        return h(jsonObject.toString());
    }
    /**
     * Delete Escape Sequence Code in Original-String
     * @param originStr is String in which an escape sequence "EXISTS"
     * @return Replaced String
     */
    private String h(String originStr) {
        final String[] table = getEscapeSequenceCodeList();
        for(String code: table) {
            originStr = originStr.replaceAll(code, "");
        }
        return originStr;
    }
    private String[] getEscapeSequenceCodeList() { return escapeSequenceCodeList; }
    private void setEscapeSequenceCodeList(String[] escapeSequenceCodeList) {
        this.escapeSequenceCodeList = escapeSequenceCodeList;
    }
}
