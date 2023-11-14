package com.example.tasklist2023;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.usersTable.MyUser;
import com.example.tasklist2023.data.usersTable.MyUserQuery;
import com.example.tasklist2023.data.usersTable.MyUserQuery_Impl;
import com.google.android.material.textfield.TextInputEditText;

public class SignInActivity extends AppCompatActivity {
   private TextInputEditText etEamil,etPassword;
   private Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);//بناء واجهة المستعمل- كل الكائنات الموجودة على الواجهة

        etEamil=findViewById(R.id.etEmail);//وضع مؤشر\صفة على الكائن المبني بواجهة المستعمل
        etPassword=findViewById(R.id.etPassword);
        btnSignUp=findViewById(R.id.btnSignUp);
    }

    public void onClickSignUp(View v)
    {
        Intent i=new Intent(SignInActivity.this,SignUpActivity.class);
        startActivity(i);
    }


    /**
     *  معالج حدث للزر sign in
     * @param v
     */
    public void onClickSignIn(View v)
    {
        checkEmailPassw();//دالة لاستخراج وفحص فحوى حقول المُدخلة
    }
    private void checkEmailPassw() {
        boolean isAllOK = true;// يحوي نتيجة فحص الحقوا ان كانت سليمة
        //استخراج النص من حقل الايميل
        String email = etEamil.getText().toString();
        //استخراج نص كلمة المرور
        String password = etPassword.getText().toString();
        // فحص الياميل ان كان طوله اقل من 6 او لا يحوي @ فهو خطأ
        if (email.length() < 6 || email.contains("@") == false) {
            //تعديل المتغير ليدل على ان الفحص اعطى نتيجة خاطئة
            isAllOK = false;
            //عرض ملاحظة خطا على الشاشة داخل حقل البريد
            etEamil.setError("Wrong Email");
        }
        if (password.length() < 8 || password.contains(" ") == true) {
            isAllOK = false;
            etPassword.setError("Wrong Password");
        }

        if (isAllOK) {
            Toast.makeText(this, "All OK", Toast.LENGTH_SHORT).show();
        }
    }
            //بناء قاعدة بيانات وارجاع مؤشر عليها1
            AppDataBase db=AppDataBase.getDB(getApplicationContext());
            //2 مؤشر لكائن عمليات  الجدول
            MyUserQuery userQuery=db.getMyUserQuery();

            MyUser myUser = userQuery.checkEmailPassw(email, password);
            if(myUser==null)
                Toast.makeText(this, "Wrong Email Or Password", Toast.LENGTH_LONG).show();
            else
            {
                Intent i=new Intent(SignInActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }
}