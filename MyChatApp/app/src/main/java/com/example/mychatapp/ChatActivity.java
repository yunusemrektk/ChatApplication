package com.example.mychatapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.*;

public class ChatActivity extends AppCompatActivity {
    public static String messagetoServer;
    public static String messageFromServer;
    public static boolean isThreadFinished = false;
    public static JsonArray clientInfos = new JsonArray();
    MainActivity ma = new MainActivity();
    Config cfg = new Config();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatProcess();
        getMessageFromServer();
    }

    public void getMessageFromServer() {

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        final TextView getMessage = new TextView(ChatActivity.this);
                        InputStreamReader isr = new InputStreamReader(ma.socket.getInputStream());
                        BufferedReader br = new BufferedReader(isr);

                        messageFromServer = br.readLine();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JsonArray jsonArrayReader = JsonParser.parseString(messageFromServer).getAsJsonArray();
                                JsonObject objectReader = JsonParser.parseString(jsonArrayReader.get(0).toString()).getAsJsonObject();

                                String messageTime= objectReader.get("Time").getAsString();
                                String userName=objectReader.get("UserName").getAsString();
                                String messageJso = objectReader.get("Message").getAsString();
                                String messageBody = userName+ "\r\n"+ messageJso+"\r\n"+messageTime;

                                LinearLayout convLayout= (LinearLayout) findViewById(R.id.messageLayout);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.gravity = Gravity.LEFT;
                                lp.setMargins(16,16,0,0);
                                getMessage.setLayoutParams(lp);
                                getMessage.setText(messageBody);
                                getMessage.setTextSize(16);
                                getMessage.setBackground(getBaseContext().getResources().getDrawable(R.drawable.background_left));
                                getMessage.setPadding(16,16,16,16);

                                convLayout.addView(getMessage);

                            }
                        });

                        try{
                            Thread.sleep(300);
                        }catch (Exception e){
                            
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        thread.start();


    }

    public void chatProcess(){
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                sendMessagetoServer();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessagetoServer(){

        TextView textMessage = (TextView) findViewById(R.id.textMessagefromLoad);
        messagetoServer = textMessage.getText().toString();

        LocalTime time = LocalTime.now();
        String timePattern = time.format(DateTimeFormatter.ofPattern("HH:mm"));

        String messageBody = messagetoServer +"\r\n"+timePattern;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("UserName",ma.userName);
        jsonObject.addProperty("Message",messagetoServer);
        jsonObject.addProperty("Time",timePattern);
        clientInfos.add(jsonObject);

        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    isThreadFinished = false;
                    PrintWriter writer = new PrintWriter(ma.socket.getOutputStream());
                    writer.println(clientInfos);
                    writer.flush();
                    isThreadFinished = true;

                }catch (Exception e){

                }
            }
        });
        thread.start();
        thread.interrupt();

        try{
            Thread.sleep(300);
        }catch (Exception e){

        }
        if(!textMessage.getText().toString().contentEquals("")){
            LinearLayout convLayout =  (LinearLayout) findViewById(R.id.messageLayout);
            TextView sendMessage = new TextView(this);

            if(convLayout.getParent()!=null){
                convLayout.removeView(sendMessage);
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.RIGHT;
            lp.setMargins(0,16,16,0);
            sendMessage.setLayoutParams(lp);
            sendMessage.setText(messageBody);
            sendMessage.setTextSize(16);
            sendMessage.setBackgroundResource(R.drawable.background_right);
            sendMessage.setPaddingRelative(16,16,16,16);

            convLayout.addView(sendMessage);
        }

        clientInfos = new JsonArray();
        jsonObject = new JsonObject();
        textMessage.setText("");

        if(isThreadFinished){
            return;
        }
    }
}