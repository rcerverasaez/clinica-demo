function showOutboxError() {
  document.getElementById("outbox-error").hidden = false;
}

// Prepara los recordatorios y actualiza el buzón sin recargar la página
async function prepareReminders(form) {
  const response = await fetch(form.action, { method: "POST" });
  if (!response.ok) {
    showOutboxError();
    return;
  }
  const page = new DOMParser().parseFromString(await response.text(), "text/html");
  document.getElementById("contenido").replaceChildren(...page.getElementById("contenido").childNodes);
}

document.addEventListener("submit", (event) => {
  if (event.target.id === "outbox-form") {
    event.preventDefault();
    prepareReminders(event.target).catch(showOutboxError);
  }
});
