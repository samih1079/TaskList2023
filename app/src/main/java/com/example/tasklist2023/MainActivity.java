package com.example.tasklist2023;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tasklist2023.data.AppDataBase;
import com.example.tasklist2023.data.mySubjectsTable.MySubject;
import com.example.tasklist2023.data.mySubjectsTable.MySubjectQuery;
import com.example.tasklist2023.data.mytasksTable.MyTask;
import com.example.tasklist2023.data.mytasksTable.MyTaskQuery;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

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
    @Override
    protected void onResume() {
        super.onResume();
        initAllListView();
        initSubjectSpnr();
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

                }
                if(menuItem.getItemId()==R.id.mnDelete)
                {
                    AppDataBase.getDB(MainActivity.this).getMyTaskQuery().deleteTask(item.keyId);
                    initAllListView();
                    initSubjectSpnr();
                }
                if(menuItem.getItemId()==R.id.mnDelete)
                {

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
        }
        if(item.getItemId()==R.id.mnLogout)
        {
            showYesNoDialog();
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








}