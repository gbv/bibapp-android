package de.eww.bibapp.ui.mods;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import de.eww.bibapp.adapter.WatchlistAdapter;

public class WatchlistItemDetailsLookup extends ItemDetailsLookup<String> {

    private final RecyclerView recyclerView;

    WatchlistItemDetailsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetailsLookup.ItemDetails<String> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof WatchlistAdapter.ViewHolder) {
                return ((WatchlistAdapter.ViewHolder) holder).getItemDetails();
            }
        }

        return null;
    }
}
