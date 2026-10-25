package es.demo.clinica.audit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import es.demo.clinica.FixedClockTestConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = "clinica.demo-data=false")
@Transactional
@Import(FixedClockTestConfig.class)
class AuditServiceTest {

    @Autowired
    private AuditService audit;

    @Test
    void recordsEventsWithTheirActorAndReadsThemBack() {
        audit.record("recepcion", AuditAction.CANCELLATION, "cita-1", "Cancelada desde recepción");
        audit.record(AuditService.PATIENT_ACTOR, AuditAction.MESSAGE, "cita-1", "Mensaje del paciente");

        List<AuditEvent> events = audit.recent();

        assertTrue(events.stream().anyMatch(e -> e.getType() == AuditAction.CANCELLATION
                && "recepcion".equals(e.getActor())));
        assertTrue(events.stream().anyMatch(e -> e.getType() == AuditAction.MESSAGE
                && AuditService.PATIENT_ACTOR.equals(e.getActor())));
    }
}
