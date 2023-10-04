package com.example.tasklist2023.data.mySubjectsTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class MySubject
{
    @PrimaryKey(autoGenerate = true)
    public long key_id;
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
