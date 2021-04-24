package rs.ac.bg.etf.running.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.atomic.AtomicBoolean;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.databinding.FragmentRegisterBinding;
import rs.ac.bg.etf.running.users.UserViewModel;

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
            String confirmPassword = binding.confirmPassword.getEditableText().toString();
            if(checkErrors(email, username, password, confirmPassword) == false) {
                userViewModel.insertUser(new User(
                                        0,
                                        email,
                                        username,
                                        password
                                ));
                Toast.makeText(getContext(), R.string.user_created, Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean checkErrors(String email, String username, String password,
    String confirmPassword){


        if(email.length() == 0 || username.length() == 0 || password.length() == 0 || confirmPassword.length() == 0) {
            Toast.makeText(getContext(), R.string.empty_values, Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(password.length() < 4){
            Toast.makeText(getContext(), R.string.password_short, Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(!confirmPassword.equals(password)){
            Toast.makeText(getContext(), R.string.password_dont_match, Toast.LENGTH_SHORT).show();
            return true;
        } else if(!isEmailValid(email)){
            Toast.makeText(getContext(), R.string.mail_not_valid, Toast.LENGTH_SHORT).show();
            return true;
        } else {

            AtomicBoolean err = new AtomicBoolean(false);

            userViewModel.getUsersList().observe(getActivity(), users -> {
                for(int i = 0; i < users.size(); i++){
                    if(users.get(i).getUsername().equals(username)){
                        Toast.makeText(getContext(), R.string.username_exists, Toast.LENGTH_SHORT).show();
                        err.set(true);
                    } else if(users.get(i).getEmail().equals(email)) {
                        Toast.makeText(getContext(), R.string.mail_exists, Toast.LENGTH_SHORT).show();
                        err.set(true);
                    }
                }
            });

            return err.get();
        }




    }
}
