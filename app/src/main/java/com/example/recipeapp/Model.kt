package com.example.recipeapp

data class Recipe(
    var name: String = "",
    var type: String = "",
    var desc: String = "",
    var createdAt: String = "",
    var ingredients: ArrayList<Map<String, String>> = ArrayList(),
    var step: String = "",
    var image: String ="",
){
    companion object {
        fun from(map: Map<String, Any>) =
        object {
            val name by map
            val type by map
            val desc by map
            val createdAt by map
            val ingredients by map
            val step by map
            val image by map


            val data = Recipe(
                name as String, type as String, desc as String, createdAt as String,
                ingredients as ArrayList<Map<String, String>>, step as String, image as String)
        }.data
    }
}

//data class Ingredient(
//    var name: String = "",
//    var amount: String = ""
//){
////    companion object {
////        fun from(map: Map<String, String>) =
////            object {
////                val name by map
////                val amount by map
////
////                val data = Recipe(
////                    name, amount)
////            }.data
////    }
//}