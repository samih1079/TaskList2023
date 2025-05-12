package com.example.tasklist2023.data.mySubjectsTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * ממשק פעולות עם טבלת הנושאים במסד הניתונים
 */
@Dao
public interface MySubjectQuery {
    /**
     * اعادة جميع معطيات جدول المواضيع
     * @return * قائمة من المواضيع
     */
    @Query("SELECT * FROM MySubject")
    List<MySubject> getAllSubjects();

    /**
     * ادخال مهمات
     * @param s * مجموعة مهمات
     */
    @Insert
    void insert(MySubject...s);  // ثلاثة نقاط  تعني  مجموهة

    /**
     * تعديل المهعمات
     * @param s
    @Update
    void update(MySubject... s);

    /**
     * حذفق مهمة او مهمات
     * @param s * حذف المهمات (حسب المفتاح الرئيسي)
     */
    @Delete
    void deleteTask(MySubject...s);

    @Query("DELETE FROM MySubject WHERE key_id=:keyid")
    void delete(long keyid);
    @Query("SELECT * From MySubject WHERE title=:sub")
    MySubject checkSubject(String sub);
}
