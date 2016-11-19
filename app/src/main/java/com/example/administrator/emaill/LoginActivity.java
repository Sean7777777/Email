package com.example.administrator.emaill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * Created by Administrator on 2016/11/13.
 */

public class LoginActivity extends Activity{
    private Button btn_login,btn_back;
    private EditText edit_user,edit_pws;
    String buffer=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        btn_login= (Button) findViewById(R.id.btn_login);
        btn_back= (Button) findViewById(R.id.btn_back);
        edit_user= (EditText) findViewById(R.id.edit_user);
        edit_pws= (EditText) findViewById(R.id.edit_pws);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sender = edit_user.getText().toString();
                        String password = edit_pws.getText().toString();
                        final String user = Base64.encodeToString(sender.substring(0, sender.indexOf("@")).getBytes(), Base64.NO_WRAP);
                        Log.i("user", "*******" + sender.substring(0, sender.indexOf("@")) + "*******");
                        final String pass = Base64.encodeToString(password.getBytes(), Base64.NO_WRAP);
                        Log.i("user", "*******" + password + "*******");
                        try {
                            Socket socket = new Socket("smtp.163.com", 25);
                            InputStream inputStream = socket.getInputStream();
                            OutputStream outputStream = socket.getOutputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            PrintWriter writter = new PrintWriter(outputStream, true);
                            buffer = reader.readLine();
                            Log.i("str", "*******" + buffer + "*******");
                            //与服务器握手
                            writter.println("HELO smtp.163.com");
                            buffer = reader.readLine();
                            Log.i("str", "*******" + buffer + "*******");
                            //请求登录
                            writter.println("auth login");
                            buffer = reader.readLine();
                            Log.i("str", "*******" + buffer + "*******");
                            writter.println(user);   //用户名
                            buffer = reader.readLine();
                            Log.i("str", "*******" + buffer + "*******");
                            writter.println(pass);   //密码
                            buffer = reader.readLine();
                            Log.i("str", "*******" + buffer + "*******");
                            if(buffer.contains("235 Authentication successful")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent=new Intent(LoginActivity.this,UserActivity.class);
                                        intent.putExtra("user_name",edit_user.getText().toString());
                                        intent.putExtra("password",edit_pws.getText().toString());
                                        startActivity(intent);
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this,"用户名与密码不匹配！！！",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,MailActivity.class);
                startActivity(intent);
            }
        });
    }
}
