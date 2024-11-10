package com.example.recipme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipme.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firestore: FirebaseFirestore

    private lateinit var mainAdapter: MainAdapter

    private var recipeListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firestore = Firebase.firestore

        initRecipeList()

        binding.iconAddRecipe.setOnClickListener {
            openAddRecipe()
        }
    }

    private fun initRecipeList() {
        mainAdapter = MainAdapter(
            this,
            onEditBtnClick = {
                openEditRecipe(it)
            },
            onViewBtnClick = {
                openViewRecipe(it)
            },
            onDeleteBtnClick = {
                deleteRecipe(it)
               // Toast.makeText(getApplicationContext(),"Recipe has been deleted", LENGTH_SHORT).show()
            },
            onFavBtnClick = { recipeId, isFav ->
                changeFav(recipeId, isFav)
            }
        )

        binding.recipeRV.layoutManager = LinearLayoutManager(this)
        binding.recipeRV.adapter = mainAdapter
    }

    private fun listenRecipes() {
        recipeListener = firestore.collection("recipe").addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firestore fetch recipe", error.message ?: "")
            }

            val data = value?.toObjects<Recipe>()

            data?.let {
                mainAdapter.submitList(it)
                Log.d("Recipes :", it.toString())
            }
        }
    }

    // delete recipe with alert dialog
    private fun deleteRecipe(id: String) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Delete Recipe")
        builder.setMessage("Are you sure you want to delete this item? ")
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            firestore.collection("recipe").document(id).delete()
            Toast.makeText(getApplicationContext(),"Recipe has been deleted", LENGTH_SHORT).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
        }

        builder.show()
    }

//    private fun deleteRecipe(id: String) {
//        firestore.collection("recipe").document(id).delete()
//    }

    private fun changeFav(id: String, isFav: Boolean) {
        firestore.collection("recipe").document(id).update("isFavorite", isFav)

    }

    private fun openAddRecipe() {
        val i = Intent(this, AddRecipeActivity::class.java)

        startActivity(i)
    }

    private fun openViewRecipe(recipe: Recipe) {
        val i = Intent(this, ViewRecipeActivity::class.java)
        i.putExtra("RECIPE", recipe)

        startActivity(i)
    }

    private fun openEditRecipe(recipe: Recipe) {
        val i = Intent(this, EditRecipeActivity::class.java)
        i.putExtra("RECIPE", recipe)

        startActivity(i)
    }

    override fun onStart() {
        super.onStart()

        listenRecipes()
    }

    override fun onStop() {
        super.onStop()

        recipeListener?.remove()
        recipeListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite -> {
                startActivity(Intent(this, FavoriteRecipeActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}