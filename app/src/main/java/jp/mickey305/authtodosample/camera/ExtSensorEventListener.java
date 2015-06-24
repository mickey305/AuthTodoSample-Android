package jp.mickey305.authtodosample.camera;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ExtSensorEventListener implements SensorEventListener{
    public static final int MATRIX_SIZE = 16;
    public static final int DIMENSION = 3;
    private float[] magneticValues = new float[DIMENSION];
	private float[] accelerometerValues = new float[DIMENSION];
    private float[] orientationValues = new float[DIMENSION];

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) { return; }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                // 地磁気センサ
                magneticValues = event.values.clone();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                // 加速度センサ
                accelerometerValues = event.values.clone();
                break;
        }

        getOrientation(magneticValues, accelerometerValues, orientationValues);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public float[] getOrientationValues() { return this.orientationValues; }

    /**
     * Computes the device's orientation based on the sensor values.
     *
     * @param gravity
     *        is an array of 3 floats containing the gravity vector expressed in
     *        the device's coordinate. You can simply use the
     *        {@link android.hardware.SensorEvent#values values} returned by a
     *        {@link android.hardware.SensorEvent SensorEvent} of a
     *        {@link android.hardware.Sensor Sensor} of type
     *        {@link android.hardware.Sensor#TYPE_ACCELEROMETER
     *        TYPE_ACCELEROMETER}.
     *
     * @param geomagnetic
     *        is an array of 3 floats containing the geomagnetic vector
     *        expressed in the device's coordinate. You can simply use the
     *        {@link android.hardware.SensorEvent#values values} returned by a
     *        {@link android.hardware.SensorEvent SensorEvent} of a
     *        {@link android.hardware.Sensor Sensor} of type
     *        {@link android.hardware.Sensor#TYPE_MAGNETIC_FIELD
     *        TYPE_MAGNETIC_FIELD}.
     *
     * @param O
     *        an array of 3 floats to hold the result.
     *
     * @return The array values passed as argument.
     */
    private static float[] getOrientation(float[] geomagnetic, float[] gravity, float[] O) {
        // 加速度センサと地磁気センタから回転行列を取得
        if(geomagnetic != null && gravity != null) {
            float[] rotationMatrix = new float[MATRIX_SIZE];
            float[] inclinationMatrix = new float[MATRIX_SIZE];
            float[] remapedMatrix = new float[MATRIX_SIZE];

            SensorManager.getRotationMatrix(
                    rotationMatrix, inclinationMatrix, gravity, geomagnetic);

            SensorManager.remapCoordinateSystem(
                    rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remapedMatrix);
            return SensorManager.getOrientation(remapedMatrix, O);
        }
        return null;
    }
}
