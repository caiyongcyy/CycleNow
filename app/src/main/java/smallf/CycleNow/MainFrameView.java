package smallf.CycleNow;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.widget.Toast;
import android.widget.SimpleAdapter;
import android.widget.ListView;
import java.util.HashMap;
import java.util.ArrayList;


/**
 * Created by Administrator on 2015/3/24.
 */
public class MainFrameView extends Activity {

    private boolean bLeftViewState = false;

    SimpleAdapter listItemAdapter;  // ListView的适配器
    ArrayList<HashMap<String, Object>> listItem;  // ListView的数据源，这里是一个HashMap的列表
    ListView myList;  // ListView控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.mainframe);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.mycustomtitle);

        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ((ImageButton) findViewById(R.id.header_left_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bLeftViewState = mDrawerLayout.isDrawerOpen(Gravity.LEFT);
                if(bLeftViewState){
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
                else{
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

/*        listItem = new ArrayList<HashMap<String, Object>>();
        listItemAdapter = new SimpleAdapter(this, listItem, R.layout.mainframe,
                new String[]{"image", "title", "text"},
                new int[]{R.id.ItemImage, R.id.ItemTitle, R.id.ItemText});
        myList.setAdapter(listItemAdapter);*/
    }

}
