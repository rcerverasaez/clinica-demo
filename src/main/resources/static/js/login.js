const loginButton = document.querySelector("#login-form button");

// Muestra un aviso temporal en la esquina de la pantalla
function showToast(message) {
  const toast = document.createElement("div");
  toast.className = "toast";
  toast.setAttribute("role", "alert");
  toast.textContent = message;
  document.body.append(toast);
  setTimeout(() => toast.remove(), 6000);
}

// Envía el formulario cuando los dos campos están rellenos
function submitLogin() {
  const form = document.getElementById("loginForm");
  if (form.reportValidity()) {
    form.submit();
  }
}

loginButton.addEventListener("click", () => {
  try {
    submitLogin();
  } catch (error) {
    console.error("Error al iniciar sesión:", error);
    showToast(loginButton.dataset.errorMessage);
  }
});
