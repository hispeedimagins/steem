package com.steemapp.lokisveil.steemapp.Interfaces

import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView

interface CommonRecyclerAndPaged {
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    fun getCurrentList(): PagedList<Any>?

    fun getItem(position: Int): Any?

    fun getItemCount(): Int

    fun getItemViewType(position: Int): Int
}