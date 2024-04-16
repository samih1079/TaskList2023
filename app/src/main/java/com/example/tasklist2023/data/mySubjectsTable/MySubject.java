package com.example.tasklist2023.data.mySubjectsTable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * מחלקה מייצגת נושא של קבוצת מטלות
 */
@Entity
public class MySubject
{
    /**
     * מזהה ייחדי למטלה
     */
    public String id;
    /**
     * מזהה של המשתמש שהוסיף את המטלה
     */
    public String userId;
    /**
     * מזהה ייחודי לאחסון במודל טבלאי
     */
    @PrimaryKey(autoGenerate = true)
    public long key_id;
    /**
     * כותרת המטלה
     */
    public String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getKey_id() {
        return key_id;
    }
}
