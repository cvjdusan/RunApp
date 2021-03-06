package rs.ac.bg.etf.running.workouts;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutListBinding;

import static android.media.CamcorderProfile.get;

@AndroidEntryPoint
public class WorkoutListFragment extends Fragment {

    public static final String DIALOG_KEY = "dialog-key";
    private FragmentWorkoutListBinding binding;
    private WorkoutViewModel workoutViewModel;
    private NavController navController;
    private MainActivity mainActivity;

    private boolean isSort = false, isFilter = false;

    public WorkoutListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutListBinding.inflate(inflater, container, false);

        binding.toolbar.inflateMenu(R.menu.menu_workout_list_options);
        binding.toolbar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.workout_menu_item_sort:
                    new WorkoutFilterDialogFragment().show(getChildFragmentManager(), null);

                    getChildFragmentManager().setFragmentResultListener(DIALOG_KEY, this,
                            (requestKey, result) -> {
                                String resSort = (String) result.getSerializable(WorkoutFilterDialogFragment.SORT_KEY);
                                String resFilter = (String) result.getSerializable(WorkoutFilterDialogFragment.FILTER_KEY);

                                String[] helper = resFilter.split("/");

                                double from = Double.parseDouble(helper[0]);
                                double to = Double.parseDouble(helper[1]);
                                int filterArg = Integer.parseInt(helper[2]);;

                                if(filterArg != -1)
                                    isFilter = true;
                                else
                                    isFilter = false;

                                workoutViewModel.setFrom(from);
                                workoutViewModel.setTo(to);
                                workoutViewModel.setFilter(filterArg);

                                if (resSort.equals("1")) {
                                    isSort = true;
                                    workoutViewModel.sort();
                                }
                                else {
                                    isSort = false;
                                    workoutViewModel.showUnsorted();
                                }

                                String text = "";
                                if(isFilter)
                                    text = " Filter on |";
                                else
                                    text = " Filter off |";

                                if(isSort)
                                    text += " Sort on";
                                else
                                    text += " Sort off";

                                binding.sortOrFilter.setText(text);
                            });
                    return false;
            }
            return false;
        });


        WorkoutAdapter workoutAdapter = new WorkoutAdapter(
                workoutIndex -> {
                    WorkoutListFragmentDirections.ActionWorkoutListToWorkoutDetails action =
                            WorkoutListFragmentDirections.actionWorkoutListToWorkoutDetails();
                    action.setWorkoutIndex(workoutIndex);
                    navController.navigate(action);
                }
        );

        workoutViewModel.getWorkoutList().observe(
                getViewLifecycleOwner(),
                workoutAdapter::setWorkoutList);

        binding.recyclerView.setAdapter(workoutAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));

        binding.floatingActionButton.inflate(R.menu.menu_workout_list_fab);

        String text = "";
        if(isFilter)
            text = " Filter on |";
        else
            text = " Filter off |";

        if(isSort)
            text += " Sort on";
        else
            text += " Sort off";

        binding.sortOrFilter.setText(text);

        binding.floatingActionButton.setOnActionSelectedListener(actionItem -> {
            switch (actionItem.getId()) {
                case R.id.workout_fab_create:
                    navController.navigate(WorkoutListFragmentDirections.createWorkout());
                    return false;
                case R.id.workout_fab_start:
                    navController.navigate(WorkoutListFragmentDirections.startWorkout());
                    return false;
            }
            return true;
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }
}