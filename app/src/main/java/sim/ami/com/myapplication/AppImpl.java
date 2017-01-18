package sim.ami.com.myapplication;

import android.app.Application;
import android.content.Intent;

/**
 * Created by Administrator on 4/25/2016.
 */
public class AppImpl extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
       // Intent intent= new Intent(this, ChatHeadService.class);
       // startService(intent);
    }
}
