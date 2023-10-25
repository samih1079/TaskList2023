package com.example.tasklist2023;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;

public class SignInActivity extends AppCompatActivity {
   private TextInputEditText etEamil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);//بناء واجهة المستعمل- كل الكائنات الموجودة على الواجهة

        etEamil=findViewById(R.id.etEmail);//وضع مؤشر\صفة على الكائن المبني بواجهة المستعمل
    }
}