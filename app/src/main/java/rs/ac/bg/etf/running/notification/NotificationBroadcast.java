package rs.ac.bg.etf.running.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.rest.CurrentWeatherModel;
import rs.ac.bg.etf.running.users.Session;

public class NotificationBroadcast extends BroadcastReceiver {

    private static String TAG = "TAG_AL";

    @Override
    public void onReceive(Context context, Intent intent) {

        CurrentWeatherModel currentWeatherModel = Session.getCurrentWeatherModel();

        String desc = "";
        if(currentWeatherModel != null) {
            desc = "Temperature: " + currentWeatherModel.main.temp + " C\n";
            desc += ", Feels like: " + currentWeatherModel.main.feels_like + " C\n";
            desc += ", Humidity: " + currentWeatherModel.main.humidity + "%\n";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyAlarm")
                .setSmallIcon(R.drawable.baseline_directions_run_24)
                .setContentTitle("No excuses, workout!")
                .setContentText(desc)
                .setColorized(true)
                .setColor(Color.RED)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());

    }


}
