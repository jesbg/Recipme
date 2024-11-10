package com.example.recipme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.recipme.databinding.ActivityViewRecipeBinding
import com.google.firebase.firestore.FirebaseFirestore

class ViewRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewRecipeBinding
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Detail Recipe"
        }


        recipe = intent.getParcelableExtra("RECIPE")!!

        populateDetails()
    }

    private fun populateDetails() {
        Glide
            .with(this)
            .load(recipe.image)
            .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.rounded_main)))
            .into(binding.img)

        binding.tieRecipeName.text = recipe.name
        binding.tieRecipeCookingTime.text = recipe.cookingtime.toString()
        binding.tieIngredients.text = recipe.ingredients
        binding.tiePreparation.text = recipe.preparations
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}