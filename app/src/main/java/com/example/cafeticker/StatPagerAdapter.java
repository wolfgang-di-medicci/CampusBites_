package com.example.cafeticker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class StatPagerAdapter extends FragmentStateAdapter {
    public StatPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DailyStatFragment();
            case 1:
                return new WeeklyStatFragment();
            case 2:
                return new MonthlyStatFragment();
            default:
                return new DailyStatFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
