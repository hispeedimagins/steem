package com.steemapp.lokisveil.steemapp.Interfaces

import android.arch.paging.PagedList
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

interface CommonRecyclerAndPaged {
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    fun getCurrentList(): PagedList<Any>?

    fun getItem(position: Int): Any?

    fun getItemCount(): Int

    fun getItemViewType(position: Int): Int
}