package com.insteem.ipfreely.steem.StickyItemDecorator

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


interface StickyRecyclerHeadersAdapter<VH : RecyclerView.ViewHolder> {

    /**
     * @return the number of views in the adapter
     */
    fun getItemCount(): Int

    /**
     * Get the ID of the header associated with this item.  For example, if your headers group
     * items by their first letter, you could return the character representation of the first letter.
     * Return a value &lt; 0 if the view should not have a header (like, a header view or footer view)
     *
     * @param position the position of the view to get the header ID of
     * @return the header ID
     */
    fun getHeaderId(position: Int): Long

    /**
     * Creates a new ViewHolder for a header.  This works the same way onCreateViewHolder in
     * Recycler.Adapter, ViewHolders can be reused for different views.  This is usually a good place
     * to inflate the layout for the header.
     *
     * @param parent the view to create a header view holder for
     * @return the view holder
     */
    fun onCreateHeaderViewHolder(parent: ViewGroup): VH

    /**
     * Binds an existing ViewHolder to the specified adapter position.
     *
     * @param holder the view holder
     * @param position the adapter position
     */
    fun onBindHeaderViewHolder(holder: VH, position: Int)
}