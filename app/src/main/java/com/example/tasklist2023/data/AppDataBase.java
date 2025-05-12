package com.example.tasklist2023.data;

import com.example.tasklist2023.data.mySubjectsTable.MySubject;
import com.example.tasklist2023.data.mySubjectsTable.MySubjectQuery;
import com.example.tasklist2023.data.mytasksTable.MyTaskQuery;
import com.example.tasklist2023.data.usersTable.MyUserQuery;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


/**
تعريف الجداول ورقم الاصدار
version
عند تغيير اي شي يخص جدول او داول علينا تغيير رقم الاصدار
ليتم بناء قاعدة البيانات من جديد
 */
@Database(entities = {MyUser.class, MySubject.class, MyTask.class},version = 8,exportSchema = false)
/**
 * الفئة المسؤولة عن بناء قاعدة البايانات بكل جداولها
 * وتوفر لنا كائن للتعامل مع قاعدة البيانات
 */
public abstract class  AppDataBase extends RoomDatabase
{

    /**
     * كائن للتعامل مع قاعدة البيانات
     */
    private static AppDataBase db;

    /**
     * يعيد كائن لعمليات جدول المستعملين
     * @return ..  يعيد كائن لعمليات جدول المستعملين
     */
    public abstract MyUserQuery getMyUserQuery();

    /**
     * يعيد كائن لعمليات جدول الموضيع
     * @return .. يعيد كائن لعمليات جدول الموضيع
     */
    public abstract MySubjectQuery getMySubjectQuery();

    /**
     * يعيد كائن لعمليات جدول المهمات
     * @return ..      * يعيد كائن لعمليات جدول المهمات
     */
    public abstract MyTaskQuery getMyTaskQuery();

    /**
     * بناء قاعدة البيانات واعادة كائن يؤشر عليها
     * @param context היקשר (אפליקציה, מסך..) ששיך לא מסד הניתונים
     * @return ..      * بناء قاعدة البيانات واعادة كائن يؤشر عليها
     */
    public static AppDataBase getDB(Context context)
    {
        if(db==null)
        {
            db = Room.databaseBuilder(context, AppDataBase.class, "samihDataBase")//اسم قاعدة اليانات
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return db;
    }

}
