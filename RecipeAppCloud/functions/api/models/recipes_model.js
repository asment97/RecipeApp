const database = require("../database");

// Here, we are implementing the class with Singleton design pattern

class RecipesModel {
  constructor() {
    if (this.instance) return this.instance;
    RecipesModel.instance = this;
  }

  get() {
    return database.getList("recipes");
  }

  getById(id) {
    return database.get("recipes", id);
  }

  create(recipe) {
    return database.createWithID("recipes", recipe);
  }

  delete(id) {
    return database.delete("recipes", id);
  }

  update(id, recipe) {
    return database.set("recipes", id, recipe);
  }
}

module.exports = new RecipesModel();
