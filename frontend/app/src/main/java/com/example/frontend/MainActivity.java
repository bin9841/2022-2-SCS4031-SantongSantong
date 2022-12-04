package com.example.frontend;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Version;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    Button btn_rqst, area1, area2, area3;
    public static String selected_area;
    TextView areaView;
    Retrofit retrofit;
    private NetworkService networkService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        ApplicationController application = ApplicationController.getInstance();
        application.buildNetworkService("9559-128-134-83-160.jp.ngrok.io");
        networkService = ApplicationController.getInstance().getNetworkService();

        areaView = (TextView) findViewById(R.id.slc_area);

        btn_rqst = findViewById(R.id.detect_request_button);
        area1 = findViewById(R.id.area1);
        area2 = findViewById(R.id.area2);
        area3 = findViewById(R.id.area3);
        Button.OnClickListener onClickListener = new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                switch(v.getId()){
                    case R.id.area1:
                        selected_area = "1";
                        areaView.setText("현재 설정된 근무지 : 1번");
                        break;
                    case R.id.area2:
                        selected_area = "2";
                        areaView.setText("현재 설정된 근무지 : 2번");
                        break;
                    case R.id.area3:
                        selected_area = "3";
                        areaView.setText("현재 설정된 근무지 : 3번");
                        break;
//                    case R.id.detect_request_button:
//                       Call<List<Version>> versionCall = networkService.get_version();
//                        versionCall.enqueue(new Callback<List<Version>>);
//
//                        String version_txt = "";
//                        for(Version version.versionList){
//                           version_txt += version.getVersion()+"\n";
//                    }
//                        Intent intent = new Intent(MainActivity.this, PopupActivity.class);
//                        startActivity(intent);
                }
            }
        };
//        btn_rqst.setOnClickListener(onClickListener);
        area1.setOnClickListener(onClickListener);
        area2.setOnClickListener(onClickListener);
        area3.setOnClickListener(onClickListener);
    }
    @OnClick(R.id.detect_request_button)
    public void btn_rqst_Click(){
        //GET
        Call<List<Version>> versionCall = networkService.get_version();
        versionCall.enqueue(new Callback<List<Version>>() {
            @Override
            public void onResponse(Call<List<Version>> call, Response<List<Version>> response) {
                if(response.isSuccessful()) {
                    List<Version> versionList = response.body();

                } else {
                    int StatusCode = response.code();
                    Log.i(ApplicationController.TAG, "Status Code : " + StatusCode);
                }
            }
            @Override
            public void onFailure(Call<List<Version>> call, Throwable t) {
                Log.i(ApplicationController.TAG, "Fail Message : " + t.getMessage());
            }
        });
    }
}


