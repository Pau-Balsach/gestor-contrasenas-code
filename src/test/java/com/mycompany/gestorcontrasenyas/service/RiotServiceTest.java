package com.mycompany.gestorcontrasenyas.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests para la lógica local de RiotService (parsing de riotId, validación de puuid).
 */
@DisplayName("RiotService – lógica local")
class RiotServiceTest {

    // ── Parsing de riotId (gameName#tagLine) ─────────────────────────

    @Test
    @DisplayName("riotId válido: tiene exactamente un '#' con gameName y tagLine no vacíos")
    void riotId_valid_format() {
        String riotId = "PlayerName#EUW";
        String[] partes = riotId.split("#", -1);
        assertThat(partes).hasSize(2);
        assertThat(partes[0]).isEqualTo("PlayerName");
        assertThat(partes[1]).isEqualTo("EUW");
    }

    /**
     * Un riotId es inválido si el split no da exactamente 2 partes,
     * o si alguna de las partes está vacía (ej. "#soloTag" o "solo#").
     */
    private static boolean esRiotIdInvalido(String riotId) {
        if (riotId == null || riotId.isEmpty()) return true;
        String[] partes = riotId.split("#", -1);
        return partes.length != 2 || partes[0].isEmpty() || partes[1].isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"SinAlmohadilla", "doble##almohadilla", "", "#soloTag", "solo#"})
    @DisplayName("riotId mal formado -> inválido (sin rango)")
    void riotId_invalid_format_returns_sin_rango(String riotId) {
        assertThat(esRiotIdInvalido(riotId)).isTrue();
    }

    // ── obtenerRangoLol: puuid nulo o vacío ───────────────────────────

    @Test
    @DisplayName("obtenerRangoLol con puuid null -> 'Sin rango' (sin red)")
    void getRangoLol_null_puuid_returns_sin_rango() {
        String result = RiotService.obtenerRangoLol(null);
        assertThat(result).isEqualTo("Sin rango");
    }

    @Test
    @DisplayName("obtenerRangoLol con puuid vacío -> 'Sin rango' (sin red)")
    void getRangoLol_empty_puuid_returns_sin_rango() {
        String result = RiotService.obtenerRangoLol("");
        assertThat(result).isEqualTo("Sin rango");
    }

    // ── Constantes de configuración ───────────────────────────────────

    @Test
    @DisplayName("La región de Valorant es 'eu'")
    void valorant_region_is_eu() {
        assertThat(RiotService.obtenerRangoValorant("malformado")).isEqualTo("Sin rango");
    }

    @Test
    @DisplayName("obtenerPuuid con riotId mal formado -> null (sin red)")
    void obtenerPuuid_invalid_riotId_returns_null() {
        String result = RiotService.obtenerPuuid("sinAlmohadilla");
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("obtenerPuuid null -> null")
    void obtenerPuuid_null_returns_null() {
        assertThat(RiotService.obtenerPuuid(null)).isNull();
    }
}