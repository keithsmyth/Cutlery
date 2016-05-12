package com.keithsmyth.cutlery.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SwipeItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final SwipeListener swipeListener;

    public SwipeItemTouchHelperCallback(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        swipeListener.onItemSwiped(viewHolder.getAdapterPosition());
    }

    interface SwipeListener {

        void onItemSwiped(int position);
    }
}
