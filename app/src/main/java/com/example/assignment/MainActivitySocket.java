package com.example.assignment;

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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivitySocket extends AppCompatActivity {

    TextView status, ip, port;
    Button btn_start, btn_stop;

    private String serverHost = " 192.168.1.22";
    private  int serverPort = 9999;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_socket);

        status = (TextView) findViewById(R.id.status_server);
        ip = (TextView) findViewById(R.id.tv_IP);
        port = (TextView) findViewById(R.id.tv_Port);
        btn_start = (Button) findViewById(R.id.button_start);
        btn_stop = (Button) findViewById(R.id.button_stop);
    }

    private ServerThread serverThread;

    public void onClickStartServer(View view){
        ip.setText(serverHost);
        port.setText(serverPort);
        btn_start.setEnabled(false);
        btn_stop.setEnabled(true);
        serverThread = new ServerThread();
        serverThread.startServer();
    }

    public void onClickStopServer(View view){
        btn_start.setEnabled(true);
        btn_stop.setEnabled(false);
        serverThread = new ServerThread();
        if (serverThread == null) {
            serverThread.stopServer();
            status.setText("Stop Server");
        }
    }

    class ServerThread extends Thread implements Runnable{
        private boolean serverRunning;
        private ServerSocket serverSocket;

        public void startServer(){
            serverRunning = true;
            start();
        }

        public void stopServer(){
            serverRunning = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (serverSocket != null) {
                        try {
                            serverSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                status.setText("Stop Serverr");
                            }
                        });
                    }
                }
            });
        }

        private ArrayList<Client> clients = new ArrayList<>();

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(serverPort);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("Waiting for Client");
                    }
                });

                while(serverRunning){
                    Socket socket = serverSocket.accept();

                    Client client = new Client(socket);
                    client.start();
                    clients.add(client);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("Kết nối thành công!");
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        class Client extends Thread{
            private Socket clientSocket;
            private BufferedReader br;
            private PrintWriter output;
            public Client(Socket socket) {
                clientSocket = socket;
                try {
                    br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    output = new PrintWriter(clientSocket.getOutputStream(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

    }
}