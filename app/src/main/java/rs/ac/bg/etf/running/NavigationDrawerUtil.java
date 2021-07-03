package rs.ac.bg.etf.running;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.navigation.NavigationView;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import rs.ac.bg.etf.running.login.LoginFragment;
import rs.ac.bg.etf.running.routes.RouteViewModel;
import rs.ac.bg.etf.running.users.Session;
import rs.ac.bg.etf.running.workouts.WorkoutService;
import rs.ac.bg.etf.running.workouts.WorkoutStartFragment;

public class NavigationDrawerUtil {

    private interface NavHostFragmentChanger {
        NavController change(int id);
    }

    private static NavigationDrawerUtil.NavHostFragmentChanger navHostFragmentChanger;

    public static void setup(
            NavigationView navigationView,
            FragmentManager fragmentManager,
            int[] navResourceIds,
            int containerId,
            MainActivity mainActivity,
            boolean firstTime) {
        Map<Integer, String> navGraphIdToTagMap = new HashMap<>();
        int homeNavGraphId = 0;

        if(firstTime)
            navigationView.setCheckedItem(R.id.nav_graph_routes);

        for (int i = 0; i < navResourceIds.length; i++) {
            String tag = "navHostFragment#" + i;

            NavHostFragment navHostFragment = obtainNavHostFragment(
                    fragmentManager,
                    tag,
                    navResourceIds[i],
                    containerId
            );
            int navGraphId = navHostFragment.getNavController().getGraph().getId();

            navGraphIdToTagMap.put(navGraphId, tag);

            if (i == 0) {
                homeNavGraphId = navGraphId;
            }

            if (navigationView.getCheckedItem().getItemId() == navGraphId) {
                attachNavHostFragment(fragmentManager, navHostFragment, i == 0);
            } else {
                detachNavHostFragment(fragmentManager, navHostFragment);
            }
        }

        String homeTag = navGraphIdToTagMap.get(homeNavGraphId);

        AtomicReference<Boolean> isOnHomeWrapper = new AtomicReference<>(
                homeNavGraphId == navigationView.getCheckedItem().getItemId());

        AtomicReference<String> currentTagWrapper = new AtomicReference<>(
                navGraphIdToTagMap.get(navigationView.getCheckedItem().getItemId()));

        navHostFragmentChanger = id -> {
                if(id == R.id.logout){
                    Session.setCurrentUser(null);
                    mainActivity.getDrawer().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    SharedPreferences preferences = mainActivity.getSharedPreferences("checkbox", mainActivity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(LoginFragment.REMEMBER_USER_KEY, "false");
                    editor.apply();

                    Intent intent = new Intent();
                    intent.setClass(mainActivity, WorkoutService.class);
                    mainActivity.stopService(intent);
                    SharedPreferences sharedPreferences = mainActivity.getSharedPreferences(WorkoutStartFragment.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                    sharedPreferences.edit().remove(WorkoutStartFragment.START_TIMESTAMP_KEY).commit();
                    sharedPreferences.edit().remove(WorkoutStartFragment.CURRENT_DURATION_KEY).commit();
                    sharedPreferences.edit().remove(WorkoutStartFragment.CURRENT_START_KEY).commit();
                    intent = new Intent(mainActivity, LoginActivity.class);
                    mainActivity.startActivity(intent);
                    mainActivity.finish();
                }
                else {
                    if (!fragmentManager.isStateSaved()) {
                        String dstTag = navGraphIdToTagMap.get(id);

                        navigationView.getMenu().findItem(id).setChecked(true);

                        NavHostFragment homeNavHostFragment = (NavHostFragment)
                                fragmentManager.findFragmentByTag(homeTag);
                        NavHostFragment dstNavHostFragment = (NavHostFragment)
                                fragmentManager.findFragmentByTag(dstTag);

                        if (!dstTag.equals(currentTagWrapper.get())) {
                            fragmentManager.popBackStack(homeTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            if (!dstTag.equals(homeTag)) {
                                fragmentManager.beginTransaction()
                                        .detach(homeNavHostFragment)
                                        .attach(dstNavHostFragment)
                                        .setPrimaryNavigationFragment(dstNavHostFragment)
                                        .addToBackStack(homeTag)
                                        .setReorderingAllowed(true)
                                        .commit();
                            }

                            currentTagWrapper.set(dstTag);
                            isOnHomeWrapper.set(dstTag.equals(homeTag));
                        }
                        return dstNavHostFragment.getNavController();
                    }
                }
                return null;
        };


        navigationView.setNavigationItemSelectedListener(
                menuItem -> navHostFragmentChanger.change(menuItem.getItemId()) != null);

        int finalHomeNavGraphId = homeNavGraphId;
        fragmentManager.addOnBackStackChangedListener(() -> {
            if (!isOnHomeWrapper.get() && !isOnBackStack(fragmentManager, homeTag)) {
                navigationView.setCheckedItem(finalHomeNavGraphId);
            }
        });

    }

    public static NavController changeNavHostFragment(int id) {
        return navHostFragmentChanger.change(id);
    }

    private static NavHostFragment obtainNavHostFragment(
            FragmentManager fragmentManager,
            String tag,
            int navResourceId,
            int containerId) {
        NavHostFragment existingNavHostFragment = (NavHostFragment)
                fragmentManager.findFragmentByTag(tag);
        if (existingNavHostFragment != null) {
            return existingNavHostFragment;
        }

        NavHostFragment newNavHostFragment = NavHostFragment.create(navResourceId);
        fragmentManager.beginTransaction()
                .add(containerId, newNavHostFragment, tag)
                .commitNow();
        return newNavHostFragment;
    }

    private static void attachNavHostFragment(
            FragmentManager fragmentManager,
            NavHostFragment navHostFragment,
            boolean isPrimary) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.attach(navHostFragment);
        if (isPrimary) {
            fragmentTransaction.setPrimaryNavigationFragment(navHostFragment);
        }
        fragmentTransaction.commitNow();
    }

    private static void detachNavHostFragment(
            FragmentManager fragmentManager,
            NavHostFragment navHostFragment) {
        fragmentManager.beginTransaction()
                .detach(navHostFragment)
                .commitNow();
    }

    private static boolean isOnBackStack(
            FragmentManager fragmentManager,
            String backStackEntryName) {
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            if (fragmentManager.getBackStackEntryAt(i).getName().equals(backStackEntryName)) {
                return true;
            }
        }
        return false;
    }
}
