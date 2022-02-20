const recipesModel = require("../models/recipes_model");
const db = require("../database");
const express = require("express");
const usersModel = require("../models/users_model");
const router = express.Router();

// Get all recipes
router.get("/", async (req, res, next) => {
  try {
    const recipes = await recipesModel.get();
    return res.json(recipes);
  } catch (e) {
    return next(e);
  }
});

// Get one recipe by id
router.get("/:id", async (req, res, next) => {
  try {
    const recipe = await recipesModel.getById(req.params.id);
    if (!recipe) return res.sendStatus(404);

    return res.json(recipe);
  } catch (e) {
    return next(e);
  }
});

// Create a new recipe
router.post("/", async (req, res, next) => {
  try {
    const recipe = await recipesModel.create(req.body);
    if (!recipe) return res.sendStatus(409);
    return res.status(201).json(recipe);
  } catch (e) {
    return next(e);
  }
});

// Delete an recipe
router.delete("/:id", async (req, res, next) => {
  try {
    const recipe = await recipesModel.delete(req.params.id);
    if (!recipe) return res.sendStatus(404);
    return res.sendStatus(200);
  } catch (e) {
    return next(e);
  }
});

// Update an recipe
router.patch("/:id", async (req, res, next) => {
  try {
    const id = req.params.id;
    const data = req.body;

    const doc = await recipesModel.getById(id);
    if (!doc) return res.sendStatus(404);

    // Merge existing fields with the ones to be updated
    Object.keys(data).forEach((key) => (doc[key] = data[key]));

    const updateResult = await recipesModel.update(id, doc);
    if (!updateResult) return res.sendStatus(404);

    return res.json(doc);
  } catch (e) {
    return next(e);
  }
});

// Replace an recipe
router.put("/:id", async (req, res, next) => {
  try {
    const updateResult = await recipesModel.update(req.params.id, req.body);
    if (!updateResult) return res.sendStatus(404);

    const result = await recipesModel.getById(req.params.id);
    return res.json(result);
  } catch (e) {
    return next(e);
  }
});

module.exports = router;
