package com.example.tasklist2023;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.mySubjectsTable.MySubject;
import com.example.tasklist2023.data.mySubjectsTable.MySubjectQuery;
import com.example.tasklist2023.data.mytasksTable.MyTask;
import com.example.tasklist2023.data.mytasksTable.MyTaskAdapter;
import com.example.tasklist2023.data.mytasksTable.MyTaskQuery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * מסך ראשי מציג כל המטלות עם אפשרות חיפוש והוספה
 */
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_CODE = 100;
    //spnr1 تعريف صفة للكائن المرئي
    private Spinner spnrSubject;
    private FloatingActionButton fabAdd;
    private ListView lstTasks;
    private MyTaskAdapter tasksAdapter;

    private SearchView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lstTasks=findViewById(R.id.lstvTasks);//הפניה לרכיב הגרפי שמציג אוסף
        sv=findViewById(R.id.srchV);
        tasksAdapter=new MyTaskAdapter(this,R.layout.task_item_layout);//בניית המתאם

        lstTasks.setAdapter(tasksAdapter);//קישור המתאם אם המציג הגרפי לאוסף
        //הוספת מאזין לפתיחת תפריט בלחיצה על פריט מסוים
        lstTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override                                                   //i رقم العنصر الذي سبب الحدث
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopUpMenu(view, tasksAdapter.getItem(i)); //i رقم العنصر الذي سبب الحدث
            }
        });
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
        initSubjectSpnr_FB();
        sv.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                readTaskFrom_FB();

                return true;
            }
        });
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                tasksAdapter.getFilter().filter(s);

                return true;
            }
        });
        //realTimeUpdate_subjects();



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

        //initSubjectSpnr();
        initSubjectSpnr_FB();
        //initAllListView();
   //     initAllListView_FB();
    }

    /**
     * تجهيز قائمة جميع المهمات وعرضها ب ListView
     */
    private void initAllListView() {
        AppDataBase db=AppDataBase.getDB(getApplicationContext());
        MyTaskQuery taskQuery = db.getMyTaskQuery();

        List<MyTask> allTasks = taskQuery.getAllTasks();
        ArrayAdapter<MyTask> taksAdapter=new ArrayAdapter<MyTask>(this, android.R.layout.simple_list_item_1);
        taksAdapter.addAll(allTasks);
        lstTasks.setAdapter(taksAdapter);
        lstTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override                                                   //i رقم العنصر الذي سبب الحدث
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showPopUpMenu(view, taksAdapter.getItem(i)); //i رقم العنصر الذي سبب الحدث
            }
        });
    }
//    /**
//     * تجهيز قائمة جميع المهمات وعرضها ب ListView
//     */
//    private void initAllListView_FB() {
//        //בדיקה אם נחר נושא
//        if(spnrSubject==null || spnrSubject.getSelectedItem()==null)
//            return;
//        //בניית מתאם אם לא נבנה קודם
//        if(tasksAdapter==null) {
//            tasksAdapter = new MyTaskAdapter(getApplicationContext(), R.layout.task_item_layout);
//            lstTasks.setAdapter(tasksAdapter);
//        }
//       readTaskFrom_FB();// הורדת ניתונים והוספתם למתאם
//
//    }

    /**
     *  קריאת נתונים ממסד הנתונים firestore
     * @return .... רשימת הנתונים שנקראה ממסד הנתונים
     */
    public void readTaskFrom_FB()
    {
        if(spnrSubject.getSelectedItem()==null)return;;
        //בניית רשימה ריקה
        ArrayList<MyTask> arrayList =new ArrayList<>();
        //קבחת הפנייה למסד הנתונים
        FirebaseFirestore ffRef = FirebaseFirestore.getInstance();
        //קישור לקבוצה collection שרוצים לקרוא
        ffRef.collection("MyUsers").
                document(FirebaseAuth.getInstance().getUid()).
                collection("subjects").
                document(spnrSubject.getSelectedItem().toString()).
                //הוספת מאזין לקריאת הנתוניחם
                collection("Tasks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    /**
                     * תגובה לאירוע השלמת קריאת הנתונים
                     * @param task הנתונים שהתקבלו מענן מסד הנתונים
                     */
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {// אם בקשת הנתונים התקבלה בהצלחה
                            //מעבר על כל ה״מסמכים״= עצמים והוספתם למבנה הנתונים
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                //המרת העצם לטיפוס שלו// הוספת העצם למבנה הנתונים
                                arrayList.add(document.toObject(MyTask.class));
                            }
                            tasksAdapter.clear();//ניקוי המתאם מכל הנתונים
                            tasksAdapter.addAll(arrayList);//הוספת כל הנתונים למתאם
                            tasksAdapter.setOrginal(arrayList);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Error Reading data"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void realTimeUpdate_subjects()
    {
        FirebaseFirestore ffRef = FirebaseFirestore.getInstance();
        ffRef.collection("MyUsers").
                document(FirebaseAuth.getInstance().getUid()).
                collection("subjects")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        initSubjectSpnr_FB();
                    }
                });
    }
    private void realTimeUpdate_tasks()
    {
        FirebaseFirestore ffRef = FirebaseFirestore.getInstance();
        ffRef.collection("MyUsers").
                document(FirebaseAuth.getInstance().getUid()).
                collection("subjects")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        initSubjectSpnr_FB();
                    }
                });
    }
    /**
     * عملية تجهيز السبنر بالمواضيع
     */
    private void initSubjectSpnr() {
        AppDataBase db = AppDataBase.getDB(getApplicationContext());//قاعدة بناء
        MySubjectQuery subjectQuery = db.getMySubjectQuery();//عمليات جدول المواضيع
        List<MySubject> allSubjects = subjectQuery.getAllSubjects();//استخراج جميع المواضيع
        //تجهيز الوسيط
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);//
        subjectAdapter.add("ALL");//ستظهر اولا بالسبنر تعني عرض جميع المهمات
        for (MySubject subject : allSubjects) {//اضافة المواضيع للوسيط
            subjectAdapter.add(subject.title);
        }
        spnrSubject.setAdapter(subjectAdapter);//ربط السبنر بالوسيط
        //معالج حدث لاختيار موضوع بالسبنر
        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //استخراج الموضوع حسب رقمه الترتيبي i
                String item = subjectAdapter.getItem(i);
                if(item.equals("ALL"))//هذه يعني عرض جميع المهام
                    initAllListView();
                else {
                    //استخراج كائن الموضوع الذي اخترناه لاستخراج رقمه id
                    MySubject subject = subjectQuery.checkSubject(item);
                    //استدعاء العملية التي تجهز القائمة حسب رقم الموضوع id
                    initListViewBySubjId(subject.getKey_id());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    /**
     * عملية تجهيز السبنر بالمواضيع
     */
    private void initSubjectSpnr_FB() {
//        AppDataBase db = AppDataBase.getDB(getApplicationContext());//قاعدة بناء
//        MySubjectQuery subjectQuery = db.getMySubjectQuery();//عمليات جدول المواضيع
//        List<MySubject> allSubjects = subjectQuery.getAllSubjects();//استخراج جميع المواضيع
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);//
        FirebaseFirestore ffRef = FirebaseFirestore.getInstance();
        CollectionReference myUsers = ffRef.collection("MyUsers");
        DocumentReference document = myUsers.document(FirebaseAuth.getInstance().getUid());
        document.collection("subjects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                subjectAdapter.clear();
              //  subjectAdapter.add("ALL");//ستظهر اولا بالسبنر تعني عرض جميع المهمات
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                for (DocumentSnapshot doc : documents) {//اضافة المواضيع للوسيط
                    MySubject mySubject = doc.toObject(MySubject.class);
                    subjectAdapter.add(mySubject.getTitle());
                }
                spnrSubject.setAdapter(subjectAdapter);//ربط السبنر بالوسيط
                readTaskFrom_FB();

            }
        });


        //معالج حدث لاختيار موضوع بالسبنر
        spnrSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                readTaskFrom_FB();
//                //استخراج الموضوع حسب رقمه الترتيبي i
//                String item = subjectAdapter.getItem(i);
//                if(item.equals("ALL"))//هذه يعني عرض جميع المهام
//                    initAllListView();
//                else {
//                    //استخراج كائن الموضوع الذي اخترناه لاستخراج رقمه id
//                    MySubject subject = subjectQuery.checkSubject(item);
//                    //استدعاء العملية التي تجهز القائمة حسب رقم الموضوع id
//                    initListViewBySubjId(subject.getKey_id());
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    /**
     * تجهيز قائمة المهمات حسب رقم الموضوع
     * @param key_id رقم الموضوع
     */
    private void initListViewBySubjId(long key_id)
    {
        AppDataBase db=AppDataBase.getDB(getApplicationContext());
        MyTaskQuery taskQuery = db.getMyTaskQuery();
                                //يجب اضافة عملية تعيد جميع المهمات حسب رقم الموضوع
        List<MyTask> allTasks = taskQuery.getTasksBySubjId(key_id);

        ArrayAdapter<MyTask> taksAdapter=new ArrayAdapter<MyTask>(this, android.R.layout.simple_list_item_1);
        taksAdapter.addAll(allTasks);
        lstTasks.setAdapter(taksAdapter);
    }

    /**
     * دالة مساعدة لفتح قائمة تتلقى
     * بارمترا للكائن الذي سبب فتح القائمة
     * @param v
     * @param item
     */
    public void showPopUpMenu(View v, MyTask item)
    {
        //بناء قائمة popup menu
        PopupMenu popup = new PopupMenu(this, v);//v الكائن الذي سبب فتح القائمة
                        //ملف القائمة
        popup.inflate(R.menu.popup_menu);
        //اضافة معالج حدث لاختيار عنصر من القائمة
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.mnAddTask)
                {
                    //هنا نكتب رد الفعل لاختيار هذا العنصر من القائمة
                    Toast.makeText(MainActivity.this, "To Add", Toast.LENGTH_SHORT).show();

                }
                if(menuItem.getItemId()==R.id.mnDelete)
                {
//                    AppDataBase db = AppDataBase.getDB(MainActivity.this);
//                    MyTaskQuery myTaskQuery = db.getMyTaskQuery();
//                    myTaskQuery.deleteTask(item.keyId);
                    Toast.makeText(MainActivity.this, "To del (isn't completed)", Toast.LENGTH_SHORT).show();
//                    initAllListView();
//                    initSubjectSpnr();
                     FirebaseFirestore db=FirebaseFirestore.getInstance();
                    db.collection("MyUsers").
                            document(FirebaseAuth.getInstance().getUid()).
                            collection("subjects").
                            document(spnrSubject.getSelectedItem().toString()).
                            collection("Tasks").document(item.id).
                            delete().
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        readTaskFrom_FB();
                                        Toast.makeText(MainActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            
                }
                if(menuItem.getItemId()==R.id.mnEdit)
                {
                    Toast.makeText(MainActivity.this, "To Edit", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        popup.show();//فتح وعرض القائمة
    }

    /**
     * بناء قائمة التي تفتح من النقاط الثلاثة بزاوية الشاشة
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {                            //اسم ملف القئمة
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     * معالج حدث اختيار عنصر من عناصر القائمة التس تفتح من النقاط الثلاثة بزاوية الشاشة
     * @param item العنصر لاذي تم اختياره من القائمة
     * @return
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item)
    {      //فحص العنصر الذي سبب الحدث حسب ال id
        if(item.getItemId()==R.id.mnSettings)
        {
            Toast.makeText(this, "Settingds", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.mnLogout)
        {
            showYesNoDialog();
        }
        if(item.getItemId()==R.id.mnPlayMusic)
        {
            Toast.makeText(this, "Play  music", Toast.LENGTH_SHORT).show();
            Intent serviceIntn=new Intent(getApplicationContext(),MyAudioPlayerService.class);
            startService(serviceIntn);

        }
        if(item.getItemId()==R.id.mnStopMusic)
        {
            Toast.makeText(this, "Stop Music", Toast.LENGTH_SHORT).show();
            Intent serviceIntn=new Intent(getApplicationContext(),MyAudioPlayerService.class);
            stopService(serviceIntn);
        }
        return true;
    }

    /**
     * بناء ديالوج
     */
    public void showYesNoDialog()
    {
        //تجهيز بنّاء شباك حوار "ديالوغ" يتلقى بارمتر مؤشر للنشاط (اكتيفيتي) الحالي
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out");// تحديد العنوان
        builder.setMessage("Are you sure?");// تحدي فحوى شباك الحوار
                               //النض على الزر ومعالج الحدث
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //معالجة خدث للموافقة
                Toast.makeText(MainActivity.this, "Signing out", Toast.LENGTH_SHORT).show();

                FirebaseAuth.getInstance().signOut();

                finish();
            }
        });
                                    //النض على الزر ومعالج الحدث
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //معالجة خدث للموافقة
                Toast.makeText(MainActivity.this, "Signing out", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();//بناء شباك الحوار -ديالوغ
        dialog.show();//عرض الشباك
    }



    private void downloadImageUsingPicasso(String imageUrL, ImageView toView)
    {
//        Picasso.with(getContext())
//                .load(imageUrL)
//                .centerCrop()
//                .error(R.drawable.common_full_open_on_phone)
//                .resize(90,90)
//                .into(toView);
    }

    private void downloadImageToLocalFile(String fileURL, final ImageView toView) {
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        final File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                Toast.makeText(getApplicationContext(), "downloaded Image To Local File", Toast.LENGTH_SHORT).show();
                toView.setImageURI(Uri.fromFile(localFile));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(), "onFailure downloaded Image To Local File "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });
    }
    private void downloadImageToMemory(String fileURL, final ImageView toView)
    {
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                toView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 90, 90, false));
                Toast.makeText(getApplicationContext(), "downloaded Image To Memory", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(), "onFailure downloaded Image To Local File "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });

    }


    public boolean deleteFile(String fileURL) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(getApplicationContext(), "file deleted", Toast.LENGTH_SHORT).show();
                Log.e("firebasestorage", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(getApplicationContext(), "onFailure: did not delete file "+exception.getMessage(), Toast.LENGTH_SHORT).show();

                Log.e("firebasestorage", "onFailure: did not delete file"+exception.getMessage());
                exception.printStackTrace();
            }
        });
        return false;
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//בדיקת גרסאות
            //בדיקה אם ההשאה לא אושרה בעבר
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                //רשימת ההרשאות שרוצים לבקש אישור
                String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
                //בקשת אישור ההשאות (שולחים קוד הבקשה)
                //התשובה תתקבל בפעולה onRequestPermissionsResult
                requestPermissions(permissions, PERMISSION_CODE);
            }
        }
    }


}