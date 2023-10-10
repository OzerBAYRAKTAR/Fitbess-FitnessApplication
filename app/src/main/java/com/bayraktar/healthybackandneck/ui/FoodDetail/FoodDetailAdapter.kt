package com.bayraktar.healthybackandneck.ui.FoodDetail
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayraktar.healthybackandneck.Models.FoodModel.FoodItems
import com.bayraktar.healthybackandneck.databinding.ItemFoodBinding
import com.bayraktar.healthybackandneck.databinding.ItemOtherfoodsBinding
import com.bayraktar.healthybackandneck.utils.RecyclerViewClickListener


class FoodDetailAdapter(
    private var lsMenu: List<FoodItems>,
    private val rclClickListener: RecyclerViewClickListener
): RecyclerView.Adapter<FoodDetailAdapter.ItemHolder>() {


    inner class ItemHolder(val binding: ItemOtherfoodsBinding, recyclerViewClickListener: RecyclerViewClickListener): RecyclerView.ViewHolder(binding.root)
        , View.OnClickListener {

        private val mRecyclerViewClickListener: RecyclerViewClickListener = recyclerViewClickListener

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mRecyclerViewClickListener.recyclerviewListClicked(p0!!,adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemBinding = ItemOtherfoodsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemHolder(itemBinding,rclClickListener)
    }
    //update old list with new list
    fun setData(list: List<FoodItems>) {
        this.lsMenu = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val model =lsMenu[position]

        holder.binding.apply {
            txtTitleOther.text = model.title.toString()
            imageOtherfood.setImageResource(model.imageFood)

        }
    }

    override fun getItemCount(): Int = lsMenu.size
}