package rs.ac.bg.etf.running;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.databinding.ActivityMainBinding;
//import rs.ac.bg.etf.running.workouts.WorkoutListFragmentDirections;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    public static final String LOG_TAG = "running-app-example";

    public static final String INTENT_ACTION_WORKOUT = "rs.ac.bg.etf.running.WORKOUT";

    private ActivityMainBinding binding;

    private ActionBar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = getSupportActionBar();
   //     navController = Navigation.findNavController(this, R.id.nav_host_container);

     //   NavigationUI.setupWithNavController(navigationView, navController);
      //  NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);

//        this.configureToolBar();
//
//        this.configureDrawerLayout();
//
//        this.configureNavigationView();





//        if (savedInstanceState == null) {
//            setupNavigation();
//        }
//
//        if (getIntent().getAction().equals(INTENT_ACTION_WORKOUT)) {
//            NavController navController = BottomNavigationUtil
//                    .changeNavHostFragment(R.id.nav_graph_workouts);
//            if (navController != null) {
//                navController.navigate(WorkoutListFragmentDirections.startWorkout());
//            }
//        }
    }

//    private void configureToolBar(){
//        this.toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//    }
//
//    // 2 - Configure Drawer Layout
//    private void configureDrawerLayout(){
//        this.drawerLayout = findViewById(R.id.drawerLayout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_app_bar_open_drawer_description, R.string.nav_app_bar_open_drawer_description);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //setupNavigation();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.navigation.navigation_routes :

                break;
            case R.navigation.navigation_calories:
                break;
            default:
                break;
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

//    private void setupNavigation() {
//        int[] navResourceIds = new int[]{
//                R.navigation.navigation_routes,
//                R.navigation.navigation_workouts,
//                R.navigation.navigation_calories
//        };
//        BottomNavigationUtil.setup(
//                binding.bottomNavigation,
//                getSupportFragmentManager(),
//                navResourceIds,
//                R.id.nav_host_container
//        );
//    }


}