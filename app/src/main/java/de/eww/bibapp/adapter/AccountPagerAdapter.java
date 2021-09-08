package de.eww.bibapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.eww.bibapp.ui.account.AccountBookedFragment;
import de.eww.bibapp.ui.account.AccountBorrowedFragment;
import de.eww.bibapp.ui.account.AccountFeesFragment;

public class AccountPagerAdapter extends FragmentStateAdapter {

    public AccountPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 2:
                return new AccountFeesFragment();
            case 1:
                return new AccountBookedFragment();
            default:
                return new AccountBorrowedFragment();
        }
    }


    @Override
    public int getItemCount() {
        return 3;
    }
}
