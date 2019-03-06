package com.insteem.ipfreely.steem.sb

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.*


class CardList(context: Context) {

    private var mList: SharedPreferences? = null

    val cards: List<String>?
        get() {
            var result: MutableList<String>? = null
            if (mList != null) {
                val list = mList!!.getStringSet(KEY_SET, null)
                if (list != null && list!!.size > 0) {
                    result = ArrayList<String>(list!!.size)
                    result!!.addAll(list!!)
                    result.sort()
                }
            }
            return result
        }

    //		int result = 0;
    //		Set<String> items = (Set<String>)mList.getStringSet(KEY_SET, null);
    //		if (items != null) {
    //			Log.d("test", "getCardSize: " + items.size());
    //			result = items.size();
    //		}
    //		return result;
    var cardCount: Int
        get() = cardcount
        set(count) {
            cardcount = count
        }

    init {
        mList = context.getSharedPreferences(PRFERENCE_NAME, android.content.Context.MODE_PRIVATE)
    }

    fun addCard(data: String?): Int {
        var result = 0
        if (data != null) {
            var items: MutableSet<String>? = mList!!.getStringSet(KEY_SET, null)
            if (items == null) {
                items = HashSet()
            }
            result = items.size
            Log.d("test", "addCard: $data")
            items.add(data)
            val editor = mList!!.edit()
            editor.putStringSet(KEY_SET, items)
            editor.commit()
            cardcount++
        }
        return result
    }

    fun removeCard(data: String?): Boolean {
        var result = false
        if (data != null) {
            // int count = mList.getInt(KEY_COUNT, 0);
            var items = mList!!.getStringSet(KEY_SET, null)
            items.remove(data)

            if (items.size == 0)
                cardcount = 0

            val editor = mList!!.edit()
            // editor.putInt(KEY_COUNT, count - 1);
            // editor.remove(KEY_PREFIX + index);
            editor.putStringSet(KEY_SET, items)
            editor.commit()
            Log.d("test", "removeCard: " + data + ", current count = " + items.size)
            result = true
        }
        return result
    }


    fun setCardCount(lastitem: String) {
        var value = 0
        try {
            value = Integer.valueOf(lastitem)
            cardCount = value + 1
        } catch (e: NumberFormatException) {
        }

    }

    fun clearList() {
        val editor = mList!!.edit()
        editor.clear()
        editor.commit()

        cardcount = 0
    }

    companion object {
        private val PRFERENCE_NAME = "com.abc.smartbulletin.sample"
        private val KEY_COUNT = "count"
        private val KEY_SET = "item_set"
        private val KEY_PREFIX = "item"
        private var cardcount = 0
    }
}