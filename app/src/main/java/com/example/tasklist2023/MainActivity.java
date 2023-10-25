package com.example.tasklist2023;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.mySubjectsTable.MySubject;
import com.example.tasklist2023.data.mySubjectsTable.MySubjectQuery;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //spnr1 تعريف صفة للكائن المرئي
    private Spinner spnrSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //spnr2 وضع مؤشر الصفة على الكائن المرئي الموجود بواجهة المستعمل
       spnrSubject = findViewById(R.id.spnrSubject);

        //spnr3 بناء الوسيط وتحديد واهة تنسيق لمعطى واحد
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spnr4 data source مصدر معطيات (ممكن ان يكون قائمة من قاعدة بيانات مثلا)
        String[] ar = {"Math", "CS", "Phs", "Arb", "Eng"};
        //spnr5 تحديد المعطيات للوسيط
        adapter.addAll(ar);
        //spnr6 ريط الكائن المرئي بالوسيط
        spnrSubject.setAdapter(adapter);


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

        List<MySubject> allSubjects = subjectQuery.getAllSubjects();
        for (MySubject subject : allSubjects) {
            Log.d("SA",subject.title);
        }



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