
package com.curso.formagest.controller;

import com.curso.formagest.dto.SolicitudRequest;
import com.curso.formagest.dto.SolicitudResponse;
import com.curso.formagest.enums.EstadoSolicitud;
import com.curso.formagest.enums.SituacionLaboral;
import com.curso.formagest.model.Alumno;
import com.curso.formagest.model.Edicion;
import com.curso.formagest.model.Solicitud;
import com.curso.formagest.repository.AlumnoRepository;
import com.curso.formagest.repository.EdicionRepository;
import com.curso.formagest.repository.SolicitudRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SolicitudController.
 *
 * Verifies CRUD operations and business rules related
 * to student applications in FormaGest.
 *
 * Uses JUnit 5 and Mockito to simulate repository
 * operations without accessing the PostgreSQL database.
 *
 * Each test follows the Arrange, Act, Assert pattern.
 *
 * These tests invoke controller methods directly.
 * HTTP request mapping and Bean Validation are not
 * tested at the web framework level.
 */
class SolicitudControllerTest {

    /**
     * Mocked repository for application records.
     */
    private SolicitudRepository solicitudRepository;

    /**
     * Mocked repository for student records.
     */
    private AlumnoRepository alumnoRepository;

    /**
     * Mocked repository for course edition records.
     */
    private EdicionRepository edicionRepository;

    /**
     * Controller instance under test.
     */
    private SolicitudController solicitudController;

    /**
     * Initializes repository mocks and the controller
     * before each test.
     */
    @BeforeEach
    void setUp() {

        solicitudRepository = mock(SolicitudRepository.class);
        alumnoRepository = mock(AlumnoRepository.class);
        edicionRepository = mock(EdicionRepository.class);

        solicitudController = new SolicitudController(
                solicitudRepository,
                alumnoRepository,
                edicionRepository
        );
    }

    /**
     * Creates an application entity with predefined
     * values for testing.
     *
     * @param solicitudId application identifier
     * @param alumnoId student identifier
     * @param edicionId course edition identifier
     * @return initialized application entity
     */
    private Solicitud crearSolicitudPrueba(
            Long solicitudId,
            Long alumnoId,
            Long edicionId) {

        Alumno alumno = mock(Alumno.class);
        Edicion edicion = mock(Edicion.class);

        when(alumno.getId()).thenReturn(alumnoId);
        when(edicion.getId()).thenReturn(edicionId);

        Solicitud solicitud = new Solicitud();

        solicitud.setId(solicitudId);
        solicitud.setAlumno(alumno);
        solicitud.setEdicion(edicion);
        solicitud.setFechaSolicitud(LocalDate.of(2026, 10, 30));
        solicitud.setSituacionLaboral(SituacionLaboral.DESEMPLEADO);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setObservaciones("Test application");

        return solicitud;
    }

    /**
     * Creates a request DTO with valid test data.
     *
     * @param alumnoId student identifier
     * @param edicionId course edition identifier
     * @return initialized request DTO
     */
    private SolicitudRequest crearRequestPrueba(
            Long alumnoId,
            Long edicionId) {

        SolicitudRequest request = new SolicitudRequest();

        request.setAlumnoId(alumnoId);
        request.setEdicionId(edicionId);
        request.setSituacionLaboral(SituacionLaboral.DESEMPLEADO);
        request.setObservaciones("Test application");

        return request;
    }

    // =========================================================
    // GET - LIST ALL APPLICATIONS
    // =========================================================

    /**
     * Verifies that listing applications returns
     * all available application records.
     *
     * Expected result:
     * A list containing two applications.
     */
    @Test
    void listarSolicitudes_devuelveTodas() {

        // Arrange
        Solicitud solicitud1 =
                crearSolicitudPrueba(1L, 2L, 3L);

        Solicitud solicitud2 =
                crearSolicitudPrueba(2L, 4L, 3L);

        when(solicitudRepository.findAll())
                .thenReturn(List.of(solicitud1, solicitud2));

        // Act
        List<SolicitudResponse> resultado =
                solicitudController.listarSolicitudes();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());

        verify(solicitudRepository, times(1)).findAll();
    }

    // =========================================================
    // GET - APPLICATION BY ID
    // =========================================================

    /**
     * Verifies that retrieving an existing application
     * returns HTTP 200 with the correct information.
     */
    @Test
    void obtenerSolicitud_existente_devuelve200() {

        // Arrange
        Long solicitudId = 1L;

        Solicitud solicitud =
                crearSolicitudPrueba(solicitudId, 2L, 3L);

        when(solicitudRepository.findById(solicitudId))
                .thenReturn(Optional.of(solicitud));

        // Act
        ResponseEntity<SolicitudResponse> response =
                solicitudController.obtenerSolicitud(solicitudId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        SolicitudResponse body = response.getBody();
        assertNotNull(body);

        assertEquals(1L, body.getId());
        assertEquals(2L, body.getAlumnoId());
        assertEquals(3L, body.getEdicionId());

        assertEquals(
                LocalDate.of(2026, 10, 30),
                body.getFechaSolicitud()
        );

        assertEquals(
                SituacionLaboral.DESEMPLEADO,
                body.getSituacionLaboral()
        );

        assertEquals(
                EstadoSolicitud.PENDIENTE,
                body.getEstado()
        );

        assertEquals(
                "Test application",
                body.getObservaciones()
        );

        verify(solicitudRepository, times(1))
                .findById(solicitudId);
    }

    /**
     * Verifies that retrieving a nonexistent application
     * returns HTTP 404 (Not Found).
     */
    @Test
    void obtenerSolicitud_inexistente_devuelve404() {

        // Arrange
        Long solicitudId = 999L;

        when(solicitudRepository.findById(solicitudId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<SolicitudResponse> response =
                solicitudController.obtenerSolicitud(solicitudId);

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNull(response.getBody());

        verify(solicitudRepository, times(1))
                .findById(solicitudId);
    }

    // =========================================================
    // GET - APPLICATIONS BY STUDENT
    // =========================================================

    /**
     * Verifies that applications can be retrieved
     * by student identifier.
     *
     * Expected result:
     * Only applications associated with the student
     * are returned by the mocked repository.
     */
    @Test
    void listarPorAlumno_devuelveSolicitudesDelAlumno() {

        // Arrange
        Long alumnoId = 2L;

        Solicitud solicitud1 =
                crearSolicitudPrueba(1L, alumnoId, 3L);

        Solicitud solicitud2 =
                crearSolicitudPrueba(2L, alumnoId, 4L);

        when(solicitudRepository.findByAlumnoId(alumnoId))
                .thenReturn(List.of(solicitud1, solicitud2));

        // Act
        List<SolicitudResponse> resultado =
                solicitudController.listarPorAlumno(alumnoId);

        // Assert
        assertEquals(2, resultado.size());

        assertTrue(resultado.stream()
                .allMatch(s -> alumnoId.equals(s.getAlumnoId())));

        verify(solicitudRepository, times(1))
                .findByAlumnoId(alumnoId);
    }

    // =========================================================
    // GET - APPLICATIONS BY COURSE EDITION
    // =========================================================

    /**
     * Verifies that applications can be retrieved
     * by course edition identifier.
     */
    @Test
    void listarPorEdicion_devuelveSolicitudesDeLaEdicion() {

        // Arrange
        Long edicionId = 3L;

        Solicitud solicitud1 =
                crearSolicitudPrueba(1L, 2L, edicionId);

        Solicitud solicitud2 =
                crearSolicitudPrueba(2L, 4L, edicionId);

        when(solicitudRepository.findByEdicionId(edicionId))
                .thenReturn(List.of(solicitud1, solicitud2));

        // Act
        List<SolicitudResponse> resultado =
                solicitudController.listarPorEdicion(edicionId);

        // Assert
        assertEquals(2, resultado.size());

        assertTrue(resultado.stream()
                .allMatch(s -> edicionId.equals(s.getEdicionId())));

        verify(solicitudRepository, times(1))
                .findByEdicionId(edicionId);
    }

    // =========================================================
    // POST - CREATE APPLICATION
    // =========================================================

    /**
     * Verifies that creating a valid application
     * returns HTTP 201 (Created).
     *
     * Expected result:
     * The application is initialized with the current
     * date and PENDIENTE status before being saved.
     */
    @Test
    void crearSolicitud_valida_devuelve201() {

        // Arrange
        Long alumnoId = 2L;
        Long edicionId = 3L;

        SolicitudRequest request =
                crearRequestPrueba(alumnoId, edicionId);

        Alumno alumno = mock(Alumno.class);
        Edicion edicion = mock(Edicion.class);

        when(alumno.getId()).thenReturn(alumnoId);
        when(edicion.getId()).thenReturn(edicionId);

        when(alumnoRepository.existsById(alumnoId))
                .thenReturn(true);

        when(edicionRepository.existsById(edicionId))
                .thenReturn(true);

        when(solicitudRepository.existsByAlumnoIdAndEdicionId(
                alumnoId, edicionId))
                .thenReturn(false);

        when(alumnoRepository.getReferenceById(alumnoId))
                .thenReturn(alumno);

        when(edicionRepository.getReferenceById(edicionId))
                .thenReturn(edicion);

        when(solicitudRepository.saveAndFlush(any(Solicitud.class)))
                .thenAnswer(invocation -> {

                    Solicitud solicitud =
                            invocation.getArgument(0);

                    solicitud.setId(1L);

                    return solicitud;
                });

        // Act
        ResponseEntity<SolicitudResponse> response =
                solicitudController.crearSolicitud(request);

        // Assert
        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        SolicitudResponse body = response.getBody();
        assertNotNull(body);

        assertEquals(1L, body.getId());
        assertEquals(alumnoId, body.getAlumnoId());
        assertEquals(edicionId, body.getEdicionId());

        assertEquals(
                LocalDate.now(),
                body.getFechaSolicitud()
        );

        assertEquals(
                SituacionLaboral.DESEMPLEADO,
                body.getSituacionLaboral()
        );

        assertEquals(
                EstadoSolicitud.PENDIENTE,
                body.getEstado()
        );

        assertEquals(
                "Test application",
                body.getObservaciones()
        );

        verify(solicitudRepository, times(1))
                .saveAndFlush(any(Solicitud.class));
    }

    /**
     * Verifies that an application cannot be created
     * when the student does not exist.
     *
     * Expected result:
     * HTTP 404 and no application saved.
     */
    @Test
    void crearSolicitud_alumnoInexistente_devuelve404() {

        // Arrange
        SolicitudRequest request =
                crearRequestPrueba(999L, 3L);

        when(alumnoRepository.existsById(999L))
                .thenReturn(false);

        // Act
        ResponseEntity<SolicitudResponse> response =
                solicitudController.crearSolicitud(request);

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(alumnoRepository).existsById(999L);

        verifyNoInteractions(
                edicionRepository,
                solicitudRepository
        );
    }

    /**
     * Verifies that an application cannot be created
     * when the course edition does not exist.
     *
     * Expected result:
     * HTTP 404 and no application saved.
     */
    @Test
    void crearSolicitud_edicionInexistente_devuelve404() {

        // Arrange
        SolicitudRequest request =
                crearRequestPrueba(2L, 999L);

        when(alumnoRepository.existsById(2L))
                .thenReturn(true);

        when(edicionRepository.existsById(999L))
                .thenReturn(false);

        // Act
        ResponseEntity<SolicitudResponse> response =
                solicitudController.crearSolicitud(request);

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(alumnoRepository).existsById(2L);
        verify(edicionRepository).existsById(999L);

        verifyNoInteractions(solicitudRepository);
    }

    /**
     * Verifies that duplicate applications are rejected.
     *
     * Expected result:
     * HTTP 409 (Conflict) and no new application saved.
     */
    @Test
    void crearSolicitud_duplicada_devuelve409() {

        // Arrange
        Long alumnoId = 2L;
        Long edicionId = 3L;

        SolicitudRequest request =
                crearRequestPrueba(alumnoId, edicionId);

        when(alumnoRepository.existsById(alumnoId))
                .thenReturn(true);

        when(edicionRepository.existsById(edicionId))
                .thenReturn(true);

        when(solicitudRepository.existsByAlumnoIdAndEdicionId(
                alumnoId, edicionId))
                .thenReturn(true);

        // Act
        ResponseEntity<SolicitudResponse> response =
                solicitudController.crearSolicitud(request);

        // Assert
        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        verify(solicitudRepository, times(1))
                .existsByAlumnoIdAndEdicionId(alumnoId, edicionId);

        verify(solicitudRepository, never())
                .saveAndFlush(any(Solicitud.class));
    }

    // =========================================================
    // PUT - UPDATE APPLICATION
    // =========================================================

    /**
     * Verifies that an existing application can be updated.
     *
     * Expected result:
     * HTTP 200 with updated employment status and observations.
     *
     * The student, edition, date and application status
     * must remain unchanged.
     */
    @Test
    void actualizarSolicitud_existente_devuelve200() {

        // Arrange
        Long solicitudId = 1L;

        Solicitud solicitud =
                crearSolicitudPrueba(solicitudId, 2L, 3L);

        SolicitudRequest request =
                crearRequestPrueba(99L, 88L);

        request.setSituacionLaboral(
                SituacionLaboral.TRABAJADOR
        );

        request.setObservaciones(
                "Updated application"
        );

        when(solicitudRepository.findById(solicitudId))
                .thenReturn(Optional.of(solicitud));

        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // Act
        ResponseEntity<SolicitudResponse> response =
                solicitudController.actualizarSolicitud(
                        solicitudId, request
                );

        // Assert
        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        SolicitudResponse body = response.getBody();
        assertNotNull(body);

        assertEquals(
                SituacionLaboral.TRABAJADOR,
                body.getSituacionLaboral()
        );

        assertEquals(
                "Updated application",
                body.getObservaciones()
        );

        // Verify immutable fields.
        assertEquals(2L, body.getAlumnoId());
        assertEquals(3L, body.getEdicionId());

        assertEquals(
                LocalDate.of(2026, 10, 30),
                body.getFechaSolicitud()
        );

        assertEquals(
                EstadoSolicitud.PENDIENTE,
                body.getEstado()
        );

        verify(solicitudRepository, times(1))
                .save(solicitud);
    }

    /**
     * Verifies that updating a nonexistent application
     * returns HTTP 404.
     *
     * Expected result:
     * No application is saved.
     */
    @Test
    void actualizarSolicitud_inexistente_devuelve404() {

        // Arrange
        Long solicitudId = 999L;

        SolicitudRequest request =
                crearRequestPrueba(2L, 3L);

        when(solicitudRepository.findById(solicitudId))
                .thenReturn(Optional.empty());

        // Act
        ResponseEntity<SolicitudResponse> response =
                solicitudController.actualizarSolicitud(
                        solicitudId, request
                );

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(solicitudRepository).findById(solicitudId);

        verify(solicitudRepository, never())
                .save(any(Solicitud.class));
    }

    // =========================================================
    // DELETE - REMOVE APPLICATION
    // =========================================================

    /**
     * Verifies that an existing application
     * can be deleted successfully.
     *
     * Expected result:
     * HTTP 204 (No Content).
     */
    @Test
    void eliminarSolicitud_existente_devuelve204() {

        // Arrange
        Long solicitudId = 1L;

        when(solicitudRepository.existsById(solicitudId))
                .thenReturn(true);

        // Act
        ResponseEntity<Void> response =
                solicitudController.eliminarSolicitud(solicitudId);

        // Assert
        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        assertNull(response.getBody());

        verify(solicitudRepository, times(1))
                .deleteById(solicitudId);

        verify(solicitudRepository, times(1))
                .flush();
    }

    /**
     * Verifies that deleting a nonexistent application
     * returns HTTP 404 (Not Found).
     *
     * Expected result:
     * No deletion operation is performed.
     */
    @Test
    void eliminarSolicitud_inexistente_devuelve404() {

        // Arrange
        Long solicitudId = 999L;

        when(solicitudRepository.existsById(solicitudId))
                .thenReturn(false);

        // Act
        ResponseEntity<Void> response =
                solicitudController.eliminarSolicitud(solicitudId);

        // Assert
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        verify(solicitudRepository, times(1))
                .existsById(solicitudId);

        verify(solicitudRepository, never())
                .deleteById(anyLong());

        verify(solicitudRepository, never())
                .flush();
    }
}
