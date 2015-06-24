package jp.mickey305.authtodosample;

import android.app.Application;
import android.widget.Toast;

import jp.mickey305.authtodosample.util.SharedPreferencesObject;

public class TodoApplication extends Application {
    private static final String TAG = "TodoApplication";
    private boolean statusLogin;
    private int faceId;
    private SharedPreferencesObject sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = new SharedPreferencesObject(this);
        setStatusLogin(sp.readBool(getResources().getString(R.string.setting_key_login_status)));
        setFaceId(sp.readInt(getResources().getString(R.string.setting_key_face_id_num)));

    }

    private void showToast(String msg) {
        Toast.makeText(this, "#"+TAG+" " + msg, Toast.LENGTH_LONG).show();
    }

    public boolean isStatusLogin() {
        return statusLogin;
    }

    public void setStatusLogin(boolean statusLogin) {
        this.statusLogin = statusLogin;
    }

    public int getFaceId() {
        return faceId;
    }

    public void setFaceId(int faceId) {
        this.faceId = faceId;
    }
}
