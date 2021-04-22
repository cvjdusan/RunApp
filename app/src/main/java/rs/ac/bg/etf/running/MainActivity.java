package rs.ac.bg.etf.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
import rs.ac.bg.etf.running.workouts.WorkoutListFragmentDirections;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "running-app-example";

    public static final String INTENT_ACTION_WORKOUT = "rs.ac.bg.etf.running.WORKOUT";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (savedInstanceState == null) {
            setupNavigation();
        }
//
//        if (getIntent().getAction().equals(INTENT_ACTION_WORKOUT)) {
//            NavController navController = BottomNavigationUtil
//                    .changeNavHostFragment(R.id.nav_graph_workouts);
//            if (navController != null) {
//                navController.navigate(WorkoutListFragmentDirections.startWorkout());
//            }
//        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setupNavigation();
    }

    private void setupNavigation() {
        int[] navResourceIds = new int[]{
                R.navigation.navigation_routes,
                R.navigation.navigation_workouts,
                R.navigation.navigation_calories
        };
        BottomNavigationUtil.setup(
                binding.bottomNavigation,
                getSupportFragmentManager(),
                navResourceIds,
                R.id.nav_host_container
        );
    }

}