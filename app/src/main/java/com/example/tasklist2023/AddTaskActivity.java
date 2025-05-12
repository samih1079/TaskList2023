package com.example.tasklist2023;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.users.FullAccount;
import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.mySubjectsTable.MySubject;
import com.example.tasklist2023.data.mySubjectsTable.MySubjectQuery;
import com.example.tasklist2023.data.MyTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * מסך הוספת מטלה
 */
public class AddTaskActivity extends AppCompatActivity {

    private static final String APP_KEY = "sc2c0sfsy50rzgb";//مفتاح التطبيق الذي استخرجناه
    public static final String TAG = "Dropbox";//‏استعمالها عند طباعة ملاحظات على الشاشة
    //مفتاح الاستعمال الحالي
    private static String currentAccessToken="sl.u.AFshbvInDitebptktWUxdWIsEjcNwiwdmhSFbBrv_N2tkjT8pPUT5FHdLhaq-EqaHF-YnuKA21J8mcZRF3mIFvN25jcktIMFLv-bVnhZfp7C62xgUGh70hnSPT6p4ZHpcwhI7y7y4Hbu1-ZQ1a4KjoJ9LWfVMmOtbk_1dgffa28um0XgNz-V0NdfjDF8q7xO6h_vzwHeriBdyOvehfU0m_n13X5D0StP0QvegG1RAVm4yEGGSIC7NYP7bIEk61Muiq0hipRt3leJV0NlI2o4lMlmvBIgfaovixxMqwPdhbOeyhdzsP07Fj6XtiBkqoMnti-WCZwUBCqRzL5JT8VYStrItQM3Cj7UwZ9XOQrGCRmNVgZWm_FrPsmWn_Acten0AiWaaMDDxMp1pxLCfukBZiXPcMgr3OZkWCJzDEMHo36te7q8C92WW1oa0U0msOd7NbiLKSnZq8wCmpjIvZeFBG6ZJOAU9sQM6wx4f6tlxRxNO4s_DwrS6tlubbJc23baNVW4Bmsk5X4vbevLLWsX0_gt7LnhulKJeC6Ro8X7O0HF5RRFcOoNgFC83adBnI11W16oJihwYjTAtyjz3IcegfIjCPqlKmNkfepiy0JARrsTPSLvIgDGvbwv-jn9EHSBy0B7EYc5Wi15ZPzis_E3UFKKycj7ISXQVyEVeThkELhBcFJrZyB-n5qbpLBrAvj2ufh8vezJ9glTM4sdRnNdcYcFFPSxUD_vX8jG23e0XuZts0DOCGvYFPKsiwOeZRNmuKmyx17MHpd-unPiMXfGGx3A6_rwxHv0EuNpTsLbpzlLQ6eUg_jpEpHI7ag0zC8PmucuNtftxVYGLFToh83yg5R__N2eBYnncmPGVMEatu_uDO-SygLWfxBBDj17cvk4MaFkBRrDQWWWKx2nYnKlKwxC52kdgRWdkEl2Z0z3itDINzRiSsih1lgCFkLs4Hh5OKb0KneFbjk_uUpLcVgYvCvUVLHbFGEsG3OHxAmxiXl_wwoKH7oI6sWfYpie3K-LiIDDZpaeMZX49GuWzYDNQ59c_SbhfUotdNgZp19fLjezEGj2kM6YpUkCyB4DVnFa4nn_YJgcAdvxtKE9iuqRrGhdd2td1IPD5RrkUiyLfx0yGhc_TDejeCz3V4sTaApBbkJ71rKzuhwZ-Xb8hFk39UxBntwg_EWRlw59nfzjMMb0nYNPe_moFtuQPst0iJ6ZEmm_HWBFCKyQzOx3Cc-k_dCvpwcc9eZ5YNLfNVelgSt1OmlUA7M3apJHTOjto-R-sXA";
    /**
    // متنسق Client ل-dropbox, لتحميل الملفات
     */
    private DbxClientV2 dropboxClient;
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
    private final int PERMISSION_CODE = 101;//קוד מזהה לבחירת הרשאת גישה לקבצים
    private static final int REQUEST_CONTACT_PICKER = 102;//קוד בקשת בחירת איש קשר מהטלפון
    private Button btnPickContact;//בחירת איש קשר מאנשי הקשר
    private TextView tvUplodedImg;//טקסט להעלאת תמונה
    private final int IMAGE_PICK_CODE = 100;// קוד מזהה לבקשת בחירת תמונה
    /**
     * הצכת ובחירת תמונה
     */
    private ImageButton imgBtnl;//כפתור/ לחצן לבחירת תמונה והצגתה
    private Uri toUploadimageUri;// כתוב הקובץ(תמונה) שרוצים להעלות
    private Uri downladuri;//כתובת הקוץ בענן אחרי ההעלאה

    private VideoView vidV;
    private final int Video_PICK_CODE = 103;// קוד מזהה לבקשת בחירת תמונה

    private Uri toUploadVideoUri;// כתוב הקובץ(תמונה) שרוצים להעלות
    private Uri downladVideoUri;//כתובת הקוץ בענן אחרי ההעלאה
    private MySubject subject;
    private MyTask myTask;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        autoEtSubj = findViewById(R.id.autoEtSubj);
        initAutoEtSubjects();//دالة لاستخراج القيم وعرضها بالحقل السابق
        etShortTitle = findViewById(R.id.etShortTitle);
        etText = findViewById(R.id.etText);
        sbImportance = findViewById(R.id.skbrImportance);
        tvUplodedImg = findViewById(R.id.tvUplodedImg);
        btnSave = findViewById(R.id.btnSaveTask);
        btnCancel = findViewById(R.id.btnCancelTask);
        btnPickContact=findViewById(R.id.btnPickContact);
        btnPickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkSelfPermission(android.Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_DENIED)
                {
                    String[] permissions = {Manifest.permission.READ_CONTACTS};
                    //בקשת אישור ההשאות (שולחים קוד הבקשה)
                    //התשובה תתקבל בפעולה onRequestPermissionsResult
                    requestPermissions(permissions, 103);
                }
                else {
                    // 3. Create an intent to pick a contact
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

                    // 4. Start activity for result, passing permission request code (if needed)
                    startActivityForResult(intent, REQUEST_CONTACT_PICKER); // Replace with your request code
                }
            }
        });
        //upload: 3
        imgBtnl = findViewById(R.id.imgBtn);
        imgBtnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload: 8
                //checkPermission();
                pickFile();
            }
        });

        vidV=findViewById(R.id.vidV);
        vidV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionForVideo();
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
            //  uploadImage(toUploadimageUri);
            //uploadToDropbox(toUploadimageUri);

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
    private void pickVideoFromGallery() {
        //intent to pick image
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("video/*");
        startActivityForResult(pickIntent, Video_PICK_CODE);
    }
    //upload: 5:handle result of picked images

    /**
     * @param requestCode מספר הקשה
     * @param resultCode  תוצאה הבקשה (אם נבחר משהו או בוטלה)
     * @param data        הנתונים שניבחרו
     */
    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //אם נבחר משהו ואם זה קוד בקשת התמונה
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            //set image to image view
            toUploadimageUri = data.getData();//קבלת כתובת התמונה הנתונים שניבחרו
            imgBtnl.setImageURI(toUploadimageUri);// הצגת התמונה שנבחרה על רכיב התמונה
        }
        if (resultCode == RESULT_OK && requestCode == Video_PICK_CODE) {
            //set image to image view
            toUploadVideoUri = data.getData();//קבלת כתובת התמונה הנתונים שניבחרו
            vidV.setVideoURI(toUploadVideoUri);// הצגת התמונה שנבחרה על רכיב התמונה
            vidV.seekTo(2);
        }
        if(resultCode==RESULT_OK && requestCode==REQUEST_CONTACT_PICKER) {
            etShortTitle.setText("");

            Cursor cursor1, cursor2;

            //get data from intent
            Uri uri = data.getData();

            cursor1 = getContentResolver().query(uri, null, null, null, null);

            if (cursor1.moveToFirst()){
                //get contact details
                String contactId = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
                String contactName = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactThumnail = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                String idResults = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                int idResultHold = Integer.parseInt(idResults);

                etShortTitle.append("ID: "+contactId);
                etShortTitle.append("\nName: "+contactName);

                if (idResultHold == 1){
                    cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+contactId,
                            null,
                            null
                    );
                    //a contact may have multiple phone numbers
                    while (cursor2.moveToNext()){
                        //get phone number
                        String contactNumber = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //set details

                        etShortTitle.append("\nPhone: "+contactNumber);
                        //before setting image, check if have or not
                        if (contactThumnail != null){
                            imgBtnl.setImageURI(Uri.parse(contactThumnail));
                        }
                        else {
                            //im.setImageResource(R.drawable.ic_person);
                        }
                    }
                    cursor2.close();
                }
                cursor1.close();
            }
        }
    }

    // Helper methods to retrieve phone numbers and emails
    private List<String> getPhoneNumbers(String contactId) {
        List<String> phoneNumbers = new ArrayList<>();
        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{contactId}, null);
        if (phoneCursor != null) {
            try {
                while (phoneCursor.moveToNext()) {
                    @SuppressLint("Range") String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNumbers.add(phoneNumber);
                }
            } finally {
                phoneCursor.close();
            }
        }
        return phoneNumbers;
    }

    //upload: 6

    /**
     * בדיקה האם יש הרשאה לגישה לקבצים בטלפון
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//בדיקת גרסאות
            //בדיקה אם ההשאה לא אושרה בעבר
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_DENIED&&
                    checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                //רשימת ההרשאות שרוצים לבקש אישור
                String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.READ_MEDIA_IMAGES};

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
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED )) {
                //permission was granted אם יש אישור
                pickImageFromGallery();
            } else {
                //permission was denied אם אין אישור
                Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void checkPermissionForVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//בדיקת גרסאות
            //בדיקה אם ההשאה לא אושרה בעבר
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_DENIED&&
                    checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                //רשימת ההרשאות שרוצים לבקש אישור
                String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.READ_MEDIA_IMAGES};
                //בקשת אישור ההשאות (שולחים קוד הבקשה)
                //התשובה תתקבל בפעולה onRequestPermissionsResult
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                //permission already granted אם יש הרשאה מקודם אז מפעילים בחירת תמונה מהטלפון
                pickVideoFromGallery();
            }
        } else {//אם גרסה ישנה ולא צריך קבלת אישור
            pickVideoFromGallery();
        }
    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//         accessToken = Auth.getOAuth2Token();
//
//        if (accessToken != null) {
//            // Save the token securely (e.g., SharedPreferences or EncryptedStorage)
//            Log.d("Dropbox", "Token: " + accessToken);
//            finish(); // close auth activity
//
//        }
//    }
//    private void uploadToDropbox(Uri toUploadimageUri) {
//        DbxRequestConfig config = DbxRequestConfig.newBuilder("MyApp/1.0").build();
//        DbxClientV2 client = new DbxClientV2(config, "sl.u.AFuORQxTC96s6EV3DybxDH_aRbs7zwSJxmBgRo06nrNdmLUbTO6xwOKV41ZjN-YxowAeAS1oaPhSrLrH5t0DuofsWcBB1T_q1eRiafTlVt-SFCc1XJ611mNOtg_pZgrbAFVtxhWcjXA6_dAuqpy5iw_0_eShxhWmIvNfIxhf_ZmlD2lWSqfLm1Z7IIKss520g1y0dW_ZSLF8lwmsiblDfG_y55KSi_xRyddp5PFtUrMNO_-PgRm1kz8f9xlIRLHYpTDSDymXRDw8pBJCIw-B8s9pPg1NYXwK2IcebG3TE5Q15YE5MzbzMdCw09hRCBDz4yXk8aSfIJi8hqllsIZsQ0V5RAqc25vF08I-UP1IVQnhJkWEVqKD6y5lWmRiJ-yfJviLWHPumBwmbDYI6CF6guhRyS5fI_2U9wmB52VbFtxolSDm5IcCYW4L22pBKycU3jHilky34FOr_1D5H9UN3QdFmt1azZFLgHgs_FJnvBFqUVs5vtK1MD2KP3JB5ZkDBwuUDSaHAq1F3Lm12XoUa1mh8OHChV5Rec4oLlTw9DHsD21sNQk7jsLLtPSXsLAllB0Jnb-zqoL04gfB_dNcDquBq7iQZiMYoSti79CHIORg98aohJfcB1-rtew0A9vs-SfN42Ymr1fLsJs2AIZKJYw9Z8A3S9T_z1PHA4CIyvHcGcKIfvYIDNriCcm-Tmvg4IEeIVXeR_ewDk2P6ibV60u4CwRiSapfzAzHnWDmUuOw2SSzQ8GJR2ovxfw3Cc3qpsfqxlG3smyc418zgjT1ytcAHuzODGbe-2ND6Ql00pSY8jY3MV0kMQgr2XTNMAKKyvzLlMa81qCV6tE9HzsiLLXKtl2RPX_iMKh7TWTX_eXApuiyQJXnbO8X3-a01S8lHUPlLCvRU-BC1PKZ16IIs9pQpjf9Dtnk8im140n9A35LvsCtLBTP8XuXI96nHC9L0QHZ_QoHCza4cYAI57CF060gau-pWrFIsmVdpoE5KZKHF6RVcM2URvy2XolB8JhuIW2xw2WszRuVoOZnnn26Nklw2pD--PUJJmpt2Nl-TbwVWGWdr4HwekqW7tlynEAl_gNz-2UjYjdNfWnT8IcevKTlxtt_GCAqijJipMosTu3kEBwVDXrmDuq3VVE9PhGXIZwJjMgMiH0sLq4thLRw-6fawq2jq_tJpn6_4irZE67huJet3CTUsCY2ADezKTF2yChI1uCiY9dQZNr0dtC4hvDsngWbJuiGKAsTl5FsvbGgq6houxJbZPq_lOtsEAprpis");
//
//// Example: Upload a file DROPBOX


    @Override
    protected void onResume() {
        super.onResume();
        if (currentAccessToken != null) {
            initializeDropboxClient(currentAccessToken);
            // Fetch user info to confirm login validity and update UI
           // fetchAccountInfoAndUpdateUi();
        } else {
           // updateUiForLoggedOutState();
        }
        // Handle potential redirect from Dropbox auth
        handleDropboxAuthResult();
    }

    private void updateUiForLoggedInState(String userName) {
//        loginButton.setVisibility(View.GONE);
//        pickFileButton.setVisibility(View.VISIBLE);
//        pickFileButton.setEnabled(true);
//        loggedInUserText.setVisibility(View.VISIBLE);
//        loggedInUserTextUserText.setText("Logged in as: " + userName);
    }

    private void updateUiForLoggedOutState() {
//        loginButton.setVisibility(View.VISIBLE);
//        pickFileButton.setVisibility(View.GONE); // Hide or disable pick file button
//        pickFileButton.setEnabled(false);
//        loggedInUserText.setVisibility(View.GONE);
//        loggedInUserText.setText("");
    }


    // --- Authentication ---

    private void startDropboxAuthorization() {
        Auth.startOAuth2Authentication(this, APP_KEY);
    }

    private void handleDropboxAuthResult() {
        String token = Auth.getOAuth2Token();
        if (token != null && !token.equals(currentAccessToken)) { // Check if it's a NEW token
            Log.d("Dropbox", "Dropbox auth successful, received new token.");
            currentAccessToken = token;
            storeAccessToken(currentAccessToken);
            initializeDropboxClient(currentAccessToken);
            fetchAccountInfoAndUpdateUi(); // Update UI after successful login
        }
        // No else needed here, onResume will be called regardless.
        // The check in onCreate/onResume for existing token handles the UI state.
    }

    private void fetchAccountInfoAndUpdateUi() {
        if (dropboxClient == null) {
            Log.w("Dropbox", "Cannot fetch account info, client not initialized.");
            // Maybe token expired or is invalid? Clear it and update UI.
            clearAccessToken();
            updateUiForLoggedOutState();
            return;
        }
        // Run network call on background thread
        new FetchAccountTask(AddTaskActivity.this, dropboxClient).execute();
    }


    // --- Token Management ---

    private void storeAccessToken(String token) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("dropbox-access-token", token).apply();
        Log.d(TAG, "Access token stored.");
    }

    private String getAccessToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString("dropbox-access-token", null);
    }

    private void clearAccessToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().remove("dropbox-access-token").apply();
        currentAccessToken = null; // Clear in-memory token too
        dropboxClient = null; // Invalidate client
        Log.d(TAG, "Access token cleared.");
    }

    // --- Dropbox Client Initialization ---

    private void initializeDropboxClient(String accessToken) {
        if (accessToken == null) {
            Log.e(TAG, "Cannot initialize Dropbox client with null token.");
            dropboxClient = null;
            return;
        }
        if (dropboxClient == null || !accessToken.equals(currentAccessToken)) { // Re-init if token changed
            Log.d(TAG, "Initializing Dropbox client.");
            DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("YourAppName/1.0").build();
            dropboxClient = new DbxClientV2(requestConfig, accessToken);
            currentAccessToken = accessToken; // Ensure current token matches client
        } else {
            Log.d(TAG, "Dropbox client already initialized.");
        }
    }


    // --- File Picking ---

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    if (fileUri != null) {
                        Log.d(TAG, "File selected: " + fileUri.toString());
                       // uploadFileToDropbox(fileUri);
                        uploadFile(fileUri);
                    } else {
                        Toast.makeText(this, "Failed to get file URI", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "File selection cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    private void pickFile() {
        if (currentAccessToken == null || dropboxClient == null) {
            Toast.makeText(this, "Please log in to Dropbox first", Toast.LENGTH_SHORT).show();
            // Optionally trigger login: startDropboxAuthorization();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("image/*");
        filePickerLauncher.launch(intent);
    }
    public void uploadFile(Uri fileUri) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);

                FileMetadata metadata = dropboxClient.files().uploadBuilder("/" +UUID.randomUUID().toString() )
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
                runOnUiThread(() -> tvUplodedImg.setText(metadata.getName()));
                Log.d(TAG, "Upload successful: " + metadata.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // --- File Upload ---

    private void uploadFileToDropbox(Uri fileUri) {
        if (dropboxClient == null) {
            Toast.makeText(this, "Dropbox client not ready. Please log in.", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "Starting upload task for URI: " + fileUri);
        new UploadFileTask(this, dropboxClient, fileUri).execute();
    }


    // --- AsyncTasks (Background Operations) ---

    // Task to fetch user account info (confirm login & get name)
    private static class FetchAccountTask extends AsyncTask<Void, Void, FullAccount> {
        private final WeakReference<Activity> activityReference;
        private final DbxClientV2 dropboxClient;
        private Exception error;

        FetchAccountTask(Activity activity, DbxClientV2 client) {
            this.activityReference = new WeakReference<>(activity);
            this.dropboxClient = client;
        }

        @Override
        protected FullAccount doInBackground(Void... voids) {
            try {
                return dropboxClient.users().getCurrentAccount();
            } catch (DbxException e) {
                error = e;
                Log.e("Dropbox", "Error fetching account info", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(FullAccount account) {
            AddTaskActivity activity = (AddTaskActivity) activityReference.get();
            if (activity != null && !activity.isFinishing()) {
                if (account != null) {
                    Log.d("Dropbox", "Successfully fetched account: " + account.getName().getDisplayName());
                    activity.updateUiForLoggedInState(account.getName().getDisplayName());
                } else {
                    Log.w("Dropbox", "Failed to fetch account info.", error);
                    Toast.makeText(activity, "Failed to verify Dropbox login.", Toast.LENGTH_SHORT).show();
                    // Token might be invalid/expired, clear it
                    activity.clearAccessToken();
                    activity.updateUiForLoggedOutState();
                }
            }
        }
    }

    // Task to upload the selected file (from Step 5)
    // --- Paste the UploadFileTask class code from Step 5 here ---
    private static class UploadFileTask extends AsyncTask<Void, Long, FileMetadata> {
        // ... (Full code from Step 5, including constructor, onPreExecute, doInBackground, onPostExecute)
        // Make sure the WeakReference points to MainActivity
        private final WeakReference<Activity> activityReference;
        private final DbxClientV2 dropboxClient;
        private final Uri fileUri;
        private final ContentResolver contentResolver;
        private Exception error = null;
        private String fileName = "unknown_file";

        UploadFileTask(Activity activity, DbxClientV2 dropboxClient, Uri fileUri) {
            this.activityReference = new WeakReference<>(activity);
            this.dropboxClient = dropboxClient;
            this.fileUri = fileUri;
            this.contentResolver = activity.getContentResolver();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Activity activity = activityReference.get();
            if (activity != null && !activity.isFinishing()) {
//                if (activity.uploadProgressBar != null) {
//                    activity.uploadProgressBar.setVisibility(View.VISIBLE);
//                    activity.uploadProgressBar.setProgress(0);
//                }
                Toast.makeText(activity, "Starting upload...", Toast.LENGTH_SHORT).show();

                // Get filename
                try (Cursor cursor = contentResolver.query(fileUri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            fileName = cursor.getString(nameIndex);
                        }
                    }
                } catch (Exception e) { Log.e("Dropbox", "Error getting file name", e); }
            }
        }

        @Override
        protected FileMetadata doInBackground(Void... params) {
            Activity activity = activityReference.get();
            if (activity == null || activity.isFinishing()){
                return null; // Activity gone, cancel task
            }

            String dropboxPath = "/" + fileName; // Root path, change if needed
            Log.d("Dropbox", "Uploading " + fileName + " to Dropbox path: " + dropboxPath);

            try (InputStream inputStream = contentResolver.openInputStream(fileUri)) {
                if (inputStream == null) throw new IOException("Unable to open input stream for URI: " + fileUri);

                FileMetadata metadata = dropboxClient.files().uploadBuilder(dropboxPath)
                        .withMode(WriteMode.OVERWRITE)
                        .uploadAndFinish(inputStream);
                Log.d("Dropbox", "Upload successful: " + metadata.getName());
                return metadata;

            } catch (DbxException | IOException e) {
                error = e;
                Log.e(TAG, "Error uploading file to Dropbox", e);
            }
            return null;
        }


        @Override
        protected void onPostExecute(FileMetadata result) {
            super.onPostExecute(result);
            Activity activity = activityReference.get();
            if (activity != null && !activity.isFinishing()) {
//                if (activity.uploadProgressBar != null) {
//                    activity.uploadProgressBar.setVisibility(View.GONE);
//                }
                if (error != null) {
                    Toast.makeText(activity, "Error uploading file: " + error.getMessage(), Toast.LENGTH_LONG).show();
                } else if (result != null) {
                    Toast.makeText(activity, "File uploaded: " + result.getName(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, "Upload failed.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "Upload finished but Activity is gone.");
            }
        }
    } // End of UploadFileTask


}
