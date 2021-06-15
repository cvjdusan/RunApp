package rs.ac.bg.etf.running;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import dagger.hilt.android.AndroidEntryPoint;
import rs.ac.bg.etf.running.databinding.ActivityLoginBinding;
import rs.ac.bg.etf.running.login.LoginAdapter;
import rs.ac.bg.etf.running.login.LoginFragment;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String isChecked = preferences.getString(LoginFragment.REMEMBER_USER_KEY, "");

        if(isChecked.equals("true")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            tabLayout = findViewById(R.id.tab_layout);
            viewPager = findViewById(R.id.view_pager);

            tabLayout.addTab(tabLayout.newTab().setText("Login"));
            tabLayout.addTab(tabLayout.newTab().setText("Register"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), getLifecycle());

            viewPager.setAdapter(adapter);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    tabLayout.selectTab(tabLayout.getTabAt(position));
                }
            });

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
  ;
    }
}