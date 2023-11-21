package com.example.tasklist2023;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.mySubjectsTable.MySubject;
import com.example.tasklist2023.data.mySubjectsTable.MySubjectQuery;
import com.example.tasklist2023.data.mytasksTable.MyTask;
import com.example.tasklist2023.data.mytasksTable.MyTaskQuery;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //spnr1 تعريف صفة للكائن المرئي
    private Spinner spnrSubject;
    private FloatingActionButton fabAdd;
    private ListView lstTasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAdd=findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(i);
            }
        });
        //spnr2 وضع مؤشر الصفة على الكائن المرئي الموجود بواجهة المستعمل
       spnrSubject = findViewById(R.id.spnrSubject);
        initSubjectSpnr();
        lstTasks=findViewById(R.id.lstvTasks);
        initAllListView();




//        Log.d("SM","onCreate");
//        Toast.makeText(this, "onCreate", Toast.LENGTH_LONG).show();

//        //بناء قاعدة بيانات وارجاع مؤشر عليها1
//        AppDataBase db=AppDataBase.getDB(getApplicationContext());
//        //2 مؤشر لكائن عمليات  لجدول
//        MySubjectQuery subjectQuery = db.getMySubjectQuery();
//        //3  بناء كائن من نوع الجدول وتحديد قيم الصفات
//        MySubject s1=new MySubject();
//        s1.setTitle("Math");
//        MySubject s2=new MySubject();
//        s2.title="Computers";
//        //4 اضافة كائن للجدول
//        subjectQuery.insert(s1);
//        subjectQuery.insert(s2);
//
//        List<MySubject> allSubjects = subjectQuery.getAllSubjects();
//        for (MySubject subject : allSubjects) {
//            Log.d("SA",subject.title);
//        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        initAllListView();
        initSubjectSpnr();
    }

    private void initAllListView() {
        AppDataBase db=AppDataBase.getDB(getApplicationContext());
        MyTaskQuery taskQuery = db.getMyTaskQuery();

        List<MyTask> allTasks = taskQuery.getAllTasks();

        ArrayAdapter<MyTask> tsksAdapter=new ArrayAdapter<MyTask>(getApplicationContext(), android.R.layout.simple_list_item_1);
        tsksAdapter.addAll(allTasks);
        lstTasks.setAdapter(tsksAdapter);


    }

    private void initSubjectSpnr() {
        AppDataBase db=AppDataBase.getDB(getApplicationContext());
        MySubjectQuery subjectQuery = db.getMySubjectQuery();

        List<MySubject> allSubjects = subjectQuery.getAllSubjects();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        for (MySubject subject : allSubjects) {
            adapter.add(subject.title);
        }
        spnrSubject.setAdapter(adapter);
    }


}