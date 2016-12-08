package com.wenjian.criminalintent.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.wenjian.criminalintent.base.SingleFragmentActivity;
import com.wenjian.criminalintent.fragment.CrimeCameraFragment;
import com.wenjian.criminalintent.util.JLog;

public class CrimeCameraActivity extends SingleFragmentActivity {

    private SensorEventListener mEventListener;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (Sensor.TYPE_ACCELEROMETER != sensorEvent.sensor.getType()) {
                    return;
                }

                float[] values = sensorEvent.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];
//                JLog.i("wj","x:"+x+"---y:"+y+"---z:"+z);
              /*  if (((x > -15 && x < -10) || (x < 15 && x > 10)) && Math.abs(y) < 1.5) {
                    JLog.i("wj","横屏啦");
                }else {
                    JLog.i("wj","竖屏啦");
                }
*/
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
        mSensorManager.registerListener(mEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected Fragment createFragment() {
        return CrimeCameraFragment.newInstance();
    }

    @Override
    protected boolean needFullScreen() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mEventListener);
    }
}
