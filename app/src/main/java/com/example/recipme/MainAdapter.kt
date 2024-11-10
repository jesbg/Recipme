package com.example.recipme

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class MainAdapter(
    private val context: Context,
    private val onViewBtnClick: (recipe: Recipe) -> Unit,
    private val onEditBtnClick: (recipe: Recipe) -> Unit,
    private val onDeleteBtnClick: (recipeId: String) -> Unit,
    private val onFavBtnClick: (recipeId: String, isFav: Boolean) -> Unit
) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    var recipes: MutableList<Recipe> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(inputList: List<Recipe>) {
        recipes.clear()
        recipes.addAll(inputList)

        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.tv_recipeName)
        val cookingTime = itemView.findViewById<TextView>(R.id.tv_recipeCookingTime)
        val viewBtn = itemView.findViewById<Button>(R.id.viewbtn)
        val editBtn = itemView.findViewById<Button>(R.id.editbtn)
        val deleteBtn = itemView.findViewById<ImageButton>(R.id.imgBtn_preparationDelete)
        val image = itemView.findViewById<ImageView>(R.id.iv_recipe)
        val recipeNoPicture = itemView.findViewById<LinearLayout>(R.id.ll_recipeNoPicture)
        val favorite = itemView.findViewById<ImageButton>(R.id.imgBtn_recipeFavorite)

        fun bind(recipe: Recipe, position: Int) {
            name.text = recipe.name
            cookingTime.text = "${recipe.cookingtime} ${if ((recipe.cookingtime?.toIntOrNull() ?: 0) > 1) "Minutes" else "Minute"}"

            if (recipe.image.isNullOrEmpty()) {
                recipeNoPicture.visibility = View.VISIBLE
            } else {
                recipeNoPicture.visibility = View.GONE
                Glide
                    .with(context)
                    .load(recipe.image)
                    .transform(CenterCrop(), RoundedCorners(context.resources.getDimensionPixelSize(R.dimen.rounded_main)))
                    .into(image)
            }

            favorite.imageTintMode = PorterDuff.Mode.SRC_IN

            if (recipe.isFavorite) {
                favorite.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_favorite_filled))
                favorite.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
            } else {
                favorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_outlined))
                favorite.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray))

            }

            viewBtn.setOnClickListener {
                onViewBtnClick(recipe)
            }

            editBtn.setOnClickListener {
                onEditBtnClick(recipe)
            }

            deleteBtn.setOnClickListener {
                onDeleteBtnClick(recipe.id!!)
            }

            favorite.setOnClickListener {
                onFavBtnClick(recipe.id!!, !recipe.isFavorite)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipes[position], position)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
}