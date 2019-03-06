package com.insteem.ipfreely.steem.StickyItemDecorator

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * Calculates the position and location of header views
 */
class HeaderPositionCalculator(private val mAdapter: StickyRecyclerHeadersAdapter<*>,
                               private val mHeaderProvider: HeaderProvider,
                               private val mOrientationProvider: OrientationProvider,
                               private val mDimensionCalculator: DimensionCalculator) {

    /**
     * The following fields are used as buffers for internal calculations. Their sole purpose is to avoid
     * allocating new Rect every time we need one.
     */
    private val mTempRect1 = Rect()
    private val mTempRect2 = Rect()

    /**
     * Determines if a view should have a sticky header.
     * The view has a sticky header if:
     * 1. It is the first element in the recycler view
     * 2. It has a valid ID associated to its position
     *
     * @param itemView given by the RecyclerView
     * @param orientation of the Recyclerview
     * @param position of the list item in question
     * @return True if the view should have a sticky header
     */
    fun hasStickyHeader(itemView: View, orientation: Int, position: Int): Boolean {
        val offset: Int
        val margin: Int
        mDimensionCalculator.initMargins(mTempRect1, itemView)
        if (orientation == LinearLayout.VERTICAL) {
            offset = itemView.getTop()
            margin = mTempRect1.top
        } else {
            offset = itemView.getLeft()
            margin = mTempRect1.left
        }

        return offset <= margin && mAdapter.getHeaderId(position) >= 0
    }

    /**
     * Determines if an item in the list should have a header that is different than the item in the
     * list that immediately precedes it. Items with no headers will always return false.
     *
     * @param position of the list item in questions
     * @param isReverseLayout TRUE if layout manager has flag isReverseLayout
     * @return true if this item has a different header than the previous item in the list
     */
    fun hasNewHeader(position: Int, isReverseLayout: Boolean): Boolean {
        if (indexOutOfBounds(position)) {
            return false
        }

        val headerId = mAdapter.getHeaderId(position)

        if (headerId < 0) {
            return false
        }

        var nextItemHeaderId: Long = -1
        val nextItemPosition = position + if (isReverseLayout) 1 else -1
        if (!indexOutOfBounds(nextItemPosition)) {
            nextItemHeaderId = mAdapter.getHeaderId(nextItemPosition)
        }
        val firstItemPosition = if (isReverseLayout) mAdapter.getItemCount() - 1 else 0

        return position == firstItemPosition || headerId != nextItemHeaderId
    }

    private fun indexOutOfBounds(position: Int): Boolean {
        return position < 0 || position >= mAdapter.getItemCount()
    }

    fun initHeaderBounds(bounds: Rect, recyclerView: RecyclerView, header: View, firstView: View, firstHeader: Boolean) {
        val orientation = mOrientationProvider.getOrientation(recyclerView)
        initDefaultHeaderOffset(bounds, recyclerView, header, firstView, orientation)

        if (firstHeader && isStickyHeaderBeingPushedOffscreen(recyclerView, header)) {
            val viewAfterNextHeader = getFirstViewUnobscuredByHeader(recyclerView, header)
            val firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterNextHeader!!)
            val secondHeader = mHeaderProvider.getHeader(recyclerView, firstViewUnderHeaderPosition)
            translateHeaderWithNextHeader(recyclerView, mOrientationProvider.getOrientation(recyclerView), bounds,
                    header, viewAfterNextHeader, secondHeader)
        }
    }

    private fun initDefaultHeaderOffset(headerMargins: Rect, recyclerView: RecyclerView, header: View, firstView: View, orientation: Int) {
        val translationX: Int
        val translationY: Int
        mDimensionCalculator.initMargins(mTempRect1, header)

        val layoutParams = firstView.getLayoutParams()
        var leftMargin = 0
        var topMargin = 0
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams
            leftMargin = marginLayoutParams.leftMargin
            topMargin = marginLayoutParams.topMargin
        }

        if (orientation == LinearLayoutManager.VERTICAL) {
            translationX = firstView.getLeft() - leftMargin + mTempRect1.left
            translationY = Math.max(
                    firstView.getTop() - topMargin - header.getHeight() - mTempRect1.bottom,
                    getListTop(recyclerView) + mTempRect1.top)
        } else {
            translationY = firstView.getTop() - topMargin + mTempRect1.top
            translationX = Math.max(
                    firstView.getLeft() - leftMargin - header.getWidth() - mTempRect1.right,
                    getListLeft(recyclerView) + mTempRect1.left)
        }

        headerMargins.set(translationX, translationY, translationX + header.getWidth(),
                translationY + header.getHeight())
    }

    private fun isStickyHeaderBeingPushedOffscreen(recyclerView: RecyclerView, stickyHeader: View): Boolean {
        val viewAfterHeader = getFirstViewUnobscuredByHeader(recyclerView, stickyHeader)
        val firstViewUnderHeaderPosition = recyclerView.getChildAdapterPosition(viewAfterHeader!!)
        if (firstViewUnderHeaderPosition == RecyclerView.NO_POSITION) {
            return false
        }

        val isReverseLayout = mOrientationProvider.isReverseLayout(recyclerView)
        if (firstViewUnderHeaderPosition > 0 && hasNewHeader(firstViewUnderHeaderPosition, isReverseLayout)) {
            val nextHeader = mHeaderProvider.getHeader(recyclerView, firstViewUnderHeaderPosition)
            mDimensionCalculator.initMargins(mTempRect1, nextHeader)
            mDimensionCalculator.initMargins(mTempRect2, stickyHeader)

            if (mOrientationProvider.getOrientation(recyclerView) == LinearLayoutManager.VERTICAL) {
                val topOfNextHeader = viewAfterHeader!!.getTop() - mTempRect1.bottom - nextHeader.height - mTempRect1.top
                val bottomOfThisHeader = recyclerView.paddingTop + stickyHeader.getBottom() + mTempRect2.top + mTempRect2.bottom
                if (topOfNextHeader < bottomOfThisHeader) {
                    return true
                }
            } else {
                val leftOfNextHeader = viewAfterHeader!!.getLeft() - mTempRect1.right - nextHeader.width - mTempRect1.left
                val rightOfThisHeader = recyclerView.paddingLeft + stickyHeader.getRight() + mTempRect2.left + mTempRect2.right
                if (leftOfNextHeader < rightOfThisHeader) {
                    return true
                }
            }
        }

        return false
    }

    private fun translateHeaderWithNextHeader(recyclerView: RecyclerView, orientation: Int, translation: Rect,
                                              currentHeader: View, viewAfterNextHeader: View, nextHeader: View) {
        mDimensionCalculator.initMargins(mTempRect1, nextHeader)
        mDimensionCalculator.initMargins(mTempRect2, currentHeader)
        if (orientation == LinearLayoutManager.VERTICAL) {
            val topOfStickyHeader = getListTop(recyclerView) + mTempRect2.top + mTempRect2.bottom
            val shiftFromNextHeader = viewAfterNextHeader.getTop() - nextHeader.getHeight() - mTempRect1.bottom - mTempRect1.top - currentHeader.getHeight() - topOfStickyHeader
            if (shiftFromNextHeader < topOfStickyHeader) {
                translation.top += shiftFromNextHeader
            }
        } else {
            val leftOfStickyHeader = getListLeft(recyclerView) + mTempRect2.left + mTempRect2.right
            val shiftFromNextHeader = viewAfterNextHeader.getLeft() - nextHeader.getWidth() - mTempRect1.right - mTempRect1.left - currentHeader.getWidth() - leftOfStickyHeader
            if (shiftFromNextHeader < leftOfStickyHeader) {
                translation.left += shiftFromNextHeader
            }
        }
    }

    /**
     * Returns the first item currently in the RecyclerView that is not obscured by a header.
     *
     * @param parent Recyclerview containing all the list items
     * @return first item that is fully beneath a header
     */
    private fun getFirstViewUnobscuredByHeader(parent: RecyclerView, firstHeader: View): View? {
        val isReverseLayout = mOrientationProvider.isReverseLayout(parent)
        val step = if (isReverseLayout) -1 else 1
        val from = if (isReverseLayout) parent.childCount - 1 else 0
        var i = from
        while (i >= 0 && i <= parent.childCount - 1) {
            val child = parent.getChildAt(i)
            if (!itemIsObscuredByHeader(parent, child, firstHeader, mOrientationProvider.getOrientation(parent))) {
                return child
            }
            i += step
        }
        return null
    }

    /**
     * Determines if an item is obscured by a header
     *
     *
     * @param parent
     * @param item        to determine if obscured by header
     * @param header      that might be obscuring the item
     * @param orientation of the [RecyclerView]
     * @return true if the item view is obscured by the header view
     */
    private fun itemIsObscuredByHeader(parent: RecyclerView, item: View, header: View, orientation: Int): Boolean {
        val layoutParams = item.getLayoutParams() as RecyclerView.LayoutParams
        mDimensionCalculator.initMargins(mTempRect1, header)

        val adapterPosition = parent.getChildAdapterPosition(item)
        if (adapterPosition == RecyclerView.NO_POSITION || mHeaderProvider.getHeader(parent, adapterPosition) !== header) {
            // Resolves https://github.com/timehop/sticky-headers-recyclerview/issues/36
            // Handles an edge case where a trailing header is smaller than the current sticky header.
            return false
        }

        if (orientation == LinearLayoutManager.VERTICAL) {
            val itemTop = item.getTop() - layoutParams.topMargin
            val headerBottom = getListTop(parent) + header.getBottom() + mTempRect1.bottom + mTempRect1.top
            if (itemTop >= headerBottom) {
                return false
            }
        } else {
            val itemLeft = item.getLeft() - layoutParams.leftMargin
            val headerRight = getListLeft(parent) + header.getRight() + mTempRect1.right + mTempRect1.left
            if (itemLeft >= headerRight) {
                return false
            }
        }

        return true
    }

    private fun getListTop(view: RecyclerView): Int {
        return if (view.layoutManager!!.clipToPadding) {
            view.paddingTop
        } else {
            0
        }
    }

    private fun getListLeft(view: RecyclerView): Int {
        return if (view.layoutManager!!.clipToPadding) {
            view.paddingLeft
        } else {
            0
        }
    }
}