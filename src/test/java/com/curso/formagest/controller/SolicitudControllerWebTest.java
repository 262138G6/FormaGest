
package com.curso.formagest.controller;

import com.curso.formagest.enums.EstadoSolicitud;
import com.curso.formagest.enums.SituacionLaboral;
import com.curso.formagest.model.Alumno;
import com.curso.formagest.model.Edicion;
import com.curso.formagest.model.Solicitud;
import com.curso.formagest.repository.AlumnoRepository;
import com.curso.formagest.repository.EdicionRepository;
import com.curso.formagest.repository.SolicitudRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HTTP-level tests for SolicitudController.
 *
 * Verifies request mappings, JSON serialization,
 * Bean Validation and HTTP response status codes.
 *
 * Uses standalone MockMvc and mocked repositories.
 * No Spring Boot application context or PostgreSQL
 * database connection is required.
 *
 * Test data is created before Mockito stubbing to
 * prevent nested stubbing errors.
 */
class SolicitudControllerWebTest {

    private SolicitudRepository solicitudRepository;
    private AlumnoRepository alumnoRepository;
    private EdicionRepository edicionRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LocalValidatorFactoryBean validator;

    /**
     * Initializes repository mocks and MVC test
     * infrastructure before each test.
     */
    @BeforeEach
    void setUp() {

        solicitudRepository = mock(SolicitudRepository.class);
        alumnoRepository = mock(AlumnoRepository.class);
        edicionRepository = mock(EdicionRepository.class);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        SolicitudController controller = new SolicitudController(
                solicitudRepository,
                alumnoRepository,
                edicionRepository
        );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    /**
     * Releases validation resources after each test.
     */
    @AfterEach
    void tearDown() {
        validator.close();
    }

    /**
     * Creates an application entity with valid
     * related identifiers and test data.
     *
     * @param id application identifier
     * @return configured application entity
     */
    private Solicitud crearSolicitud(Long id) {

        Alumno alumno = mock(Alumno.class);
        Edicion edicion = mock(Edicion.class);

        when(alumno.getId()).thenReturn(1L);
        when(edicion.getId()).thenReturn(2L);

        Solicitud solicitud = new Solicitud();

        solicitud.setId(id);
        solicitud.setAlumno(alumno);
        solicitud.setEdicion(edicion);
        solicitud.setFechaSolicitud(LocalDate.of(2026, 10, 30));
        solicitud.setSituacionLaboral(
                SituacionLaboral.DESEMPLEADO
        );
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setObservaciones("Application test");

        return solicitud;
    }

    /**
     * Returns valid JSON for creating or updating
     * a course application.
     *
     * @return JSON request body
     */
    private String crearRequestJson() {

        return """
                {
                  "alumnoId": 1,
                  "edicionId": 2,
                  "situacionLaboral": "DESEMPLEADO",
                  "observaciones": "Application test"
                }
                """;
    }

    // =========================================================
    // GET - LIST APPLICATIONS
    // =========================================================

    /**
     * Verifies that GET /api/solicitudes returns
     * HTTP 200 and a JSON array of applications.
     */
    @Test
    void listarSolicitudes_devuelve200YJson() throws Exception {

        // Arrange: create test data before configuring Mockito.
        Solicitud solicitud1 = crearSolicitud(10L);
        Solicitud solicitud2 = crearSolicitud(11L);

        when(solicitudRepository.findAll())
                .thenReturn(List.of(solicitud1, solicitud2));

        // Act and Assert
        mockMvc.perform(get("/api/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].alumnoId").value(1))
                .andExpect(jsonPath("$[0].edicionId").value(2))
                .andExpect(jsonPath("$[0].estado")
                        .value("PENDIENTE"));

        verify(solicitudRepository).findAll();
    }

    /**
     * Verifies that an empty application list
     * is returned as an empty JSON array.
     */
    @Test
    void listarSolicitudes_vacias_devuelve200()
            throws Exception {

        // Arrange
        when(solicitudRepository.findAll())
                .thenReturn(List.of());

        // Act and Assert
        mockMvc.perform(get("/api/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(solicitudRepository).findAll();
    }

    // =========================================================
    // GET - APPLICATION BY ID
    // =========================================================

    /**
     * Verifies that an existing application
     * is returned with HTTP 200.
     */
    @Test
    void obtenerSolicitud_existente_devuelve200()
            throws Exception {

        // Arrange: create the entity before repository stubbing.
        Solicitud solicitud = crearSolicitud(10L);

        when(solicitudRepository.findById(10L))
                .thenReturn(Optional.of(solicitud));

        // Act and Assert
        mockMvc.perform(get("/api/solicitudes/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.alumnoId").value(1))
                .andExpect(jsonPath("$.edicionId").value(2))
                .andExpect(jsonPath("$.situacionLaboral")
                        .value("DESEMPLEADO"))
                .andExpect(jsonPath("$.estado")
                        .value("PENDIENTE"));

        verify(solicitudRepository).findById(10L);
    }

    /**
     * Verifies that a nonexistent application
     * returns HTTP 404 (Not Found).
     */
    @Test
    void obtenerSolicitud_inexistente_devuelve404()
            throws Exception {

        // Arrange
        when(solicitudRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act and Assert
        mockMvc.perform(get("/api/solicitudes/999"))
                .andExpect(status().isNotFound());

        verify(solicitudRepository).findById(999L);
    }

    // =========================================================
    // GET - FILTER BY STUDENT
    // =========================================================

    /**
     * Verifies that applications can be retrieved
     * using the student identifier.
     */
    @Test
    void listarPorAlumno_devuelve200() throws Exception {

        // Arrange: create the entity before repository stubbing.
        Solicitud solicitud = crearSolicitud(10L);

        when(solicitudRepository.findByAlumnoId(1L))
                .thenReturn(List.of(solicitud));

        // Act and Assert
        mockMvc.perform(get("/api/solicitudes/alumno/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].alumnoId").value(1))
                .andExpect(jsonPath("$[0].id").value(10));

        verify(solicitudRepository).findByAlumnoId(1L);
    }

    // =========================================================
    // GET - FILTER BY COURSE EDITION
    // =========================================================

    /**
     * Verifies that applications can be retrieved
     * using the course edition identifier.
     */
    @Test
    void listarPorEdicion_devuelve200() throws Exception {

        // Arrange: create the entity before repository stubbing.
        Solicitud solicitud = crearSolicitud(10L);

        when(solicitudRepository.findByEdicionId(2L))
                .thenReturn(List.of(solicitud));

        // Act and Assert
        mockMvc.perform(get("/api/solicitudes/edicion/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].edicionId").value(2))
                .andExpect(jsonPath("$[0].id").value(10));

        verify(solicitudRepository).findByEdicionId(2L);
    }

    // =========================================================
    // POST - CREATE APPLICATION
    // =========================================================

    /**
     * Verifies that a valid application is created
     * with HTTP 201 and PENDIENTE status.
     */
    @Test
    void crearSolicitud_valida_devuelve201() throws Exception {

        // Arrange
        when(alumnoRepository.existsById(1L))
                .thenReturn(true);

        when(edicionRepository.existsById(2L))
                .thenReturn(true);

        when(solicitudRepository
                .existsByAlumnoIdAndEdicionId(1L, 2L))
                .thenReturn(false);

        Alumno alumno = mock(Alumno.class);
        Edicion edicion = mock(Edicion.class);

        when(alumno.getId()).thenReturn(1L);
        when(edicion.getId()).thenReturn(2L);

        when(alumnoRepository.getReferenceById(1L))
                .thenReturn(alumno);

        when(edicionRepository.getReferenceById(2L))
                .thenReturn(edicion);

        when(solicitudRepository.saveAndFlush(any(Solicitud.class)))
                .thenAnswer(invocation -> {

                    Solicitud saved = invocation.getArgument(0);

                    // Simulates a generated database identifier.
                    saved.setId(10L);

                    return saved;
                });

        // Act and Assert
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(crearRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.alumnoId").value(1))
                .andExpect(jsonPath("$.edicionId").value(2))
                .andExpect(jsonPath("$.estado")
                        .value("PENDIENTE"))
                .andExpect(jsonPath("$.situacionLaboral")
                        .value("DESEMPLEADO"));

        verify(solicitudRepository)
                .saveAndFlush(any(Solicitud.class));
    }

    /**
     * Verifies that creating an application for
     * a nonexistent student returns HTTP 404.
     */
    @Test
    void crearSolicitud_alumnoInexistente_devuelve404()
            throws Exception {

        // Arrange
        when(alumnoRepository.existsById(1L))
                .thenReturn(false);

        // Act and Assert
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(crearRequestJson()))
                .andExpect(status().isNotFound());

        verify(solicitudRepository, never())
                .saveAndFlush(any(Solicitud.class));
    }

    /**
     * Verifies that creating an application for
     * a nonexistent edition returns HTTP 404.
     */
    @Test
    void crearSolicitud_edicionInexistente_devuelve404()
            throws Exception {

        // Arrange
        when(alumnoRepository.existsById(1L))
                .thenReturn(true);

        when(edicionRepository.existsById(2L))
                .thenReturn(false);

        // Act and Assert
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(crearRequestJson()))
                .andExpect(status().isNotFound());

        verify(solicitudRepository, never())
                .saveAndFlush(any(Solicitud.class));
    }

    /**
     * Verifies that duplicate applications
     * return HTTP 409 (Conflict).
     */
    @Test
    void crearSolicitud_duplicada_devuelve409()
            throws Exception {

        // Arrange
        when(alumnoRepository.existsById(1L))
                .thenReturn(true);

        when(edicionRepository.existsById(2L))
                .thenReturn(true);

        when(solicitudRepository
                .existsByAlumnoIdAndEdicionId(1L, 2L))
                .thenReturn(true);

        // Act and Assert
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(crearRequestJson()))
                .andExpect(status().isConflict());

        verify(solicitudRepository, never())
                .saveAndFlush(any(Solicitud.class));
    }

    /**
     * Verifies that missing mandatory fields
     * cause HTTP 400 (Bad Request).
     */
    @Test
    void crearSolicitud_datosInvalidos_devuelve400()
            throws Exception {

        // Arrange
        String invalidJson = """
                {
                  "alumnoId": null,
                  "edicionId": 2,
                  "situacionLaboral": "DESEMPLEADO"
                }
                """;

        // Act and Assert
        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        // Validation must prevent repository access.
        verifyNoInteractions(
                solicitudRepository,
                alumnoRepository,
                edicionRepository
        );
    }

    // =========================================================
    // PUT - UPDATE APPLICATION
    // =========================================================

    /**
     * Verifies that an existing application
     * can be updated with HTTP 200.
     *
     * Only employment status and observations
     * should be modified.
     */
    @Test
    void actualizarSolicitud_existente_devuelve200()
            throws Exception {

        // Arrange
        Solicitud existente = crearSolicitud(10L);

        when(solicitudRepository.findById(10L))
                .thenReturn(Optional.of(existente));

        when(solicitudRepository.save(any(Solicitud.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        String updateJson = """
                {
                  "alumnoId": 1,
                  "edicionId": 2,
                  "situacionLaboral": "TRABAJADOR",
                  "observaciones": "Updated application"
                }
                """;

        // Act and Assert
        mockMvc.perform(put("/api/solicitudes/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.alumnoId").value(1))
                .andExpect(jsonPath("$.edicionId").value(2))
                .andExpect(jsonPath("$.situacionLaboral")
                        .value("TRABAJADOR"))
                .andExpect(jsonPath("$.observaciones")
                        .value("Updated application"))
                .andExpect(jsonPath("$.estado")
                        .value("PENDIENTE"));

        verify(solicitudRepository).save(existente);
    }

    /**
     * Verifies that updating a nonexistent
     * application returns HTTP 404.
     */
    @Test
    void actualizarSolicitud_inexistente_devuelve404()
            throws Exception {

        // Arrange
        when(solicitudRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act and Assert
        mockMvc.perform(put("/api/solicitudes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(crearRequestJson()))
                .andExpect(status().isNotFound());

        verify(solicitudRepository, never())
                .save(any(Solicitud.class));
    }

    // =========================================================
    // DELETE - REMOVE APPLICATION
    // =========================================================

    /**
     * Verifies that deleting an existing
     * application returns HTTP 204.
     */
    @Test
    void eliminarSolicitud_existente_devuelve204()
            throws Exception {

        // Arrange
        when(solicitudRepository.existsById(10L))
                .thenReturn(true);

        // Act and Assert
        mockMvc.perform(delete("/api/solicitudes/10"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(solicitudRepository).deleteById(10L);
        verify(solicitudRepository).flush();
    }

    /**
     * Verifies that deleting a nonexistent
     * application returns HTTP 404.
     */
    @Test
    void eliminarSolicitud_inexistente_devuelve404()
            throws Exception {

        // Arrange
        when(solicitudRepository.existsById(999L))
                .thenReturn(false);

        // Act and Assert
        mockMvc.perform(delete("/api/solicitudes/999"))
                .andExpect(status().isNotFound());

        verify(solicitudRepository, never())
                .deleteById(anyLong());

        verify(solicitudRepository, never()).flush();
    }
}
