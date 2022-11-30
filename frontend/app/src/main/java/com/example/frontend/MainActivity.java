package com.example.frontend;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {
    Button btn_setarea, btn_rqst;
    AlertDialog.Builder builder;
    String[] areas;
    public String selected_area;
    TextView areaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        areaView = (TextView) findViewById(R.id.slc_area);
        selected_area = areaView.getText().toString().substring(12,13);


        btn_rqst = findViewById(R.id.detect_request_button);
        btn_rqst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1){
                ClientThread thread = new ClientThread();
                thread.start();
                TextView detecting_text = (TextView) findViewById(R.id.detect_text);
                detecting_text.setText("탐지 중..");
                Intent pu = new Intent(MainActivity.this, PopupActivity.class);
                startActivity(pu);
            }
        });
        btn_setarea = findViewById(R.id.set_area_button);
        btn_setarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v2) {
                showDialog();
            }
        });

    }
    class ClientThread extends Thread{
        @Override
        public void run(){
            String host = "localhost";
            int port = 8000;
            try {
                Socket socket = new Socket(host, port);

                ObjectOutputStream outstream = new ObjectOutputStream(socket.getOutputStream());
                outstream.writeObject("탐지 요청");
                outstream.flush();

                ObjectInputStream instream = new ObjectInputStream(socket.getInputStream());
                Object input = instream.readObject();

                Intent intent = new Intent(MainActivity.this, PopupActivity.class);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showDialog(){
        areas = getResources().getStringArray(R.array.area);
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("근무 지역을 선택하세요");
        builder.setItems(areas, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(getApplicationContext(), "요원님의 구역이 "+areas[which]+"로 설정되었습니다.", Toast.LENGTH_SHORT).show();
            selected_area = areas[which];
            areaView.setText("현재 설정된 근무지 : "+selected_area);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
