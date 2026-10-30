
package com.curso.formagest.controller;

import com.curso.formagest.model.Alumno;
import com.curso.formagest.repository.AlumnoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HTTP-level tests for AlumnoController.
 *
 * Verifies request mappings, JSON serialization,
 * request validation and HTTP response status codes.
 *
 * Uses standalone MockMvc and a mocked repository.
 * No Spring Boot application context or PostgreSQL
 * database connection is required.
 */
class AlumnoControllerWebTest {

    /**
     * Mocked repository used by the controller.
     */
    private AlumnoRepository alumnoRepository;

    /**
     * MVC test client used to simulate HTTP requests.
     */
    private MockMvc mockMvc;

    /**
     * JSON serializer used to build request bodies.
     */
    private ObjectMapper objectMapper;

    /**
     * Validator used to enforce Jakarta Bean Validation
     * constraints on incoming request bodies.
     */
    private LocalValidatorFactoryBean validator;

    /**
     * Initializes the mocked repository, controller,
     * validation provider and standalone MockMvc.
     */
    @BeforeEach
    void setUp() {

        alumnoRepository = mock(AlumnoRepository.class);

        objectMapper = new ObjectMapper();

        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        AlumnoController controller =
                new AlumnoController(alumnoRepository);

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
     * Creates a valid student entity for HTTP tests.
     *
     * @param id student identifier, or null for a new student
     * @return a student with valid test data
     */
    private Alumno crearAlumno(Long id) {

        Alumno alumno = new Alumno();

        alumno.setId(id);
        alumno.setNombre("Javier");
        alumno.setApellidos("Romero");
        alumno.setEmail("javier@example.com");
        alumno.setTelefono("600123456");

        return alumno;
    }

    // =========================================================
    // GET - LIST STUDENTS
    // =========================================================

    /**
     * Verifies that GET /api/alumnos returns HTTP 200
     * and a JSON array containing registered students.
     */
    @Test
    void listarAlumnos_devuelve200YJson() throws Exception {

        // Arrange
        when(alumnoRepository.findAll())
                .thenReturn(List.of(
                        crearAlumno(1L),
                        crearAlumno(2L)
                ));

        // Act and Assert
        mockMvc.perform(get("/api/alumnos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Javier"))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(alumnoRepository).findAll();
    }

    // =========================================================
    // GET - STUDENT BY ID
    // =========================================================

    /**
     * Verifies that retrieving an existing student
     * returns HTTP 200 and the correct JSON data.
     */
    @Test
    void obtenerAlumno_existente_devuelve200() throws Exception {

        // Arrange
        when(alumnoRepository.findById(1L))
                .thenReturn(Optional.of(crearAlumno(1L)));

        // Act and Assert
        mockMvc.perform(get("/api/alumnos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Javier"))
                .andExpect(jsonPath("$.apellidos").value("Romero"))
                .andExpect(jsonPath("$.email")
                        .value("javier@example.com"));

        verify(alumnoRepository).findById(1L);
    }

    /**
     * Verifies that requesting a nonexistent student
     * returns HTTP 404 (Not Found).
     */
    @Test
    void obtenerAlumno_inexistente_devuelve404() throws Exception {

        // Arrange
        when(alumnoRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act and Assert
        mockMvc.perform(get("/api/alumnos/999"))
                .andExpect(status().isNotFound());

        verify(alumnoRepository).findById(999L);
    }

    // =========================================================
    // POST - CREATE STUDENT
    // =========================================================

    /**
     * Verifies that POST /api/alumnos accepts valid JSON
     * and returns HTTP 201 with the created student.
     */
    @Test
    void crearAlumno_valido_devuelve201() throws Exception {

        // Arrange
        Alumno alumno = crearAlumno(null);

        when(alumnoRepository.save(any(Alumno.class)))
                .thenAnswer(invocation -> {

                    Alumno saved = invocation.getArgument(0);

                    // Simulates a generated database identifier.
                    saved.setId(1L);

                    return saved;
                });

        // Act and Assert
        mockMvc.perform(post("/api/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alumno)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Javier"))
                .andExpect(jsonPath("$.email")
                        .value("javier@example.com"));

        verify(alumnoRepository).save(any(Alumno.class));
    }

    /**
     * Verifies that POST rejects a missing required
     * student name with HTTP 400 (Bad Request).
     */
    @Test
    void crearAlumno_sinNombre_devuelve400() throws Exception {

        // Arrange
        Alumno alumno = crearAlumno(null);
        alumno.setNombre(null);

        // Act and Assert
        mockMvc.perform(post("/api/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alumno)))
                .andExpect(status().isBadRequest());

        verify(alumnoRepository, never())
                .save(any(Alumno.class));
    }

    /**
     * Verifies that POST rejects an invalid email
     * address with HTTP 400 (Bad Request).
     */
    @Test
    void crearAlumno_emailInvalido_devuelve400() throws Exception {

        // Arrange
        Alumno alumno = crearAlumno(null);
        alumno.setEmail("correo-invalido");

        // Act and Assert
        mockMvc.perform(post("/api/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(alumno)))
                .andExpect(status().isBadRequest());

        verify(alumnoRepository, never())
                .save(any(Alumno.class));
    }

    // =========================================================
    // PUT - UPDATE STUDENT
    // =========================================================

    /**
     * Verifies that PUT updates an existing student
     * and returns HTTP 200 with updated JSON.
     */
    @Test
    void actualizarAlumno_existente_devuelve200() throws Exception {

        // Arrange
        Alumno existente = crearAlumno(1L);

        Alumno datos = crearAlumno(null);
        datos.setNombre("Carlos");
        datos.setApellidos("Martinez");
        datos.setEmail("carlos@example.com");

        when(alumnoRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(alumnoRepository.save(any(Alumno.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        // Act and Assert
        mockMvc.perform(put("/api/alumnos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(datos)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andExpect(jsonPath("$.apellidos").value("Martinez"))
                .andExpect(jsonPath("$.email")
                        .value("carlos@example.com"));

        verify(alumnoRepository).save(existente);
    }

    /**
     * Verifies that updating a nonexistent student
     * returns HTTP 404 (Not Found).
     */
    @Test
    void actualizarAlumno_inexistente_devuelve404() throws Exception {

        // Arrange
        when(alumnoRepository.findById(999L))
                .thenReturn(Optional.empty());

        // Act and Assert
        mockMvc.perform(put("/api/alumnos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                crearAlumno(null))))
                .andExpect(status().isNotFound());

        verify(alumnoRepository, never())
                .save(any(Alumno.class));
    }

    // =========================================================
    // DELETE - REMOVE STUDENT
    // =========================================================

    /**
     * Verifies that deleting an existing student
     * returns HTTP 204 (No Content).
     */
    @Test
    void eliminarAlumno_existente_devuelve204() throws Exception {

        // Arrange
        when(alumnoRepository.existsById(1L))
                .thenReturn(true);

        // Act and Assert
        mockMvc.perform(delete("/api/alumnos/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(alumnoRepository).deleteById(1L);
        verify(alumnoRepository).flush();
    }

    /**
     * Verifies that deleting a nonexistent student
     * returns HTTP 404 (Not Found).
     */
    @Test
    void eliminarAlumno_inexistente_devuelve404() throws Exception {

        // Arrange
        when(alumnoRepository.existsById(999L))
                .thenReturn(false);

        // Act and Assert
        mockMvc.perform(delete("/api/alumnos/999"))
                .andExpect(status().isNotFound());

        verify(alumnoRepository, never())
                .deleteById(anyLong());

        verify(alumnoRepository, never()).flush();
    }

    /**
     * Verifies that deletion returns HTTP 409 (Conflict)
     * when related records prevent deleting a student.
     */
    @Test
    void eliminarAlumno_conRelaciones_devuelve409() throws Exception {

        // Arrange
        when(alumnoRepository.existsById(1L))
                .thenReturn(true);

        doThrow(new DataIntegrityViolationException(
                "Foreign key constraint violation"
        )).when(alumnoRepository).flush();

        // Act and Assert
        mockMvc.perform(delete("/api/alumnos/1"))
                .andExpect(status().isConflict());

        verify(alumnoRepository).deleteById(1L);
        verify(alumnoRepository).flush();
    }
}
