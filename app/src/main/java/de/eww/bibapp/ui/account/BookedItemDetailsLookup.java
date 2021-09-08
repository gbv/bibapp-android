package de.eww.bibapp.ui.account;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import de.eww.bibapp.adapter.BookedAdapter;

public class BookedItemDetailsLookup extends ItemDetailsLookup<String> {

    private final RecyclerView recyclerView;

    BookedItemDetailsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetailsLookup.ItemDetails<String> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof BookedAdapter.ViewHolder) {
                return ((BookedAdapter.ViewHolder) holder).getItemDetails();
            }
        }

        return null;
    }
}
