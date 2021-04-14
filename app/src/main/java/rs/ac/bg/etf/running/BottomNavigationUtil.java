package rs.ac.bg.etf.running;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BottomNavigationUtil {

    private interface NavHostFragmentChanger {
        NavController change(int id);
    }

    private static NavHostFragmentChanger navHostFragmentChanger;

    public static void setup(
            BottomNavigationView bottomNavigationView,
            FragmentManager fragmentManager,
            int[] navResourceIds,
            int containerId) {
        Map<Integer, String> navGraphIdToTagMap = new HashMap<>();
        int homeNavGraphId = 0;

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

            if (bottomNavigationView.getSelectedItemId() == navGraphId) {
                attachNavHostFragment(fragmentManager, navHostFragment, i == 0);
            } else {
                detachNavHostFragment(fragmentManager, navHostFragment);
            }
        }

        String homeTag = navGraphIdToTagMap.get(homeNavGraphId);

        AtomicReference<Boolean> isOnHomeWrapper = new AtomicReference<>(
                homeNavGraphId == bottomNavigationView.getSelectedItemId());

        AtomicReference<String> currentTagWrapper = new AtomicReference<>(
                navGraphIdToTagMap.get(bottomNavigationView.getSelectedItemId()));

        navHostFragmentChanger = id -> {
            if (!fragmentManager.isStateSaved()) {
                String dstTag = navGraphIdToTagMap.get(id);

                bottomNavigationView.getMenu().findItem(id).setChecked(true);

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
            return null;
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(
                menuItem -> navHostFragmentChanger.change(menuItem.getItemId()) != null);

        int finalHomeNavGraphId = homeNavGraphId;
        fragmentManager.addOnBackStackChangedListener(() -> {
            if (!isOnHomeWrapper.get() && !isOnBackStack(fragmentManager, homeTag)) {
                bottomNavigationView.setSelectedItemId(finalHomeNavGraphId);
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
