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

import org.json.JSONObject;

/**
 * Created by misaki_35 on 15/07/05.
 * JSONCurator is Utility Class packaged JSONObject Information. This Old Version Class link is
 * {@link jp.mickey305.authtodosample.util.JSONPicker}. And This Object add curate-relation method
 * and more.
 */
public class JSONCurator {

    private JSONObject jsonObject;

    public JSONCurator(JSONObject jsonObject) {
        setJsonObject(jsonObject);
    }

    /**
     *
     * @return
     */
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    /**
     *
     * @param jsonObject
     */
    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    /**
     * JSONStringMap is Information Class JSONObject converted into String
     */
    public class JSONStringMap {

    }

}
