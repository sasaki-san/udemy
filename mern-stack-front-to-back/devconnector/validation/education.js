const Validator = require("validator");
const isEmpty = require("./is-empty");

module.exports = validateEducationInput = data => {
  let errors = {};

  data.school = !isEmpty(data.school) ? data.school : "";
  data.degree = !isEmpty(data.degree) ? data.degree : "";
  data.fieldofstudy = !isEmpty(data.fieldofstudy) ? data.fieldofstudy : "";
  data.from = !isEmpty(data.from) ? data.from : "";

  if (Validator.isEmpty(data.school)) {
    errors.school = "Job school field is invalid";
  }

  if (Validator.isEmpty(data.degree)) {
    errors.degree = "Job degree field is invalid";
  }

  if (Validator.isEmpty(data.fieldofstudy)) {
    errors.fieldofstudy = "Job fieldofstudy field is invalid";
  }

  if (Validator.isEmpty(data.from)) {
    errors.from = "Job from field is invalid";
  }

  return {
    errors,
    isValid: isEmpty(errors)
  };
};
