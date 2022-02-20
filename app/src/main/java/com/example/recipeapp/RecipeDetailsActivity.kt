package com.example.recipeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recipeapp.databinding.ActivityRecipeDetailsBinding
import com.squareup.picasso.Picasso

class RecipeDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipeDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val type = intent.getStringExtra("type")
        val desc = intent.getStringExtra("desc")
        val image = intent.getStringExtra("image")
        val ingredients = intent.getStringExtra("ingredients")
        val step = intent.getStringExtra("step")

        Picasso.get().load(image).into(binding.rdImage)
        binding.rdName.text = name
        binding.rdType.text = type
        binding.rdDesc.text = desc
        binding.rdIngredient.text = ingredients
        binding.rdStep.text = step

    }
}