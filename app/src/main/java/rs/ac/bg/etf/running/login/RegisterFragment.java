package rs.ac.bg.etf.running.login;

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

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.data.Workout;
import rs.ac.bg.etf.running.databinding.FragmentLoginBinding;
import rs.ac.bg.etf.running.databinding.FragmentRegisterBinding;
import rs.ac.bg.etf.running.users.UserViewModel;
import rs.ac.bg.etf.running.workouts.WorkoutViewModel;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private UserViewModel userViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);

        binding.registerButton.setOnClickListener(view -> {
            String email = binding.email.getEditableText().toString();
            String username = binding.username.getEditableText().toString();
            String password = binding.password.getEditableText().toString();
            userViewModel.insertUser(new User(
                    0,
                    email,
                    username,
                    password
            ));
            Toast.makeText(getContext(), email + " " + username + " " + password
                    , Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }
}
