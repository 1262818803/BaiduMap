package demo.com.demo2;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by asus on 2018/5/29.
 */

public class SecondActivity extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
