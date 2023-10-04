package com.example.tasklist2023;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.mySubjectsTable.MySubject;
import com.example.tasklist2023.data.mySubjectsTable.MySubjectQuery;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //35323132123123
        setContentView(R.layout.activity_main);
        Log.d("SM","onCreate");
        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();

        //بناء قاعدة بيانات وارجاع مؤشر عليها1
        AppDataBase db=AppDataBase.getDB(getApplicationContext());
        //2 مؤشر لكائن عمليات  لجدول
        MySubjectQuery subjectQuery = db.getMySubjectQuery();
        //3  بناء كائن من نوع الجدول وتحديد قيم الصفات
        MySubject s1=new MySubject();
        s1.setTitle("Math");
        MySubject s2=new MySubject();
        s2.title="Computers";
        //4 اضافة كائن للجدول
        subjectQuery.insert(s1);
        subjectQuery.insert(s2);




    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("SM","onRestart");
        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("SM","onResume");
        Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("SM","onPause");
        Toast.makeText(this, "onPause", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("SM","onStop");
        Toast.makeText(this, "onStop", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("SM","onDestroy");
        Toast.makeText(this, "onDestroy", Toast.LENGTH_LONG).show();
    }
}