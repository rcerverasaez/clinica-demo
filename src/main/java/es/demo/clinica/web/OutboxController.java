package es.demo.clinica.web;

import es.demo.clinica.notifications.OutboxModeRules;
import es.demo.clinica.notifications.ReminderService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Buzón de recepción: recordatorios simulados y botón para enviarlos sin esperar a la hora. */
@Controller
public class OutboxController {

    private final ReminderService reminders;
    private final OutboxModeRules modeRules;

    public OutboxController(ReminderService reminders, OutboxModeRules modeRules) {
        this.reminders = reminders;
        this.modeRules = modeRules;
    }

    @GetMapping("/recepcion/buzon")
    public String outbox(Model model) {
        model.addAttribute("notifications", reminders.outbox());
        return "recepcion-buzon";
    }

    @PostMapping("/recepcion/buzon/enviar")
    public String sendNow(@RequestParam(required = false) String mode, RedirectAttributes redirect,
            HttpServletResponse response) {
        Optional<String> rejection = modeRules.rejectionFor(mode);
        if (rejection.isPresent()) {
            response.setHeader("X-Reject-Reason", rejection.get());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
        int sent = reminders.sendDueReminders();
        redirect.addFlashAttribute("sent", sent);
        return "redirect:/recepcion/buzon";
    }
}
