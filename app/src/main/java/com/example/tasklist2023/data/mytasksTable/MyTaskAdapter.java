package com.example.tasklist2023.data.mytasksTable;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;

import com.example.tasklist2023.R;
import com.example.tasklist2023.data.MyTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * אוסף ניתונים ומתאם בין הניתונים לרכיב גרפי שמציג אוסף ניתונים
 */
public class MyTaskAdapter extends ArrayAdapter<MyTask> {
    //המזהה של קובץ עיצוב הפריט
    private final int itemLayout;
    private MyFilter myfilter;
    ArrayList<MyTask> orginal=new ArrayList<>();

    public void setOrginal(ArrayList<MyTask> orginal) {
        this.orginal = new ArrayList<>(orginal);
    }

    /**
     * פעולה בונה מתאם
     * @param context קישור להקשר (מסך- אקטיביטי)
     * @param resource עיצוב של פריט שיציג הנתונים של העצם
     */
    public MyTaskAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout =resource;
    }
    /**
     * בונה פריט גרפי אחד בהתאם לעיצוב והצגת נתוני העצם עליו
     * @param position מיקום הפריט החל מ 0
     * @param convertView
     * @param parent רכיב האוסף שיכיל את הפריטים כמו listview
     * @return  . פריט גרפי שמציג נתוני עצם אחד
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //בניית הפריט הגרפי מתו קובץ העיצוב
        View vitem=convertView;
        if(vitem==null)
         vitem=convertView=LayoutInflater.from(getContext()).inflate(itemLayout,parent,false);
        //קבלת הפניות לרכיבים בקובץ העיצוב
        ImageView imageView=vitem.findViewById(R.id.imgVitm);
        TextView tvTitle=vitem.findViewById(R.id.tvItmTitle);
        TextView tvText=vitem.findViewById(R.id.tvItmText);
        TextView tvImportance=vitem.findViewById(R.id.tvItmImportance);
        ImageView btnSendSMS=vitem.findViewById(R.id.imgBtnSendSmsitm);
        ImageView btnEdit=vitem.findViewById(R.id.imgBtnEdititm);
        ImageView btnCall=vitem.findViewById(R.id.imgBtnCallitm);
        ImageView btnDel=vitem.findViewById(R.id.imgBtnDeleteitm);
        //קבלת הנתון (עצם) הנוכחי
        MyTask current=getItem(position);
        //הצגת הנתונים על שדות הריב הגרפי
        tvTitle.setText(current.getShortTitle());
        tvText.setText(current.getText());
        tvImportance.setText("Importance:"+current.getImportance());
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // openSendSmsApp(current.getText(),"");
                openSendWhatsAppV2(current.getText(),"");
            }
        });


        downloadImageUsingPicasso(current.getImage(),imageView);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();
                delMyTaskFromDB_FB(current);
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAPhoneNymber(current.getText());
            }
        });

        return vitem;

    }



    /**
     *  פתיחת אפליקצית שליחת sms
     * @param msg .. ההודעה שרוצים לשלוח
     * @param phone
     */
    public void openSendSmsApp(String msg, String phone)
    {
        //אינטנט מרומז לפתיחת אפליקצית ההודות סמס
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        //מעבירים מספר הטלפון
        smsIntent.setData(Uri.parse("smsto:"+phone));
        //ההודעה שנרצה שתופיע באפליקצית ה סמס
        smsIntent.putExtra("sms_body",msg);
        smsIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //פתיחת אפליקציית ה סמס
        getContext().startActivity(smsIntent);
    }
    /**
     *  פתיחת אפליקצית שליחת whatsapp
     * @param msg .. ההודעה שרוצים לשלוח
     * @param phone
     */
    public void openSendWhatsAppV1(String msg, String phone)
    {
        //אינטנט מרומז לפתיחת אפליקצית ההודות סמס
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        //מעבירים מספר הטלפון
       // sendIntent.setData(Uri.parse("smsto:"+phone));
        //ההודעה שנרצה שתופיע באפליקצית ה סמס
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("text/plain");
        sendIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //פתיחת אפליקציית ה סמס
        getContext().startActivity(sendIntent);
    }
    /**
     *  פתיחת אפליקצית שליחת whatsapp
     * @param msg .. ההודעה שרוצים לשלוח
     * @param phone
     */
    public void openSendWhatsAppV2(String msg, String phone)
    {
        //אינטנט מרומז לפתיחת אפליקצית ההודות סמס
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);;
        String url = null;
        try {
            url = "https://api.whatsapp.com/send?phone="+phone+"&text="+ URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
            Toast.makeText(getContext(), "there is no whatsapp!!", Toast.LENGTH_SHORT).show();
        }
        sendIntent.setData(Uri.parse(url));
        sendIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //פתיחת אפליקציית ה סמס
        getContext().startActivity(sendIntent);
    }

    /**
     * ביצוע שיחה למפסר טלפון
     * todo הוספת הרשאה בקובץ המניפיסט
     * <uses-permission android:name="android.permission.CALL_PHONE" />
     * @param phone מספר טלפון שרוצים להתקשר אליו
     */
    private void callAPhoneNymber(String phone)
    {
        //בדיקה אם יש הרשאה לביצוע שיחה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//בדיקת גרסאות
            //בדיקה אם ההשאה לא אושרה בעבר
            if (checkSelfPermission(getContext(),Manifest.permission.CALL_PHONE) == PermissionChecker.PERMISSION_DENIED) {
                //רשימת ההרשאות שרוצים לבקש אישור
                String[] permissions = {Manifest.permission.CALL_PHONE};
                //בקשת אישור הרשאות (שולחים קוד הבקשה)
                //התשובה תתקבל בפעולה onRequestPermissionsResult
                requestPermissions((Activity) getContext(),permissions, 100);
            }
            else
            {
                //אינטנט מרומז לפתיחת אפליקצית ההודות סמס
                Intent phone_intent = new Intent(Intent.ACTION_CALL);
                phone_intent.setData(Uri.parse("tel:" + phone));
                getContext().startActivity(phone_intent);
            }
        }

    }

    /**
     * הצגת תמונה ישירות מהענן בעזרת המחלקה ״פיקאסו״
     * @param imageUrL כתובת התמונה בענן/שרת
     * @param toView רכיב תמונה המיועד להצגת התמונה אחרי ההורדה
     */
    private void downloadImageUsingPicasso(String imageUrL, ImageView toView)
    {
        // אם אין תמונה= כתובת ריקה אז לא עושים כלום מפסיקים את הפעולה
        if(imageUrL==null) return;
        //todo: add dependency to module gradle:
        //    implementation 'com.squareup.picasso:picasso:2.5.2'
        Picasso.with(getContext())
                .load(imageUrL)//הורדת התמונה לפי כתובת
                .centerCrop()
                .error(R.drawable.androidparty)//התמונה שמוצגת אם יש בעיה בהורדת התמונה
                .resize(90,90)//שינוי גודל התמונה
                .into(toView);// להציג בריכיב התמונה המיועד לתמונה זו
    }

    /**
     * הורדת הקובץ/התמונה לאחסון מיקומי של הטלפון והגתה על רכיב תמונה
     * @param fileURL כתובת הקובץ באחסון הענן
     * @param toView רכיב התמונה המיועד להצגת התמונה
     */
    private void downloadImageToLocalFile(String fileURL, final ImageView toView) {
        if(fileURL==null) return;// אם אין תמונה= כתובת ריקה אז לא עושים כלום מפסיקים את הפעולה
        // הפניה למיקום הקובץ באיחסון
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        final File localFile;
        try {// יצירת קובץ מיקומי לפי שם וסוג קובץ
            localFile = File.createTempFile("images", "jpg");
            //הורדת הקובץ והוספת מאיזין שבודק אם ההורדה הצליחה או לא
            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Toast.makeText(getContext(), "downloaded Image To Local File", Toast.LENGTH_SHORT).show();
                    toView.setImageURI(Uri.fromFile(localFile));
                }
                //מאזין אם ההורדה נכשלה
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Toast.makeText(getContext(), "onFailure downloaded Image To Local File "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    exception.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * הורדת קובץ/תמונה לזיכרון של הטלפון (לא לאחסון)
     * @param fileURL כתובת הקובץ באחסון הענן
     * @param toView רכיב התמונה המיועד להצגת התמונה
     */
    private void downloadImageToMemory(String fileURL, final ImageView toView)
    {
        if(fileURL==null)return;
        // הפניה למיקום הקובץ באיחסון
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        final long ONE_MEGABYTE = 1024 * 1024;
        //הורדת הקובץ והוספת מאזין שבודק אם ההורדה הצליחה או לא
        httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                toView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 90, 90, false));
                Toast.makeText(getContext(), "downloaded Image To Memory", Toast.LENGTH_SHORT).show();

            }
            //מאזין אם ההורדה נכשלה
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getContext(), "onFailure downloaded Image To Local File "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });

    }

    /**
     * מחיקת פריט כולל התמונה מבסיס הנתונים
     * @param myTask הפריט שמוחקים
     */
    private void delMyTaskFromDB_FB(MyTask myTask)
    {
        //הפנייה/כתובת  הפריט שרוצים למחוק
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("MyUsers").
                document(FirebaseAuth.getInstance().getUid()).
                collection("subjects").
                document(myTask.sbjId).
                collection("Tasks").document(myTask.id).
                delete().//מאזין אם המחיקה בוצעה
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            remove(myTask);// מוחקים מהמתאם
                            deleteFile(myTask.getImage());// מחיקת הקובץ
                            Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * מחיקת קובץ האיחסון הענן
     * @param fileURL כתובת הקובץ המיועד למחיקה
     */
    private void deleteFile(String fileURL) {
        // אם אין תמונה= כתובת ריקה אז לא עושים כלום מפסיקים את הפעולה
        if(fileURL==null){
            Toast.makeText(getContext(), "Theres no file to delete!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        // הפניה למיקום הקובץ באיחסון
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        //מחיקת הקובץ והוספת מאזין שבודק אם ההורדה הצליחה או לא
      storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful())
              {
                  Toast.makeText(getContext(), "file deleted", Toast.LENGTH_SHORT).show();
              }
              else {
                  Toast.makeText(getContext(), "onFailure: did not delete file "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
              }
          }
      });
    }

    private void checkCallPhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//בדיקת גרסאות
            //בדיקה אם ההשאה לא אושרה בעבר
            if (checkSelfPermission(getContext(),Manifest.permission.CALL_PHONE) == PermissionChecker.PERMISSION_DENIED) {
                //רשימת ההרשאות שרוצים לבקש אישור
                String[] permissions = {Manifest.permission.CALL_PHONE};
                //בקשת אישור ההשאות (שולחים קוד הבקשה)
                //התשובה תתקבל בפעולה onRequestPermissionsResult
                requestPermissions((Activity) getContext(),permissions, 100);
            }
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if(myfilter==null)
             myfilter=new MyFilter();
        return myfilter;
    }
    class MyFilter extends Filter{
                    ArrayList<MyTask> filteredTasks=new ArrayList<>();

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            ArrayList<MyTask> temp=new ArrayList<>();

             int countChanges=0;
            for (int i = 0; i < getCount(); i++) {
                MyTask myTask = getItem(i);
                if(myTask. toString().toLowerCase().contains(charSequence.toString().toLowerCase()))
                {
                    countChanges++;
                    temp.add(myTask);
                }
            }
            if(charSequence.length()>0 ) {
                results.values = new ArrayList<>( temp);
                results.count = temp.size();
            }
            else {
                results.values = new ArrayList<>(orginal);
                results.count = orginal.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredTasks= (ArrayList<MyTask>) filterResults.values;
            clear();
            addAll(new ArrayList<>(filteredTasks));
            notifyDataSetChanged();

        }
    }

}
