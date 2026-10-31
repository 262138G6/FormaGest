
package com.curso.formagest.validation;

import com.curso.formagest.model.Curso;
import com.curso.formagest.model.Edicion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EdicionTest {

    @Test
    void deberiaCrearEdicionCorrectamente() {
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setCodigo("JAVA01");
        curso.setNombre("Java");
        curso.setDescripcion("Curso de Java");

        LocalDate fechaInicio = LocalDate.of(2026, 1, 10);
        LocalDate fechaFin = LocalDate.of(2026, 1, 20);

        Edicion edicion = new Edicion();
        edicion.setId(1L);
        edicion.setCurso(curso);
        edicion.setFecha_inicio(fechaInicio);
        edicion.setFecha_fin(fechaFin);
        edicion.setPlazas(20);

        assertEquals(1L, edicion.getId());
        assertSame(curso, edicion.getCurso());
        assertEquals(fechaInicio, edicion.getFecha_inicio());
        assertEquals(fechaFin, edicion.getFecha_fin());
        assertEquals(20, edicion.getPlazas());
    }

    @Test
    void deberiaAceptarUnRangoDeFechasValido() {
        Edicion edicion = new Edicion();

        edicion.setFecha_inicio(LocalDate.of(2026, 1, 10));
        edicion.setFecha_fin(LocalDate.of(2026, 1, 20));

        assertTrue(edicion.isValidRange());
    }

    @Test
    void deberiaAceptarFechasIguales() {
        LocalDate fecha = LocalDate.of(2026, 1, 10);

        Edicion edicion = new Edicion();
        edicion.setFecha_inicio(fecha);
        edicion.setFecha_fin(fecha);

        assertTrue(edicion.isValidRange());
    }

    @Test
    void deberiaRechazarUnRangoDeFechasInvertido() {
        Edicion edicion = new Edicion();

        edicion.setFecha_inicio(LocalDate.of(2026, 1, 20));
        edicion.setFecha_fin(LocalDate.of(2026, 1, 10));

        assertFalse(edicion.isValidRange());
    }
}
