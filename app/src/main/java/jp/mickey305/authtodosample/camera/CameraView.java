package jp.mickey305.authtodosample.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import jp.mickey305.authtodosample.R;

public class CameraView extends SurfaceView implements
        SurfaceHolder.Callback, PictureListener.PictureDataCallback {
    private static final String TAG = "CameraView";
    private Context context;
    private PictureListener mPictureListener;
    private AutoFocusListener mAutoFocusListener;
    private Callback callback;
    private Camera myCamera;
    private boolean cameraAvailable;
    private static CAMERA_MODE CAMERA_STATUS;

    public CameraView(Context context, CAMERA_MODE mode) {
        super(context);
        setContext(context);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mPictureListener = new PictureListener(context);
        mPictureListener.setPictureDataCallback(this);
        mAutoFocusListener = new AutoFocusListener();

        setCameraStatus(mode);
    }

    public interface Callback {
        void onPictureDataInserted(byte[] pictureData);
    }

    public enum CAMERA_MODE {
        BACK    (0),
        IN      (1);

        CAMERA_MODE(int nativeInt) { this.nativeInt = nativeInt; }
        final int nativeInt;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setCameraAvailable(false);
        openMyCamera(CAMERA_STATUS);
        try {
            getMyCamera().setPreviewDisplay(holder);
        } catch (Exception e) {
            //e.printStackTrace();
            releaseMyCamera();
            setMyCamera(null);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        final boolean portrait = isPortrait();
        final int width_tmp = width;
        final int height_tmp = height;

        // カメラプレビューレイアウトの設定
        int previewWidth = getResources().getInteger(R.integer.camera_width);
        int previewHeight = getResources().getInteger(R.integer.camera_height);
        android.view.ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        if (portrait) {
            layoutParams.width = previewWidth;
            layoutParams.height = previewHeight;
        } else {
            layoutParams.width = previewHeight;
            layoutParams.height = previewWidth;
        }
        this.setLayoutParams(layoutParams);

        new Thread() {
            @Override
            public void run() {
                stopMyCameraPreview();
                Camera.Parameters parameters = getMyCameraParameters();

                // 画面の向きを設定
                if (portrait) {
                    getMyCamera().setDisplayOrientation(90);
                } else {
                    getMyCamera().setDisplayOrientation(0);
                }

                // 対応するプレビューサイズ・保存サイズを取得する
                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();

                Camera.Size previewSize = getOptimalPreviewSize(
                        previewSizes,
                        width_tmp,
                        height_tmp
                );
                Camera.Size pictureSize = pictureSizes.get(0);

                parameters.setPreviewSize(previewSize.width, previewSize.height);
                parameters.setPictureSize(pictureSize.width, pictureSize.height);

                // パラメータを設定してカメラを再開
                getMyCamera().setParameters(parameters);
                startMyCameraPreview();
            }
        }.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        setCameraAvailable(false);

        stopMyCameraPreview();
        releaseMyCamera();
        setMyCamera(null);
    }

    @Override
    public void onDataProcessingFinished() {
        // カメラを再開
        startMyCameraPreview();
    }

    @Override
    public void onDataInserted(byte[] data) {
        if(callback != null) callback.onPictureDataInserted(data);
    }

    private boolean isPortrait() {
        return (getMyContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT);
    }
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        final double targetRatio = (double) w / h;
        if (sizes == null) { return null; }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
    private void setCameraAvailable(boolean cameraAvailable) {
        this.cameraAvailable = cameraAvailable;
    }
    private void openMyCamera(CAMERA_MODE mode) {
        Camera  camera = (mode == CAMERA_MODE.BACK)?
                Camera.open(): Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        setMyCamera(camera);
    }
    private void stopMyCameraPreview() {
        getMyCamera().stopPreview();
    }
    private void startMyCameraPreview() {
        getMyCamera().startPreview();
        setCameraAvailable(true);
    }
    private void releaseMyCamera() { getMyCamera().release(); }
    private Camera getMyCamera() { return this.myCamera; }
    private void setMyCamera(Camera camera) { this.myCamera = camera; }
    private Camera.Parameters getMyCameraParameters() { return getMyCamera().getParameters(); }
    private void setContext(Context context) { this.context = context; }
    private Context getMyContext() { return this.context; }

    public void takePicture() {
        if(isCameraAvailable()) {
            getMyCamera().takePicture(null, null, mPictureListener);
            setCameraAvailable(false);
        }
    }
    public boolean isCameraAvailable() { return cameraAvailable; }
    public void setCallback(Callback callback) { this.callback = callback; }
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Camera.Parameters params = this.getMyCameraParameters();
            if (!params.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_FIXED)) {
                getMyCamera().autoFocus(mAutoFocusListener);
            }
        }
        return true;
    }

    //______________________________________________________________________________________________
    private static void setCameraStatus(CAMERA_MODE mode) { CAMERA_STATUS = mode; }

    public static boolean isInCamera() { return (CAMERA_STATUS == CAMERA_MODE.IN); }
    public static CAMERA_MODE getCameraStatus() { return CAMERA_STATUS; }
}
