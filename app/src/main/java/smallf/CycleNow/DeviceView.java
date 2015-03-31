package smallf.CycleNow;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Administrator on 2015/3/26.
 */
public class DeviceView  extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.device);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitle);

    }
}