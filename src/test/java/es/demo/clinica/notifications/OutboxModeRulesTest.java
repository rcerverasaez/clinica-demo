package es.demo.clinica.notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OutboxModeRulesTest {

    @Test
    void rechazaCadaModoQueNoCumpleSuRegla() {
        OutboxModeRules rules = new OutboxModeRules(false, 12, "");

        assertEquals("mode-disabled", rules.rejectionFor("standard").orElseThrow());
        assertEquals("horizon-too-long", rules.rejectionFor("express").orElseThrow());
        assertEquals("sender-missing", rules.rejectionFor("grouped").orElseThrow());
    }

    @Test
    void aceptaLosModosQueCumplenSuRegla() {
        OutboxModeRules rules = new OutboxModeRules(true, 24, "recordatorios@clinica.demo");

        assertTrue(rules.rejectionFor("standard").isEmpty());
        assertTrue(rules.rejectionFor("express").isEmpty());
        assertTrue(rules.rejectionFor("grouped").isEmpty());
    }
}
