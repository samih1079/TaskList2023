package com.example.tasklist2023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.List;
import java.util.UUID;

public class AddTaskActivity extends AppCompatActivity {
    private Button btnSave,btnCancel;
    private SeekBar sbImportance;
    private TextInputEditText etShortTitle, etText;
    private AutoCompleteTextView autoEtSubj;
    private Uri downladuri;
    private int IMAGE_PICK_CODE=100;
    private int PERMISSION_CODE=101;
    private Uri toUploadimageUri;

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
               // checkAndSaveTask();
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
            //קבלת הפניה למסד הניתונים
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            //קבלת מזהה המשתמש שנכנס לאפליקציה
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //קבלת קישור לאוסף המקצועות שנמצא במסמך המשתמש-לפי המזהה שלו-
            CollectionReference subjCollection = db.collection("MyUsers")
                    .document(uid)
                    .collection("subjects");
            //  לקבל מזהה ייחודי למסמך החדש
            String sbjId = subjCollection.document().getId();
            //בניית עצם מקצוע עם מזהה של המשתמש שיצר אותו
                MySubject subject=new MySubject();
                subject.title=subjText;
                subject.id=sbjId;
                subject.userId=uid;

            //בניית עצם למשימה
            MyTask myTask=new MyTask();
            myTask.importance=importance;
            myTask.shortTitle=shortTitle;
            myTask.text=text;
            myTask.subjId=subject.getKey_id();//קביעת מזהה המקצוע ששיכת לו המשימה
            //הוספת מקצוע לאוסף המקצועות (מזהה המקצוע הוא השם שלו)
                                                            //הוספת מאזין שבודק אם ההוספה הצליחה
            subjCollection.document(subjText).set(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        //אם ההוספה של המקצוע הצליחה מוסיפים משימה למקצוע
                        //קבלת קישור/כתובת לאוסף המשימות
                        CollectionReference tasksCollection = subjCollection.document(subjText).collection("Tasks");
                        // קבלת מזהה למסמך החדש
                        String taskId = tasksCollection.document().getId();
                        myTask.id=taskId;//עידכון תכונת המזהה של המשימה
                        // הוספת (מסמך) המשימה לאוסף המשימות
                                                                    //הוספת המאזין לבדיקת הצלחת ההוספה
                        tasksCollection.document(taskId).set(myTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(AddTaskActivity.this, "Adding myTask Succeeded", Toast.LENGTH_SHORT).show();
                                    finish();;
                                }
                                else
                                {
                                    Toast.makeText(AddTaskActivity.this, "Adding myTask failed"+task.getException().toString(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(AddTaskActivity.this, "Adding mySubject failed"+task.getException().toString(), Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }
    }

    //upload: 5
    private void uploadImage(Uri filePath) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            FirebaseStorage storage= FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            StorageTask<UploadTask.TaskSnapshot> uploadTask = ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    downladuri = task.getResult();
                                   // t.setImage(downladuri.toString());

                                }
                            });

                            Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }else
        {

        }
    }
    private void pickImageFromGallery(){
        //intent to pick image
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //permission was granted
                    pickImageFromGallery();
                }
                else {
                    //permission was denied
                    Toast.makeText(this,"Permission denied...!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //handle result of picked images
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode==RESULT_OK && requestCode== IMAGE_PICK_CODE){
            //set image to image view
            toUploadimageUri = data.getData();
           // imgBtnl.setImageURI(toUploadimageUri);
        }
    }
}
