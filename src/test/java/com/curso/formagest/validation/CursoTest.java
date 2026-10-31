package com.curso.formagest.validation;

import com.curso.formagest.model.Curso;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CursoTest {

    @Test
    void deberiaCrearCursoCorrectamente() {

        // GIVEN: preparamos los datos
        Curso curso = new Curso();

        curso.setId(1L);
        curso.setCodigo("JAVA01");
        curso.setNombre("Java");
        curso.setDescripcion("Curso de Java");
        curso.setHoras(40);
        curso.setActivo(true);

        // WHEN: obtenemos los datos
        // (en este caso, no necesitamos ejecutar ninguna operación)

        // THEN: comprobamos los resultados
        assertEquals(1L, curso.getId());
        assertEquals("JAVA01", curso.getCodigo());
        assertEquals("Java", curso.getNombre());
        assertEquals("Curso de Java", curso.getDescripcion());
        assertEquals(40, curso.getHoras());
        assertTrue(curso.isActivo());

        assertNotNull(curso.getEdiciones());
        assertTrue(curso.getEdiciones().isEmpty());
    }

}

