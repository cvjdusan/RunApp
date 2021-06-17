package rs.ac.bg.etf.running.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import rs.ac.bg.etf.running.databinding.FragmentAlarmBinding;
import rs.ac.bg.etf.running.workouts.WorkoutService;

@AndroidEntryPoint
public class AlarmFragment extends Fragment {

    private FragmentAlarmBinding binding;
    private NavController navController;
    private MainActivity mainActivity;

    public AlarmFragment() {
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
        // Inflate the layout for this fragment
        binding = FragmentAlarmBinding.inflate(inflater, container, false);
        String[] weekdays = getResources().getStringArray(R.array.days);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                mainActivity,
                android.R.layout.simple_list_item_1,
                weekdays);

        binding.spinner.setAdapter(arrayAdapter);

        binding.confirm.setOnClickListener(view -> {
            Intent intent = new Intent(mainActivity, AlarmBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, 0, intent, 0);

            Intent intentLocation = new Intent();
            intentLocation.setClass(mainActivity, WorkoutService.class);
            intentLocation.setAction(WorkoutService.INTENT_ACTION_LOCATION);
            mainActivity.startService(intentLocation);

            AlarmManager alarmManager = (AlarmManager) mainActivity.getSystemService(mainActivity.ALARM_SERVICE);

            int days[] = {
                    Calendar.MONDAY,
                    Calendar.TUESDAY,
                    Calendar.WEDNESDAY,
                    Calendar.THURSDAY,
                    Calendar.FRIDAY,
                    Calendar.SATURDAY,
                    Calendar.SUNDAY
            };

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

            Long alarmTime = alarmCalendar.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 7 * AlarmManager.INTERVAL_DAY, pendingIntent);

            Toast.makeText(mainActivity,
                    "You set an alarm for " + hour + ":" + minute + " on every " + daysString[binding.spinner.getSelectedItemPosition()],
                    Toast.LENGTH_SHORT).show();

        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}