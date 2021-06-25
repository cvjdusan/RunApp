package rs.ac.bg.etf.running.workouts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import rs.ac.bg.etf.running.MapsActivity;
import rs.ac.bg.etf.running.data.Location;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutDetailsBinding;
import rs.ac.bg.etf.running.location.LocationViewModel;
import rs.ac.bg.etf.running.users.Session;

public class WorkoutDetailsFragment extends Fragment {
    private MainActivity mainActivity;

    private FragmentWorkoutDetailsBinding binding;

    private NavController navController;
    private WorkoutViewModel workoutViewModel;

    private LocationViewModel locationViewModel;
    private List<Location> locations;
    public static List<Location> locationDraw;
    private Workout workout;


    public WorkoutDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        locations = Session.getCurrentLocations();
        workout = Objects.requireNonNull(workoutViewModel
                .getWorkoutList()
                .getValue())
                .get(
                        WorkoutDetailsFragmentArgs.fromBundle(requireArguments()).getWorkoutIndex()
                );

        locationDraw = new ArrayList<>();
        for(int i = 0; i < locations.size(); i++) {
            Location current = locations.get(i);
            if (workout.getId() == current.getIdWorkout())
                locationDraw.add(current);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false);
        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });

        binding.showMapButton.setOnClickListener(l -> {
            Intent intent = new Intent(mainActivity, MapsActivity.class);
            startActivity(intent);
        });

        binding.workoutDate.setText(DateTimeUtil.getSimpleDateFormat().format(
                workout.getDate()));
        binding.workoutLabel.setText(
                workout.getLabel());
        binding.workoutDistance.setText(String.format("%.2f km",
                workout.getDistance()));
        binding.workoutPace.setText(String.format("%s min/km", DateTimeUtil.realMinutesToString(
                workout.getDuration() / workout.getDistance())));
        binding.workoutDuration.setText(String.format("%s min", DateTimeUtil.realMinutesToString(
                workout.getDuration())));
        binding.steps.setText(workout.getSteps() + " steps");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


}
