package smallf.CycleNow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.Message;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.EditText;
import android.content.ContentValues;
import android.database.Cursor;
import java.lang.String;

/**
 * Created by Administrator on 2015/3/19.
 */



public class LoginView extends Activity {


    EditText edittext_user;
    EditText edittext_password;
    Button btn_register;
    Button btn_login;
    ProgressBar progressbar_login;
    TextView textview_login_state;

    UIThread loginthread;
    VerifyThread thread_verify;
    NetComunication netlink;

    //NetComunication linkserver;

    int nProgressMax = 100;
    int nThresholdVerify = nProgressMax/3;
    int nThresholdLoad = nProgressMax/3*2;
    int nThresholdOver = nProgressMax-1;

    boolean bTerminateFlag = false; //置为true可停止登陆UI刷新
    boolean bVerifySuccess = false;

    /*msg type*/
    int UI_REFRESH = 0;
    int LOGIN_VERIFY = 1;
    int LOGIN_RESULT = 2;

    /*login verify result*/
    int LOGIN_VERIFY_SUCCESS = 0;
    int LOGIN_VERIFY_FAIL = 1;

    /*login result*/
    int LOGIN_RESULT_SUCCESS = 0;
    int LOGIN_RESULT_FAIL = 1;

    DBHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loginview);
    //    getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.customtitle);

        edittext_user = (EditText)findViewById(R.id.login_edittext_user);
        edittext_password = (EditText)findViewById(R.id.login_edittext_password);
        btn_register = (Button)findViewById(R.id.login_btn_register);
        btn_login = (Button)findViewById(R.id.login_btn_login);
        progressbar_login = (ProgressBar)findViewById(R.id.login_progressBar_login);
        textview_login_state = (TextView)findViewById(R.id.login_textview_loginstate);
        netlink = new NetComunication(myhandler);


        /*开发过程写入nama，password方便登陆，后续删除*/
        edittext_user.setText("李四");
        edittext_password.setText("123");
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String struser = edittext_user.getText().toString();
                String strpassword = edittext_password.getText().toString();
                if(struser.isEmpty() || strpassword.isEmpty()){
                    textview_login_state.setText(R.string.str_login_edittext_null);
                    textview_login_state.setTextColor(Color.rgb(255,0,0));
                    textview_login_state.setVisibility(View.VISIBLE);
                    return;
                }
                edittext_user.setEnabled(false);
                edittext_password.setEnabled(false);
                btn_login.setEnabled(false);

                textview_login_state.setTextColor(Color.rgb(0,0,0));
                textview_login_state.setText(R.string.str_login_state_network);
                progressbar_login.setVisibility(View.VISIBLE);
                textview_login_state.setVisibility(View.VISIBLE);

                bTerminateFlag = false;
                bVerifySuccess = false;
                loginthread = new UIThread();
                loginthread.start();
                //NetComunication.Instance().start();
                new VerifyThread().start();


                //添加用户验证
                //DBHelper helper = new DBHelper(getApplicationContext());


            }
        });

        //InitSQLValue();

    }

    /*private void InitSQLValue(){
        String name = "李四";
        String desc = "123";
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("desc", desc);
        helper = new DBHelper(getApplicationContext());
        helper.insert(values);
    }*/

    private Boolean VerifyUser(String username, String password){
        Cursor c = helper.query();
        if(c.moveToFirst() == false){
            return true;
        }

        int nNameIndex = c.getColumnIndex("name");
        int nPasswordIndex = c.getColumnIndex("desc");

        while(c.moveToNext()){
            if(c.getString(nNameIndex).equals(username) && c.getString(nPasswordIndex).equals(password)){
                helper.close();
                return true;
            }
        }
        helper.close();
        return false;
    }

    String str = "abc";
    class VerifyThread extends Thread{
        @Override
        public void run(){
            netlink.InitSocket();
            netlink.start();
            String name = edittext_user.getText().toString();
            String password = edittext_password.getText().toString();
            String msg = "login" + ":" + name + ":" + password;
            netlink.SendMessage(msg);
            SystemClock.sleep(2000);
            /*登陆验证超时*/
            if(!bVerifySuccess){
                bTerminateFlag = true;
            }
            netlink.CloseThread();

        }
    }

    class UIThread extends Thread {
        @Override
        public void run() {
            int nProgress = 0;
            while((!bTerminateFlag) && (nProgress < nProgressMax)) {
                Message msg = new Message();
                msg.what = UI_REFRESH;
                msg.arg1 = nProgress;
                LoginView.this.myhandler.sendMessage(msg);
                nProgress++;
                if(nProgress == nThresholdVerify && bVerifySuccess == false){
                    nProgress--;
                }
                SystemClock.sleep(15);
            }
            Message msg = new Message();
            if(bTerminateFlag){
                msg.what = LOGIN_RESULT;
                msg.arg1 = LOGIN_RESULT_FAIL;
            }
            else {
                msg.what = LOGIN_RESULT;
                msg.arg1 = LOGIN_RESULT_SUCCESS;
            }
            LoginView.this.myhandler.sendMessage(msg);
        }
    }

    Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == UI_REFRESH){
                if(msg.arg1 == nThresholdVerify){
                    textview_login_state.setText(R.string.str_login_state_verify);
                }
                else if(msg.arg1 == nThresholdLoad){
                    textview_login_state.setText(R.string.str_login_state_download);
                }
                else if(msg.arg1 == nThresholdOver){
                    textview_login_state.setText(R.string.str_login_state_ok);
                }
                progressbar_login.setProgress(msg.arg1);
            }
            else if (msg.what == LOGIN_VERIFY){
                if(msg.arg1 == LOGIN_VERIFY_FAIL){
                    bTerminateFlag = true;
                }
                else if(msg.arg1 == LOGIN_VERIFY_SUCCESS){
                    bVerifySuccess = true;
                }
            }
            else if (msg.what == LOGIN_RESULT) {
                if(msg.arg1 == LOGIN_RESULT_FAIL){
                    progressbar_login.setVisibility(View.GONE);
                    edittext_user.setEnabled(true);
                    edittext_password.setEnabled(true);
                    btn_login.setEnabled(true);

                    textview_login_state.setTextColor(Color.rgb(255,0,0));
                    textview_login_state.setVisibility(View.VISIBLE);
                    textview_login_state.setText(R.string.str_logoin_fail);
                }
                else if(msg.arg1 == LOGIN_RESULT_SUCCESS){
                    progressbar_login.setVisibility(View.GONE);
                    textview_login_state.setVisibility(View.GONE);
                    edittext_user.setEnabled(true);
                    edittext_password.setEnabled(true);
                    btn_login.setEnabled(true);

                    Intent in = new Intent();
                    in.setClassName(getApplicationContext(), "smallf.CycleNow.MainFrameView");
                    startActivity(in);
                    LoginView.this.finish();
                }
            }

            super.handleMessage(msg);
        }
    };
}
