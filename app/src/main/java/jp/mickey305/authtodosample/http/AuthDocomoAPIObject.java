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
    public static final String TAG = AuthDocomoAPIObject.class.getSimpleName();
    private static final String uri = URI_FACIAL_RECOGNITION;
    private Context context;
    private REQUEST_MODE requestMode;
    private String apiKey;
    private String query;
    private HashMap<String,String> params;
    private RequestQueue requestQueue;
    private int faceId;

    /**
     *
     * @param context
     */
    public AuthDocomoAPIObject(Context context) {
        setContext(context);
        setApiKey(getContext());

        requestQueue = Volley.newRequestQueue(getContext());
        setParams(null);
    }

    /**
     *
     * @param context
     * @param requestMode
     */
    public AuthDocomoAPIObject(Context context, REQUEST_MODE requestMode) {
        this(context);
        setRequestMode(requestMode);
    }

    /**
     *
     * @param context
     * @param requestMode
     * @param params
     */
    public AuthDocomoAPIObject(
            Context context, REQUEST_MODE requestMode, HashMap<String, String> params) {
        this(context, requestMode);
        setParams(params);
    }

    /**
     * リクエストモード（ドコモ顔認証APIリファレンス参照）
     */
    public enum REQUEST_MODE {
        REGISTER    (0),
        VERIFY      (1),
        LIST        (2),
        TAG_EDIT    (3),
        DELETE      (4);

        REQUEST_MODE(int code) { this.code  = code; }
        final int code;
    }

    /**
     * クエリを設定する
     * @param query はAPIに送信するURLクエリ
     */
    private void setQuery(String query) { this.query = query; }

    /**
     * APIキーを設定する
     * @param context
     */
    private void setApiKey(Context context) {
        apiKey = context.getResources().getString(R.string.api_key);
    }

    /**
     * faceIdを取得する
     * @return faceId
     */
    private int getFaceId() { return faceId; }

    private void setContext(Context context) { this.context = context; }

    private Context getContext() { return context; }

    /**
     * パラメータを設定する
     * @return
     */
    private HashMap<String, String> getParams() { return params; }

    /**
     * リクエストモードを取得する
     * @return is REQUEST_MODE
     */
    private REQUEST_MODE getRequestMode() { return requestMode; }

    /**
     * APIキーを取得する
     * @return
     */
    private String getApiKey() { return apiKey; }

    /**
     * クエリを取得する
     * @return
     */
    private String getQuery() { return query; }

    /**
     * リクエスト用（クエリ付き）URLを取得する
     * @return
     */
    private String getRequestUrl() { return uri + getQuery(); }

    /**
     * スコアを判定する
     * @param score
     * @param context
     * @return true or false
     */
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
     * - onDeleteSucceeded: 削除が成功した場合に呼ばれるメソッド
     * - onDeleteFailed: 削除が失敗した場合に呼ばれるメソッド
     * - onDeleteExceptionOccurred: Response情報を処理中に例外が発生した場合に呼ばれるメソッド（削除が処理エ
     *   ラーにより失敗した場合に呼ばれるメソッド）
     */
    public interface RegisterCallback {
        void onRegisterSucceeded(int registeredFaceId);
        void onRegisterExceptionOccurred(JSONException e);
    }
    public interface AuthCallback {
        void onAuthFacialRecognitionSucceeded(int score);
        void onAuthFacialRecognitionRejected(int score);
        void onAuthExceptionOccurred(JSONException e);
    }
    public interface ListCallback {}
    public interface TagEditCallback {}
    public interface DeleteCallback {
        void onDeleteSucceeded();
        void onDeleteFailed();
        void onDeleteExceptionOccurred(JSONException e);
    }

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
    }*/
    public void setDeleteCallback(DeleteCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

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
     * - 内部で使用されているPostJsonObjectRequestくらすへのリンクはこちらから
     *   {@link jp.mickey305.authtodosample.http.request.PostJsonObjectRequest}
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
                getResponseErrorListener()
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
     * - 内部で使用されているPostJsonObjectRequestくらすへのリンクはこちらから
     *   {@link jp.mickey305.authtodosample.http.request.PostJsonObjectRequest}
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
                getResponseErrorListener()
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

    /**
     * クエリを追加する
     * @param queryArray はリクエスト用のクエリを格納しているHashMapオブジェクト（keyとvalueの対）
     */
    private void addQuery(HashMap<String, String> queryArray) {
        String query = "";
        int i = 0;
        for(HashMap.Entry entry : queryArray.entrySet()){
            if( i++ == 0 && isEmptyQuery() ){
                query += "?" + entry.getKey().toString() + "=" + entry.getValue().toString();
                continue;
            }
            query += "&" + entry.getKey().toString() + "=" + entry.getValue().toString();
        }
        if(!isEmptyQuery()) setQuery(getQuery() + query);
        else setQuery(query);
    }

    /**
     * クエリが空か
     * @return
     */
    private boolean isEmptyQuery() {
        return (getQuery() == null || getQuery().equals(""));
    }

    /**
     * String to Integer
     * @param str
     * @return
     */
    private int toInteger(String str) {
        return Integer.parseInt(str);
    }

    /**
     * String to Double
     * @param str
     * @return
     */
    private double toDouble(String str) {
        return Double.parseDouble(str);
    }

    /**
     * String to Float
     * @param str
     * @return
     */
    private float toFloat(String str) {
        return Float.parseFloat(str);
    }

    /**
     * String to Byte
     * @param str
     * @return
     */
    private byte toByte(String str) {
        return Byte.parseByte(str);
    }

    /**
     * String to Long
     * @param str
     * @return
     */
    private long toLong(String str) {
        return Long.parseLong(str);
    }

    /**
     * String to Short
     * @param str
     * @return
     */
    private short toShort(String str) {
        return Short.parseShort(str);
    }

    /**
     * クエリに設定するリクエストモードの文字列を取得する
     * @param mode はリクエストのタイプ（enum REQUEST_MODE）
     * @param context
     * @return リクエストモードの文字列
     */
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

    /**
     * 接続モードのリスナーを作成する
     * @param mode はリクエストのタイプ（enum REQUEST_MODE）
     * @return レスポンスリスナー（インスタンス化されたリスナー）
     */
    private Response.Listener<JSONObject> getResponseListener(final REQUEST_MODE mode) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(responseCallback != null) responseCallback.onResponseSucceeded();
                switch (mode) {
                    case REGISTER: runRegisterJob(response); break;
                    case VERIFY:   runVerifyJob(response); break;
                    case LIST:     runListJob(response); break;
                    case TAG_EDIT: runTagEditJob(response); break;
                    case DELETE:   runDeleteJob(response); break;
                    default:       break;
                }
                if(onConnectionStatusListener != null) {
                    onConnectionStatusListener.onAPIConnectionStop();
                }
            }
        };
    }

    /**
     * エラー時のリスナーを作成する
     * @return レスポンスリスナー（インスタンス化されたリスナー）
     */
    private Response.ErrorListener getResponseErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(responseCallback != null) responseCallback.onRequestError(error);
                runErrorJob(error);
                if(onConnectionStatusListener != null) {
                    onConnectionStatusListener.onAPIConnectionStop();
                }
            }
        };
    }

    /**
     * 登録モードのレスポンス処理
     * @param response は返ってきたJSONObject{@link org.json.JSONObject}
     */
    private void runRegisterJob(JSONObject response) {
        // REQUEST REGISTER: RESPONSE HERE
        AuthDocomoJSON picker = new AuthDocomoJSON(response);
        setFaceId(0);
        try {
            setFaceId(toInteger(picker.getValue("faceId", 0)));
            if(getFaceId() != 0) {
                if(registerCallback != null) registerCallback.onRegisterSucceeded(getFaceId());
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            if(registerCallback != null) registerCallback.onRegisterExceptionOccurred(e);
        }
    }

    /**
     * 認証モードのレスポンス処理
     * @param response は返ってきたJSONObject{@link org.json.JSONObject}
     */
    private void runVerifyJob(JSONObject response) {
        // REQUEST VERIFY: RESPONSE HERE
        AuthDocomoJSON picker = new AuthDocomoJSON(response);
        int score = 0;
        int faceIdVerify;
        int i = 0;
        try {
            final int max = getContext().getResources().getInteger(R.integer.face_id_max);
            while (i < max) {
                if(getFaceId() == 0) { break; }
                faceIdVerify = toInteger(picker.getValue("faceId", i));
                score = toInteger(picker.getValue("score", i));
                if(getFaceId() == faceIdVerify) { break; }
                ++i;
            }
            if(judgeScore(score, getContext())) {
                // Verify Succeeded
                if(authCallback != null) authCallback.onAuthFacialRecognitionSucceeded(score);
            } else {
                // Verify Rejected
                if(authCallback != null) authCallback.onAuthFacialRecognitionRejected(score);
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            if(authCallback != null) authCallback.onAuthExceptionOccurred(e);
        }
    }

    /**
     * リストモードのレスポンス処理
     * @param response は返ってきたJSONObject{@link org.json.JSONObject}
     */
    private void runListJob(JSONObject response) {
        // REQUEST LIST: RESPONSE HERE

    }

    /**
     * タグエディットモードのレスポンス処理
     * @param response は返ってきたJSONObject{@link org.json.JSONObject}
     */
    private void runTagEditJob(JSONObject response) {
        // REQUEST TAG_EDIT: RESPONSE HERE

    }

    /**
     * 削除モードのレスポンス処理
     * @param response は返ってきたJSONObject{@link org.json.JSONObject}
     */
    private void runDeleteJob(JSONObject response) {
        // REQUEST DELETE: RESPONSE HERE
        String value = "";
        AuthDocomoJSON picker = new AuthDocomoJSON(response);
        try {
            if(picker.isExistKey("status")) {
                value = picker.getValue("status", 0);
                if(value.equals("success")) {
                    // Delete Succeeded
                    if(deleteCallback != null) deleteCallback.onDeleteSucceeded();
                }
            } else {
                value = picker.getValue("errorInfo", 0);
                if(value.equals("NoFace")) {
                    // Delete Failed
                    if(deleteCallback != null) deleteCallback.onDeleteFailed();
                }
            }
        } catch (JSONException e) {
            //e.printStackTrace();
            if(deleteCallback != null) deleteCallback.onDeleteExceptionOccurred(e);
        }
    }

    /**
     * 通信エラー時のレスポンス処理
     * @param e は通信時のエラー（VolleyError{@link com.android.volley.VolleyError}）
     */
    private void runErrorJob(VolleyError e) {
        // REQUEST ALL: RESPONSE ERROR HERE

    }
}