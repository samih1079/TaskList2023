package com.example.tasklist2023.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

/**
 * כיתת עזר לעבודה עם מסד הנתונים<br>
 * כולל קבועים ומתודות עזר לעבודה עם מסד הנתונים
 * @author amit
 *
 */
public class MyRTDBUtils
{
    //כתובת מסד הניתונים
    public  static final FirebaseDatabase dataRef=FirebaseDatabase.getInstance();
    public static final FirebaseAuth authRef=FirebaseAuth.getInstance();
    public static final FirebaseStorage storageRef=FirebaseStorage.getInstance();


    // קבוצת המשתמשים
    public final static  String USERS="Users";
    // קבוצת המטלות
    public final static  String TASKS="Tasks";
    // קבוצת הנושאים
    public final static  String SUBJECTS="Subjects";
    // קבוצת הקבוצות
    public final static  String GROUPS="Groups";

   public static DatabaseReference getUserRef()
   {
       return dataRef.getReference(USERS).child(authRef.getCurrentUser().getUid());
   }

}