package smallf.CycleNow;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.bluetooth.BluetoothAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by smallF on 2015/2/26.
 */

public class BlueTooth extends ActionBarActivity {
    /*variables*/
    private BluetoothAdapter mBluetoothAdapter =null;
    private ArrayAdapter<String> mArrayAdapter;
    private ListView mBTListView;

    private final static int REQUEST_ENABLE_BT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);

        BlueToothProc();
/*
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        //
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
				
      */
    }
    /*Search bluetooth devices operation*/
    public void BlueToothProc()
    {
        //Open bluetooth;
        mBluetoothAdapter = mBluetoothAdapter.getDefaultAdapter();
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mBTListView = (ListView) findViewById(R.id.BTDeviceList);
//        mArrayAdapter.add("hello!");
        mBTListView.setAdapter(mArrayAdapter);

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

        Button but1 = (Button)findViewById(R.id.start);
        but1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bStartScan = mBluetoothAdapter.startDiscovery();
                if(bStartScan == true){
                    //Toast.makeText(this, "开始扫描设备！", Toast.LENGTH_SHORT).show();
                }
                // Create a BroadcastReceiver for ACTION_FOUND
                final BroadcastReceiver mReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        // When discovery finds a device
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                            // Get the BluetoothDevice object from the Intent
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            // Add the name and address to an array adapter to show in a ListView
                            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        }
                    }
                };
                // Register the BroadcastReceiver
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
            }
        });
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

    }


}
