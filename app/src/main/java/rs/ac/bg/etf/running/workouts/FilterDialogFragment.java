package rs.ac.bg.etf.running.workouts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.databinding.FragmentFilterDialogBinding;


public class FilterDialogFragment extends DialogFragment {

    private FragmentFilterDialogBinding binding;
    public static final String SET_FILTER_SORT_KEY = "set-filter-sort-key";

    double low = -1;
    double high = -1;
    boolean sort = false;

    public FilterDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        binding = FragmentFilterDialogBinding.inflate(LayoutInflater.from(getContext()));
        Activity activity = requireActivity();

        binding.ok.setOnClickListener(view -> {
            sort = binding.checkboxSort.isChecked();


            Bundle bundle = new Bundle();
            bundle.putSerializable(SET_FILTER_SORT_KEY, (sort ? "1" : "0"));
            getParentFragmentManager().setFragmentResult(WorkoutListFragment.DIALOG_KEY, bundle);

            this.dismiss();
        });

        return new AlertDialog.Builder(activity).setView(binding.getRoot()).create();
    }
}