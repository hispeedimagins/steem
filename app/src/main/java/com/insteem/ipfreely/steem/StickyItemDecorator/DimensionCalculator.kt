package com.insteem.ipfreely.steem.StickyItemDecorator

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup

/**
 * Helper to calculate various view dimensions
 */
class DimensionCalculator {

    /**
     * Populates [Rect] with margins for any view.
     *
     *
     * @param margins rect to populate
     * @param view for which to get margins
     */
    fun initMargins(margins: Rect, view: View) {
        val layoutParams = view.getLayoutParams()

        if (layoutParams is ViewGroup.MarginLayoutParams) {
            val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
            initMarginRect(margins, marginLayoutParams)
        } else {
            margins.set(0, 0, 0, 0)
        }
    }

    /**
     * Converts [MarginLayoutParams] into a representative [Rect].
     *
     * @param marginRect Rect to be initialized with margins coordinates, where
     * [MarginLayoutParams.leftMargin] is equivalent to [Rect.left], etc.
     * @param marginLayoutParams margins to populate the Rect with
     */
    private fun initMarginRect(marginRect: Rect, marginLayoutParams: ViewGroup.MarginLayoutParams) {
        marginRect.set(
                marginLayoutParams.leftMargin,
                marginLayoutParams.topMargin,
                marginLayoutParams.rightMargin,
                marginLayoutParams.bottomMargin
        )
    }

}