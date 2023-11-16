package com.example.tasklist2023.data.mytasksTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
/**
 * فئة تُمثل مهمة
 */
@Entity
public class MyTask
{
    @PrimaryKey(autoGenerate = true)
    /** رقم المهمة */
    public long keyId;
    /** درجة الاهمية 1-5 */
    public int importance;
    /**عنوان قصير */
    public String shortTitle;
    /** نص المهمة */
    public String text;
    /**  زمن بناء المهمة*/
    public long time;
    /** هل تمت المهمة */
    public boolean isCompleted;
    /**رقم موضوع المهمة*/
    long subjId;
    /**رقم المستعمل الذي اضاف المهمة*/
    long userId;

    @Override
    public String toString() {
        return "MyTask{" +
                "keyId=" + keyId +
                ", importance=" + importance +
                ", text='" + text + '\'' +
                ", time=" + time +
                ", isCompleted=" + isCompleted +
                ", subjId=" + subjId +
                ", userId=" + userId +
                '}';
    }
}