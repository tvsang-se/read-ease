import { translateFunc } from "./translator.js";
import { registerEureka } from "./eureka.js";

import express from "express";
import cors from "cors";

// Creating express object
const app = express();
const PORT = 8081;

// Cors
var allowlist = ["http://localhost:8080"];
var corsOptionsDelegate = function (req, callback) {
  var corsOptions;
  if (allowlist.indexOf(req.header("Origin")) !== -1) {
    corsOptions = { origin: true }; // reflect (enable) the requested origin in the CORS response
  } else {
    corsOptions = { origin: false }; // disable CORS for this request
  }
  callback(null, corsOptions); // callback expects two parameters: error and options
};
app.use(cors(corsOptionsDelegate)); //

app.use(express.json());

// parse requests of content-type - application/x-www-form-urlencoded
app.use(express.urlencoded({ extended: true }));

// Defining port number

app.post("/api/translate", async (req, res) => {
  try {
    await translateFunc(req, res);
  } catch (error) {
    console.log(error);
    res.status(500).json({
      message: "Fail to transtale !",
    });
  }
});

app.get("/", (req, res) => {
  res.json({ message: "Welcome to translate server !" });
});


// Server setup
app.listen(PORT, () => {
  registerEureka('TRANSLATE-SERVICE',PORT)
  console.log(`Running server on PORT ${PORT}...`);
});
