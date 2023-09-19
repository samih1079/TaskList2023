package com.example.tasklist2023.data.usersTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * واجهة تحوي عمليات\دوال\استعلامات على قاعدة البيانات
 */
@Dao//لتحديد ان الواجهخة تحوي استعلامات على قاعدة بايانات
public interface MyUserQuery
{
    @Query("SELECT * FROM MyUser")
    List<MyUser> getAll();

    @Query("SELECT * FROM MyUser WHERE keyid IN (:userIds)")
    List<MyUser> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM MyUser WHERE email = :myEmail AND " +
            "passw = :myPassw LIMIT 1")
    MyUser checkEmailPassw(String myEmail, String myPassw);

    @Insert
    void insertAll(MyUser... users);

    @Delete
    void delete(MyUser user);

    @Query("Delete From MyUser WHERE keyid=:id ")
    void delete(int id);

    @Insert
    void insert(MyUser myUser);
    @Update
    void update(MyUser...values);


}
