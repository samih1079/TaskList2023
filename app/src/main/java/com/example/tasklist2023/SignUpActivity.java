package com.example.tasklist2023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.MyUser;
import com.example.tasklist2023.data.usersTable.MyUserQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * מסך רישום לפי מיל וסיסמא
 */
public class SignUpActivity extends AppCompatActivity {
    private Button btnSave, btnCancel;
    private TextInputEditText etName, etEmail,
            etPassword, etRePassword, etPhone;
    //بناء الكائن الذي سيتم حفظه
    MyUser user=new MyUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRePassword = findViewById(R.id.etRePassword);
        etPhone = findViewById(R.id.etPhone);
        etName = findViewById(R.id.etFullName);

    }

    public void onClick(View v) {
        if (v == btnSave) {
            checkAndSignUP_FB();
            //checkAndSave();
        }
        if (v == btnCancel) {
            finish();
        }
    }

    //فحص القيم المدخلة في حقول النص وترحيلها
    private void checkAndSave() {
        boolean isAllOk = true;
        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();
        String phone = etPhone.getText().toString();
        String email = etEmail.getText().toString();

        if (email.length() < 6 || email.contains("@") == false) {
            isAllOk = false;
            etEmail.setError("Wrong Email");
        }
        if (password.length() < 6 || password.equals(rePassword) == false) {
            isAllOk = false;
            etPassword.setError("Wrong Password");
            etRePassword.setError("Wrong Password");
        }
        if (phone.length() < 9) {
            isAllOk = false;
            etPhone.setError("Wrong Phone");
        }
        if (name.length() < 2) {
            isAllOk = false;
            etPhone.setError("at least 2 letters");
        }

        if (isAllOk) {
            AppDataBase db = AppDataBase.getDB(getApplicationContext());
            MyUserQuery userQuery = db.getMyUserQuery();
            //فحص هل البريد إلكتروني موجود من قبل أي تم التسجيل من قبل
            if (userQuery.checkEmail(email) != null) {
                etEmail.setError("found email");
            } else// إن لم يكن البريد موجودا نقوم ببناء كائن للمستعمل وإدخاله في الجدول المستعملينMyUser
            {
                //بناء الكائن
                MyUser myUser = new MyUser();
                //تحديد قيم الصفات بالقيم التي استخرجناها
                myUser.email = email;
                myUser.fullName = name;
                myUser.phone = phone;
                myUser.passw = password;
                //إضافة الكائن الجديد للجدول
                userQuery.insert(myUser);
                //اغلاق الشاشة الحالية
                finish();
            }
        }

    }

    private void checkAndSignUP_FB() {
        boolean isAllOk = true;
        String name = etName.getText().toString();
        String password = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();
        String phone = etPhone.getText().toString();
        String email = etEmail.getText().toString();

        if (email.length() < 6 || email.contains("@") == false) {
            isAllOk = false;
            etEmail.setError("Wrong Email");
        }
        if (password.length() < 6 || password.equals(rePassword) == false) {
            isAllOk = false;
            etPassword.setError("Wrong Password");
            etRePassword.setError("Wrong Password");
        }

        if (phone.length() < 9) {
            isAllOk = false;
            etPhone.setError("Wrong Phone");
        }
        if (name.length() < 2) {
            isAllOk = false;
            etName.setError("at least 8 letters");
        }
        if (isAllOk) {
            //עצם לביצוע רישום كائن لعملية التسجيل
            FirebaseAuth auth = FirebaseAuth.getInstance();
            //יצירת חשבון בעזרת מיל וסיסמא
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override//התגובה שמתקבל הניסיון הרישום בענן
                public void onComplete(@NonNull Task<AuthResult> task) {// הפרמטר מכיל מידע מהשרת על תוצאת הבקשה לרישום
                    if (task.isSuccessful()) {// אם הפעולה הצליחה
                        Toast.makeText(SignUpActivity.this, "Signing up Succeeded", Toast.LENGTH_SHORT).show();
                        saveUser_FB(email,password,name,phone);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Signing up Failed", Toast.LENGTH_SHORT).show();
                        etEmail.setError(task.getException().getMessage());// הצגת הודעת השגיאה שהקבלה מהענן
                    }

                }
            });
        }

    }

    private void saveUser_FB(String email, String name, String phone, String passw) {

        //مؤشر لقاعدة البيانات
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //استخراج الرقم المميز للمستعمل الذي سجل الدخول
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        user.setEmail(email);
        user.setFullName(name);
        user.setPhone(phone);
        user.setPassw(passw);
        user.setId(uid);
        //اضافة كائن "لمجموعة" المستعملين ومعالج حدث لفحص نجاح الاضافة
        db.collection("MyUsers").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Succeeded to add User", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "Failed to add User", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}