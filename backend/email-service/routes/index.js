var express = require('express');
const { sendMail } = require('../controllers/sendMail');
var router = express.Router();

router.post('/api/email/send', sendMail);

module.exports = router;
