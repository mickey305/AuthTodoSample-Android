package jp.mickey305.authtodosample;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import jp.mickey305.authtodosample.camera.CameraView;
import jp.mickey305.authtodosample.http.AuthDocomoAPIObject;

import org.json.JSONException;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.mickey305.authtodosample.layout.CameraOverlayLayout;
import jp.mickey305.authtodosample.util.CustomDialog;

public class AuthFragment extends Fragment implements
        View.OnClickListener,
        CameraView.Callback,
        AuthDocomoAPIObject.AuthCallback,
        AuthDocomoAPIObject.ResponseCallback,
        AuthDocomoAPIObject.OnConnectionStatusListener
{
    private static final String TAG = "AuthFragment";
    private AccessCallback accessCallback;
    private CameraView myCameraView;
    private byte[] imageByteArray;
    private AuthDocomoAPIObject docomoAPI;
    private ProgressDialog progressDialog;

    @InjectView(R.id.textViewAPIAuthTitle) TextView textViewTitle;
    @InjectView(R.id.buttonTake) ImageButton btnTakePicture;
    @InjectView(R.id.layout_mycamera) ViewGroup viewGroup;
    @InjectView(R.id.auth_surface_frame) LinearLayout authSurfaceFrame;

    public AuthFragment() { }

    public interface AccessCallback {
        void onLoginSucceeded();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        docomoAPI = new AuthDocomoAPIObject(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.auth, container, false);
        ButterKnife.inject(this, rootView);
        btnTakePicture.setOnClickListener(this);

        myCameraView = new CameraView(getActivity(), CameraView.CAMERA_MODE.IN);
        myCameraView.setCallback(this);

        viewGroup.addView(myCameraView);
        CameraOverlayLayout overLayer = new CameraOverlayLayout(getActivity());
        viewGroup.addView(overLayer);
        viewGroup.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.dp_24));

        authSurfaceFrame.setElevation(getResources().getDimension(R.dimen.card_elevation));

        textViewTitle.setText("Facial Recognition Login");

        return rootView;
    }

    @Override
    public void onClick(View v) {
        myCameraView.takePicture();

    }

    @Override
    public void onPictureDataInserted(byte[] pictureData) {
        imageByteArray = pictureData;

        CustomDialog dialog = new CustomDialog();
        dialog.setOnClickListener(new CustomDialog.OnClickListener() {
            @Override
            public void onClickPositiveButton() {
                TodoApplication app = (TodoApplication) getActivity().getApplication();
                // APIに登録した写真と照合する
                if(imageByteArray != null) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("inputBase64", docomoAPI.encodeBase64(imageByteArray));
                    docomoAPI.setFaceId(app.getFaceId());
                    docomoAPI.setRequestMode(AuthDocomoAPIObject.REQUEST_MODE.VERIFY);
                    docomoAPI.setParams(params);
                    docomoAPI.setAuthCallback(AuthFragment.this);
                    docomoAPI.setResponseCallback(AuthFragment.this);
                    docomoAPI.setOnConnectionStatusListener(AuthFragment.this);
                    docomoAPI.connect();
                }
            }
            @Override
            public void onClickNegativeButton() { }
        });
        dialog.setTitle("OK, Please Click Here (Access Button).");
        dialog.show(getFragmentManager(), TAG);

    }

    @Override
    public void onAuthFacialRecognitionSucceeded() {
        //showToast("Succeeded");
        if(accessCallback != null) accessCallback.onLoginSucceeded();
    }
    @Override
    public void onAuthFacialRecognitionRejected() {
        //showToast("Rejected");
    }
    @Override
    public void onAuthExceptionOccurred(JSONException e) {
        //showToast(e.toString());
    }

    @Override
    public void onResponseSucceeded() { }
    @Override
    public void onRequestError(VolleyError e) {
        //showToast(e.toString());
    }

    @Override
    public void onAPIConnectionStart() {
        //showToast("Start");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("connecting to docomo API...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    public void onAPIConnectionStop() {
        //showToast("Stop");
        progressDialog.dismiss();
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), "#"+TAG+" " +msg, Toast.LENGTH_LONG).show();
    }

    private void showLog(String msg) {
        Log.d(TAG, msg);
    }

    public void setAccessCallback(AccessCallback callback) {
        accessCallback = callback;
    }
}
