package com.example.recipeapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class RecipeAdapter(private val context: Context,
                    private val dataSource: ArrayList<Recipe>) : BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item, parent, false)

        val tvName: TextView = rowView.findViewById(R.id.name)
        val tvDesc: TextView = rowView.findViewById(R.id.desc)
        val tvDate: TextView = rowView.findViewById(R.id.date)
        val imageView: ImageView = rowView.findViewById(R.id.image)

        val  recipe = getItem(position) as Recipe

        tvName.text = recipe.name
        tvDesc.text = recipe.desc
        tvDate.text = recipe.createdAt
        Picasso.get().load(recipe.image).into(imageView)

        return rowView
    }
}