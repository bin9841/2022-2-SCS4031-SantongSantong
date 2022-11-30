package com.example.frontend;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PopupActivity extends AppCompatActivity {
    ImageView image;
    TextView alert_title, alert_content;
    Button btn_rsc, btn_rsc_cpt;
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.drown_popup);

        alert_title = findViewById(R.id.alert_title);
        alert_content = findViewById(R.id.alert_content);
        btn_rsc = findViewById(R.id.btn_rsc);
        btn_rsc_cpt = findViewById(R.id.btn_rsc_cpt);

        image = findViewById(R.id.drowning_photo);

        btn_rsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_rsc.setEnabled(false);
                btn_rsc_cpt.setEnabled(true);
                alert_title.setText("구조 중");
                alert_content.setText("구조 완료 시, 버튼을 눌러주세요");
            }
        });

        btn_rsc_cpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        // 바깥 레이어 클릭 시 안 닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return  true;
    }
    // 취소 버튼 X?
    public void onBackPressed(){
        return;
    }




}

