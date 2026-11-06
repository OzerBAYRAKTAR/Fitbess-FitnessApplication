package com.bayraktar.healthybackandneck.ui.HomePage

import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.core.view.doOnLayout
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.databinding.ItemRegionTileBinding

fun populateRegionGrid(grid: GridLayout, regions: List<TrainingRegion>, compact: Boolean = false, onClick: (TrainingRegion) -> Unit) {
    grid.removeAllViews()
    regions.forEachIndexed { index, region ->
        val item = ItemRegionTileBinding.inflate(LayoutInflater.from(grid.context), grid, false)
        item.root.clipToOutline = true
        item.regionImage.clipToOutline = true
        item.regionImage.setImageResource(region.icon)
        val title = if (region.key == "back") R.string.region_back else region.title
        item.regionLabel.setText(title)
        if (compact) {
            val density = grid.resources.displayMetrics.density
            val needsMoreLabelSpace = grid.resources.configuration.screenWidthDp < 360 || grid.resources.configuration.fontScale > 1.2f
            item.root.orientation = LinearLayout.HORIZONTAL
            item.root.minimumHeight = (84 * density).toInt()
            item.regionImage.layoutParams = LinearLayout.LayoutParams(((if (needsMoreLabelSpace) 40 else 52) * density).toInt(), (68 * density).toInt())
            item.regionLabel.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = (8 * density).toInt()
            }
            item.regionLabel.gravity = android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
            item.regionLabel.minLines = 1
        }
        item.root.tag = region.key
        item.root.contentDescription = grid.context.getString(title)
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
