package com.example.recipeapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class MainActivity : AppCompatActivity() {

    private companion object {
        private  const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    private val db = Firebase.firestore

    var recipeList: ArrayList<Recipe> = ArrayList()

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val spinner: Spinner = binding.spinnerRecipeTypes
        ArrayAdapter.createFromResource(
            this,
            R.array.recipe_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        listView = binding.lvRecipes

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                recipeList.clear()
                var query: Task<QuerySnapshot>
                if (spinner.selectedItemPosition == 0){
                    query = db.collection("recipes").get()
                } else{
                    query = db.collection("recipes").whereEqualTo("type", spinner.selectedItem.toString()).get()
                }
                query.addOnSuccessListener{
                        result ->

                    for(document in result.documents){
                        Log.d("",document.data.toString())
                        val recipe = document.data?.let { Recipe.from(it) }

                        if (recipe != null) {
                            recipeList.add(recipe)
                        }
                    }
                    val adapter = RecipeAdapter(this@MainActivity, recipeList)
                    listView.adapter = adapter
                }.addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }

            }

        }

        binding.lvRecipes.setOnItemClickListener{ parent, view, position, id ->
            val name = recipeList[position].name
            val type = recipeList[position].type
            val desc = recipeList[position].desc
            val image = recipeList[position].image
            val ingredients = recipeList[position].ingredients
            val step = recipeList[position].step

            Log.d("", ingredients.toString())

            var ingredientsString = ""

            ingredients.forEach{ ingredient ->
                ingredientsString += ingredient["name"].toString()+ "  "+ ingredient["amount"].toString()+"\n"
            }

            var intent = Intent(this@MainActivity, RecipeDetailsActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("type", type)
            intent.putExtra("desc", desc)
            intent.putExtra("image", image)
            intent.putExtra("ingredients", ingredientsString)
            intent.putExtra("step", step)

            startActivity(intent)

        }

        binding.btnAdd.setOnClickListener {
            createRecipeLauncher.launch(Intent(this@MainActivity, AddRecipeActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.mi_logout){
            FirebaseAuth.getInstance().signOut()

            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
            Log.i(TAG, "Logout")
        }
        return super.onOptionsItemSelected(item)
    }

    private val createRecipeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            recipeList.clear()
            db.collection("recipes").get().addOnSuccessListener{
                    result ->

                for(document in result.documents){
                    Log.d("",document.data.toString())
                    val recipe = document.data?.let { Recipe.from(it) }

                    if (recipe != null) {
                        recipeList.add(recipe)
                    }
                }
                val adapter = RecipeAdapter(this, recipeList)
                listView.adapter = adapter
            }.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
        }
    }
}