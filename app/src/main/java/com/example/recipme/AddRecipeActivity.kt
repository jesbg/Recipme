package com.example.recipme

import android.os.Bundle

class AddRecipeActivity : ModifyRecipeAbstract() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Add Recipe"

        binding.saveBtn.setOnClickListener {
            saveRecipe()
        }
    }

    private fun saveRecipe() {
        if (!checkIsInputsValid()) {
            return
        }

        val documentCreated = firestore.collection("recipe").document()
        val documentId = documentCreated.id

        val recipe = getRecipeFromInput(documentId)

        firestore.collection("recipe").document(documentId).set(recipe.copy(id = documentId))

        finish()
    }
}