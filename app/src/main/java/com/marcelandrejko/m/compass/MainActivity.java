package com.marcelandrejko.m.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    private android.widget.ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mSensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        this.iv = this.findViewById(R.id.imageView);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor acc = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mag = this.mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor rot = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (rot!=null) {
            this.mSensorManager.registerListener(this, rot, SensorManager.SENSOR_DELAY_FASTEST);
        } else if (acc!=null && mag!=null) {
            this.mSensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_FASTEST);
            this.mSensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_FASTEST);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, this.mAccelerometerReading, 0, this.mAccelerometerReading.length);
            if (SensorManager.getRotationMatrix(this.mRotationMatrix, null, this.mAccelerometerReading, this.mMagnetometerReading))
                this.updateOrientationAngles();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, this.mMagnetometerReading, 0, this.mMagnetometerReading.length);
            if (SensorManager.getRotationMatrix(this.mRotationMatrix, null, this.mAccelerometerReading, this.mMagnetometerReading))
                this.updateOrientationAngles();
        } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(this.mRotationMatrix, event.values);
            this.updateOrientationAngles();
        }
    }

    public void updateOrientationAngles() {
        SensorManager.getOrientation(this.mRotationMatrix, this.mOrientationAngles);
        long azimuth = Math.round(Math.toDegrees(this.mOrientationAngles[0]));
        this.iv.setRotation(-azimuth);
        azimuth = azimuth<0 ? 360+azimuth : azimuth;
        this.setTitle("Azimuth "+azimuth+"°");
    }
}
