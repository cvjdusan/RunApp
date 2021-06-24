package rs.ac.bg.etf.running.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.rest.CurrentWeatherModel;
import rs.ac.bg.etf.running.users.Session;

public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CurrentWeatherModel currentWeatherModel = Session.getCurrentWeatherModel();

        String desc = "Temperature: " + currentWeatherModel.main.temp + " C";
        desc += ", Feels like: " + currentWeatherModel.main.feels_like + " C";
        desc += ", Humidity: " + currentWeatherModel.main.humidity + "%";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyAlarm")
                .setSmallIcon(R.drawable.baseline_directions_run_24)
                .setContentTitle("No excuses, workout!")
                .setContentText(desc)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, builder.build());
    }
}
