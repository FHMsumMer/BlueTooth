package com.fhm.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private boolean isConnection = false;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public static BluetoothSocket bluetoothSocket = null;
    private static final UUID uuid = UUID
            .fromString("00001106-0000-1000-8000-00805F9B34FB");
    public BluetoothDevice device;
    private static InputStream inputStream;
    private static OutputStream outputStream;
    private TextView tv_chat;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initData();
    }

    private void initView() {
        tv_chat = (TextView) findViewById(R.id.tv_chat);
    }

    private void initData() {
        device = bluetoothAdapter.getRemoteDevice(getDeviceAddress());
        // 一上来就先连接蓝牙设备
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = connect();
                if (flag == false) {
                    // 连接失败

                } else {
                    // 连接成功
                    read();
                }
            }
        }).start();
    }

    /**
     * 获得从上一个Activity传来的蓝牙地址
     *
     * @return String
     */
    private String getDeviceAddress() {
        // 直接通过Context类的getIntent()即可获取Intent
        Intent intent = this.getIntent();
        return intent.getStringExtra("deviceAddress");
    }

    /**
     * 连接蓝牙设备
     */

    public boolean connect() {
        if (!isConnection) {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
                bluetoothSocket.connect();
                isConnection = true;
                if (bluetoothAdapter.isDiscovering()) {
                    System.out.println("关闭适配器!");
                    bluetoothAdapter.isDiscovering();
                }
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "连接失败!", Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChatActivity.this, device.getName() + "连接成功!",
                            Toast.LENGTH_SHORT).show();
                }
            });

            return true;
        } else {
            return true;
        }
    }


    @Override
    protected void onDestroy() {
        System.out.println("断开蓝牙设备连接");
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * 接收数据!
     */
    private void read() {
        if (isConnection) {
            try {
                inputStream = bluetoothSocket.getInputStream();
                outputStream = bluetoothSocket.getOutputStream();

                byte[] buffer = new byte[1024];
                byte[] buffer2 = new byte[1024];
                //这里会一直等待读取
                while (true) {
                    int bytes;
                    do {
                        bytes = this.inputStream.read(buffer);
                    } while (bytes <= 0);
                    for (int i = 0; i < buffer.length; ++i) {
                        buffer[i] = 0;
                    }
                    final String ceshishujv = new String(buffer2, 0, bytes);
                    System.out.println("ceshishujv......" + ceshishujv);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_chat.setText(ceshishujv);
                        }
                    });
                }
            } catch (IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "连接断开了，请重新连接", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }
    }
}









