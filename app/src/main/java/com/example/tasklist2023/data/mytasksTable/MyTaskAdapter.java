package com.example.tasklist2023.data.mytasksTable;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tasklist2023.R;

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
        ImageButton btnSendSMS=vitem.findViewById(R.id.imgBtnSendSmsitm);
        ImageButton btnEdit=vitem.findViewById(R.id.imgBtnEdititm);
        ImageButton btnCall=vitem.findViewById(R.id.imgBtnCallitm);
        ImageButton btnDel=vitem.findViewById(R.id.imgBtnDeleteitm);
        //קבלת הנתון (עצם) הנוכחי
        MyTask current=getItem(position);
        //הצגת הנתונים על שדות הריב הגרפי
        tvTitle.setText(current.getShortTitle());
        tvText.setText(current.getText());
        tvImportance.setText("Importance:"+current.getImportance());

        return vitem;

    }
}
