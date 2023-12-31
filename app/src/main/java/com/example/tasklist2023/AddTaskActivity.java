package com.example.tasklist2023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.mySubjectsTable.MySubject;
import com.example.tasklist2023.data.mySubjectsTable.MySubjectQuery;
import com.example.tasklist2023.data.mytasksTable.MyTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
        initAutoEtSubjects();//دالة لاستخراج القيم وعرضها بالحقل السابق
        etShortTitle=findViewById(R.id.etShortTitle);
        etText=findViewById(R.id.etText);
        sbImportance=findViewById(R.id.skbrImportance);
        btnSave=findViewById(R.id.btnSaveTask);
        btnCancel=findViewById(R.id.btnCancelTask);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSaveTask_FB();
                checkAndSaveTask();
            }
        });
    }

    /**
     * استخراج اسماء المواضيع من جدول المواضيع وعرضه بالحقل من نوع
     * AutoCompleteTextView
     * طريقة التعامل معه شبيه بال"سبنر"
     */
    private void initAutoEtSubjects() {
        //مؤشر لقعادة الباينات
        AppDataBase db=AppDataBase.getDB(getApplicationContext());
        //مؤشر لواجهة استعملامات جدول المواضيع
        MySubjectQuery subjectQuery = db.getMySubjectQuery();
        //ااستخراج جميع المواضيع من الجدول
        List<MySubject> allSubjects = subjectQuery.getAllSubjects();
        //تجهيز الوسيط
        ArrayAdapter<MySubject> adapter = new ArrayAdapter<MySubject>(this,
                android.R.layout.simple_dropdown_item_1line);
        adapter.addAll(allSubjects);//اضافة جميع المعطيات للوسيط
        autoEtSubj.setAdapter(adapter);//ربط الحقل بالوسيط
        //معالجة حدث لعرض المواضيع عند الضغط على الحقل
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
            if(subjectQuery.checkSubject(subjText)==null)//فحص هل الموضوع موجود من قبل بالجدول
            {   //بناء موضوع جديد واضافته
                MySubject subject=new MySubject();
                subject.title=subjText;
                subjectQuery.insert(subject);
            }
            //استخراج الموضوع لاننا بحاجة لرقمه التسلسلي id
            MySubject subject = subjectQuery.checkSubject(subjText);
            //بناء مهمة جديدة وتحديد صفاتها
            MyTask task=new MyTask();
            task.importance=importance;
            task.shortTitle=shortTitle;
            task.text=text;
            task.subjId=subject.getKey_id();//تحديد رقم الموضوع للمهمة
            db.getMyTaskQuery().insertTask(task);//اضافة المهمة للجدول
            finish();//اغلاق الشاشة
        }
    }

    private void checkAndSaveTask_FB()
    {
        boolean isAllOK=true;
        String subjText=autoEtSubj.getText().toString();
        String shortTitle=etShortTitle.getText().toString();
        String text=etText.getText().toString();
        int importance=sbImportance.getProgress();

        if(isAllOK)
        {
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            CollectionReference subjCollection = db.collection("MyUsers")
                    .document(uid)
                    .collection("subjects");
            String sbjId = subjCollection.document().getId();
            //بناء موضوع جديد واضافته
                MySubject subject=new MySubject();
                subject.title=subjText;
                subject.id=sbjId;
                subject.userId=uid;

            //بناء مهمة جديدة وتحديد صفاتها
            MyTask myTask=new MyTask();
            myTask.importance=importance;
            myTask.shortTitle=shortTitle;
            myTask.text=text;
            myTask.subjId=subject.getKey_id();//تحديد رقم الموضوع للمهمة

            subjCollection.document(subjText).set(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        CollectionReference tasksCollection = subjCollection.document(subjText).collection("Tasks");
                        String taskId = tasksCollection.document().getId();
                        myTask.id=taskId;
                        myTask.sbjId=sbjId;

                        tasksCollection.document(taskId).set(myTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(AddTaskActivity.this, "Taskk Add FB", Toast.LENGTH_SHORT).show();
                                    finish();;
                                }
                            }
                        });


                    }
                }
            });


        }
    }

}
