package jp.mickey305.authtodosample.userSettings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import jp.mickey305.authtodosample.util.CustomDialog;
import jp.mickey305.authtodosample.R;
import jp.mickey305.authtodosample.util.SharedPreferencesObject;
import jp.mickey305.authtodosample.http.AuthDocomoAPIObject;

import org.json.JSONException;

import java.util.HashMap;

public class TodoPreferenceFragment extends PreferenceFragment implements
        Preference.OnPreferenceClickListener,
        AuthDocomoAPIObject.RegisterCallback,
        AuthDocomoAPIObject.DeleteCallback,
        AuthDocomoAPIObject.ResponseCallback,
        AuthDocomoAPIObject.OnConnectionStatusListener,
        TakePictureDialog.Callback
{
    private static final String TAG = "TodoPreferenceFragment";
    private SharedPreferencesObject sp;
    private Callback callback;
    private AuthDocomoAPIObject docomoAPI;
    private int faceId;
    private byte[] pictureByteArray;
    private TakePictureDialog dialogPicture;
    private ProgressDialog progressDialog;

    public interface Callback {
        void onAPIRegisterSucceeded();
        void onAPIDeleteSucceeded();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        Preference prefFaceId, prefDeleteInfo;
        prefFaceId = findPreference(getString(R.string.setting_key_face_id_num));
        prefDeleteInfo = findPreference(getString(R.string.setting_key_delete_info));
        prefFaceId.setOnPreferenceClickListener(this);
        prefDeleteInfo.setOnPreferenceClickListener(this);

        docomoAPI = new AuthDocomoAPIObject(getActivity());
        sp = new SharedPreferencesObject(getActivity());
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        final String key = preference.getKey();
        if(key.equals(getString(R.string.setting_key_face_id_num))) {
            dialogPicture = new TakePictureDialog();
            dialogPicture.setTitle("OK, Take Picture");
            dialogPicture.setCallback(this);
            dialogPicture.show(getFragmentManager(), TAG);

        }else if(key.equals(getString(R.string.setting_key_delete_info))) {
            CustomDialog dialog = new CustomDialog();
            dialog.setTitle("OK, Delete Facial Info");
            dialog.setOnClickListener(new CustomDialog.OnClickListener() {
                @Override
                public void onClickPositiveButton() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("faceId", "ALL");
                    docomoAPI.setRequestMode(AuthDocomoAPIObject.REQUEST_MODE.DELETE);
                    docomoAPI.setDeleteCallback(TodoPreferenceFragment.this);
                    docomoAPI.setOnConnectionStatusListener(TodoPreferenceFragment.this);
                    docomoAPI.setResponseCallback(TodoPreferenceFragment.this);
                    docomoAPI.setParams(params);
                    docomoAPI.connect();
                }
                @Override
                public void onClickNegativeButton() { }
            });
            dialog.show(getFragmentManager(), TAG);

        }
        return false;
    }

    /*
     * 撮影ボタンを押した時のダイアログ内で呼ばれる
     */
    @Override
    public void onPictureTaken(byte[] data) {
        setPictureByteArray(data);

        CustomDialog dialog = new CustomDialog();
        dialog.setTitle("OK, Register Facial Info");
        dialog.setOnClickListener(new CustomDialog.OnClickListener() {
            @Override
            public void onClickPositiveButton() {
                if(getPictureByteArray() != null) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("inputBase64", docomoAPI.encodeBase64(getPictureByteArray()));
                    docomoAPI.setRequestMode(AuthDocomoAPIObject.REQUEST_MODE.REGISTER);
                    docomoAPI.setParams(params);
                    docomoAPI.setRegisterCallback(TodoPreferenceFragment.this);
                    docomoAPI.setOnConnectionStatusListener(TodoPreferenceFragment.this);
                    docomoAPI.setResponseCallback(TodoPreferenceFragment.this);
                    docomoAPI.connect();
                }
            }
            @Override
            public void onClickNegativeButton() {
                dialogPicture.show(getFragmentManager(), TAG);
            }
        });
        dialog.show(getFragmentManager(), TAG);
    }

    @Override
    public void onAPIConnectionStart() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("connecting to docomo API...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    public void onAPIConnectionStop() {
        progressDialog.dismiss();
    }

    @Override
    public void onResponseSucceeded() { }
    @Override
    public void onRequestError(VolleyError e) {
        showToast(e.toString());
    }

    /*
     * Checkボタンを押した時のダイアログ内で呼ばれる
     */
    @Override
    public void onRegisterSucceeded(int registeredFaceId) {
        showToast("Registered faceId is "+registeredFaceId);
        sp.write(
                getResources().getString(R.string.setting_key_face_id_num),
                registeredFaceId
        );
        setFaceId(registeredFaceId);
        if(callback != null) callback.onAPIRegisterSucceeded();
    }
    @Override
    public void onRegisterExceptionOccurred(JSONException e) { }

    /*
     * 削除ボタンを押した時のダイアログ内で呼ばれる
     */
    @Override
    public void onDeleteSucceeded() {
        showToast("Delete Succeeded");
        sp.write(getResources().getString(R.string.setting_key_face_id_num), 0);
        setFaceId(0);
        if(callback != null) callback.onAPIDeleteSucceeded();}
    @Override
    public void onDeleteFailed() {
        showToast("Delete Failed");
    }
    @Override
    public void onDeleteExceptionOccurred(JSONException e) { }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), TAG+": "+msg, Toast.LENGTH_LONG).show();
    }
    private void showLog(String msg) {
        Log.d(TAG, msg);
    }
    private void setFaceId(int faceId) { this.faceId = faceId; }
    private void setPictureByteArray(byte[] pictureByteArray) {
        this.pictureByteArray = pictureByteArray;
    }
    private byte[] getPictureByteArray() { return pictureByteArray; }

    public int getFaceId() { return faceId; }
    public void setCallback(Callback callback) { this.callback = callback; }
}
