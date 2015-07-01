package jp.mickey305.authtodosample.layout;

import android.content.Context;
import android.content.res.Configuration;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import jp.mickey305.authtodosample.R;

public class CameraOverlayLayout extends RelativeLayout {
    public CameraOverlayLayout(Context context) {
        super(context);

        final boolean portrait = isPortrait();
        setBackground(portrait);
        setLayout(portrait);
    }

    private void setLayout(boolean portrait) {
        int previewWidth = getResources().getInteger(R.integer.camera_width);
        int previewHeight = getResources().getInteger(R.integer.camera_height);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (portrait) {
            layoutParams.width = previewWidth;
            layoutParams.height = previewHeight;
        } else {
            layoutParams.width = previewHeight;
            layoutParams.height = previewWidth;
        }
        this.setLayoutParams(layoutParams);
    }

    private void setBackground(boolean portrait) {
        if(portrait) {
            this.setBackground(getResources().getDrawable(R.drawable.layout_face_camera));
        } else {

        }
    }

    private boolean isPortrait() {
        return (getContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT);
    }
}
