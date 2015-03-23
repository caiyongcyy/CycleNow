package smallf.CycleNow;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.BaseAdapter;
import android.bluetooth.BluetoothAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by smallF on 2015/2/26.
 * 1.0 mod by xlwang 2015/03/09: Add bluetooth connection methods;
 */

public class BlueToothActivity extends ActionBarActivity {
    /*variables*/
    private BluetoothAdapter mBluetoothAdapter =null;
    private SimpleAdapter mSimpleAdapter;
    private ArrayList<Map<String,Object>> mBTInfoData= new ArrayList<Map<String,Object>>();
    private ListView mBTListView;
    private BroadcastReceiver mReceiver = null;
    private final static int REQUEST_ENABLE_BT = 1;
    IBinder serviceBinder;
    BtService btServ;
    Intent intent;

    /**************service 命令*********/
    static final int CMD_STOP_SERVICE = 0x01;
    static final int CMD_INIT =0x03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        setTitle(" 蓝牙设置");

        intent = new Intent(this,BtService.class);
        startService(intent);
        BlueToothProc();
    }

    /*Search bluetooth devices operation*/
    public void BlueToothProc()
    {
        //Open bluetooth;
        mBluetoothAdapter = mBluetoothAdapter.getDefaultAdapter();
        mSimpleAdapter = new SimpleAdapter(this, mBTInfoData, R.layout.bt_listview,
                new String[]{"ItemImage","Name","Address"},new int[]{R.id.ItemImage,R.id.Name,R.id.Address});
        mBTListView = (ListView) findViewById(R.id.BTDeviceList);


        mBTListView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                    long id) {
            //String sTitle = mSimpleAdapter.getItem(position).toString();
            Map<String,String> item = new HashMap<String,String>();
            item = (Map)mSimpleAdapter.getItem(position);
            BlueToothActivity.this.SendInit(item.get("Address"));
            }
        });

        if(null == mBluetoothAdapter)
        {
            Toast.makeText(this, "没有蓝牙设备！", Toast.LENGTH_SHORT).show();
            return;//
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(mBluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            Toast.makeText(this, "蓝牙设备已打开！", Toast.LENGTH_SHORT).show();
        }

        boolean bStartScan = mBluetoothAdapter.startDiscovery();
        if(bStartScan == true){
            //Toast.makeText(this, "开始扫描设备！", Toast.LENGTH_SHORT).show();
        }
        mBTInfoData.clear();

        final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //获取已配对设备
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Map<String,Object> item = new HashMap<String,Object>();
                item.put("ItemImage", R.drawable.bluetooth);
                item.put("Name", device.getName()+ " 已配对");
                item.put("Address", device.getAddress());
                mBTInfoData.add(item);
                mBTListView.setAdapter(mSimpleAdapter);
            }
        }

        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    boolean bDevicePaird = false;
                    if(pairedDevices.size() > 0){
                        for (BluetoothDevice devicePair : pairedDevices){
                            if( devicePair.getAddress().equals(device.getAddress()) ){
                                bDevicePaird = true;
                            }
                        }
                    }
                    if (!bDevicePaird){
                        Map<String,Object> item = new HashMap<String,Object>();
                        item.put("ItemImage", R.drawable.bluetooth);
                        item.put("Name", device.getName());
                        item.put("Address", device.getAddress());
                        mBTInfoData.add(item);
                        mBTListView.setAdapter(mSimpleAdapter);
                    }
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {
                    setTitle("搜索完成");
                    Log.d("BroadcastReceiver", "find over");
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter1.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter1); // Don't forget to unregister during onDestroy
    }

    @Override
    protected void onDestroy(){
        unregisterReceiver(mReceiver);
        stopService(intent);
        super.onDestroy();
    }

    //返回按键不退出该activity
/*    @Override
    public void onBackPressed(){

        return;
    }*/

/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    public void SendInit(String sAddress){
        Intent intent = new Intent();//创建Intent对象
        intent.setAction("android.intent.action.cmd");
        intent.putExtra("cmd", CMD_INIT);
        intent.putExtra("Address", sAddress);
        sendBroadcast(intent);//发送广播
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，
            case RESULT_OK:
                Toast.makeText(this, "蓝牙设备已打开！", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.startDiscovery();
                break;

            case RESULT_CANCELED:
                Toast.makeText(this, "蓝牙设备打开失败！", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

        /*Customed ListViewAdapter*/
        class ListViewAdapter extends BaseAdapter {
            View[] itemViews;

            public ListViewAdapter(String[] itemTitles, String[] itemTexts,
                                   int[] itemImageRes) {
                itemViews = new View[itemTitles.length];

                for (int i = 0; i < itemViews.length; i++) {
                    itemViews[i] = makeItemView(itemTitles[i], itemTexts[i],
                            itemImageRes[i]);
                }
            }

            public int getCount() {
                return itemViews.length;
            }

            public View getItem(int position) {
                return itemViews[position];
            }

            public long getItemId(int position) {
                return position;
            }

            private View makeItemView(String strTitle, String strText, int resId) {
                LayoutInflater inflater = (LayoutInflater) BlueToothActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // 使用View的对象itemView与R.layout.item关联
                View itemView = inflater.inflate(R.layout.bt_listview, null);

                // 通过findViewById()方法实例R.layout.item内各组件
                TextView title = (TextView) itemView.findViewById(R.id.Name);
                title.setText(strTitle);
                TextView text = (TextView) itemView.findViewById(R.id.Address);
                text.setText(strText);
                ImageView image = (ImageView) itemView.findViewById(R.id.ItemImage);
                image.setImageResource(resId);

                return itemView;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    return itemViews[position];
                return convertView;
            }
        }
    }

    /* 停止客户端连接 */
    private void shutdownClient() {
    }

}

