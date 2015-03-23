package smallf.CycleNow;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    //定义变量
    static final int CMD_SEND_DATA = 0x02;
    static final int CMD_RUN = 0x04;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //得到按钮实例
        Button button_start = (Button)findViewById(R.id.button_start);
        Button button_bt = (Button)findViewById(R.id.button_bt);
        //设置监听按钮点击事件

        button_start.setOnClickListener(new View.OnClickListener() {
            boolean pressed = false;
            @Override
            public void onClick(View v) {

                if(pressed == false) {
                    //得到textview实例
                    TextView hellotv = (TextView) findViewById(R.id.textView);
                    //读取strings.xml定义的interact_message信息并写到textview上
                    hellotv.setText("Started!!");

                    SendCmdRun();
                    pressed = true;
                }
                else {
                    TextView hellotv = (TextView) findViewById(R.id.textView);
                    //弹出Toast提示按钮被点击了
                    //Toast.makeText(MainActivity.this, "unClicked", Toast.LENGTH_SHORT).show();
                    //读取strings.xml定义的interact_message信息并写到textview上
                    hellotv.setText("路线");
/*                    Button hcchu = (Button) findViewById(R.id.hcchu);
                    hcchu.setText("NEW BUTTON");*/
                    pressed = false;
                }
            }
        });

        button_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,BlueToothActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
