package rs.ac.bg.etf.running.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.MainActivity;
import rs.ac.bg.etf.running.R;
import rs.ac.bg.etf.running.data.User;
import rs.ac.bg.etf.running.databinding.ActivityLoginBinding;
import rs.ac.bg.etf.running.databinding.FragmentLoginBinding;
import rs.ac.bg.etf.running.databinding.FragmentRouteBrowseBinding;
import rs.ac.bg.etf.running.users.UserViewModel;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private UserViewModel userViewModel;

    public static final String REMEMBER_KEY = "remember";


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

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.username.getEditableText().toString();
                String password = binding.password.getEditableText().toString();


                try {
                    User user = getUser(username);
                    if(user != null && checkPassword(user, password)){
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
            }
        });

        binding.remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    SharedPreferences preferences = activity.getSharedPreferences("checkbox", activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(REMEMBER_KEY, "true");
                    editor.apply();
                } else {
                    SharedPreferences preferences = activity.getSharedPreferences("checkbox", activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(REMEMBER_KEY, "false");
                    editor.apply();
                }
            }
        });

        return binding.getRoot();
    }

    private User getUser(String username) throws ExecutionException, InterruptedException {
        Callable<User> callable = new Callable<User>() {
            @Override
            public User call() throws Exception {
                return userViewModel.findUserByUsername(username);
            }
        };

        Future<User> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

    private Boolean checkPassword(User user, String password) throws ExecutionException, InterruptedException {
        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return user.getPassword().equals(password);
            }
        };

        Future<Boolean> future = Executors.newSingleThreadExecutor().submit(callable);

        return future.get();
    }

}
