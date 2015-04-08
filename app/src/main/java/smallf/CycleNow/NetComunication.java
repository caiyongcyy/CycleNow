package smallf.CycleNow;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.Message;
import android.os.Handler;

/**
 * Created by Administrator on 2015/4/1.
 */
public class NetComunication extends Thread{
    private String ServerIP = "192.168.1.100";
    private int ServerPort = 7890;
    private Socket socket;
    PrintWriter  output;
    BufferedReader input;

    public static final String bm="gbk";

    Handler myhandler;

    Boolean bclose = false;

    /*msg type*/
    int UI_REFRESH = 0;
    int LOGIN_VERIFY = 1;

    /*Login_verify result*/
    int LOGIN_VERIFY_SUCCESS = 0;
    int LOGIN_VERIFY_FAIL = 1;

    public NetComunication(Handler handler){
        myhandler = handler;
    //    InitSocket();
    }


    public void InitSocket(){
        try{
            socket = new Socket(ServerIP, ServerPort);
            output = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), bm)),
                    true);
            //input = new InputStreamReader(socket.getInputStream());
            //input = socket.getInputStream();
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (UnknownHostException e)
        {
            //handleException(e, "unknown host exception: " + e.toString());
        }
        catch (IOException e)
        {
            //handleException(e, "io exception: " + e.toString());
        }
    }

    @Override
    public void run(){
        String line;
        String verify_success = "verify success";
        String verify_fail = "verify fail";
        while(!bclose){
            try
            {
                line = "";
                if (socket != null && input != null) {
                    while((line = input.readLine()) != null) {
                        if (!(line.isEmpty())) {
                            Message msg = new Message();
                            if (line.equals(verify_success)) {
                                msg.what = LOGIN_VERIFY;
                                msg.arg1 = LOGIN_VERIFY_SUCCESS;
                            } else if (line.equals(verify_fail)) {
                                msg.what = LOGIN_VERIFY;
                                msg.arg1 = LOGIN_VERIFY_FAIL;
                            }
                            myhandler.sendMessage(msg);
                            input.close();
                            socket.close();
                            socket = null;
                        }
                    }

                }

            }
            catch (UnknownHostException e)
            {
                //handleException(e, "unknown host exception: " + e.toString());
            }
            catch (IOException e)
            {
                //handleException(e, "io exception: " + e.toString());
            }
        }
    }

    public void CloseThread(){
        bclose = true;

    }

    public void SendMessage(String msg){
        output.println(msg);
    }
}
