package com.example.frontend;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    Button btn_setarea, btn_rqst;
    AlertDialog.Builder builder;
    String[] areas;
    public String selected_area;
    TextView areaView;
    private Socket socket;
    String TAG = "socketTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "Application created");
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        final String ipNumber = "127.0.0.1";

        areaView = (TextView) findViewById(R.id.slc_area);
        selected_area = areaView.getText().toString().substring(12, 13);

        btn_rqst = findViewById(R.id.detect_request_button);
        btn_rqst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                Toast.makeText(getApplicationContext(), "Connet 시도", Toast.LENGTH_SHORT).show();
                String addr = ipNumber;
                ConnectThread thread = new ConnectThread(addr);
                thread.start();
                //StartThread sthread = new StartThread();
                //sthread.start();
                Intent intent = new Intent(MainActivity.this, PopupActivity.class);
                startActivity(intent);
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

    public void showDialog() {
        areas = getResources().getStringArray(R.array.area);
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("근무 지역을 선택하세요");
        builder.setItems(areas, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "요원님의 구역이 " + areas[which] + "로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                selected_area = areas[which];
                areaView.setText("현재 설정된 근무지 : " + selected_area);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    // socket data 송/수신
    class StartThread extends Thread {
        int bytes;
        String Dtmp;
        int dlen;

        public StartThread() {
        }
        public String byteArrayToHex(byte[] a){
            StringBuilder sb = new StringBuilder();
            for(final byte b: a)
                sb.append(String.format("%02x ", b&0xff));
            return sb.toString();
        }
        public void run() {
            // 데이터 송신
            try {
                String OutData = "AT+START\n";
                byte[] data = OutData.getBytes();
                OutputStream output = socket.getOutputStream();
                output.write(data);
                Log.d(TAG, "AT+START\\n COMMAND 송신");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"데이터 송신 오류");
            }
            // 데이터 수신
            try {
                Log.d(TAG, "데이터 수신 준비");
                //TODO:수신 데이터(프로토콜) 처리
                while (true) {
                    byte[] buffer = new byte[1024];
                    InputStream input = socket.getInputStream();
                    bytes = input.read(buffer);
                    Log.d(TAG, "byte = " + bytes);
                    //바이트 헥사(String)로 바꿔서 Dtmp String에 저장.
                    Dtmp = byteArrayToHex(buffer);
                    Dtmp = Dtmp.substring(0,bytes*3);
                    Log.d(TAG, Dtmp);
                    //프로토콜 나누기
                    String[] DSplit = Dtmp.split("a5 5a"); // sync(2byte) 0xA5, 0x5A
                    Dtmp = "";
                    for(int i=1;i<DSplit.length-1;i++){ // 제일 처음과 끝은 잘림. 데이터 버린다.
                        Dtmp = Dtmp + DSplit[i] + "\n";
                    }
                    dlen =  DSplit.length- 2;
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }
            }catch(IOException e){
                e.printStackTrace();
                Log.e(TAG,"수신 에러");
            }
        }
    }
    // socket stop 시 데이터 송신(?)
    class StopThread extends Thread{
        public StopThread(){
        }
        public void run(){
            // 데이터 송신
            try {
                String OutData = "AT+STOP\n";
                byte[] data = OutData.getBytes();
                OutputStream output = socket.getOutputStream();
                output.write(data);
                Log.d(TAG, "AT+STOP\\n COMMAND 송신");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // socket connect
    class ConnectThread extends Thread {
        String hostname;

        public ConnectThread(String addr) {
            hostname = addr;
        }

        public void run() {
            try { // 클라이언트 소켓 생성
                int port = 8000;
                socket = new Socket(hostname, port);
                Log.d(TAG, "Socket 생성, 연결");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (UnknownHostException uhe) {
                // 소켓 생성 시 전달되는 호스트의 IP를 식별할 수 없음

                Log.e(TAG, "생성 error : 호스트의 IP 주소 식별할 수 없음." +
                        "(잘못된 주소 값 또는 호스트 이름 사용)");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException ioe) {
                // 소켓 생성 괒어에서 I/O 에러 발생
                Log.e(TAG, "생성 Error : 네트워크 응답 없음");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Error : 네트워크 응답 없음",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (SecurityException se) {
                // security manager에서 허용되지 않은 기능 수행.

                Log.e(TAG, " 생성 Error : 보안(Security) 위반에 대해 " +
                        "보안 관리자(Security Manager)에 의해 발생." +
                        "(프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 보안(Security) 위반에 대해 " +
                                "보안 관리자(Security Manager)에 의해 발생." +
                                "(프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IllegalArgumentException le) {
                // 소켓 생성 시 전달되는 포트 번호(65536)이 허용 범위(0~65535)를 벗어남.

                Log.e(TAG, " 생성 Error : 메서드에 잘못된 파라미터가" +
                        " 전달되는 경우 발생. (0~65535 범위 밖의 포트 번호 사용," +
                        " null 프록시(proxy) 전달)");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), " Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생. (0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

}
