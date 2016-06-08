package com.keithsmyth.cutlery.view;

import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public class SwipeItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final SwipeListener swipeListener;

    @Nullable private View swipeBackground;

    public SwipeItemTouchHelperCallback(SwipeListener swipeListener) {
        this.swipeListener = swipeListener;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.END);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        swipeListener.onItemSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (swipeBackground != null) {
            if (isCurrentlyActive) {
                swipeBackground.setY(viewHolder.itemView.getTop());
                swipeBackground.setVisibility(View.VISIBLE);
            } else {
                swipeBackground.setVisibility(View.GONE);
            }
        }
    }

    public void setSwipeBackground(@Nullable View swipeBackground) {
        this.swipeBackground = swipeBackground;
    }

    public interface SwipeListener {

        void onItemSwiped(int position);
    }
}
