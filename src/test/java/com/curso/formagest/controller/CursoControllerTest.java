
package com.curso.formagest.controller;

import com.curso.formagest.model.Curso;
import com.curso.formagest.repository.CursoRepository;
import com.curso.formagest.model.Edicion;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CursoControllerTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoController cursoController;

    @Test
    void deberiaObtenerTodosLosCursos() {

        // GIVEN: preparamos los datos
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setCodigo("JAVA01");
        curso.setNombre("Java");
        curso.setDescripcion("Curso de Java");
        curso.setHoras(40);
        curso.setActivo(true);

        when(cursoRepository.findAll())
                .thenReturn(List.of(curso));

        // WHEN: ejecutamos el método del controlador
        List<Curso> resultado = cursoController.getAllCursos();

        // THEN: comprobamos el resultado
        assertEquals(1, resultado.size());
        assertEquals("Java", resultado.get(0).getNombre());

        // Comprobamos que se ha llamado al repositorio
        verify(cursoRepository).findAll();
    }


    @Test
    void deberiaObtenerCursoCuandoExiste() {

        // GIVEN
        Curso curso = new Curso();
        curso.setId(1L);
        curso.setCodigo("JAVA01");
        curso.setNombre("Java");
        curso.setDescripcion("Curso de Java");

        when(cursoRepository.findById(1L))
                .thenReturn(java.util.Optional.of(curso));

        // WHEN
        var respuesta = cursoController.getCurso(1L);

        // THEN
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());
        assertEquals("Java", respuesta.getBody().getNombre());

        verify(cursoRepository).findById(1L);
    }

    @Test
    void deberiaDevolver404CuandoCursoNoExiste() {

        // GIVEN
        when(cursoRepository.findById(99L))
                .thenReturn(java.util.Optional.empty());

        // WHEN
        var respuesta = cursoController.getCurso(99L);

        // THEN
        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(cursoRepository).findById(99L);
    }


    @Test
    void deberiaCrearCursoSinId() {

        // GIVEN: preparamos un curso con un ID enviado por el cliente
        Curso curso = new Curso();
        curso.setId(10L);
        curso.setCodigo("JAVA02");
        curso.setNombre("Java avanzado");
        curso.setDescripcion("Curso avanzado de Java");
        curso.setHoras(60);
        curso.setActivo(true);

        // Simulamos el guardado en el repositorio
        when(cursoRepository.save(any(Curso.class)))
                .thenAnswer(invocation -> {
                    Curso cursoGuardado = invocation.getArgument(0);

                    // Comprobamos que el controlador ha eliminado el ID
                    assertNull(cursoGuardado.getId());

                    // Simulamos que la base de datos genera un ID
                    cursoGuardado.setId(1L);

                    return cursoGuardado;
                });

        // WHEN: ejecutamos el método del controlador
        Curso resultado = cursoController.crear(curso);

        // THEN: comprobamos el resultado
        assertNotNull(resultado.getId());
        assertEquals(1L, resultado.getId());
        assertEquals("JAVA02", resultado.getCodigo());
        assertEquals("Java avanzado", resultado.getNombre());

        // Comprobamos que se ha llamado al repositorio una vez
        verify(cursoRepository, times(1)).save(curso);
    }


    @Test
    void deberiaActualizarCursoCuandoExiste() {

        // GIVEN: curso original
        Curso cursoOriginal = new Curso();
        cursoOriginal.setId(1L);
        cursoOriginal.setCodigo("JAVA01");
        cursoOriginal.setNombre("Java básico");
        cursoOriginal.setDescripcion("Curso básico");
        cursoOriginal.setHoras(30);
        cursoOriginal.setActivo(false);

        // Curso con los nuevos datos
        Curso cursoActualizado = new Curso();
        cursoActualizado.setCodigo("JAVA02");
        cursoActualizado.setNombre("Java avanzado");
        cursoActualizado.setDescripcion("Curso avanzado");
        cursoActualizado.setHoras(60);
        cursoActualizado.setActivo(true);

        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(cursoRepository.findById(1L))
                .thenReturn(java.util.Optional.of(cursoOriginal));
        when(cursoRepository.save(any(Curso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        var respuesta = cursoController.update(cursoActualizado, 1L);

        // THEN
        assertEquals(200, respuesta.getStatusCode().value());
        assertNotNull(respuesta.getBody());

        assertEquals("JAVA02", respuesta.getBody().getCodigo());
        assertEquals("Java avanzado", respuesta.getBody().getNombre());
        assertEquals("Curso avanzado", respuesta.getBody().getDescripcion());
        assertEquals(60, respuesta.getBody().getHoras());
        assertTrue(respuesta.getBody().isActivo());

        // Comprobamos las llamadas al repositorio
        verify(cursoRepository).existsById(1L);
        verify(cursoRepository).findById(1L);
        verify(cursoRepository).save(cursoOriginal);
    }

    @Test
    void deberiaDevolver404CuandoSeActualizaCursoInexistente() {

        // GIVEN
        Curso curso = new Curso();
        curso.setNombre("Java");

        when(cursoRepository.existsById(99L)).thenReturn(false);

        // WHEN
        var respuesta = cursoController.update(curso, 99L);

        // THEN
        assertEquals(404, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(cursoRepository).existsById(99L);

        // No debería intentar buscar ni guardar el curso
        verify(cursoRepository, never()).findById(anyLong());
        verify(cursoRepository, never()).save(any(Curso.class));
    }


    @Test
    void deberiaDevolver404CuandoSeEliminaCursoInexistente() {

        // GIVEN
        when(cursoRepository.existsById(99L)).thenReturn(false);

        // WHEN
        var respuesta = cursoController.deleteCurso(99L);

        // THEN
        assertEquals(404, respuesta.getStatusCode().value());

        verify(cursoRepository).existsById(99L);
        verify(cursoRepository, never()).findById(anyLong());
        verify(cursoRepository, never()).deleteById(anyLong());
    }

    @Test
    void deberiaDevolver409CuandoCursoTieneEdiciones() {

        // GIVEN
        Curso curso = new Curso();
        curso.setId(1L);
        curso.getEdiciones().add(new Edicion());

        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(cursoRepository.findById(1L))
                .thenReturn(java.util.Optional.of(curso));

        // WHEN
        var respuesta = cursoController.deleteCurso(1L);

        // THEN
        assertEquals(409, respuesta.getStatusCode().value());
        assertEquals(
                "No se puede eliminar un curso asociado a una edición",
                respuesta.getBody()
        );

        verify(cursoRepository).existsById(1L);
        verify(cursoRepository).findById(1L);
        verify(cursoRepository, never()).deleteById(anyLong());
    }

    @Test
    void deberiaEliminarCursoCuandoNoTieneEdiciones() {

        // GIVEN
        Curso curso = new Curso();
        curso.setId(1L);

        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(cursoRepository.findById(1L))
                .thenReturn(java.util.Optional.of(curso));

        // WHEN
        var respuesta = cursoController.deleteCurso(1L);

        // THEN
        assertEquals(204, respuesta.getStatusCode().value());
        assertNull(respuesta.getBody());

        verify(cursoRepository).deleteById(1L);
    }

}
