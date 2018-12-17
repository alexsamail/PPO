package com.example.cx61.ppo

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class NewsMargin(internal var Space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = Space
        outRect.bottom = Space
        outRect.right = Space
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = Space
        }
    }
}