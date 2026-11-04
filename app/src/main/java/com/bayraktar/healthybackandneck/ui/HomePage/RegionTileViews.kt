package com.bayraktar.healthybackandneck.ui.HomePage

import android.view.LayoutInflater
import android.widget.GridLayout
import androidx.core.view.doOnLayout
import com.bayraktar.healthybackandneck.databinding.ItemRegionTileBinding

fun populateRegionGrid(grid: GridLayout, regions: List<TrainingRegion>, onClick: (TrainingRegion) -> Unit) {
    grid.removeAllViews()
    regions.forEachIndexed { index, region ->
        val item = ItemRegionTileBinding.inflate(LayoutInflater.from(grid.context), grid, false)
        item.regionImage.setImageResource(region.icon)
        item.regionLabel.setText(region.title)
        item.root.tag = region.key
        item.root.contentDescription = grid.context.getString(region.title)
        item.root.setOnClickListener { onClick(region) }
        item.root.layoutParams = GridLayout.LayoutParams(
            GridLayout.spec(index / grid.columnCount), GridLayout.spec(index % grid.columnCount, 1f)
        ).apply {
            width = 0
            val gap = (4 * grid.resources.displayMetrics.density).toInt()
            setMargins(gap, gap, gap, gap)
        }
        grid.addView(item.root)
    }
    // Reserve enough space for the longest localized label, also with large fonts.
    grid.doOnLayout {
        val height = (0 until grid.childCount).maxOfOrNull { grid.getChildAt(it).measuredHeight } ?: 0
        for (index in 0 until grid.childCount) grid.getChildAt(index).minimumHeight = height
    }
}
