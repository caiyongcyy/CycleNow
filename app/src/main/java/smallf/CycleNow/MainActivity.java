package smallf.CycleNow;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;
//import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        //得到按钮实例
        Button but1 = (Button)findViewById(R.id.hcchu);
        Button but2 = (Button)findViewById(R.id.hcchu2);
        //设置监听按钮点击事件

            but1.setOnClickListener(new View.OnClickListener() {
                boolean pressed = false;
                @Override
                public void onClick(View v) {

                    if(pressed == false) {
                        //得到textview实例
                        TextView hellotv = (TextView) findViewById(R.id.textView);
                        //弹出Toast提示按钮被点击了
                        //Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        //读取strings.xml定义的interact_message信息并写到textview上
                        hellotv.setText(R.string.interact_message);
                        Button hcchu = (Button) findViewById(R.id.hcchu);
                        hcchu.setText(R.string.button_send);
                        pressed = true;
                        adapter.enable();
                    }
                    else {
                        TextView hellotv = (TextView) findViewById(R.id.textView);
                        //弹出Toast提示按钮被点击了
                        //Toast.makeText(MainActivity.this, "unClicked", Toast.LENGTH_SHORT).show();
                        //读取strings.xml定义的interact_message信息并写到textview上
                        hellotv.setText("路线");
                        Button hcchu = (Button) findViewById(R.id.hcchu);
                        hcchu.setText("NEW BUTTON");
                        pressed = false;
                        adapter.disable();
                    }
                }
            });


        but2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,BlueTooth.class);
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
}
