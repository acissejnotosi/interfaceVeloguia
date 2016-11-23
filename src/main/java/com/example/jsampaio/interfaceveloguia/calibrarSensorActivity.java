package com.example.jsampaio.interfaceveloguia;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class calibrarSensorActivity extends AppCompatActivity{


    MapsCorridaLivreActivity.ConnectedThread mConnectedThread;
    public Handler mHandler; // Our main handler that will receive callback notifications
    public BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    MapsCorridaLivreActivity corrida;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrar_sensor);




    }






}
