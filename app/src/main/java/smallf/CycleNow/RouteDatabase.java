package smallf.CycleNow;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/3/26.
 */
public class RouteDatabase extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.routedatabase);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitle);
        //((TextView) findViewById(R.id.header_text)).setText("路线库");
    }
}

