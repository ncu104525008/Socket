package com.example.sango.sockettest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private Button serverBtn;
    private Button clientBtn;
    private EditText ipAddr;
    private Socket socket;
    public static final String file_name = FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/1.jpg";
    public static final String server_file_name = FileUtil.getExternalStorageDir(FileUtil.APP_DIR) + "/server.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serverBtn = (Button) findViewById(R.id.button);
        clientBtn = (Button) findViewById(R.id.button2);
        ipAddr = (EditText) findViewById(R.id.editText);

        serverBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread tests = new Thread(serverSocket);
                tests.start();
            }
        });

        clientBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread test = new Thread(clientSocket);
                test.start();
            }
        });
    }

    Runnable clientSocket = new Runnable() {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(ipAddr.getText().toString());
                Log.e("Socket", "Client: Connecting...");

                socket = new Socket(serverAddr, 1111);
                OutputStream outputStream = socket.getOutputStream();

                File myFile = new File(file_name);
                if(myFile.exists()) {
                    byte[] mybytearray = new byte[(int) myFile.length()];
                    FileInputStream fis = new FileInputStream(myFile);

                    BufferedInputStream bis = new BufferedInputStream(fis, 8*1024);
                    bis.read(mybytearray, 0, mybytearray.length);
                    outputStream.write(mybytearray, 0, mybytearray.length);
                    outputStream.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable serverSocket = new Runnable() {
        @Override
        public void run() {
            try {
                Log.w("Server: ", "Connecting...");
                ServerSocket myServer = new ServerSocket(1111);
                while (true) {
                    Socket client = myServer.accept();
                    Log.w("Server: ", "Receiving...");

                    OutputStream out = new FileOutputStream(server_file_name);
                    byte buff[] = new byte[1024];
                    int len;

                    InputStream inputStream = client.getInputStream();
                    try {
                        while((len = inputStream.read(buff)) != -1) {
                            out.write(buff, 0, len);
                        }
                        out.close();
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        client.close();
                        Log.w("Server: ", "Done.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
