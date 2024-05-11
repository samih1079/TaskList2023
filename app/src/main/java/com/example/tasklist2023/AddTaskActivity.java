package com.example.tasklist2023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
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

/**
 * מסך הוספת מטלה
 */
public class AddTaskActivity extends AppCompatActivity {
    /**
     * לחצני שמירה וביטול בהתאם
     */
    private Button btnSave, btnCancel;
    /**
     * רכיב לקביעות חשיבות
     */
    private SeekBar sbImportance;
    /**
     * שדות קלט לכותרת וגוף המטלה
     */
    private TextInputEditText etShortTitle, etText;
    /**
     * שדה קלט עם אפשרות השלמה אוטומטית לבחירת נושא
     */
    private AutoCompleteTextView autoEtSubj;
    //upload: 0.1 add firebase storage
    //upload: 0.2 add this permissions to manifest xml
    //          <uses-permission android:name="android.permission.INTERNET" />
    //          <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

    //upload: 1 add Xml image view or button and upload button
    //upload: 2 add next fileds
    private final int IMAGE_PICK_CODE = 100;// קוד מזהה לבקשת בחירת תמונה
    private final int PERMISSION_CODE = 101;//קוד מזהה לבחירת הרשאת גישה לקבצים
    /**
     * הצכת ובחירת תמונה
     */
    private ImageButton imgBtnl;//כפתור/ לחצן לבחירת תמונה והצגתה
    private Button btnUpload;// לחצן לביצוע העלאת התמונה
    private Uri toUploadimageUri;// כתוב הקובץ(תמונה) שרוצים להעלות
    private Uri downladuri;//כתובת הקוץ בענן אחרי ההעלאה
    StorageTask uploadTask;// עצם לביצוע ההעלאה
    private MySubject subject;
    private MyTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        autoEtSubj = findViewById(R.id.autoEtSubj);
        initAutoEtSubjects();//دالة لاستخراج القيم وعرضها بالحقل السابق
        etShortTitle = findViewById(R.id.etShortTitle);
        etText = findViewById(R.id.etText);
        sbImportance = findViewById(R.id.skbrImportance);
        btnSave = findViewById(R.id.btnSaveTask);
        btnCancel = findViewById(R.id.btnCancelTask);
        //upload: 3
        imgBtnl = findViewById(R.id.imgBtn);
        imgBtnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload: 8
                checkPermission();
            }
        });
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
        AppDataBase db = AppDataBase.getDB(getApplicationContext());
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
    private void checkAndSaveTask() {
        boolean isAllOK = true;
        String subjText = autoEtSubj.getText().toString();
        String shortTitle = etShortTitle.getText().toString();
        String text = etText.getText().toString();
        int importance = sbImportance.getProgress();

        if (isAllOK) {
            AppDataBase db = AppDataBase.getDB(getApplicationContext());
            MySubjectQuery subjectQuery = db.getMySubjectQuery();
            if (subjectQuery.checkSubject(subjText) == null)//فحص هل الموضوع موجود من قبل بالجدول
            {   //بناء موضوع جديد واضافته
                MySubject subject = new MySubject();
                subject.title = subjText;
                subjectQuery.insert(subject);
            }
            //استخراج الموضوع لاننا بحاجة لرقمه التسلسلي id
            MySubject subject = subjectQuery.checkSubject(subjText);
            //بناء مهمة جديدة وتحديد صفاتها
            MyTask task = new MyTask();
            task.importance = importance;
            task.shortTitle = shortTitle;
            task.text = text;
            task.subjId = subject.getKey_id();//تحديد رقم الموضوع للمهمة
            db.getMyTaskQuery().insertTask(task);//اضافة المهمة للجدول
            finish();//اغلاق الشاشة
        }
    }

    private void checkAndSaveTask_FB() {
        boolean isAllOK = true;
        String subjText = autoEtSubj.getText().toString();
        String shortTitle = etShortTitle.getText().toString();
        String text = etText.getText().toString();
        int importance = sbImportance.getProgress();
        if (subjText.length() == 0) {
            isAllOK = false;
            autoEtSubj.setError("must wrute or select subject");
        }
        if (shortTitle.length() == 0) {
            isAllOK = false;
            etShortTitle.setError("must write title min 1 char");
        }
        if (isAllOK) {
            //בניית עצם מקצוע עם מזהה של המשתמש שיצר אותו
            subject = new MySubject();
            subject.title = subjText;
            //בניית עצם למשימה
            myTask = new MyTask();
            myTask.importance = importance;
            myTask.shortTitle = shortTitle;
            myTask.text = text;
            //myTask.subjId=subject.getKey_id();//קביעת מזהה המקצוע ששיכת לו המשימה
            //ביצוע העלאת התמונה ואחרי שהתמונה עלתה שומרים את הכתובת שלה בעצם ושומרום אותו במסד הנתונים
            uploadImage(toUploadimageUri);
        }
    }

    private void saveSubjAndTask() {
        //קבלת הפניה למסד הניתונים
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //קבלת מזהה המשתמש שנכנס לאפליקציה
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //קבלת קישור לאוסף המקצועות שנמצא במסמך המשתמש-לפי המזהה שלו-
        CollectionReference subjCollection = db.collection("MyUsers")
                .document(uid)
                .collection("subjects");
        //  לקבל מזהה ייחודי למסמך החדש
        String sbjId = subjCollection.document().getId();
        subject.id = sbjId;
        subject.userId = uid;
        //הוספת מקצוע לאוסף המקצועות (מזהה המקצוע הוא השם שלו)
        //הוספת מאזין שבודק אם ההוספה הצליחה
        subjCollection.document(subject.title).set(subject).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //אם ההוספה של המקצוע הצליחה מוסיפים משימה למקצוע
                    //קבלת קישור/כתובת לאוסף המשימות
                    CollectionReference tasksCollection = subjCollection.document(subject.title).collection("Tasks");
                    // קבלת מזהה למסמך החדש
                    String taskId = tasksCollection.document().getId();
                    myTask.id = taskId;//עידכון תכונת המזהה של המשימה
                    myTask.sbjId=subject.title;
                    // הוספת (מסמך) המשימה לאוסף המשימות
                    //הוספת המאזין לבדיקת הצלחת ההוספה
                    tasksCollection.document(taskId).set(myTask).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddTaskActivity.this, "Adding myTask Succeeded", Toast.LENGTH_SHORT).show();
                                finish();
                                ;
                            } else {
                                Toast.makeText(AddTaskActivity.this, "Adding myTask failed" + task.getException().toString(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                } else {
                    Toast.makeText(AddTaskActivity.this, "Adding mySubject failed" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //upload: 6
    private void uploadImage(Uri filePath) {

        if (filePath != null) {
            //יצירת דיאלוג התקדמות
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();//הצגת הדיאלוג
            //קבלץ כתובת האחסון בענן
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference();
            //יצירת תיקיה ושם גלובלי לקובץ
            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            // יצירת ״תהליך מקביל״ להעלאת תמונה
           ref.putFile(filePath)
                    //הוספת מאזין למצב ההעלאה
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();// הסתרת הדיאלוג
                                //קבלת כתובת הקובץ שהועלה
                                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        downladuri = task.getResult();
                                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                        myTask.setImage(downladuri.toString());//עידכון כתובת התמונה שהועלתה

                                        saveSubjAndTask();
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    //הוספת מאזין שמציג מהו אחוז ההעלאה
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //חישוב מה הגודל שהועלה
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        } else {
            saveSubjAndTask();
        }
    }

    //upload:4
    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    //upload: 5:handle result of picked images

    /**
     * @param requestCode מספר הקשה
     * @param resultCode  תוצאה הבקשה (אם נבחר משהו או בוטלה)
     * @param data        הנתונים שניבחרו
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //אם נבחר משהו ואם זה קוד בקשת התמונה
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to image view
            toUploadimageUri = data.getData();//קבלת כתובת התמונה הנתונים שניבחרו
            imgBtnl.setImageURI(toUploadimageUri);// הצגת התמונה שנבחרה על רכיב התמונה
        }
    }
    //upload: 6

    /**
     * בדיקה האם יש הרשאה לגישה לקבצים בטלפון
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//בדיקת גרסאות
            //בדיקה אם ההשאה לא אושרה בעבר
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //רשימת ההרשאות שרוצים לבקש אישור
                String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
                //בקשת אישור ההשאות (שולחים קוד הבקשה)
                //התשובה תתקבל בפעולה onRequestPermissionsResult
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //permission already granted אם יש הרשאה מקודם אז מפעילים בחירת תמונה מהטלפון
                pickImageFromGallery();
            }
        } else {//אם גרסה ישנה ולא צריך קבלת אישור
            pickImageFromGallery();
        }
    }
    //upload: 7

    /**
     * @param requestCode  The request code passed in מספר בקשת ההרשאה
     * @param permissions  The requested permissions. Never null. רשימת ההרשאות לאישור
     * @param grantResults The grant results for the corresponding permissions תוצאה עבור כל הרשאה
     *                     PERMISSION_GRANTED אושר or PERMISSION_DENIED נדחה . Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {//בדיקת קוד בקשת ההרשאה
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission was granted אם יש אישור
                pickImageFromGallery();
            } else {
                //permission was denied אם אין אישור
                Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
