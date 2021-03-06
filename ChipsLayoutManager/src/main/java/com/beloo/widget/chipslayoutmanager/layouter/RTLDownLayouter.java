package com.beloo.widget.chipslayoutmanager.layouter;

import android.graphics.Rect;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.cache.IViewCacheStorage;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;

class RTLDownLayouter extends AbstractLayouter {

    private int viewRight;

    RTLDownLayouter(ChipsLayoutManager layoutManager, IChildGravityResolver childGravityResolver,
                    IViewCacheStorage cacheStorage,
                    int topOffset, int rightOffset, int bottomOffset) {
        super(layoutManager, topOffset, bottomOffset, cacheStorage, childGravityResolver);
        viewRight = rightOffset;
    }

    @Override
    void onPreLayout() {
        getCacheStorage().storeRow(rowViews);
    }

    @Override
    void onAfterLayout() {
        //go to next row, increase top coordinate, reset left
        viewRight = getCanvasRightBorder();
        rowTop = rowBottom;
    }


    @Override
    void addView(View view) {
        getLayoutManager().addView(view);
    }

    @Override
    public boolean canNotBePlacedInCurrentRow() {
        return super.canNotBePlacedInCurrentRow() || (viewRight < getCanvasRightBorder() && viewRight - currentViewWidth < getCanvasLeftBorder());
    }

    @Override
    public AbstractPositionIterator positionIterator() {
        return new IncrementalPositionIterator(getLayoutManager().getItemCount());
    }

    @Override
    Rect createViewRect(View view) {
        Rect viewRect = new Rect(viewRight - currentViewWidth, rowTop, viewRight, rowTop + currentViewHeight);
        viewRight = viewRect.left;
        rowBottom = Math.max(rowBottom, viewRect.bottom);
        return viewRect;
    }

    @Override
    public boolean onAttachView(View view) {
        rowTop = getLayoutManager().getDecoratedTop(view);
        viewRight = getLayoutManager().getDecoratedLeft(view);

        rowBottom = Math.max(rowBottom, getLayoutManager().getDecoratedBottom(view));

        return super.onAttachView(view);
    }

    @Override
    public boolean isFinishedLayouting() {
        return rowTop > getCanvasBottomBorder();
    }

}
