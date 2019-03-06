package com.insteem.ipfreely.steem.StickyItemDecorator



import android.view.View;
import android.view.ViewGroup;
import androidx.collection.LongSparseArray
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * An implementation of [HeaderProvider] that creates and caches header views
 */
class HeaderViewCache(private val mAdapter: StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>,
                      private val mOrientationProvider: OrientationProvider) : HeaderProvider {
    private val mHeaderViews = LongSparseArray<View>()

    override fun getHeader(parent: RecyclerView, position: Int): View {
        val headerId = mAdapter.getHeaderId(position)

        var header = mHeaderViews.get(headerId)
        if (header == null) {
            //TODO - recycle views
            val viewHolder   = mAdapter.onCreateHeaderViewHolder(parent)
            mAdapter.onBindHeaderViewHolder(viewHolder, position)
            header = viewHolder.itemView
            if (header!!.getLayoutParams() == null) {
                header!!.setLayoutParams(ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            }

            val widthSpec: Int
            val heightSpec: Int

            if (mOrientationProvider.getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
            } else {
                widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.UNSPECIFIED)
                heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.EXACTLY)
            }

            val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    parent.paddingLeft + parent.paddingRight, header!!.getLayoutParams().width)
            val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    parent.paddingTop + parent.paddingBottom, header!!.getLayoutParams().height)
            header!!.measure(childWidth, childHeight)
            header!!.layout(0, 0, header!!.getMeasuredWidth(), header!!.getMeasuredHeight())
            mHeaderViews.put(headerId, header)
        }
        return header
    }

    override fun invalidate() {
        mHeaderViews.clear()
    }
}