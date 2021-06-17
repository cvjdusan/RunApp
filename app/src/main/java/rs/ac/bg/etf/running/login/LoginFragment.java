package rs.ac.bg.etf.running.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.databinding.FragmentLoginBinding;
import rs.ac.bg.etf.running.users.Session;
import rs.ac.bg.etf.running.users.UserViewModel;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private UserViewModel userViewModel;

    public static final String REMEMBER_USER_KEY = "remember";
    public static final String USERNAME_KEY = "username";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        Activity activity = getActivity();

        binding.loginButton.setOnClickListener(view -> {
            String username = binding.username.getEditableText().toString();
            String password = binding.password.getEditableText().toString();

            try {
                User user = getUser(username);
                if(user != null && checkPassword(user, password)){
                    SharedPreferences preferences = activity.getSharedPreferences("username", activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(USERNAME_KEY, username);
                    editor.apply();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    activity.finish();
                } else {
                    Toast.makeText(getContext(), R.string.user_doesnt_exist, Toast.LENGTH_SHORT).show();
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        binding.remember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isChecked()){
                SharedPreferences preferences = activity.getSharedPreferences("checkbox", activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(REMEMBER_USER_KEY, "true");
               // editor.putString(REMEMBER_USER_KEY, username[0]);
                editor.apply();
            } else {
                SharedPreferences preferences = activity.getSharedPreferences("checkbox", activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(REMEMBER_USER_KEY, "false");
                editor.apply();
            }
        });

        return binding.getRoot();
    }

    private User getUser(String username) throws ExecutionException, InterruptedException {
        Callable<User> callable = () -> userViewModel.findUserByUsername(username);

        Future<User> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    private Boolean checkPassword(User user, String password) throws ExecutionException, InterruptedException {
        Callable<Boolean> callable = () -> user.getPassword().equals(password);

        Future<Boolean> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
