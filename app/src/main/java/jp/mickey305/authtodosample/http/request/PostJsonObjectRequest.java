package jp.mickey305.authtodosample.http.request;

import com.android.volley.Response;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * JsonObjectRequestを、mapのパラメータでpostできるように対応しました。
 *
 */
public class PostJsonObjectRequest extends com.android.volley.toolbox.JsonObjectRequest{

    /**
     * パラメータのマップ
     */
    private Map<String, String> mParams;

    public PostJsonObjectRequest(
            int method, String url, Map<String, String> params,
            Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        mParams = params;
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    /**
     * リクエストパラメータの形式を、JSON形式ではなく、通常のPOSTに変更する。
     * 通常のJSONObjectRequestではこの設定ができない。
     * Request.javaから引用
     * @return
     */
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    @Override
    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }
    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }
}