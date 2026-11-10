const bookingForm = document.getElementById("booking-form");
const bookingError = document.getElementById("booking-error");

// Prepara la fecha con el formato que espera la API
function toApiDate(value) {
  return new Date(value).toISOString().slice(0, 16);
}

// Reúne los datos del formulario para enviarlos a la API
function readBooking() {
  const { _csrf, ...booking } = Object.fromEntries(new FormData(bookingForm));
  return { ...booking, start: toApiDate(booking.start) };
}

// Reserva la cita y lleva a la confirmación
async function confirmBooking() {
  const response = await fetch(bookingForm.dataset.apiUrl, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(readBooking()),
  });
  if (response.status === 400) {
    bookingForm.submit();
    return;
  }
  if (!response.ok) {
    bookingError.hidden = false;
    return;
  }
  const { code } = await response.json();
  window.location.assign(`${bookingForm.action}?confirmedCode=${encodeURIComponent(code)}`);
}

bookingForm.addEventListener("submit", (event) => {
  // Si falta algún dato, el envío normal muestra los errores de cada campo
  if ([...new FormData(bookingForm).values()].every(Boolean)) {
    event.preventDefault();
    bookingError.hidden = true;
    confirmBooking().catch(() => {
      bookingError.hidden = false;
    });
  }
});
