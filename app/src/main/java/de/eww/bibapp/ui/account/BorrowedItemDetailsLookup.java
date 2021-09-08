package de.eww.bibapp.ui.account;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import de.eww.bibapp.adapter.BorrowedAdapter;

public class BorrowedItemDetailsLookup extends ItemDetailsLookup<String> {

    private final RecyclerView recyclerView;

    BorrowedItemDetailsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<String> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
            if (holder instanceof BorrowedAdapter.ViewHolder) {
                return ((BorrowedAdapter.ViewHolder) holder).getItemDetails();
            }
        }

        return null;
    }
}
