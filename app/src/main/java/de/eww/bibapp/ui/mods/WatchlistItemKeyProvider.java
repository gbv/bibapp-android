package de.eww.bibapp.ui.mods;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import de.eww.bibapp.adapter.WatchlistAdapter;
import de.eww.bibapp.network.model.ModsItem;

public class WatchlistItemKeyProvider extends ItemKeyProvider<String> {

    private final WatchlistAdapter watchlistAdapter;

    WatchlistItemKeyProvider(WatchlistAdapter watchlistAdapter) {
        super(ItemKeyProvider.SCOPE_CACHED);

        this.watchlistAdapter = watchlistAdapter;
    }

    @Nullable
    @Override
    public String getKey(int position) {
        return watchlistAdapter.getCurrentList().get(position).ppn;
    }

    @Override
    public int getPosition(@NonNull String key) {
        for (ModsItem modsItems : watchlistAdapter.getCurrentList()) {
            if (modsItems.ppn.equals(key)) {
                return watchlistAdapter.getCurrentList().indexOf(modsItems);
            }
        }

        return -1;
    }
}
