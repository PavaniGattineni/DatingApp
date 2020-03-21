package com.dating.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
    EditText login_mail, login_password;
    LinearLayout ll_sign_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);
        login_mail = (EditText) findViewById(R.id.login_mail);
        login_password = (EditText) findViewById(R.id.login_password);
        ll_sign_up = (LinearLayout) findViewById(R.id.ll_sign_up);
        ll_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(SignInActivity.this,SignUpActivity.class);
            startActivity(i);
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        finish();
    }
}
