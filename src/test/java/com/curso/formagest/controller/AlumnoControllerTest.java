
package com.curso.formagest.controller;

import com.curso.formagest.model.Alumno;
import com.curso.formagest.repository.AlumnoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AlumnoController.
 *
 * Verifies CRUD operations and error handling for
 * student records in FormaGest.
 *
 * Uses JUnit 5 and Mockito to simulate repository
 * operations without accessing the PostgreSQL database.
 *
 * Each test follows the Arrange, Act, Assert pattern.
 *
 * Tests invoke controller methods directly and do not
 * validate HTTP request mapping or Bean Validation.
 */
class AlumnoControllerTest {

    /**
     * Mocked repository for student records.
     */
    private AlumnoRepository alumnoRepository;

    /**
     * Controller instance under test.
     */
    private AlumnoController alumnoController;

    /**
     * Initializes the repository mock and controller
     * before each test.
     */
    @BeforeEach
    void setUp() {

        alumnoRepository = mock(AlumnoRepository.class);

        alumnoController = new AlumnoController(
                alumnoRepository
        );
    }

    /**
     * Creates a mocked student with predefined values.
     *
     * @param id student identifier
     * @param nombre first name
     * @param apellidos last name
     * @param email email address
     * @param telefono telephone number
     * @return configured student mock
     */
    private Alumno crearAlumnoPrueba(
            Long id,
            String nombre,
            String apellidos,
            String email,
            String telefono) {

        Alumno alumno = mock(Alumno.class);

        when(alumno.getId()).thenReturn(id);
        when(alumno.getNombre()).thenReturn(nombre);
        when(alumno.getApellidos()).thenReturn(apellidos);
        when(alumno.getEmail()).thenReturn(email);
        when(alumno.getTelefono()).thenReturn(telefono);

        return alumno;
    }

    // =========================================================
    // GET - LIST ALL STUDENTS
    // =========================================================

    /**
     * Verifies that listing students returns all
     * available student records.
     *
     * Expected result:
     * A list containing two students.
     */
    @Test
    void listarAlumnos_devuelveTodos() {

        // Arrange
        Alumno alumno1 = mock(Alumno.class);
        Alumno alumno2 = mock(Alumno.class);

        when(alumnoRepository.findAll())
                .thenReturn(List.of(alumno1, alumno2));

        // Act
        List<Alumno> resultado =
                alumnoController.listarAlumnos();

        // Assert
        assertEquals(2, resultado.size());
        assertSame(alumno1, resultado.get(0));
        assertSame(alumno2, resultado.get(1));

        verify(alumnoRepository, times(1)).findAll();
    }

    /**
     * Verifies that listing students returns an empty
     * list when no students are registered.
     */
    @Test
    void listarAlumnos_sinRegistros_devuelveListaVacia() {

        // Arrange
        when(alumnoRepository.findAll())
                .thenReturn(Collections.emptyList());

        // Act
        List<Alumno> resultado =
                alumnoController.listarAlumnos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(alumnoRepository).findAll();
    }

    // =========================================================
    // GET - STUDENT BY ID
    // =========================================================

    /**
     * Verifies that retrieving an existing student
     * returns HTTP 200 with the correct student.
     */
    @Test
    void obtenerAlumno_existente_devuelve200() {

        // Arrange
        Long alumnoId = 1L;

        Alumno alumno = mock(Alumno.class);

        when(alumnoRepository.findById(alumnoId))
                .thenReturn(Optional.of(alumno));

        // Act
        ResponseEntity<Alumno> response =
                alumnoController.obtenerAlumno(alumnoId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(alumno, response.getBody());

        verify(alumnoRepository).findById(alumnoId);
    }

    /**
     * Verifies that retrieving a nonexistent student
     * returns HTTP 404 (Not Found).
     */
    @Test
    void obtenerAlumno_inexistente_devuelve404() {

        // Arrange
        Long alumnoId = 999L;

        when(alumnoRepository.findById(alumnoId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<Alumno> response =
                alumnoController.obtenerAlumno(alumnoId);

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNull(response.getBody());

        verify(alumnoRepository).findById(alumnoId);
    }

    // =========================================================
    // POST - CREATE STUDENT
    // =========================================================

    /**
     * Verifies that creating a valid student
     * returns HTTP 201 (Created).
     *
     * Expected result:
     * The student is saved and returned in the response.
     */
    @Test
    void crearAlumno_valido_devuelve201() {

        // Arrange
        Alumno alumno = mock(Alumno.class);

        when(alumnoRepository.save(alumno))
                .thenReturn(alumno);

        // Act
        ResponseEntity<Alumno> response =
                alumnoController.crearAlumno(alumno);

        // Assert
        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertSame(alumno, response.getBody());

        verify(alumno).setId(null);
        verify(alumnoRepository).save(alumno);
    }

    /**
     * Verifies that the controller clears the supplied
     * student identifier before creating a record.
     *
     * Expected result:
     * The identifier is reset to null before saving,
     * allowing PostgreSQL to generate a new one.
     */
    @Test
    void crearAlumno_conIdRecibido_reiniciaId() {

        // Arrange
        Alumno alumno = new Alumno();
        alumno.setId(99L);

        when(alumnoRepository.save(any(Alumno.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // Act
        ResponseEntity<Alumno> response =
                alumnoController.crearAlumno(alumno);

        // Assert
        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertNull(alumno.getId());

        ArgumentCaptor<Alumno> captor =
                ArgumentCaptor.forClass(Alumno.class);

        verify(alumnoRepository).save(captor.capture());

        assertNull(captor.getValue().getId());
    }

    // =========================================================
    // PUT - UPDATE STUDENT
    // =========================================================

    /**
     * Verifies that updating an existing student
     * returns HTTP 200 with the updated record.
     *
     * Expected result:
     * The student information is updated and saved.
     */
    @Test
    void actualizarAlumno_existente_devuelve200() {

        // Arrange
        Long alumnoId = 1L;

        Alumno alumnoExistente = new Alumno();
        alumnoExistente.setId(alumnoId);
        alumnoExistente.setNombre("Javier");
        alumnoExistente.setApellidos("Romero");
        alumnoExistente.setEmail("original@test.com");
        alumnoExistente.setTelefono("600000000");

        Alumno datos = new Alumno();
        datos.setNombre("Carlos");
        datos.setApellidos("Martinez");
        datos.setEmail("actualizado@test.com");
        datos.setTelefono("611111111");

        when(alumnoRepository.findById(alumnoId))
                .thenReturn(Optional.of(alumnoExistente));

        when(alumnoRepository.save(any(Alumno.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // Act
        ResponseEntity<Alumno> response =
                alumnoController.actualizarAlumno(
                        alumnoId, datos
                );

        // Assert
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        Alumno actualizado = response.getBody();
        assertNotNull(actualizado);

        assertEquals(alumnoId, actualizado.getId());
        assertEquals("Carlos", actualizado.getNombre());
        assertEquals("Martinez", actualizado.getApellidos());
        assertEquals(
                "actualizado@test.com",
                actualizado.getEmail()
        );
        assertEquals("611111111", actualizado.getTelefono());

        verify(alumnoRepository).save(alumnoExistente);
    }

    /**
     * Verifies that updating a nonexistent student
     * returns HTTP 404 (Not Found).
     *
     * Expected result:
     * No student record is saved.
     */
    @Test
    void actualizarAlumno_inexistente_devuelve404() {

        // Arrange
        Long alumnoId = 999L;

        Alumno datos = new Alumno();

        when(alumnoRepository.findById(alumnoId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<Alumno> response =
                alumnoController.actualizarAlumno(
                        alumnoId, datos
                );

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(alumnoRepository).findById(alumnoId);

        verify(alumnoRepository, never())
                .save(any(Alumno.class));
    }

    // =========================================================
    // DELETE - REMOVE STUDENT
    // =========================================================

    /**
     * Verifies that deleting an existing student
     * returns HTTP 204 (No Content).
     *
     * Expected result:
     * The repository deletes the student and flushes
     * the pending database operations.
     */
    @Test
    void eliminarAlumno_existente_devuelve204() {

        // Arrange
        Long alumnoId = 1L;

        when(alumnoRepository.existsById(alumnoId))
                .thenReturn(true);

        // Act
        ResponseEntity<Void> response =
                alumnoController.eliminarAlumno(alumnoId);

        // Assert
        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        assertNull(response.getBody());

        verify(alumnoRepository).deleteById(alumnoId);
        verify(alumnoRepository).flush();
    }

    /**
     * Verifies that deleting a nonexistent student
     * returns HTTP 404 (Not Found).
     *
     * Expected result:
     * No deletion operation is performed.
     */
    @Test
    void eliminarAlumno_inexistente_devuelve404() {

        // Arrange
        Long alumnoId = 999L;

        when(alumnoRepository.existsById(alumnoId))
                .thenReturn(false);

        // Act
        ResponseEntity<Void> response =
                alumnoController.eliminarAlumno(alumnoId);

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(alumnoRepository).existsById(alumnoId);

        verify(alumnoRepository, never())
                .deleteById(anyLong());

        verify(alumnoRepository, never()).flush();
    }

    /**
     * Verifies that deleting a student with related
     * database records returns HTTP 409 (Conflict).
     *
     * Test scenario:
     * The repository throws a DataIntegrityViolationException
     * because related records prevent deletion.
     *
     * Expected result:
     * The controller handles the exception and returns
     * HTTP 409 instead of propagating the error.
     */
    @Test
    void eliminarAlumno_conRegistrosRelacionados_devuelve409() {

        // Arrange
        Long alumnoId = 1L;

        when(alumnoRepository.existsById(alumnoId))
                .thenReturn(true);

        doThrow(new DataIntegrityViolationException(
                "Foreign key constraint violation"
        )).when(alumnoRepository).flush();

        // Act
        ResponseEntity<Void> response =
                alumnoController.eliminarAlumno(alumnoId);

        // Assert
        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        verify(alumnoRepository).deleteById(alumnoId);
        verify(alumnoRepository).flush();
    }
}
