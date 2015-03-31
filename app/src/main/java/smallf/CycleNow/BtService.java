package smallf.CycleNow;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by EmyWong on 2015/3/22.
 */
public class BtService extends Service{

    CommandReceiver cmdReceiver;//继承自BroadcastReceiver对象，用于得到Activity发送过来的命令
    /**************service 命令*********/
    static final int CMD_STOP_SERVICE = 0x01;
    static final int CMD_SEND_DATA = 0x02;
    static final int CMD_INIT =0x03;
    static final int CMD_RUN = 0x04;

    private BluetoothAdapter mBluetoothAdapter = null;
    private OutputStream outStream = null;
    public  boolean bluetoothFlag  = true;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ClientThread mClientThread = null;
    public  SocketDataThread mBTDataMgr = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

    }

    //前台Activity调用startService时，该方法自动执行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        cmdReceiver = new CommandReceiver();
        IntentFilter filter = new IntentFilter();//创建IntentFilter对象
        //注册一个广播，用于接收Activity传送过来的命令，控制Service的行为，如：发送数据，停止服务等
        filter.addAction("android.intent.action.cmd");
        //注册Broadcast Receiver
        registerReceiver(cmdReceiver, filter);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.unregisterReceiver(cmdReceiver);//取消注册的CommandReceiver
        if(mClientThread != null)
        {
            mClientThread.cancel();
        }
        boolean retry = true;
    }

    //串口发送数据
    public void sendCmd(byte cmd, int value)
    {
        if(!bluetoothFlag){
            return;
        }
        byte[] msgBuffer = new byte[5];
        msgBuffer[0] = cmd;
        msgBuffer[1] = (byte)(value >> 0  & 0xff);
        msgBuffer[2] = (byte)(value >> 8  & 0xff);
        msgBuffer[3] = (byte)(value >> 16 & 0xff);
        msgBuffer[4] = (byte)(value >> 24 & 0xff);

        try {
            outStream.write(msgBuffer, 0, 5);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopService(){//停止服务
        stopSelf();//停止服务
    }

    //接收Activity传送过来的命令
    private class CommandReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.intent.action.cmd")){
                int cmd = intent.getIntExtra("cmd", -1);//获取Extra信息
                if(cmd == CMD_STOP_SERVICE){
                    stopService();
                }

                if(cmd == CMD_SEND_DATA)
                {
                    byte command = intent.getByteExtra("command", (byte) 0);
                    int value =  intent.getIntExtra("value", 0);
                    sendCmd(command,value);
                }

                if(cmd == CMD_RUN)
                {
                    if(mBTDataMgr!=null){
                        mBTDataMgr.run();
                    }
                }

                if(cmd == CMD_INIT)
                {
                    String sAdd = intent.getStringExtra("Address");
                    mClientThread = new ClientThread(sAdd);
                    mClientThread.run();
                }
            }
        }
    }

    public void DisplayToast(String str)
    {
        Log.i("BlueToothService：",str);
    }

    /*蓝牙客户端连接线程*/
    public class ClientThread extends Thread {
        private BluetoothSocket mmSocket;
        private  BluetoothDevice mmDevice =null;

        public ClientThread(String sAddress) {
            mBluetoothAdapter = mBluetoothAdapter.getDefaultAdapter();
            mmDevice = mBluetoothAdapter.getRemoteDevice(sAddress);
            try {
                // 连接建立之前的先配对
                if (mmDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Method creMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    creMethod.invoke(mmDevice);
                } else {
                }
            } catch (Exception e) {
                // TODO: handle exception
                DisplayToast("无法配对！");
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket tmp = null;
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try{
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
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
                    DisplayToast("Socket 连接失败！");
                } catch (IOException closeException) {
                }
                return;
            }
            DisplayToast("Socket 连接成功！");
            Log.d("mBtSocket", "end-->"+ mmSocket);

            mBTDataMgr = new SocketDataThread(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    //Managing a Connection
    public class SocketDataThread extends Thread {
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
