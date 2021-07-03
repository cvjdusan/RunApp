package rs.ac.bg.etf.running.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentNotificationBinding;
import rs.ac.bg.etf.running.workouts.WorkoutService;

@AndroidEntryPoint
public class NotificationFragment extends Fragment {

    private FragmentNotificationBinding binding;
    private NavController navController;
    private MainActivity mainActivity;

    long alarmTime;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        String[] daysOfWeek = getResources().getStringArray(R.array.days);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mainActivity,
                android.R.layout.simple_list_item_1,
                daysOfWeek);

        binding.spinner.setAdapter(arrayAdapter);

        int[] days = {
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY,
                Calendar.SATURDAY,
                Calendar.SUNDAY
        };

        binding.confirm.setOnClickListener(view -> {
            Intent intent = new Intent(mainActivity, NotificationBroadcast.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, 0, intent, 0);

            Intent intent2 = new Intent();
            intent2.setClass(mainActivity, WorkoutService.class);
            intent2.setAction(WorkoutService.INTENT_ACTION_LOCATION);
            mainActivity.startService(intent2);

            AlarmManager alarmManager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);

            String[] daysString = getResources().getStringArray(R.array.days);
            int[] daysValue = getResources().getIntArray(R.array.days_values);
            int weekdayValue = daysValue[binding.spinner.getSelectedItemPosition()];

            int hour = binding.timePicker.getHour();
            int minute = binding.timePicker.getMinute();
            int day = days[weekdayValue];

            Calendar alarmCalendar = Calendar.getInstance();

            alarmCalendar.set(Calendar.DAY_OF_WEEK, day);
            alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
            alarmCalendar.set(Calendar.MINUTE, minute);

            alarmTime = alarmCalendar.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 7 * AlarmManager.INTERVAL_DAY, pendingIntent);

            String stringDay = daysString[binding.spinner.getSelectedItemPosition()];
            Toast.makeText(mainActivity, "You will receive notifications at " + hour + ":" + minute + " on every " + stringDay, Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    public void SetAlarm(Context context){
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, NotificationFragment.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 7 * AlarmManager.INTERVAL_DAY, pi);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}