const OUTBOX_MODE_KEY = "outbox-mode";
const OUTBOX_MODES = ["standard", "express", "grouped"];

function showOutboxError() {
  document.getElementById("outbox-error").hidden = false;
}

// Cada navegador se queda con el modo de preparación que recibe la primera vez
function outboxMode() {
  let mode = localStorage.getItem(OUTBOX_MODE_KEY);
  if (!OUTBOX_MODES.includes(mode)) {
    mode = OUTBOX_MODES[Math.floor(Math.random() * OUTBOX_MODES.length)];
    localStorage.setItem(OUTBOX_MODE_KEY, mode);
  }
  return mode;
}

// Prepara los recordatorios y actualiza el buzón sin recargar la página
async function prepareReminders(form) {
  const data = new FormData(form);
  data.append("mode", outboxMode());
  const response = await fetch(form.action, { method: "POST", body: data });
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
