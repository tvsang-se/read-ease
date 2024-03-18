const { sendMail } = require("../helpers/sendMail");

module.exports.sendMail = async (req, res) => {
  const data = req.body;
  console.log("data", data);
  try {
    await sendMail({ template: data.templateName, ...data });
    res.send("Send mail successfully !");
  } catch (error) {
    console.log("error", error);
    res.status(500).send("Send mail fail !");
  }
};
