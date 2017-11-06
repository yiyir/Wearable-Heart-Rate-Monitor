package com.yiyir.wearableheartratemonitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

import static io.particle.android.sdk.utils.Py.list;

public class MainActivity extends Activity {

    private int result=0;
    private TextView hr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hr = findViewById(R.id.heartRate);
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getHeartBeat();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }




    public void getHeartBeat() {
        Async.executeAsync(ParticleCloud.get(this), new Async.ApiWork<ParticleCloud, Object>() {
            private ParticleDevice mDevice;
            @Override
            public Integer callApi(ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn("yiyir@andrew.cmu.edu", "caroline5330055");
                particleCloud.getDevices();
                mDevice = particleCloud.getDevice("270031001347353136383631");
                Log.d("yiyi", "Logged in!" + mDevice.getName());
                try {
                    result = mDevice.getIntVariable("bpm");
                    Log.d("yiyi", String.valueOf(mDevice.getIntVariable("bpm")));
                } catch (ParticleDevice.VariableDoesNotExistException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hr.setText(String.valueOf(result));
                    }
                });
                return result;
            }

            @Override
            public void onSuccess(Object o) {
                Toaster.l(MainActivity.this, "Logged in");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d("yiyi", "exception");

                exception.printStackTrace();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
