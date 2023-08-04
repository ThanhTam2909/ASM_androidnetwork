package com.example.myapplicationclient;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivityClient extends AppCompatActivity {

    EditText edt_serverHost, edt_serverport;
    Button btn_connect;

    private Socket socket;
    private String serverName;
    private int serverPort;

    private BufferedReader br_input;
    private PrintWriter output;
    private boolean connected = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_client);
        edt_serverHost = (EditText) findViewById(R.id.nhap_serverHost);
        edt_serverport = (EditText) findViewById(R.id.nhap_serverPort);
        btn_connect = (Button) findViewById(R.id.button_connect);

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!connected) {
                    serverName = edt_serverHost.getText().toString();
                    serverPort = Integer.valueOf(edt_serverport.getText().toString());
                    onClickClient(serverName, serverPort);
                }else {
                    disconnectServer();
                }
            }
        });
    }

    public void onClickClient(String serverName, int serverPort){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(serverName, serverPort);
                    br_input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    output = new PrintWriter(socket.getOutputStream(), true);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connected = true;
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void disconnectServer(){
        if ( socket != null){
            try {
                socket.close();
                connected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}