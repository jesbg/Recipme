package com.example.recipme

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.recipme.databinding.ActivityAddrecipeBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

abstract class ModifyRecipeAbstract : AppCompatActivity() {
    protected lateinit var binding: ActivityAddrecipeBinding
    protected val firestore = Firebase.firestore
    private val firebaseStorage = Firebase.storage

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            openGallery()
        }
    }

    private val startGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val uri = it.data?.data
        if (uri != null) {
            uploadImage(uri)
        }
    }

    protected val viewModel: AddRecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddrecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.img.setOnClickListener {
            changeImage()
        }

    }

    // validation
    fun checkIsInputsValid(): Boolean {
        val name = binding.tieRecipeName.text.toString()
        val cookingTime = binding.tieRecipeCookingTime.text.toString()
        val ingredients = binding.tieIngredients.text.toString()
        val preparations = binding.tiePreparation.text.toString()

        var isValid = true

        if (name.isBlank()) {
            binding.tilRecipeName.error = "Recipe name cannot empty"
            isValid = false
        }

        if (cookingTime.isBlank()) {
            binding.tilRecipeCookingTime.error = "Cooking time cannot empty"
            isValid = false
        }

        if (ingredients.isBlank()) {
            binding.tilIngredients.error = "Ingredients cannot empty"
            isValid = false
        }

        if (preparations.isBlank()) {
            binding.tilPreparation.error = "Preparations cannot empty"
            isValid = false
        }

        return isValid
    }

    fun getRecipeFromInput(id: String): Recipe {
        return Recipe(
            id = id,
            image = viewModel.imageUrl,
            name = binding.tieRecipeName.text.toString(),
            cookingtime = binding.tieRecipeCookingTime.text.toString(),
            ingredients = binding.tieIngredients.text.toString(),
            preparations = binding.tiePreparation.text.toString(),
            isFavorite = false
        )
    }


    // upload an image
    private fun changeImage() {
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
            return
        }

        requestPermission.launch(permission)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/*")
            .addCategory(Intent.CATEGORY_OPENABLE)
        val mimeTypes = arrayOf("image/jpeg", "image/png")

        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

        startGallery.launch(Intent.createChooser(intent, "Choose Image"))
    }

    private fun uploadImage(imageUri: Uri) {
        val filename = "${autoId()}.${getMimeType(this, imageUri)}"

        Log.d("Filename Upload", filename)
        val storageRef = firebaseStorage.getReference("recipe_image/${filename}")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener {
                    val url = it.toString()

                    viewModel.imageUrl = url
                    Glide
                        .with(this)
                        .load(url)
                        .transform(
                            CenterCrop(),
                            RoundedCorners(resources.getDimensionPixelSize(R.dimen.rounded_main))
                        )
                        .into(binding.img)
                }

            }.addOnFailureListener {
                Toast.makeText(this, "Failed change image", Toast.LENGTH_SHORT).show()
            }
    }

    // menu
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