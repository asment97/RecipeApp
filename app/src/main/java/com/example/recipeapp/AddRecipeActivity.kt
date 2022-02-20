package com.example.recipeapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.iterator
import com.example.recipeapp.databinding.ActivityAddRecipeBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class AddRecipeActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddRecipeBinding

    val db = Firebase.firestore

    private var imageUri: Uri? = null

    var recipe: Recipe = Recipe()

    var ingredientsList: ArrayList<Map<String, String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val spinner: Spinner = binding.arTypes
        ArrayAdapter.createFromResource(
            this,
            R.array.recipe_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val layoutList : LinearLayout = binding.arLayoutIngredient
        val buttonAdd: Button = binding.arBtnIngAdd

        buttonAdd.setOnClickListener{
            val ingredientView: View = LayoutInflater.from(this@AddRecipeActivity).inflate(R.layout.ingredient_row, null, false)

            val ingredientName: EditText = ingredientView.findViewById(R.id.ing_name)
            val ingredientAmount: EditText = ingredientView.findViewById(R.id.ing_amount)
            val buttonRemove: Button = ingredientView.findViewById(R.id.btn_ing_remove)

            buttonRemove.setOnClickListener {
                layoutList.removeView(ingredientView)
            }

            layoutList.addView(ingredientView)
        }

        binding.arBtnSelectImage.setOnClickListener {
            showImageAttachMenu()
        }

        binding.arBtnSubmit.setOnClickListener {
            recipe.createdAt = SimpleDateFormat("yyyy/MM/dd").format(java.util.Calendar.getInstance().time)
            val validData = validateData()
            if(validData){
                if(FirebaseAuth.getInstance().currentUser == null){
                    Toast.makeText(this, "No signed in user", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                db.collection("recipes").add(recipe)

                val returnIntent = Intent();
                setResult(RESULT_OK, returnIntent)
                finish()
            }

            Log.d("", SimpleDateFormat("yyyy/MM/dd").format(java.util.Calendar.getInstance().time))

        }

    }

    private fun showImageAttachMenu(){
        val popupMenu = PopupMenu(this@AddRecipeActivity, binding.arBtnSelectImage)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if(id == 0){
                pickImageCamera()
            }
            else if(id == 1) {
                pickImageGallery()
            }

            true
        }
    }
    private fun pickImageCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)

    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data

            uploadImage()

            binding.arImage.setImageURI(imageUri)
        } else {
            Toast.makeText(this@AddRecipeActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private val galleryActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            imageUri = data!!.data

            uploadImage()

            binding.arImage.setImageURI(imageUri)

        } else {
            Toast.makeText(this@AddRecipeActivity, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImage(){
        val filePathAndName = "recipes/" + binding.arTypes.selectedItem.toString() + "/" + binding.arName.text.toString()
        val reference = FirebaseStorage.getInstance().getReference(filePathAndName)
        reference.putFile(imageUri!!).addOnSuccessListener {
            taskSnapshot ->

            val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
            var uploadImageUrl = "${uriTask.result}"

            Log.d("",uriTask.result.toString())


            recipe.image = uploadImageUrl


        }.addOnFailureListener{
            e ->
            Toast.makeText(this, "Failed to upload image due to ${e.message}",Toast.LENGTH_SHORT).show()
        }

    }

    private fun validateData(): Boolean {
        val layoutList : LinearLayout = binding.arLayoutIngredient


        if(binding.arName.text.isNotEmpty()){
            recipe.name = binding.arName.text.toString()
        }else{
            Toast.makeText(this@AddRecipeActivity, "Please enter name", Toast.LENGTH_SHORT).show()
            return false
        }

        if(binding.arTypes.selectedItemPosition!= 0){
            recipe.type = binding.arTypes.selectedItem.toString()
        }else{
            Toast.makeText(this@AddRecipeActivity, "Please select type", Toast.LENGTH_SHORT).show()
            return false
        }

        if(binding.arDesc.text.isNotEmpty()){
            recipe.desc = binding.arDesc.text.toString()
        }else{
            Toast.makeText(this@AddRecipeActivity, "Please enter description", Toast.LENGTH_SHORT).show()
            return false
        }

        ingredientsList.clear()
        for(i in layoutList){
            val ingredientView: View = i
            val editTextName: EditText = ingredientView.findViewById(R.id.ing_name);
            val editTextAmount: EditText = ingredientView.findViewById(R.id.ing_amount);

            var ingredient: MutableMap<String, String> = mutableMapOf()
            if(editTextName.text.isNotEmpty()&&editTextAmount.text.isNotEmpty()){
                ingredient["name"]= editTextName.text.toString()
                ingredient["amount"] = editTextAmount.text.toString()
            }else{
                break
            }

            ingredientsList.add(ingredient)
        }
        if (ingredientsList.isNotEmpty()){
            recipe.ingredients = ingredientsList
        }else{
            Toast.makeText(this@AddRecipeActivity, "Please add your ingredient", Toast.LENGTH_SHORT).show()
            return false
        }

        if(binding.arStep.text.isNotEmpty()){
            recipe.step = binding.arStep.text.toString()
        }else{
            Toast.makeText(this@AddRecipeActivity, "Please enter step", Toast.LENGTH_SHORT).show()
            return false
        }

        if(imageUri == null){
            Toast.makeText(this@AddRecipeActivity, "Please upload an image", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(recipe.image.isEmpty()){
            Toast.makeText(this@AddRecipeActivity, "Image uploading, please wait", Toast.LENGTH_SHORT).show()
            return false
        }

        Log.d("",recipe.name)
        Log.d("",recipe.type)
        Log.d("",recipe.desc)
        Log.d("",recipe.ingredients.toString())
        Log.d("",recipe.step)
        Log.d("",recipe.image)

        return true
    }
}