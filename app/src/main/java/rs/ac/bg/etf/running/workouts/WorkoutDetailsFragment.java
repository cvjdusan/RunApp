package rs.ac.bg.etf.running.workouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutDetailsBinding;
import rs.ac.bg.etf.running.location.LocationCustomView;
import rs.ac.bg.etf.running.location.LocationViewModel;

public class WorkoutDetailsFragment extends Fragment {
    private MainActivity mainActivity;

    private FragmentWorkoutDetailsBinding binding;

    private NavController navController;

    private LocationViewModel locationViewModel;
    private WorkoutViewModel workoutViewModel;

    List<Location> locations;

    public WorkoutDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        locationViewModel = new ViewModelProvider(mainActivity).get(LocationViewModel.class);
        locationViewModel.getLocations().observe(mainActivity, locationList -> {
            locations = locationList;
        });

        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false);
        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });


        Workout workout = Objects.requireNonNull(workoutViewModel
                .getWorkoutList()
                .getValue())
                .get(
                        WorkoutDetailsFragmentArgs.fromBundle(requireArguments()).getWorkoutIndex()
                );


        binding.workoutLabel.setText(workout.getLabel());
        binding.steps.setText(workout.getSteps() + " steps");

        LocationCustomView myCustomView = new LocationCustomView(mainActivity);
        myCustomView.setLayoutParams(new ViewGroup.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 450));
        myCustomView.setLayoutParams(new ViewGroup.MarginLayoutParams(0, 10));

        binding.holder.addView(myCustomView);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

}
