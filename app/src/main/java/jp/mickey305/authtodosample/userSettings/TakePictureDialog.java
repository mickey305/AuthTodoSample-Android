package jp.mickey305.authtodosample.userSettings;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.mickey305.authtodosample.R;
import jp.mickey305.authtodosample.camera.CameraView;
import jp.mickey305.authtodosample.layout.CameraOverlayLayout;

public class TakePictureDialog extends DialogFragment implements CameraView.Callback{
    private Dialog dialog;
    private Callback listener;
    private String title;
    private TextView textViewTitle;
    private ViewGroup viewGroup;
    private CameraView myCameraView;
    private byte[] pictureArray;

    public TakePictureDialog() { }

    public interface Callback {
        void onPictureTaken();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        dialog = new Dialog(getActivity(), R.style.DimDialogFragmentCustomLightStyle);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.auth);
        dialog.findViewById(R.id.buttonTake).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myCameraView.takePicture();
            }
        });
        dialog.setCanceledOnTouchOutside(false);

        textViewTitle = (TextView) dialog.findViewById(R.id.textViewAPIAuthTitle);
        if(title != null) textViewTitle.setText(title);
        textViewTitle.setBackgroundColor(getResources().getColor(R.color.myPrimaryColor));

        LinearLayout authFrame = (LinearLayout) dialog.findViewById(R.id.auth_frame);
        authFrame.setBackground(new ColorDrawable(Color.TRANSPARENT));

        LinearLayout authSurfaceFrame = (LinearLayout) dialog.findViewById(R.id.auth_surface_frame);
        authSurfaceFrame.setElevation(getResources().getDimension(R.dimen.card_elevation));

        viewGroup = (ViewGroup) dialog.findViewById(R.id.layout_mycamera);
        myCameraView = new CameraView(getActivity(), CameraView.CAMERA_MODE.IN);
        myCameraView.setCallback(this);
        viewGroup.addView(myCameraView);
        CameraOverlayLayout overLayer = new CameraOverlayLayout(getActivity());
        viewGroup.addView(overLayer);
        viewGroup.setElevation(getResources().getDimension(R.dimen.card_elevation));

        return dialog;
    }

    @Override
    public void onPictureDataInserted(byte[] pictureData) {
        pictureArray = pictureData;
        if(listener != null) listener.onPictureTaken();
        dismiss();
    }

    public void setCallback(Callback callback) {
        listener = callback;
    }

    public void setTitle(String msg) {
        title = msg;
    }

    public byte[] getPictureArray() { return pictureArray; }
}
