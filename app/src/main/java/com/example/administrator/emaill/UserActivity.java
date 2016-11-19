package com.example.administrator.emaill;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import Decoder.BASE64Encoder;

/**
 * Created by Administrator on 2016/11/13.
 */

public class UserActivity extends Activity{
    private Button btn_send;
    private TextView textView;
    String buffer=null;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user);
        btn_send= (Button) findViewById(R.id.btn_send);
        textView= (TextView) findViewById(R.id.message);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout send= (LinearLayout) getLayoutInflater().inflate(R.layout.layout_send,null);
                final EditText text_receive= (EditText) send.findViewById(R.id.edit_receive);
                final EditText text_subject= (EditText) send.findViewById(R.id.edit_subject);
                final EditText text_message= (EditText) send.findViewById(R.id.edit_message);
                Button btn_ok= (Button) send.findViewById(R.id.btn_ok);
                AlertDialog dialog=new AlertDialog.Builder(UserActivity.this).create();
                dialog.setView(send);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(){
                            @Override
                            public void run() {
                                Intent intent=getIntent();
                                String sender = intent.getStringExtra("user_name");
                                String password = intent.getStringExtra("password");
                                String user = new BASE64Encoder().encode(sender.substring(0, sender.indexOf("@")).getBytes());
                                String pass = new BASE64Encoder().encode(password.getBytes());
                                String receiver = text_receive.getText().toString();
                                try {
                                    @SuppressWarnings("resource")
                                    Socket socket = new Socket("smtp.163.com", 25);
                                    InputStream inputStream = socket.getInputStream();
                                    OutputStream outputStream = socket.getOutputStream();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                    PrintWriter writter = new PrintWriter(outputStream, true);  //我TM去 这个true太关键了!
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
                                    writter.println("mail from:<" + sender +">");//发送者邮箱
                                    buffer = reader.readLine();
                                    Log.i("str", "*******" + buffer + "*******");
                                    writter.println("rcpt to:<" + receiver +">");   //接收者邮箱
                                    buffer = reader.readLine();
                                    Log.i("str", "*******" + buffer + "*******");
                                    //设置发送的信息
                                    writter.println("data");
                                    buffer = reader.readLine();
                                    Log.i("str", "*******" + buffer + "*******");
                                    writter.println("subject:"+text_subject.getText().toString());  //主题
                                    writter.println("from:" + sender);
                                    writter.println("to:" + receiver);
                                    writter.println("Content-Type: text/plain;charset=\"utf-8\"");
                                    writter.println();
                                    writter.println(text_message.getText().toString());
                                    writter.println(".");
                                    writter.println("");
                                    buffer = reader.readLine();
                                    Log.i("str", "*******" + buffer + "*******");
                                    if(buffer.contains("250 Mail OK")){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(UserActivity.this,"邮件发送成功！",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else{
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(UserActivity.this,"邮件发送失败！",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    //退出链接状态
                                    writter.println("rset");
                                    buffer = reader.readLine();
                                    Log.i("str", "*******" + buffer + "*******");
                                    writter.println("quit");
                                    buffer = reader.readLine();
                                    Log.i("str", "*******" + buffer + "*******");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                });
                dialog.show();

            }
        });
//        new Thread(){
//            @Override
//            public void run() {
//                Intent intent=getIntent();
//                String sender = intent.getStringExtra("user_name");
//                String password = intent.getStringExtra("password");
//                String user = new BASE64Encoder().encode(sender.substring(0, sender.indexOf("@")).getBytes());
//                String pass = new BASE64Encoder().encode(password.getBytes());
//                try {
//                    Socket client = new Socket("pop3.163.com",110);
//                    InputStream is = client.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
//                    // 创建一个PrintWriter对象，以便向套接字写入内容。
//                    OutputStream os = client.getOutputStream();
//                    PrintWriter writer = new PrintWriter(os, true);
//                    //System.out.println("S:" + bufferedReader.readLine());
//                    writer.println("user " + user);
//                    //System.out.println("S:" + bufferedReader.readLine());
//                    writer.println("pass " + pass);
//                    //System.out.println("S:" + bufferedReader.readLine());
//                    writer.println("stat");
//                    String[] temp=bufferedReader.readLine().split("");
//                    int count = Integer.parseInt(temp[1]);
//                    for (int i = 1; i < count + 1; i++) {//依次打印出邮件的内容
//                        writer.println("retr " + i);
//                        while (true) {
//                            String reply = bufferedReader.readLine();
//                            textView.setText(reply+"\n");
//                            if (reply.toLowerCase().equals(".")) {
//                                break;
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }
}
