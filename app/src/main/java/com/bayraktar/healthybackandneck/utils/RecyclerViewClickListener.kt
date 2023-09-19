package com.bayraktar.healthybackandneck.utils

import android.view.View

interface RecyclerViewClickListener {
    fun recyclerviewListClicked(v: View, position: Int)
}