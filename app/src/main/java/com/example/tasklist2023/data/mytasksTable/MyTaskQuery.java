package com.example.tasklist2023.data.mytasksTable;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao   //واجهة استعلامات على جدول معطيات
public interface MyTaskQuery {
    @Query("SELECT * FROM MyTask")
    List<MyTask> getAllTasks();

    @Insert
    void insertTask(MyTask... t);  // ثلاثة نقاط  تعني ادخال مجموهة
}