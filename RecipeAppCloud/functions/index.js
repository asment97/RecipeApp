const functions = require("firebase-functions");
const express = require("express");
const app = express();
const usersRouter = require("./api/controllers/users_controller");
const recipesRouter = require("./api/controllers/recipes_controller");
const users_model = require("./api/models/users_model");
const { admin } = require("./api/database");

app.use(express.json());
app.use("/users", usersRouter);
app.use("/recipes", recipesRouter);

// create user when user is signing up
exports.register = functions.auth.user().onCreate((user, context) => {
  users_model.createWithCustomId({
    id: user.uid,
    name: user.email.split("@")[0],
    email: user.email,
    password: user.passwordHash,
  });
});

admin.auth().createUser;

exports.deleteUser = functions.firestore
  .document("users/{userID}")
  .onDelete((snap, context) => {
    return admin
      .auth()
      .deleteUser(snap.id)
      .then(() => console.log("Deleted user with ID:" + snap.id))
      .catch((error) =>
        console.error("There was an error while deleting user:", error)
      );
  });

exports.api = functions.https.onRequest(app);

// To handle "Function Timeout" exception
exports.functionsTimeOut = functions.runWith({
  timeoutSeconds: 300,
});
