package com.example.tasklist2023.data.mytasksTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
/**
 * فئة تُمثل مهمة
 */
@Entity
public class MyTask
{
    public String id;
    public String sbjId;
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
    public long subjId;
    /**رقم المستعمل الذي اضاف المهمة*/
    public long userId;
    //عنوان الصورة
    private String image;

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

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
