package edu.uw.servicedemo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * A service to count
 */
public class CountingService extends IntentService {

    private static final String TAG = "Counting";
    private int count;
    private Handler mHandler;

    public CountingService() {
        super("CountingService");
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "Service started");
        mHandler = new Handler();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Intent received", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "Intent received");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Handling Intent");

        for(count=1; count<=10; count++){

            Log.v(TAG, "Count: "+count);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CountingService.this, "Count: " + count, Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "" + count);
                }
            });

            if(count == 5){
                stopSelf();
            }

            try {
                Thread.sleep(2000); //sleep for 2 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "Service finished");
        count = 11; //stop counting
        super.onDestroy();
    }
}
