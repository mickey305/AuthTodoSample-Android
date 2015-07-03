package jp.mickey305.authtodosample.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jp.mickey305.authtodosample.R;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraView";
    private Context context;
    private AutoFocusListener mAutoFocusListener;
    private Callback callback;
    private Camera myCamera;
    private boolean cameraAvailable;
    private static CAMERA_MODE CAMERA_STATUS;
    private byte[] picture;

    public CameraView(Context context, CAMERA_MODE mode) {
        super(context);
        setContext(context);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

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
            this.myCamera.setPreviewDisplay(holder);
            this.myCamera.setPreviewCallback(previewListener);
        } catch (Exception e) {
            //e.printStackTrace();
            this.myCamera.release();
            this.myCamera = null;
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
                myCamera.stopPreview();
                Camera.Parameters parameters = myCamera.getParameters();

                // 画面の向きを設定
                if (portrait) {
                    myCamera.setDisplayOrientation(90);
                } else {
                    myCamera.setDisplayOrientation(0);
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
                myCamera.setParameters(parameters);
                myCamera.startPreview();
                setCameraAvailable(true);
            }
        }.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        setCameraAvailable(false);

        this.myCamera.stopPreview();
        this.myCamera.setPreviewCallback(null);
        this.myCamera.release();
        this.myCamera = null;

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
        this.myCamera = camera;
    }
    private void setContext(Context context) { this.context = context; }
    private Context getMyContext() { return this.context; }
    private void setPicture(byte[] picture) { this.picture = picture; }
    private byte[] getPicture() { return picture; }

    public void takePicture() {
        if(isCameraAvailable()) {
            setCameraAvailable(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final int previewWidth = myCamera.getParameters().getPreviewSize().width;
                    final int previewHeight = myCamera.getParameters().getPreviewSize().height;
                    Bitmap bmp = getBitmapImageFromYUV(getPicture(), previewWidth, previewHeight);
                    // 画像データを回転
                    bmp = getRotatedBmp(bmp);
                    // 写真データを保存
                    //saveBmpImage(bmp);
                    // 画像データをセット
                    byte[] picture = encodeToByteArray(bmp, Bitmap.CompressFormat.JPEG);

                    if(callback != null) callback.onPictureDataInserted(picture);
                    setCameraAvailable(true);
                }
            }).start();
        }
    }
    public boolean isCameraAvailable() { return cameraAvailable; }
    public void setCallback(Callback callback) { this.callback = callback; }
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Camera.Parameters params = this.myCamera.getParameters();
            if (!params.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_FIXED)) {
                this.myCamera.autoFocus(mAutoFocusListener);
            }
        }
        return true;
    }

    //______________________________________________________________________________________________
    private static void setCameraStatus(CAMERA_MODE mode) { CAMERA_STATUS = mode; }

    public static boolean isInCamera() { return (CAMERA_STATUS == CAMERA_MODE.IN); }
    public static CAMERA_MODE getCameraStatus() { return CAMERA_STATUS; }



    //______________________________________________________________________________________________
    private final Camera.PreviewCallback previewListener = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(final byte[] data, final Camera camera) {
            setPicture(data);
        }
    };
    private byte[] getByteImageFromYUV(byte[] data, int width, int height) {
        YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 100, baos);
        return baos.toByteArray();
    }
    private Bitmap getBitmapImageFromYUV(byte[] data, int width, int height) {
        byte[] jdata = getByteImageFromYUV(data, width, height);
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
        return bmp;
    }
    private Bitmap getRotatedBmp(Bitmap bitmap) {
        // 画像データを回転する
        Matrix matrix = new Matrix();
        if(CameraView.isInCamera()) {
            // インカメラの時
            matrix.setRotate(-90);
        } else {
            // メイン（バック）カメラの時
            matrix.setRotate(90);
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
    private byte[] encodeToByteArray(Bitmap bmp, Bitmap.CompressFormat compressFormat) {
        final int imageQuality = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(compressFormat, imageQuality, baos);
        try {
            baos.flush();
        } catch (IOException e) { }
        byte[] bArray = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) { }
        return bArray;
    }
    private void saveBmpImage(Bitmap bitmap) {
        final String ext = ".jpg";
        final SimpleDateFormat photoName = new SimpleDateFormat("yyy-MM-dd-HHmmss", Locale.JAPAN);
        final String name = photoName.format(Calendar.getInstance().getTime()) + ext;
        // 画像データを保存する
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, name, null);
    }
}