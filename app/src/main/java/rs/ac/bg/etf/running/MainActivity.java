package rs.ac.bg.etf.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
import rs.ac.bg.etf.running.location.LocationViewModel;
import rs.ac.bg.etf.running.login.LoginFragment;
import rs.ac.bg.etf.running.users.Session;
import rs.ac.bg.etf.running.users.UserViewModel;
import rs.ac.bg.etf.running.workouts.WorkoutListFragmentDirections;
//import rs.ac.bg.etf.running.workouts.WorkoutListFragmentDirections;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "running-app-example";

    public static final String INTENT_ACTION_WORKOUT = "rs.ac.bg.etf.running.WORKOUT";

    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private UserViewModel userViewModel;
    private LocationViewModel locationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, null,
                R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
     //   drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        Session.setMainActivity(this);
        createNotificationChannel();

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        if(Session.getCurrentUser() == null){
            SharedPreferences mPrefs = getSharedPreferences("username", MODE_PRIVATE);
            String username = mPrefs.getString(LoginFragment.USERNAME_KEY, "");
            try {
                User user = getUser(username);
                Session.setCurrentUser(user);

                TextView usernameView = ((NavigationView) findViewById(R.id.navigation_view)).getHeaderView(0)
                        .findViewById(R.id.username);
                usernameView.setText(username);

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        locationViewModel.getLocations().observe(this, l -> {
              Session.setCurrentLocations(l);
           // Toast.makeText(this, "DOHV LISTU", Toast.LENGTH_SHORT).show();
        });


        if (savedInstanceState == null) {
            setupNavigation(true);
        }

        if(getIntent() != null && getIntent().getAction() != null) {
            if (getIntent().getAction().equals(INTENT_ACTION_WORKOUT)) {
                NavController navController = NavigationDrawerUtil
                        .changeNavHostFragment(R.id.nav_graph_workouts);
                if (navController != null) {
                    navController.navigate(WorkoutListFragmentDirections.startWorkout());
                }
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setupNavigation(false);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private User getUser(String username) throws ExecutionException, InterruptedException {
        Callable<User> callable = () -> userViewModel.findUserByUsername(username);

        Future<User> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    private void setupNavigation(boolean firstTime) {
        int[] navResourceIds = new int[]{
                R.navigation.navigation_routes,
                R.navigation.navigation_workouts,
                R.navigation.navigation_calories,
                R.navigation.navigation_playlist,
                R.navigation.navigation_notification,
                R.navigation.navigation_stats
        };

        NavigationDrawerUtil.setup(
                binding.navigationView,
                getSupportFragmentManager(),
                navResourceIds,
                R.id.nav_host_container,
                this,
                firstTime
        );

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyAlarm",
                    "AlarmChannel", importance);
            channel.setDescription("Channel for alarm");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public DrawerLayout getDrawer() {
        return drawerLayout;
    }
}