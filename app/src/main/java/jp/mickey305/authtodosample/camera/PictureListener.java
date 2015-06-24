package jp.mickey305.authtodosample.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PictureListener implements PictureCallback {
    private static final String TAG = "PictureListener";
    private Context context;
    private PictureDataCallback pictureDataCallback;

    public PictureListener(Context context) {
        this.context = context;
    }

    public interface PictureDataCallback {
        void onDataProcessingFinished();
        void onDataInserted(byte[] data);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        byte[] picture;
        // 画像データを回転
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        bmp = getRotatedBmp(bmp);
        // 写真データを保存
        //saveBmpImage(bmp, null);
        // 画像データをセット
        picture = encodeToByteArray(bmp, Bitmap.CompressFormat.JPEG);

        if(pictureDataCallback != null) pictureDataCallback.onDataProcessingFinished();
        if(pictureDataCallback != null) pictureDataCallback.onDataInserted(picture);
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

    private void saveBmpImage(Bitmap bitmap, String fileExt) {
        final String ext = (fileExt == null)? ".jpg": "."+fileExt;
        final SimpleDateFormat photoName = new SimpleDateFormat("yyy-MM-dd-HHmmss", Locale.JAPAN);
        final String name = photoName.format(Calendar.getInstance().getTime()) + ext;
        // 画像データを保存する
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, name, null);
    }

    private int radianToDegree(float rad) {
        return (int) Math.floor(Math.toDegrees(rad));
    }

    private void showToast(final String msg) {
        Toast.makeText(this.context, msg, Toast.LENGTH_LONG).show();
    }

    public void setPictureDataCallback(PictureDataCallback callback) {
        pictureDataCallback = callback;
    }
}
