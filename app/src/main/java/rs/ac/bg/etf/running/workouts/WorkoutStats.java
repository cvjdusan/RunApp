package rs.ac.bg.etf.running.workouts;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.annotation.Nullable;

import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentWorkoutStatsBinding;


public class WorkoutStats extends Fragment {

    private FragmentWorkoutStatsBinding binding;
    private WorkoutViewModel workoutViewModel;
    private MainActivity mainActivity;
    private List<Workout> workoutList;

    private final int BRONZE_STEPS = 10000;
    private final int SILVER_STEPS = 20000;
    private final int GOLD_STEPS = 30000;

    //km
    private final int BRONZE_DISTANCE = 10;
    private final int SILVER_DISTANCE = 20;
    private final int GOLD_DISTANCE= 30;

    private final int BRONZE_DURATION = 10;
    private final int SILVER_DURATION = 100;
    private final int GOLD_DURATION = 1000;

    private final int BRONZE_COLOR = Color.parseColor("#b08d57");
    private final int SILVER_COLOR = Color.GRAY;
    private final int GOLD_COLOR = Color.YELLOW;

    //setBackgroundColor

    private NavController navController;

    public WorkoutStats() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) requireActivity();
        workoutViewModel = new ViewModelProvider(mainActivity).get(WorkoutViewModel.class);

        workoutViewModel.getWorkoutList().observe(mainActivity, workoutList -> {
            this.workoutList = workoutList;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWorkoutStatsBinding.inflate(inflater, container, false);

        binding.toolbar.setNavigationOnClickListener(view -> {
            navController.navigateUp();
        });

        workoutViewModel.getWorkoutList().observe(mainActivity, workoutList -> {
            this.workoutList = workoutList;
        });

        int totalSteps = 0;
        double totalDuration = 0;
        double totalDistance = 0;
        int numOfWorkout = workoutList.size() - 1;

        for(int i = 0; i < workoutList.size(); i++){
            totalSteps += workoutList.get(i).getSteps();
            totalDuration += workoutList.get(i).getDuration();
            totalDistance += workoutList.get(i).getDistance();
        }

        double avgDuration = totalDuration / numOfWorkout;
        double avgDistance = totalDistance / numOfWorkout;

        binding.averageDistance.setText((int)avgDistance + "km");
        binding.averageTime.setText((int) avgDuration + "min");
        binding.numberOfSteps.setText(totalSteps + "");

        int stepsMedal = getMedal(totalSteps, BRONZE_STEPS, SILVER_STEPS, GOLD_STEPS);
        int durationMedal = getMedal(totalDuration, BRONZE_DURATION, SILVER_DURATION, GOLD_DURATION);
        int distanceMedal = getMedal(totalDistance, BRONZE_DISTANCE, SILVER_DISTANCE, GOLD_DISTANCE);

     //   binding.numberOfStepsMedal.setText("MEDALJA");
        binding.numberOfStepsMedal.setTextColor(stepsMedal);
      //  binding.averageDistanceMedal.setText("MEDALJA");
        binding.averageDistanceMedal.setTextColor(distanceMedal);
        //binding.averageTimeMedal.setText("MEDALJA");
        binding.averageTimeMedal.setTextColor(durationMedal);

        setMedalText(stepsMedal, SILVER_STEPS, GOLD_STEPS, binding.label11, totalSteps, "steps", "steps");
        setMedalText(distanceMedal, SILVER_DISTANCE, GOLD_DISTANCE, binding.label33, (int) totalDistance, "distance", "km");
        setMedalText(durationMedal, SILVER_DURATION, GOLD_DURATION, binding.label22, (int) totalDuration, "duration", "min");

        return binding.getRoot();
    }

    private void setMedalText(int n, int silver, int gold, TextView label, int total, String t, String unit) {
        if(n == GOLD_COLOR){
            label.setText("Congrats, you won the " + t +" challange!");
        }
        else if(n == SILVER_COLOR){
            int left = silver - total;
            label.setText("For gold medal you still need " + left + " " + unit);
        }
        else {
            int left = gold - total;
            label.setText("For silver medal you still need " + left + " " + unit);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private int getMedal(int n, int b, int s, int g){
        if(n <= b){
            return BRONZE_COLOR;
        } else if(n > b && n <= s){
            return SILVER_COLOR;
        } else {
            return GOLD_COLOR;
        }
    }

    private int getMedal(double n, int b, int s, int g){
        if(n <= b){
            return BRONZE_COLOR;
        } else if(n > b && n <= s){
            return SILVER_COLOR;
        } else {
            return GOLD_COLOR;
        }
    }


}