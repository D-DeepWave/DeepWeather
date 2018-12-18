package xyz.deepwave.DeepWeather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import xyz.deepwave.DeepWeather.WeatherActivity;

public class UserLoginActivity extends AppCompatActivity {
    private final int HANDLER_MSG_TELL_RECV = 0xff;
    private ImageView userHead;
    private EditText inputUser;
    private EditText inputPassword;
    private AppCompatButton btnLogin;
    private TextView linkSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        initView();

    }

    public void initView() {
        inputUser = findViewById(R.id.input_user);
        inputPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);
        linkSignup = findViewById(R.id.link_signup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(validate())
                startNetThread("45.77.9.232",9999,"0 "+inputUser.getText().toString()+" "+inputPassword.getText().toString());

                Log.d("QQQQQ","0 "+inputUser.getText().toString()+" "+inputPassword.getText().toString());
            }
        });
    }

    public void startNetThread(final String host, final int port, final String data) {
        Log.d("QQQQQ","enter");
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Log.d("QQQQQ","start connect!");
                        Socket socket = new Socket(host,port);
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write((data).getBytes());
                        outputStream.flush();
                       InputStream is = socket.getInputStream();
                       byte[] bytes = new byte[1024];
                       int n = is.read(bytes);

                      Message msg = handler.obtainMessage(HANDLER_MSG_TELL_RECV, new String(bytes, 0, n));
                    msg.sendToTarget();
                    is.close();
                    socket.close();

                } catch (Exception e) {
                    Log.d("QQQQ",e.toString());
                }
            }
        };
        thread.start();
    }

     @SuppressLint("HandlerLeak")
     private  Handler handler = new Handler() {
         public void handleMessage(Message msg) {
             if (msg.what == HANDLER_MSG_TELL_RECV) {
                 if(msg.obj.toString().equals("1")) {
                     Intent intent = new Intent(UserLoginActivity.this, WeatherActivity.class);
                     startActivity(intent);
                 }
                 else
                 Toast.makeText(UserLoginActivity.this, "登录失败！", Toast.LENGTH_LONG).show();
             }
         }
     };

    public boolean validate() {
        boolean valid = true;

        String user = inputUser.getText().toString();
        String password = inputPassword.getText().toString();

        if (user.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            inputPassword.setError("enter a valid email address");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }
}
