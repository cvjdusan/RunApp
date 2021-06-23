package rs.ac.bg.etf.running.workouts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Objects;

import rs.ac.bg.etf.running.databinding.FragmentFilterDialogBinding;


public class WorkoutFilterDialogFragment extends DialogFragment {


    private FragmentFilterDialogBinding binding;
    public static final String SORT_KEY = "sort-key";
    public static final String FILTER_KEY = "filter-key";

    public static final int DEFAULT_FROM = 0;
    public static final int DEFAULT_TO = 20000;
    public static final int DEFAULT_FILTER = -1;

    boolean sort = false;

    double from = DEFAULT_FROM, to = DEFAULT_TO;

    public WorkoutFilterDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        binding = FragmentFilterDialogBinding.inflate(LayoutInflater.from(getContext()));
        Activity activity = requireActivity();

        binding.ok.setOnClickListener(view -> {
            try {
                from = Double.parseDouble(Objects.requireNonNull(binding.filterFrom.getEditText()).getText().toString());
                to = Double.parseDouble(Objects.requireNonNull(binding.filterTo.getEditText()).getText().toString());
            } catch (Exception e){
                from = DEFAULT_FROM;
                to = DEFAULT_TO;
            }
            sort = binding.checkboxSort.isChecked();
            String resultFilter = from + "/" + to;



            int checkedRadioButtonId = binding.radioGroup.getCheckedRadioButtonId();

            View radioButton = binding.radioGroup.findViewById(checkedRadioButtonId);
            int idx = binding.radioGroup.indexOfChild(radioButton);

            resultFilter += "/" + idx; // -1 if there is none

            String resultSort = sort ? "1" : "0";

            Bundle bundle = new Bundle();
            bundle.putSerializable(SORT_KEY, resultSort);
            bundle.putSerializable(FILTER_KEY, resultFilter);
            getParentFragmentManager().setFragmentResult(WorkoutListFragment.DIALOG_KEY, bundle);
            this.dismiss();
        });

        return new AlertDialog.Builder(activity).setView(binding.getRoot()).create();
    }
}