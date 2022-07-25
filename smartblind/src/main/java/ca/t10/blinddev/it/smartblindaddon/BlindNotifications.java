package ca.t10.blinddev.it.smartblindaddon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class BlindNotifications {
    Context context;
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;

    public BlindNotifications(Context context) {
        this.context = context;

    }

    public void enableNotifications(String message){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("note","app", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"note")
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle("Smart Blinds")
                .setContentText(message);//message from other calls will go here.
        notification = builder.build();

        notificationManagerCompat = NotificationManagerCompat.from(context);
    }
    public void pushNotification(){
        notificationManagerCompat.notify(1,notification);
    }
}
