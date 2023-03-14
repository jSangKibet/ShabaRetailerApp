package com.acework.shabaretailer;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.acework.shabaretailer.welcome.WelcomeAboutUsFragment;
import com.acework.shabaretailer.welcome.WelcomeRetailerFragment;
import com.acework.shabaretailer.welcome.WelcomeWelcomeFragment;

public class WelcomeActivity extends AppCompatActivity {
    private ViewPager2 pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        pager = findViewById(R.id.pager);
        WelcomeFragmentAdapter wfa = new WelcomeFragmentAdapter();
        pager.setAdapter(wfa);
    }

    public void toAboutUs() {
        pager.setCurrentItem(1, true);
    }

    public void toRetailer() {
        pager.setCurrentItem(2, true);
    }

    public void toLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private class WelcomeFragmentAdapter extends FragmentStateAdapter {
        private final WelcomeWelcomeFragment wwf;
        private final WelcomeAboutUsFragment waf;
        private final WelcomeRetailerFragment wrf;

        public WelcomeFragmentAdapter() {
            super(WelcomeActivity.this);
            wwf = new WelcomeWelcomeFragment();
            waf = new WelcomeAboutUsFragment();
            wrf = new WelcomeRetailerFragment();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return wwf;
            }
            if (position == 1) {
                return waf;
            }
            return wrf;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}