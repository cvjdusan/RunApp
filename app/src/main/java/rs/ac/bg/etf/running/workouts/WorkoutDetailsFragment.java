package rs.ac.bg.etf.running.workouts;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
    private WorkoutViewModel workoutViewModel;

    private LocationViewModel locationViewModel;
    private List<Location> locations;
    public static List<Location> locationDraw = new ArrayList<>();;

    public WorkoutDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);
        locationViewModel = new ViewModelProvider(mainActivity).get(LocationViewModel.class);
        locationViewModel.getLocations().observe(mainActivity, l -> {
            this.locations = l;
        });
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


      //  Toast.makeText(mainActivity, workout.getId() + "", Toast.LENGTH_SHORT).show();

        binding.workoutLabel.setText(workout.getLabel());
        binding.steps.setText(workout.getSteps() + " steps");

//        locationDraw = new ArrayList<>();
//        for(int i = 0; i < locations.size(); i++){
//            Location current = locations.get(i);
//            if(workout.getId() == current.getIdWorkout())
//                locationDraw.add(current); // or new obj?
//        }
//        LocationCustomView locationCustomView = new LocationCustomView(mainActivity);
//        locationCustomView.setLayoutParams(new ViewGroup.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 450));
//        locationCustomView.setLayoutParams(new ViewGroup.MarginLayoutParams(0, 10));
//        binding.holder.addView(locationCustomView);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


}
