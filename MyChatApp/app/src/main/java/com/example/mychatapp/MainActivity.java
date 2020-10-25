package com.example.mychatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.*;

import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    public static String ipv4 ;
    public static Socket socket;
    public boolean isConnectionTrue=false;
    public static String userName;

    Config cfg = new Config();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        joinProcess();
    }

    public void joinProcess(){
        Button buttonJoin = (Button)findViewById(R.id.buttonJoin);
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinServer();

            }
        });
    }

    public void joinServer(){
        TextView textUsername= (TextView)findViewById(R.id.textUsername);
        userName= textUsername.getText().toString();


            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        isConnectionTrue=false;
                        socket = new Socket(cfg.ip,Integer.parseInt(cfg.port));

                        isConnectionTrue=true;
                    }catch (Exception e){
                        e.printStackTrace();
                        isConnectionTrue=false;
                    }
                }
            });
            thread.start();
            thread.interrupt();

            try{
                Thread.sleep(300);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(isConnectionTrue){
                Intent intent = new Intent(this,ChatActivity.class);
                startActivity(intent);
                Toast.makeText( MainActivity.this,"Let's join!",Toast.LENGTH_LONG).show();
            }


    }


}