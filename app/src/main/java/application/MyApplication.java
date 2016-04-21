package application;

import android.app.Application;
import android.util.Log;

import util.FileManager;

/**
 * Created by ein on 2016/4/17.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static FileManager fileManager;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        fileManager = new FileManager(getApplicationContext());
    }

    public FileManager getFileManager() {
        if (fileManager == null) {
            fileManager = new FileManager(getApplicationContext());
        }
        return fileManager;
    }

}
