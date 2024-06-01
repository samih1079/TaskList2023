package com.example.tasklist2023;

import static android.Manifest.permission.INTERNET;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

//שירות להפעלת אודיו/קול בקרע
public class MyAudioPlayerService extends Service {
    private MediaPlayer mediaPlayer;//נגן מידיה
    private boolean isPlaying;// משתנה לשמור אם הנגן מנגן או עצר

    public MyAudioPlayerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(checkSelfPermission(INTERNET)== PackageManager.PERMISSION_DENIED)
        {
            
        }
        isPlaying=false;//נמצא במצב ללא נגינה
    }

    /**
     *  בפעולה זו מתחיל השירות ומכיל את הקוד שיפעל ברקע
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mediaPlayer==null && !isPlaying)
        {
            playAudioFromUrl("https://on.soundcloud.com/nBR3h81rdGyQXPLj9");
            isPlaying=true;
        }
        return START_STICKY;
    }

    /**
     * פעולה זו מתבצעת כאשר עוצרים את השירות
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null && isPlaying)
        {
            mediaPlayer.release();
            isPlaying=false;
            mediaPlayer=null;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * הפעלת אודיו מקישור באינטרנט
     * @param url קישור לכותב קובץ באינטרנט
     */
    private void playAudioFromUrl(String url)
    {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void palyAudioFromRaw(int fileRes)
    {
        //יצירת נגן אודיו וקביעת קובץ האודיו
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.audio_halal1);
        mediaPlayer.start();
    }
}