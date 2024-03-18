const nodemailer = require("nodemailer");
const fs = require("fs");
const ejs = require("ejs");
const { convert } = require("html-to-text");
const juice = require("juice");

const smtp = nodemailer.createTransport({
  host: "smtp.gmail.com",
  port: 587,
  secure: false,
  auth: {
    user: process.env.EMAIL,
    pass: process.env.PASSWORD,
  },
  tls: {
    rejectUnauthorized: false
}
});

const sendMail = ({
  template: templateName,
  templateVars,
  ...restOfOptions
}) => {
  const templatePath = `templates/${templateName}.html`;
  const options = {
    from: process.env.EMAIL,
    ...restOfOptions,
  };

  if (templateName && fs.existsSync(templatePath)) {
    const template = fs.readFileSync(templatePath, "utf-8");
    const html = ejs.render(template, templateVars);
    // templateVars là các biến được truyền vào template thông qua hàm render
    //const text = convert(html);
    const htmlWithStylesInlined = juice(html);

    options.html = htmlWithStylesInlined;
    //options.text = text;
  }

  // hàm smtp.sendMail() này sẽ trả về cho chúng ta một Promise
  return smtp.sendMail(options);
};

module.exports = {
  sendMail: sendMail,
};
