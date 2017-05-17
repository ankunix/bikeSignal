package com.nishnha.turnsignal;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private final static int SHAKE_THRESHOLD = 600;
    public final static String EXTRA_MESSAGE = "com.nishnha.TurnSignal.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            displaySensorData(x, y, z);

            long curTime = System.currentTimeMillis();

            if (curTime - lastUpdate > 100 ) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs((x + y + z) - (last_x + last_y + last_z)) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    getRandomNumber();
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void displaySensorData(float x, float y, float z) {
        TextView sensorData = (TextView) findViewById(R.id.sensor_data);
        sensorData.setText("x: " + x + "\ny: " + y + "\nz: " + z);
        sensorData.setTextSize(30);
    }

    private void getRandomNumber() {
        ArrayList numbersGenerated = new ArrayList();

        for (int i = 0; i < 6; i++) {
            Random randNum = new Random();
            int num = randNum.nextInt(48) + 1;

            if (!numbersGenerated.contains(num)) {
                numbersGenerated.add(num);
            } else {
                i--;
            }
        }
        TextView text;

        text = (TextView) findViewById(R.id.number_1);
        text.setText("" + numbersGenerated.get(0));

        text = (TextView) findViewById(R.id.number_2);
        text.setText("" + numbersGenerated.get(1));

        text = (TextView) findViewById(R.id.number_3);
        text.setText("" + numbersGenerated.get(2));

        text = (TextView) findViewById(R.id.number_4);
        text.setText("" + numbersGenerated.get(3));

        text = (TextView) findViewById(R.id.number_5);
        text.setText("" + numbersGenerated.get(4));

        text = (TextView) findViewById(R.id.number_6);
        text.setText("" + numbersGenerated.get(5));
    }

    /** Called when the user clicks the send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
