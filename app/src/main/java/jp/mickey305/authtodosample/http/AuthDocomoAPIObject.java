package jp.mickey305.authtodosample.http;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import jp.mickey305.authtodosample.R;
import jp.mickey305.authtodosample.http.request.PostJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by misaki_35 on 15/06/19.
 * APIKEY, responseはクラス内部で自動生成されます
 * modeはREQUEST_MODEで設定できます
 * その他のパラメータはsetParamsで設定します
 * その他のクエリはconnectの引数で設定します
 */
public class AuthDocomoAPIObject implements DocomoAPIURLValues{
    public static final String TAG = "AuthDocomoAPIObject";
    private static final String uri = URI_FACIAL_RECOGNITION;
    private Context context;
    private REQUEST_MODE requestMode;
    private String apiKey;
    private String query;
    private HashMap<String,String> params;
    private RequestQueue requestQueue;
    private int faceId;

    public AuthDocomoAPIObject(Context context) {
        setContext(context);
        setApiKey(getContext());

        requestQueue = Volley.newRequestQueue(getContext());
        setParams(null);
    }
    public AuthDocomoAPIObject(Context context, REQUEST_MODE requestMode) {
        this(context);
        setRequestMode(requestMode);
    }
    public AuthDocomoAPIObject(
            Context context, REQUEST_MODE requestMode, HashMap<String, String> params) {
        this(context, requestMode);
        setParams(params);
    }

    public enum REQUEST_MODE {
        REGISTER    (0),
        VERIFY      (1),
        LIST        (2),
        TAG_EDIT    (3),
        DELETE      (4);

        REQUEST_MODE(int code) { this.code  = code; }
        final int code;
    }

    private void setQuery(String query) { this.query = query; }
    private void setApiKey(Context context) {
        apiKey = context.getResources().getString(R.string.api_key);
    }
    private void setContext(Context context) { this.context = context; }
    private HashMap<String, String> getParams() { return params; }
    private Context getContext() { return context; }
    private REQUEST_MODE getRequestMode() { return requestMode; }
    private String getApiKey() { return apiKey; }
    private String getQuery() { return query; }
    private String getRequestUrl() { return uri + getQuery(); }
    private boolean judgeScore(final int score, Context context) {
        return (score >= context.getResources().getInteger(R.integer.threshold));
    }
    private void showToast(String msg, Context context) {
        Toast.makeText(context, "#"+TAG+" "+msg, Toast.LENGTH_LONG).show();
    }
    private void showLog(final String msg) {
        Log.d(TAG, msg);
    }

    //______________________________________________________________________________________________
    /*
     * public interface and method etc.
     *
     */
    private ResponseCallback responseCallback;
    private OnConnectionStatusListener onConnectionStatusListener;
    private RegisterCallback registerCallback;
    private AuthCallback authCallback;
    private ListCallback listCallback;
    private TagEditCallback tagEditCallback;
    private DeleteCallback deleteCallback;

    /**
     * ResponseCallback: リクエストレスポンス関係
     * - onResponseSucceeded: レスポンスが正常に取得できた場合に呼ばれるメソッド
     * - onRequestError: 通信モジュールの電源が入ってイなかったり、通信が切断されたりすることによってレスポンス
     *   が正常に行われなかった場合に呼ばれるメソッド（リクエストレスポンスエラー時に呼ばれるメソッド）
     */
    public interface ResponseCallback {
        void onResponseSucceeded();
        void onRequestError(VolleyError e);
    }

    /**
     * OnConnectionStatusListener: 通信接続関係ののリスナー
     * - onAPIConnectionStart: 通信が開始された時に呼ばれるメソッド
     * - onAPIConnectionStop: 通信が終了・切断された時に呼ばれるメソッド
     */
    public interface OnConnectionStatusListener {
        void onAPIConnectionStart();
        void onAPIConnectionStop();
    }

    /**
     * 接続モードによってそれぞれのコールバック関数を設定できます。
     * RegisterCallback: 接続モードがREGISTERのとき
     * - onRegisterSucceeded: 登録が成功した時に呼ばれるメソッド
     * - onRegisterExceptionOccurred: Response情報を処理中に例外が発生した場合に呼ばれるメソッド（登録が失敗
     *   した時に呼ばれるメソッド）
     *
     * AuthCallback: 接続モードがVERIFYのとき
     * - onAuthFacialRecognitionSucceeded: 認証が成功した場合に呼ばれるメソッド
     * - onAuthFacialRecognitionRejected: 認証が失敗した場合に呼ばれるメソッド
     * - onAuthExceptionOccurred: Response情報を処理中に例外が発生した場合に呼ばれるメソッド（認証が処理エラー
     *   により失敗した場合に呼ばれるメソッド）
     *
     * ListCallback: 接続モードがLISTのとき
     *
     * TagEditCallback: 接続モードがTAG_EDITのとき
     *
     * DeleteCallback: 接続モードがDELETEのとき
     *
     */
    public interface RegisterCallback {
        void onRegisterSucceeded();
        void onRegisterExceptionOccurred(JSONException e);
    }
    public interface AuthCallback {
        void onAuthFacialRecognitionSucceeded();
        void onAuthFacialRecognitionRejected();
        void onAuthExceptionOccurred(JSONException e);
    }
    public interface ListCallback {}
    public interface TagEditCallback {}
    public interface DeleteCallback {}

    /**
     * インターフェースを設定するcallback関数
     */
    public void setResponseCallback(ResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }
    public void setOnConnectionStatusListener(
            OnConnectionStatusListener onConnectionStatusListener) {
        this.onConnectionStatusListener = onConnectionStatusListener;
    }
    public void setRegisterCallback(RegisterCallback registerCallback) {
        this.registerCallback = registerCallback;
    }
    public void setAuthCallback(AuthCallback authCallback) {
        this.authCallback = authCallback;
    }
    /*public void setListCallback(ListCallback listCallback) {
        this.listCallback = listCallback;
    }
    public void setTagEditCallback(TagEditCallback tagEditCallback) {
        this.tagEditCallback = tagEditCallback;
    }
    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }*/

    /**
     * Base64でエンコードした文字列を返す
     * @param buf はbyte配列型のデータ
     * @return
     */
    public String encodeBase64(byte[] buf){
        return Base64.encodeToString(buf, Base64.DEFAULT);
    }

    /**
     * faceIdを設定する
     * @param faceId
     */
    public void setFaceId(int faceId) { this.faceId = faceId; }

    /**
     * faceIdを取得する
     * @return
     */
    public int getFaceId() { return faceId; }

    /**
     * リクエスト用のパラメータを設定する
     * @param params
     */
    public void setParams(HashMap<String, String> params) { this.params = params; }

    /**
     * リクエストのタイプを設定する（enum REQUEST_MODE）
     * @param mode
     */
    public void setRequestMode(REQUEST_MODE mode) { requestMode = mode; }

    /**
     * APIに接続する
     */
    public void connect() {
        if(onConnectionStatusListener != null) onConnectionStatusListener.onAPIConnectionStart();
        setQuery("?APIKEY="+getApiKey()
                +"&mode="+getModeValue(getRequestMode(), getContext())
                +"&response=json");
        PostJsonObjectRequest request = new PostJsonObjectRequest(
                Request.Method.POST,
                getRequestUrl(),
                getParams(),
                getResponseListener(getRequestMode()),
                createErrorListenerI()
        );
        requestQueue.add(request);
    }

    /**
     * APIに接続する
     * @param mode はリクエストのタイプ（enum REQUEST_MODE）
     */
    public void connect(REQUEST_MODE mode) {
        setRequestMode(mode);
        connect();
    }

    /**
     * APIに接続する
     * @param queryArray はリクエスト用のクエリを格納しているHashMapオブジェクト（keyとvalueの対）
     */
    public void connect(HashMap<String, String> queryArray) {
        if(onConnectionStatusListener != null) onConnectionStatusListener.onAPIConnectionStart();
        setQuery("?APIKEY="+getApiKey()
                +"&mode="+getModeValue(getRequestMode(), getContext())
                +"&response=json");
        addQuery(queryArray);
        PostJsonObjectRequest request = new PostJsonObjectRequest(
                Request.Method.POST,
                getRequestUrl(),
                getParams(),
                getResponseListener(getRequestMode()),
                createErrorListenerI()
        );
        requestQueue.add(request);
    }

    /**
     * APIに接続する
     * @param mode はリクエストのタイプ（enum REQUEST_MODE）
     * @param queryArray はリクエスト用のクエリを格納しているHashMapオブジェクト（keyとvalueの対）
     */
    public void connect(REQUEST_MODE mode, HashMap<String, String> queryArray) {
        setRequestMode(mode);
        connect(queryArray);
    }

    //______________________________________________________________________________________________
    /*
     * private method
     *
     */
    private void addQuery(HashMap<String, String> queryArray) {
        String query = "";
        int i = 0;
        for(HashMap.Entry entry : queryArray.entrySet()){
            if( i == 0 && isEmptyQuery() ){
                query += "?" + entry.getKey().toString() + "=" + entry.getValue().toString();
            }
            query += "&" + entry.getKey().toString() + "=" + entry.getValue().toString();
            ++i;
        }
        if(!isEmptyQuery()) setQuery(getQuery() + query);
        else setQuery(query);
    }
    private boolean isEmptyQuery() {
        return (getQuery() == null || getQuery().equals(""));
    }
    private int toInteger(String str) {
        return Integer.parseInt(str);
    }
    private double toDouble(String str) {
        return Double.parseDouble(str);
    }
    private float toFloat(String str) {
        return Float.parseFloat(str);
    }
    private byte toByte(String str) {
        return Byte.parseByte(str);
    }
    private long toLong(String str) {
        return Long.parseLong(str);
    }
    private short toShort(String str) {
        return Short.parseShort(str);
    }
    private String getModeValue(REQUEST_MODE mode, Context context) {
        String value;
        switch (mode) {
            case REGISTER: value = context.getResources().getString(R.string.key_register); break;
            case VERIFY:   value = context.getResources().getString(R.string.key_verify); break;
            case LIST:     value = context.getResources().getString(R.string.key_list); break;
            case TAG_EDIT: value = context.getResources().getString(R.string.key_tag_edit); break;
            case DELETE:   value = context.getResources().getString(R.string.key_delete); break;
            default:       value = null; break;
        }
        return value;
    }
    private Response.Listener<JSONObject> getResponseListener(REQUEST_MODE mode) {
        Response.Listener<JSONObject> listener;
        switch (mode) {
            case REGISTER: listener = createResListenerRegisterI(); break;
            case VERIFY:   listener = createResListenerVerifyI(); break;
            case LIST:     listener = createResListenerListI(); break;
            case TAG_EDIT: listener = createResListenerTagEditI(); break;
            case DELETE:   listener = createResListenerDeleteI(); break;
            default:       listener = null; break;
        }
        return listener;
    }
    private Response.Listener<JSONObject> createResListenerRegisterI() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(responseCallback != null) responseCallback.onResponseSucceeded();
                // REQUEST REGISTER: RESPONSE HERE
                try {
                    setFaceId(toInteger(getRegisteredFaceId(response)));
                } catch (JSONException e) {
                    //e.printStackTrace();
                    if(registerCallback != null) registerCallback.onRegisterExceptionOccurred(e);
                }
                if(getFaceId() != 0) {
                    if(registerCallback != null) registerCallback.onRegisterSucceeded();
                }
                if(onConnectionStatusListener != null) {
                    onConnectionStatusListener.onAPIConnectionStop();
                }
            }
        };
    }
    private Response.Listener<JSONObject> createResListenerVerifyI() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(responseCallback != null) responseCallback.onResponseSucceeded();
                // REQUEST VERIFY: RESPONSE HERE
                int score = 0;
                int faceIdVerify;
                int i = 0;
                try {
                    final int max = getContext().getResources().getInteger(R.integer.face_id_max);
                    while (i < max) {
                        if(getFaceId() == 0) { break; }
                        faceIdVerify = toInteger(getVerifiedFaceId(response, i));
                        score = toInteger(getVerifiedScore(response, i));
                        if(getFaceId() == faceIdVerify) { break; }
                        ++i;
                    }
                    if(judgeScore(score, getContext())) {
                        // Verify Succeeded
                        if(authCallback != null) authCallback.onAuthFacialRecognitionSucceeded();
                    } else {
                        // Verify Rejected
                        if(authCallback != null) authCallback.onAuthFacialRecognitionRejected();
                    }
                } catch (JSONException e) {
                    //e.printStackTrace();
                    if(authCallback != null) authCallback.onAuthExceptionOccurred(e);
                }
                if(onConnectionStatusListener != null) {
                    onConnectionStatusListener.onAPIConnectionStop();
                }
            }
        };
    }
    private Response.Listener<JSONObject> createResListenerListI() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(responseCallback != null) responseCallback.onResponseSucceeded();
                // REQUEST LIST: RESPONSE HERE

                if(onConnectionStatusListener != null) {
                    onConnectionStatusListener.onAPIConnectionStop();
                }
            }
        };
    }
    private Response.Listener<JSONObject> createResListenerTagEditI() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(responseCallback != null) responseCallback.onResponseSucceeded();
                // REQUEST TAG_EDIT: RESPONSE HERE

                if(onConnectionStatusListener != null) {
                    onConnectionStatusListener.onAPIConnectionStop();
                }
            }
        };
    }
    private Response.Listener<JSONObject> createResListenerDeleteI() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(responseCallback != null) responseCallback.onResponseSucceeded();
                // REQUEST DELETE: RESPONSE HERE

                if(onConnectionStatusListener != null) {
                    onConnectionStatusListener.onAPIConnectionStop();
                }
            }
        };
    }
    private Response.ErrorListener createErrorListenerI() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(responseCallback != null) responseCallback.onRequestError(error);
                // REQUEST ALL: RESPONSE ERROR HERE

                if(onConnectionStatusListener != null) {
                    onConnectionStatusListener.onAPIConnectionStop();
                }
            }
        };
    }
    private String getVerifiedScore(JSONObject res, int index) throws JSONException {
        return getCandidateJson(getFaceRecognitionJson(res), index).getString("score");
    }
    private String getVerifiedFaceId(JSONObject res, int index) throws JSONException {
        return getCandidateJson(getFaceRecognitionJson(res), index).getString("faceId");
    }
    private JSONObject getCandidateJson(JSONObject res, int indexCandidate)
            throws JSONException {
        return res.getJSONArray("verificationFaceInfo")
                .getJSONObject(0)
                .getJSONArray("candidate")
                .getJSONObject(indexCandidate);
    }
    private String getRegisteredFaceId(JSONObject res) throws JSONException {
        return getRegistrationFaceInfoJson(getFaceRecognitionJson(res)).getString("faceId");
    }
    private JSONObject getRegistrationFaceInfoJson(JSONObject res) throws JSONException {
        return res.getJSONArray("detectionFaceInfo")
                .getJSONObject(0)
                .getJSONObject("registrationFaceInfo");
    }
    private JSONObject getFaceRecognitionJson(JSONObject res)
            throws JSONException {
        return res.getJSONObject("results")
                .getJSONObject("faceRecognition");
    }
}