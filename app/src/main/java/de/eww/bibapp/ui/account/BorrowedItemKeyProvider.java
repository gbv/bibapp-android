package de.eww.bibapp.ui.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import de.eww.bibapp.adapter.BorrowedAdapter;
import de.eww.bibapp.network.model.paia.PaiaItem;

public class BorrowedItemKeyProvider extends ItemKeyProvider<String> {

    private final BorrowedAdapter borrowedAdapter;

    BorrowedItemKeyProvider(BorrowedAdapter borrowedAdapter) {
        super(ItemKeyProvider.SCOPE_CACHED);

        this.borrowedAdapter = borrowedAdapter;
    }

    @Nullable
    @Override
    public String getKey(int position) {
        return borrowedAdapter.getCurrentList().get(position).getItem();
    }

    @Override
    public int getPosition(@NonNull String key) {
        for (PaiaItem paiaItem: borrowedAdapter.getCurrentList()) {
            if (paiaItem.getItem().equals(key)) {
                return borrowedAdapter.getCurrentList().indexOf(paiaItem);
            }
        }

        return -1;
    }
}
