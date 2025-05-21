package com.example.tasklist2023.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


public class MyRTDBUtils
{
    // מסד הנתונים לאחסון נתונים בענן
    public static FirebaseDatabase dbRef = FirebaseDatabase.getInstance();
    // הפניה לשורש המסד הנתונים
    public static DatabaseReference rootRef = dbRef.getReference();
    // הפניה למנגנון ההרשמה
    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    // הפניה למנגנון האחסון של קבצים
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    // מזהה המשתמש הנוכחי
    public static String uid = auth.getCurrentUser().getUid();

    public  static final  String USERS="USERS_GROUP";
    public  static final  String TASKS="TASKS_GROUP";
    public  static final  String SUBJECTS="SUBJECTS_GROUP";
    public  static final  String IMAGES="IMAGES_GROUP";

    public static DatabaseReference userRef = rootRef.child(USERS).child(uid);
    public static DatabaseReference tasksRef = rootRef.child(TASKS).child(uid);
    public static DatabaseReference subjectsRef = rootRef.child(SUBJECTS).child(uid);
    public static DatabaseReference imagesRef = rootRef.child(IMAGES).child(uid);
}


