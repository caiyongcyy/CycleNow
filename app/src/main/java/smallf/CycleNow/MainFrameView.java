package smallf.CycleNow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.LayoutInflater;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.widget.Toast;
import android.widget.SimpleAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;


/**
 * Created by Administrator on 2015/3/24.
 */
public class MainFrameView extends Activity {

    private boolean bLeftViewState = false;

    DrawerLayout mDrawerLayout;
    ListView myList;  // ListView控件
    List<Map<String, Object>> m_Data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainframe);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.mycustomtitle);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myList = (ListView) findViewById(R.id.mainframe_left_list);

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

        //MyAdapter adapter = new MyAdapter(this);

        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.mainframe_left_list,new String[]{"img", "description", }, new int[]{R.id.left_list_img, R.id.left_list_textview});
        m_Data = getData();
        myList.setAdapter(adapter);

        myList.setOnItemClickListener(new OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                // TODO Auto-generated method stub
                Intent in = new Intent();
                in.setClassName(MainFrameView.this, (String) m_Data.get(position).get("view"));
                startActivity(in);
            }
        });
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("img", R.drawable.bluetooth);
        map.put("description", "路线库");
        map.put("view", "smallf.CycleNow.RouteDatabase");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.bluetooth);
        map.put("description", "设备");
        map.put("view", "smallf.CycleNow.DeviceView");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("img", R.drawable.bluetooth);
        map.put("description", "排行榜");
        map.put("view", "smallf.CycleNow.RankingList");
        list.add(map);

        return list;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }
            //Toast toast = Toast.makeText(MainFrameView.this, "back", Toast.LENGTH_SHORT);
            //toast.show();
        }

        super.onKeyDown(keyCode, event);
        return true;
    }

    /*public final class ViewHolder{
        public ImageButton imgbtn;
        public TextView textview;
    }

    public class MyAdapter extends BaseAdapter{
        private LayoutInflater mInflater;

        public MyAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        //stub func
        @Override
        public int getCount() {return m_Data.size(); }
        public Object getItem(int arg0) {return null;}
        public long getItemId(int arg0) {return 0;}
        //stub func over

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.mainframe_left_list, null);
                holder.imgbtn = (ImageButton)convertView.findViewById(R.id.left_list_img_btn);
                holder.textview = (TextView)convertView.findViewById(R.id.left_list_textview);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.textview.setText((String)m_Data.get(position).get("description"));
            holder.imgbtn.setBackgroundResource((Integer)m_Data.get(position).get("img_btn"));
            holder.imgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent();
                    in.setClassName( getApplicationContext(), (String)m_Data.get(position).get("view"));
                    startActivity(in);
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                }
            });
           return convertView;
        }
    }*/
}
