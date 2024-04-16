package com.example.tasklist2023.data.mytasksTable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
/**
 * فئة تُمثل مهمة
 */
@Entity
public class MyTask
{
    /**
     * מ;הה יחודי למטלה לצורך אחסון במסד ניתונים
     */
    public String id;
    /**
     * מזהה ייחודי של נושא המטלה
     */
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSbjId() {
        return sbjId;
    }

    public void setSbjId(String sbjId) {
        this.sbjId = sbjId;
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getSubjId() {
        return subjId;
    }

    public void setSubjId(long subjId) {
        this.subjId = subjId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
