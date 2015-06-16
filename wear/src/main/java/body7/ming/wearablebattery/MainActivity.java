package body7.ming.wearablebattery;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements
        SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static String TAG = "Wearable Main Activity";
    private GoogleApiClient mGoogleApiClient;

    private TextView mTextView;


    //Sensor related
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and



        mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE),
                    SensorManager.SENSOR_DELAY_NORMAL);

        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(this);

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Has connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, " connected suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "failed" + connectionResult.toString());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(event.sensor.getName(), Float.toString(event.values[0]));

        String path = "/location" + "/" + System.currentTimeMillis();
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
        putDataMapRequest.getDataMap().putFloatArray(Sensor.STRING_TYPE_HEART_RATE, event.values);
        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.i(TAG, "Sending data was successful: " + dataItemResult.getStatus()
                                .isSuccess());
                    }
                });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
