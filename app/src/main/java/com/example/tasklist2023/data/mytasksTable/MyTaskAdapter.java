package com.example.tasklist2023.data.mytasksTable;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tasklist2023.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

/**
 * אוסף ניתונים ומתאם בין הניתונים לרכיב גרפי שמציג אוסף ניתונים
 */
public class MyTaskAdapter extends ArrayAdapter<MyTask> {
    //המזהה של קובץ עיצוב הפריט
    private final int itemLayout;
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
        View vitem= convertView;
        if(vitem==null)
              vitem=LayoutInflater.from(getContext()).inflate(itemLayout,parent,false);
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

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openSendSmsApp(current.getText(),"");
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
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:"+phone));
        smsIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
        smsIntent.putExtra("sms_body",msg);
        getContext().startActivity(smsIntent);
    }

    private void downloadImageUsingPicasso(String imageUrL, ImageView toView)
    {
        //todo: add dependency to module gradle:
        //    implementation 'com.squareup.picasso:picasso:2.5.2'
        Picasso.with(getContext())
                .load(imageUrL)
                .centerCrop()
                .error(R.drawable.androidparty)
                .resize(90,90)
                .into(toView);
    }

    private void downloadImageToLocalFile(String fileURL, final ImageView toView) {
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        final File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");


            httpsReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Toast.makeText(getContext(), "downloaded Image To Local File", Toast.LENGTH_SHORT).show();
                    toView.setImageURI(Uri.fromFile(localFile));
                }
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
    private void downloadImageToMemory(String fileURL, final ImageView toView)
    {
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                toView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 90, 90, false));
                Toast.makeText(getContext(), "downloaded Image To Memory", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getContext(), "onFailure downloaded Image To Local File "+exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });

    }


    private void deleteFile(String fileURL) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Toast.makeText(getContext(), "file deleted", Toast.LENGTH_SHORT).show();
                Log.e("firebasestorage", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(getContext(), "onFailure: did not delete file "+exception.getMessage(), Toast.LENGTH_SHORT).show();

                Log.e("firebasestorage", "onFailure: did not delete file"+exception.getMessage());
                exception.printStackTrace();
            }
        });
    }
}
