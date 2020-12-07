package com.grzegorzszawula.forser;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Timer;
import java.util.TimerTask;

public class MyForegroundService  extends Service {


    //1. Kanał notyfikacji
    public static final String CHANNEL_ID = "MyForegroundServiceChannel";
    public static final String CHANNEL_NAME = "FoSer service channel";

    //2. Odczyt danych zapisanych w Intent
    public static final String MESSAGE = "message";
    public static final String TIME = "time";
    public static final String WORK = "work";
    public static final String WORK_DOUBLE = "work_double";
    public static final String OPCJA2="2s";
    public static final String OPCJA5="5s";
    public static final String OPCJA10="10s";
    public  static  final String RESET_COUNTER="reset_counter"; //tu!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    //3
    private String message = "Nasz licznik";
    private Boolean show_time, do_work, double_speed, opcja2s, opcja5s, opcja10s, reset_counter=false;
    private final long period2s = 2000; //2s
    private final long period5s = 5000; //5s
    private final long period10s = 10000; //10s
    //4
    private Context ctx;
    private Intent notificationIntent;
    private PendingIntent pendingIntent;

    //5.
    private int counter;
    private Timer timer;
    private TimerTask timerTask;
    final Handler handler = new Handler(); //handler - umożliwia komunikacje miedzy wątkami


    @Override
    public void onCreate() {
        ctx = this;
        notificationIntent = new Intent(ctx, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        final SharedPreferences preferences=androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        reset_counter=preferences.getBoolean("reset_counter", false);

        if(reset_counter)
        {
            counter=preferences.getInt("counter", 0);

        }
        else
        {
            counter=0;
        }
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                counter++;
                handler.post(runnable);
            }
        };

    }

    @Override
    public void onDestroy() {

        final SharedPreferences preferences=androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putInt("counter", counter).apply();
        handler.removeCallbacks(runnable);
        timer.cancel();
        timer.purge();
        timer = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        message = intent.getStringExtra(MESSAGE);
        show_time = intent.getBooleanExtra(TIME,false);
        do_work = intent.getBooleanExtra(WORK,false);
        double_speed = intent.getBooleanExtra(WORK_DOUBLE,false);
        opcja2s=intent.getBooleanExtra(OPCJA2, true);
        opcja5s=intent.getBooleanExtra(OPCJA5, false);
        opcja10s=intent.getBooleanExtra(OPCJA10, false);
        reset_counter=intent.getBooleanExtra(RESET_COUNTER, false);

        createNotificationChannel();

        /*Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);*/


        Notification notification = new Notification.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_my_icon)
                .setContentTitle(getString(R.string.ser_title))
                .setShowWhen(show_time)
                .setContentText(message)
                .setLargeIcon(BitmapFactory.decodeResource (getResources() , R.drawable.circle ))
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1,notification);

        doWork();

        return START_NOT_STICKY;
    }

    private void doWork() {
       /* try {
            Thread.sleep(5000);

        } catch (Exception e) {
            //
        }

        String info = "Start working..."
                +"\n show_time=" + show_time.toString()
                +"\n do_work=" + do_work.toString()
                +"\n double_speed=" + double_speed.toString();

        Toast.makeText(this, info ,Toast.LENGTH_LONG).show();*/
        if(do_work) {
            if(double_speed==true)
            {
                if(opcja2s==true)
                {
                    timer.schedule(timerTask, 0L, period2s / 2L);
                }
                if(opcja5s==true)
                {
                    timer.schedule(timerTask, 0L, period5s / 2L);
                }
                if(opcja10s==true)
                {
                    timer.schedule(timerTask, 0L, period10s / 2L);
                }
            }
            else
            {
                if(opcja2s==true)
                {
                    timer.schedule(timerTask, 0L, period2s );
                }
                if(opcja5s==true)
                {
                    timer.schedule(timerTask, 0L, period5s );
                }
                if(opcja10s==true)
                {
                    timer.schedule(timerTask, 0L, period10s);
                }
            }
            /*timer.schedule(timerTask, 0L, double_speed ? period2s / 2L : period2s);*/
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    final Runnable runnable=new Runnable() {
        @Override
        public void run() {
            Notification notification = new Notification.Builder(ctx, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_my_icon)
                    .setContentTitle(getString(R.string.ser_title))
                    .setShowWhen(show_time)
                    .setContentText("Nasz licznik:" + " " + String.valueOf(counter) + "\n ")
                    .setLargeIcon(BitmapFactory.decodeResource (getResources() , R.drawable.circle ))
                    .setContentIntent(pendingIntent)
                    .build();

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.notify(1,notification);
        }
    };

}
