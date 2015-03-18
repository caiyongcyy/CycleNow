package smallf.CycleNow;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by smallF on 2015/2/26.
 * 1.0 mod by xlwang 2015/03/09: Add bluetooth connection methods;
 */

public class BlueTooth extends ActionBarActivity {
    /*variables*/
    private BluetoothAdapter mBluetoothAdapter =null;
    private SimpleAdapter mSimpleAdapter;
    private ArrayList<Map<String,Object>> mBTInfoData= new ArrayList<Map<String,Object>>();
    private ListView mBTListView;
    private BroadcastReceiver mReceiver = null;
    private BluetoothDevice mBluetoothDevice;
    private final static int REQUEST_ENABLE_BT = 1;
    private ClientThread mClientThread = null;
    private String mBTAddress = null;
    public SocketDataThread mManageConnectedSocket=  null;


    UUID MyUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        setTitle(" 蓝牙设置");

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
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(item.get("Address"));
            mBTAddress = mBluetoothDevice.getAddress();
            mClientThread = new ClientThread(mBluetoothDevice);
            mClientThread.run();
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
        registerReceiver(mReceiver, filter1); // Don't forget to unregister during onDestroy
    }

    @Override
    protected void onDestroy(){
        unregisterReceiver(mReceiver);
        shutdownClient();
        super.onDestroy();
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
                LayoutInflater inflater = (LayoutInflater) BlueTooth.this
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
        new Thread() {
            public void run() {
                if(mClientThread!=null)
                {
                    mClientThread.cancel();
                    mClientThread= null;
                }
            };
        }.start();
    }


    /*蓝牙客户端连接线程*/
    private class ClientThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ClientThread(BluetoothDevice device) {
            mmDevice = device;
            try {
                // 连接建立之前的先配对
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Method creMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    creMethod.invoke(device);
                } else {
                }
            } catch (Exception e) {
                // TODO: handle exception
                Toast.makeText(BlueTooth.this, "无法配对！", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket tmp = null;
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try{
                tmp = mmDevice.createRfcommSocketToServiceRecord(MyUUID);
            }catch (IOException ioexception){

            }
            mmSocket = tmp;

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                    Log.e("connect", "", connectException);
                    Log.d("clientThread", "连接失败");
                    Toast.makeText(BlueTooth.this, "Socket 连接失败！", Toast.LENGTH_SHORT).show();
                } catch (IOException closeException) {
                }
                return;
            }
            Toast.makeText(BlueTooth.this, "Socket 连接成功！", Toast.LENGTH_SHORT).show();
            Log.d("mBtSocket", "end-->"+ mmSocket);

            mManageConnectedSocket = new SocketDataThread(mmSocket);
            //mManageConnectedSocket.run();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    //Managing a Connection
    private class SocketDataThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public SocketDataThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    byte[] buf_data = new byte[bytes];
                    // Read from the InputStream
                    if( (bytes = mmInStream.read(buffer))>0 ) {
                        for(int i=0; i<bytes; i++)
                        {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);//接收的值inputstream 为 s

                        if(s.equalsIgnoreCase("o")){ //o表示opend!
                            //isClosed = false;
                        }
                        else if(s.equalsIgnoreCase("c")){  //c表示closed!
                            //isClosed = true;
                        }
                    }
              } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}

