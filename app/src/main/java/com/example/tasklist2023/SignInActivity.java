package com.example.tasklist2023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.usersTable.MyUser;
import com.example.tasklist2023.data.usersTable.MyUserQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
    private TextInputEditText etEamil, etPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);//بناء واجهة المستعمل- كل الكائنات الموجودة على الواجهة

        etEamil = findViewById(R.id.etEmail);//وضع مؤشر\صفة على الكائن المبني بواجهة المستعمل
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
    }

    public void onClickSignUp(View v) {
        Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(i);
    }


    /**
     * معالج حدث للزر sign in
     *
     * @param v
     */
    public void onClickSignIn(View v) {
        checkEmailPassw_FB();
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

        if (isAllOK)
        {
            Toast.makeText(this, "All OK", Toast.LENGTH_SHORT).show();
            //بناء قاعدة بيانات وارجاع مؤشر عليها1
            AppDataBase db=AppDataBase.getDB(getApplicationContext());
            //2 مؤشر لكائن عمليات  الجدول
            MyUserQuery userQuery=db.getMyUserQuery();
            //3 استدعاء العملية التي تنفذ الاستعلام الذي يفحص البريد وكلمة المرور ويعيد كائنا ان كان موجودا أو null إن لم يكن موجود
            MyUser myUser = userQuery.checkEmailPassw(email, password);
            if(myUser==null)//هل لا يوجد كائن حسب الإيميل والباسورد
                Toast.makeText(this, "Wrong Email Or Password", Toast.LENGTH_LONG).show();
            else
            {//أن كان هنالك حساب حساب الإيميل والباسورد ننتقل إلى الشاشة الرئيسية
                Intent i=new Intent(SignInActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    private void checkEmailPassw_FB() {
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

        if (isAllOK)
        {
            //עצם לביצוע רישום كائن لعملية التسجيل
            FirebaseAuth auth = FirebaseAuth.getInstance();
            //כניסה לחשבון בעזרת מיל וסיסמא
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override //התגובה שמתקבל מהענן מניסיון הכניסה בענן
                public void onComplete(@NonNull Task<AuthResult> task) {// הפרמטר מכיל מידע מהשרת על תוצאת הבקשה לרישום
                    if(task.isComplete()){// אם הפעולה הצליחה
                        Toast.makeText(SignInActivity.this, "Signing in Succeeded", Toast.LENGTH_SHORT).show();
                        //מעבר למסך הראשי
                        Intent i=new Intent(SignInActivity.this,MainActivity.class);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(SignInActivity.this, "Signing in Failed", Toast.LENGTH_SHORT).show();
                        etEamil.setError(task.getException().getMessage());// הצגת הודעת השגיאה שהקבלה מהענן
                    }
                }
            });
        }
    }

}
