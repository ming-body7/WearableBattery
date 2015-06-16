package body7.ming.wearablebattery;

import android.hardware.Sensor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends ActionBarActivity implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;
    private final static String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    protected void onResume(){
        //super.onResume();
        super.onStart();

        mGoogleApiClient.connect();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient,this);
        mGoogleApiClient.disconnect();
        Log.i(TAG,"onPause");
    }

    @Override
    protected void onStop(){
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        Log.i(TAG,"onStop");
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


    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.i(TAG, "On Data Changed");
        for (DataEvent event : dataEventBuffer){
            if (event.getType() == DataEvent.TYPE_DELETED){
                Log.i(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED){
                Log.i(TAG, "DataItem changed: " + event.getDataItem().getUri());
                Uri dataItemUri = event.getDataItem().getUri();
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem())
                        .getDataMap();
                float[] values = dataMap.getFloatArray(Sensor.STRING_TYPE_HEART_RATE);
            }
        }
    }




    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
