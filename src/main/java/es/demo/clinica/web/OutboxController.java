package es.demo.clinica.web;

import es.demo.clinica.notifications.ReminderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Buzón de recepción: recordatorios simulados y botón para enviarlos sin esperar a la hora. */
@Controller
public class OutboxController {

    private final ReminderService reminders;

    public OutboxController(ReminderService reminders) {
        this.reminders = reminders;
    }

    @GetMapping("/recepcion/buzon")
    public String outbox(Model model) {
        model.addAttribute("notifications", reminders.outbox());
        return "recepcion-buzon";
    }

    @PostMapping("/recepcion/buzon/enviar")
    public String sendNow(RedirectAttributes redirect) {
        int sent = reminders.sendDueReminders();
        redirect.addFlashAttribute("sent", sent);
        return "redirect:/recepcion/buzon";
    }
}
