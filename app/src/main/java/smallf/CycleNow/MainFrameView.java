package smallf.CycleNow;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.LayoutInflater;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.widget.Toast;
import android.widget.SimpleAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


/**
 * Created by Administrator on 2015/3/24.
 */
public class MainFrameView extends Activity {

    private boolean bLeftViewState = false;
    static final int CMD_SEND_DATA = 0x02;
    static final int CMD_RUN = 0x04;

    DrawerLayout mDrawerLayout;
    ListView myList;  // ListView控件
    List<Map<String, Object>> m_Data;
    Intent intent;
    LineChart mchart;
    Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainframe);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.mycustomtitle);
        intent = new Intent(this,BtService.class);
        startService(intent);

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

        Button button_start = (Button)findViewById(R.id.button_start);
        button_start.setOnClickListener(new View.OnClickListener(){
        boolean pressed = false;

            @Override
            public void onClick(View v) {
                if (pressed == false) {
                    //得到textview实例
                    TextView hellotv = (TextView) findViewById(R.id.textView);
                    //读取strings.xml定义的interact_message信息并写到textview上
                    hellotv.setText("Started!!");

                    SendCmdRun();
                    pressed = true;
                } else {
                    TextView hellotv = (TextView) findViewById(R.id.textView);
                    hellotv.setText("路线");
                    pressed = false;
                }
            }
           });

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

        //根据GPX文件话路线海拔图
        mchart = (LineChart) findViewById(R.id.chart1);
        dataInit();

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

    @Override
    protected void onDestroy(){
        stopService(intent);
        super.onDestroy();
    }

    //向串口发送数据
    public void SendData(byte command, int value){
        Intent intent = new Intent();//创建Intent对象
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("cmd", CMD_SEND_DATA);
        intent.putExtra("command", command);
        intent.putExtra("value", value);
        sendBroadcast(intent);//发送广播
    }

    //开始运行
    public void SendCmdRun(){
        Intent intent = new Intent();//创建Intent对象
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("cmd", CMD_RUN);
        sendBroadcast(intent);//发送广播
    }

    //路线数据读取
    private void dataInit() {

        //********************CUBIC LINE**************************************/
        mchart.setDescription("HOLY");

        // enable value highlighting
        mchart.setHighlightEnabled(true);

        // enable touch gestures
        mchart.setTouchEnabled(true);

        // enable scaling and dragging
        mchart.setDragEnabled(true);
        mchart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mchart.setPinchZoom(false);

        mchart.setDrawGridBackground(false);

        //tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        //XAxis x = mchart.getXAxis();
        //x.setTypeface(tf);
        //tf = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");
        YAxis y = mchart.getAxisLeft();
        //y.setTypeface(tf);
        y.setLabelCount(5);

        mchart.getAxisRight().setEnabled(false);

        // add data
        setData();

        mchart.getLegend().setEnabled(false);

        mchart.animateXY(2000, 2000);

        // dont forget to refresh the drawing
        mchart.invalidate();
        return;
    }

    //路线数据计算
    private void setData() {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> vals1 = new ArrayList<Entry>();
        try {
            String fileName = getSDPath() +"/" + "hcchu/test.gpx";
            File file = new File(fileName);
            BufferedReader br = null;
            //StringReader sr = null;
            FileReader mfile = new FileReader(file);
            Double Distance;
            Distance = 0.0;

            br = new BufferedReader(mfile);
            ArrayList<Node> nodes = new ArrayList<Node>();
            nodes = GPXData.Gpxparser(br); // gpxData is an arraylist with trkpt and wpt


            for (int i = 0; i < nodes.size(); i++) {


                String type = nodes.get(i).getType(); // getType() returns trkpt or wpt
                if (type == "trkpt") {
                    //y轴 海拔
                    vals1.add(new Entry(nodes.get(i).getEle(), i));
                    //x轴 位移
                    if(i == 0){
                        xVals.add(0 + "");
                    }
                    else{
                        Distance = Distance + GetDistance(nodes.get(i).getLat(),nodes.get(i).getLon(),nodes.get(i-1).getLat(),nodes.get(i-1).getLon());
                        xVals.add(Distance + "");
                    }
                }
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
        set1.setDrawCubic(true);
        set1.setCubicIntensity(0.2f);
        set1.setDrawFilled(true);
        set1.setDrawCircles(false);
        set1.setLineWidth(2f);
        set1.setCircleSize(5f);
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setColor(Color.rgb(104, 241, 175));
        set1.setFillColor(Color.rgb(104, 241, 175));
        set1.setFillColor(ColorTemplate.getHoloBlue());

        // create a data object with the datasets
        LineData data = new LineData(xVals, set1);
        //data.setValueTypeface(tf);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        mchart.setData(data);
        return;
    }

    //根据经纬度计算距离
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    public static double GetDistance(double lat1, double lng1, double lat2, double lng2)
    {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
        s = s * 6378137; //EARTH_RADIUS
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public String getSDPath(){
        File sdDir = null;
        //boolean sdCardExist = Environment.getExternalStorageState().equals(Android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        //if   (sdCardExist)
        //{
        sdDir = Environment.getExternalStorageDirectory();//获取根目录
        //}
        return sdDir.toString();

    }
}



