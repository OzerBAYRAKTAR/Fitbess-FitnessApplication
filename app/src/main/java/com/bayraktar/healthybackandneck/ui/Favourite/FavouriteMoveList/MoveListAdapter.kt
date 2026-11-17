package com.bayraktar.healthybackandneck.ui.Favourite.FavouriteMoveList

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise
import com.bayraktar.healthybackandneck.databinding.ItemFavoritesEmptyBinding
import com.bayraktar.healthybackandneck.databinding.ItemFavoritesFooterBinding
import com.bayraktar.healthybackandneck.databinding.ItemFavoritesHeaderBinding
import com.bayraktar.healthybackandneck.databinding.ItemSavedFavoriteBinding
import com.bayraktar.healthybackandneck.utils.ExerciseMedia

/** Header, real saved rows and empty/footer share one scrolling viewport. No access state is rendered. */
class MoveListAdapter(
    private val inspect: (Int) -> Unit,
    private val remove: (Int) -> Unit,
    private val discover: () -> Unit,
    private val retry: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var state = SavedFavoritesState()
    private var rows = emptyList<ExerciseDayExercise>()
    init { setHasStableIds(true) }

    fun submit(value: SavedFavoritesState) {
        state = value
        rows = value.uniqueRecords
        notifyDataSetChanged()
    }
    override fun getItemCount() = if (!state.loaded) 1 else if (rows.isEmpty()) 2 else rows.size + 2
    override fun getItemViewType(position: Int) = when {
        position == 0 -> HEADER
        rows.isEmpty() -> EMPTY
        position == itemCount - 1 -> FOOTER
        else -> ROW
    }
    override fun getItemId(position: Int): Long = when (getItemViewType(position)) {
        HEADER -> Long.MIN_VALUE
        EMPTY -> Long.MIN_VALUE + 1
        FOOTER -> Long.MIN_VALUE + 2
        else -> rows[position - 1].exerciseId.toLong()
    }
    private class Header(val binding: ItemFavoritesHeaderBinding) : RecyclerView.ViewHolder(binding.root)
    private class Row(val binding: ItemSavedFavoriteBinding) : RecyclerView.ViewHolder(binding.root)
    private class Empty(val binding: ItemFavoritesEmptyBinding) : RecyclerView.ViewHolder(binding.root)
    private class Footer(val binding: ItemFavoritesFooterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            HEADER -> Header(ItemFavoritesHeaderBinding.inflate(inflater, parent, false))
            EMPTY -> Empty(ItemFavoritesEmptyBinding.inflate(inflater, parent, false))
            FOOTER -> Footer(ItemFavoritesFooterBinding.inflate(inflater, parent, false))
            else -> Row(ItemSavedFavoriteBinding.inflate(inflater, parent, false))
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is Header -> holder.binding.apply {
                val hasRows = state.loaded && rows.isNotEmpty()
                favoriteCount.visibility = if (hasRows) View.VISIBLE else View.GONE
                favoriteCount.text = if (hasRows) {
                    val quantity = root.resources.getQuantityString(R.plurals.fs_saved_label, rows.size, rows.size)
                    SpannableString(quantity).apply {
                        val numberEnd = quantity.indexOf('\n').takeIf { it >= 0 } ?: quantity.length
                        setSpan(RelativeSizeSpan(2.7f), 0, numberEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(ForegroundColorSpan(ContextCompat.getColor(root.context, R.color.ac_lime)), 0, numberEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                } else null
                heroTitle.visibility = if (hasRows) View.GONE else View.VISIBLE
                heroTitle.setText(R.string.fs_empty_hero)
                heroSubtitle.setText(if (hasRows) R.string.fs_saved_hero_body else R.string.fs_empty_hero_body)
                favoritesSection.visibility = if (hasRows) View.VISIBLE else View.GONE
                favoritesLoading.visibility = if (state.loading) View.VISIBLE else View.GONE
                favoriteError.visibility = if (state.loadError || state.removeError) View.VISIBLE else View.GONE
                favoriteError.setText(if (state.removeError) R.string.fs_remove_error else R.string.fs_load_error)
                retryFavorites.visibility = if (state.loadError) View.VISIBLE else View.GONE
                retryFavorites.setOnClickListener { retry() }
            }
            is Empty -> holder.binding.btnCheck.setOnClickListener { discover() }
            is Footer -> holder.binding.discoverFavorites.setOnClickListener { discover() }
            is Row -> holder.binding.apply {
                val model = rows[position - 1]
                favoriteImage.setImageResource(ExerciseMedia.drawable(model))
                favoriteName.text = model.exerciseName
                val category = when (model.titleName) {
                    "stretch" -> R.string.fs_category_stretch
                    "bellyneck" -> R.string.fs_category_back
                    "arm" -> R.string.fs_category_arm
                    "abs" -> R.string.fs_category_abs
                    "legbutt" -> R.string.fs_category_leg
                    else -> null
                }
                favoriteCategory.visibility = if (category == null) View.GONE else View.VISIBLE
                category?.let(favoriteCategory::setText)
                deleteBtn.contentDescription = root.context.getString(R.string.fs_remove_named, model.exerciseName)
                deleteBtn.isEnabled = !state.loading && state.removing == null
                deleteBtn.setOnClickListener { remove(model.exerciseId) }
                inspectFavorite.setOnClickListener { inspect(model.exerciseId) }
                root.setOnClickListener { inspect(model.exerciseId) }
            }
        }
    }
    companion object {
        private const val HEADER = 0
        private const val ROW = 1
        private const val EMPTY = 2
        private const val FOOTER = 3
    }
}
