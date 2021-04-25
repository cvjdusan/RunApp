package rs.ac.bg.etf.running.login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.RunDatabase;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.data.UserDao;
import rs.ac.bg.etf.running.databinding.FragmentRegisterBinding;
import rs.ac.bg.etf.running.users.UserViewModel;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private UserViewModel userViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
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
            try {
                if(checkErrors(email, username, password, confirmPassword) == false) {
                    if (checkEmail(email) != null) {
                        Toast.makeText(getContext(), R.string.mail_exists, Toast.LENGTH_SHORT).show();
                    } else if (checkUsername(username) != null) {
                        Toast.makeText(getContext(), R.string.username_exists, Toast.LENGTH_SHORT).show();
                    } else {
                        userViewModel.insertUser(new User(0, email, username, password));
                        Toast.makeText(getContext(), R.string.user_created, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        return binding.getRoot();
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private Boolean checkErrors(String email, String username, String password,
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
    }

        return false;
    }

    private User checkEmail(String email) throws ExecutionException, InterruptedException {
        Callable<User> callable = new Callable<User>() {
            @Override
            public User call() throws Exception {
                return userViewModel.findUserByEmail(email);
            }
        };

        Future<User> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    private User checkUsername(String username) throws ExecutionException, InterruptedException {
        Callable<User> callable = new Callable<User>() {
            @Override
            public User call() throws Exception {
                return userViewModel.findUserByUsername(username);
            }
        };

        Future<User> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }
}
