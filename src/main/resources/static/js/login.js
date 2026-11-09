// Envía el formulario de acceso cuando los dos campos están rellenos
const form = document.getElementById("loginForm");
const button = form.querySelector("button");

button.addEventListener("click", () => {
  if (form.reportValidity()) {
    form.submit();
  }
});
