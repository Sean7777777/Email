package com.example.administrator.emaill;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MailActivity extends AppCompatActivity{

    private ImageButton btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        btn1= (ImageButton) findViewById(R.id.image_163);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MailActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
