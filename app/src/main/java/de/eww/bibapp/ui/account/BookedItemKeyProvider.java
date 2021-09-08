package de.eww.bibapp.ui.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import de.eww.bibapp.adapter.BookedAdapter;
import de.eww.bibapp.network.model.paia.PaiaItem;

public class BookedItemKeyProvider extends ItemKeyProvider<String>{

    private final BookedAdapter bookedAdapter;

    BookedItemKeyProvider(BookedAdapter bookedAdapter) {
        super(ItemKeyProvider.SCOPE_CACHED);

        this.bookedAdapter = bookedAdapter;
    }

    @Nullable
    @Override
    public String getKey(int position) {
        return bookedAdapter.getCurrentList().get(position).getItem();
    }

    @Override
    public int getPosition(@NonNull String key) {
        for (PaiaItem paiaItem: bookedAdapter.getCurrentList()) {
            if (paiaItem.getItem().equals(key)) {
                return bookedAdapter.getCurrentList().indexOf(paiaItem);
            }
        }

        return -1;
    }
}
