package com.example.tasklist2023;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.mySubjectsTable.MySubject;
import com.example.tasklist2023.data.mySubjectsTable.MySubjectQuery;
import com.example.tasklist2023.data.mytasksTable.MyTask;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AddTaskActivity extends AppCompatActivity {
    private Button btnSave,btnCancel;
    private SeekBar sbImportance;
    private TextInputEditText etShortTitle, etText;
    private AutoCompleteTextView autoEtSubj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        autoEtSubj =findViewById(R.id.autoEtSubj);
        initAutoEtSubjects();
        etShortTitle=findViewById(R.id.etShortTitle);
        etText=findViewById(R.id.etText);
        sbImportance=findViewById(R.id.skbrImportance);
        btnSave=findViewById(R.id.btnSaveTask);
        btnCancel=findViewById(R.id.btnCancelTask);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSaveTask();
            }
        });
    }

    /**
     * استخراج اسماء المواضيع من جدول المواضيع واعرضه بال "سبنر"
     */
    private void initAutoEtSubjects() {
        AppDataBase db=AppDataBase.getDB(getApplicationContext());
        MySubjectQuery subjectQuery = db.getMySubjectQuery();
        List<MySubject> allSubjects = subjectQuery.getAllSubjects();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        for (MySubject subject : allSubjects) {
            adapter.add(subject.title);
        }

        autoEtSubj.setAdapter(adapter);
        autoEtSubj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoEtSubj.showDropDown();
            }
        });
    }
///khkjhj
    private void checkAndSaveTask()
    {
        boolean isAllOK=true;
        String subjText=autoEtSubj.getText().toString();
        String shortTitle=etShortTitle.getText().toString();
        String text=etText.getText().toString();
        int importance=sbImportance.getProgress();

        if(isAllOK)
        {
            AppDataBase db=AppDataBase.getDB(getApplicationContext());
            MySubjectQuery subjectQuery = db.getMySubjectQuery();
            if(subjectQuery.checkSubject(subjText)==null)
            {
                MySubject subject=new MySubject();
                subject.title=subjText;
                subjectQuery.insert(subject);
            }
            MySubject subject = subjectQuery.checkSubject(subjText);

            MyTask task=new MyTask();
            task.importance=importance;
            task.shortTitle=shortTitle;
            task.text=text;
            task.subjId=subject.getKey_id();

            db.getMyTaskQuery().insertTask(task);
            finish();
        }
    }

}
