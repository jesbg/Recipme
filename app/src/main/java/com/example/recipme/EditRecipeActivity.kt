package com.example.recipme

import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class EditRecipeActivity : ModifyRecipeAbstract() {
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipe = intent.getParcelableExtra("RECIPE")!!

        supportActionBar?.title = "Edit Recipe"

        binding.saveBtn.setOnClickListener {
            editRecipe(recipe.id!!)
        }

        populateDetails()
    }

    private fun populateDetails() {
        viewModel.imageUrl = recipe.image ?: ""

        Glide
            .with(this)
            .load(recipe.image)
            .transform(
                CenterCrop(),
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.rounded_main))
            )
            .into(binding.img)

        binding.tieRecipeName.setText(recipe.name)
        binding.tieRecipeCookingTime.setText(recipe.cookingtime.toString())
        binding.tieIngredients.setText(recipe.ingredients)
        binding.tiePreparation.setText(recipe.preparations)
    }

    private fun editRecipe(id: String) {
        if (!checkIsInputsValid()) {
            return
        }

        val recipe = getRecipeFromInput(id)

        firestore.collection("recipe").document(id).set(recipe)

        finish()
    }
}