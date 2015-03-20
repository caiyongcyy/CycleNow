package smallf.CycleNow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Message;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.EditText;

import java.lang.String;

/**
 * Created by Administrator on 2015/3/19.
 */
public class LoginView extends ActionBarActivity {

    EditText edittext_user;
    EditText edittext_password;
    Button btn_register;
    Button btn_login;
    ProgressBar progressbar_login;
    TextView textview_login_state;

    ThreadLogin loginthread;

    int nProgressMax = 100;
    int nThresholdVerify = nProgressMax/3;
    int nThresholdLoad = nProgressMax/3*2;
    int nThresholdOver = nProgressMax-1;

    boolean bTerminateFlag = false; //置为true可停止登陆UI刷新


    int Refressh_UI_Value = 1;
    int Terminate_Progress = 2;
    int Login_Success = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);

        edittext_user = (EditText)findViewById(R.id.login_edittext_user);
        edittext_password = (EditText)findViewById(R.id.login_edittext_password);
        btn_register = (Button)findViewById(R.id.login_btn_register);
        btn_login = (Button)findViewById(R.id.login_btn_login);
        progressbar_login = (ProgressBar)findViewById(R.id.login_progressBar_login);
        textview_login_state = (TextView)findViewById(R.id.login_textview_loginstate);

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
                loginthread = new ThreadLogin();
                loginthread.start();

                //添加用户验证
            }
        });
    }

    class ThreadLogin extends Thread {
        @Override
        public void run() {
            int nProgress = 0;
            while((!bTerminateFlag) && (nProgress < nProgressMax)) {
                Message msg = new Message();
                msg.what = Refressh_UI_Value;
                msg.arg1 = nProgress;
                LoginView.this.myhandler.sendMessage(msg);
                nProgress++;
                SystemClock.sleep(15);
            }
            Message msg = new Message();
            msg.what = Terminate_Progress;
            LoginView.this.myhandler.sendMessage(msg);
        }
    }

    Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == Refressh_UI_Value){
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
            else if (msg.what == Terminate_Progress){
                progressbar_login.setVisibility(View.GONE);
                textview_login_state.setVisibility(View.GONE);
                edittext_user.setEnabled(true);
                edittext_password.setEnabled(true);
                btn_login.setEnabled(true);
            }
            else if(msg.what == Login_Success){
            /*        Intent in = new Intent();
                    in.setClassName( getApplicationContext(), "smallf.CycleNow.MainActivity");
                    startActivity( in );*/
            }

            super.handleMessage(msg);
        }
    };
}
