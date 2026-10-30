
package com.curso.formagest.controller;

import com.curso.formagest.dto.SolicitudRequest;
import com.curso.formagest.dto.SolicitudResponse;
import com.curso.formagest.enums.EstadoSolicitud;
import com.curso.formagest.model.Alumno;
import com.curso.formagest.model.Edicion;
import com.curso.formagest.model.Solicitud;
import com.curso.formagest.repository.AlumnoRepository;
import com.curso.formagest.repository.EdicionRepository;
import com.curso.formagest.repository.SolicitudRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing student applications in FormaGest.
 *
 * Provides endpoints to create, retrieve, update and delete
 * applications submitted by students for course editions.
 *
 * Uses DTOs to separate the REST API representation
 * from the underlying JPA entities.
 *
 * Base endpoint: /api/solicitudes
 */
@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    /**
     * Repository used to access application records.
     */
    private final SolicitudRepository solicitudRepository;

    /**
     * Repository used to access student records.
     */
    private final AlumnoRepository alumnoRepository;

    /**
     * Repository used to access course edition records.
     */
    private final EdicionRepository edicionRepository;

    /**
     * Retrieves all registered applications.
     *
     * HTTP method: GET
     * Endpoint: /api/solicitudes
     *
     * @return a list of application response DTOs
     */
    @GetMapping
    public List<SolicitudResponse> listarSolicitudes() {

        return solicitudRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    /**
     * Retrieves an application by its identifier.
     *
     * HTTP method: GET
     * Endpoint: /api/solicitudes/{id}
     *
     * @param id application identifier
     * @return HTTP 200 with the application if found,
     *         or HTTP 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponse> obtenerSolicitud(
            @PathVariable Long id) {

        return solicitudRepository.findById(id)
                .map(this::convertirAResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all applications submitted by a student.
     *
     * HTTP method: GET
     * Endpoint: /api/solicitudes/alumno/{alumnoId}
     *
     * @param alumnoId student identifier
     * @return applications submitted by the student
     */
    @GetMapping("/alumno/{alumnoId}")
    public List<SolicitudResponse> listarPorAlumno(
            @PathVariable Long alumnoId) {

        return solicitudRepository.findByAlumnoId(alumnoId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    /**
     * Retrieves all applications for a course edition.
     *
     * HTTP method: GET
     * Endpoint: /api/solicitudes/edicion/{edicionId}
     *
     * @param edicionId course edition identifier
     * @return applications for the specified edition
     */
    @GetMapping("/edicion/{edicionId}")
    public List<SolicitudResponse> listarPorEdicion(
            @PathVariable Long edicionId) {

        return solicitudRepository.findByEdicionId(edicionId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    /**
     * Creates a new application for a course edition.
     *
     * HTTP method: POST
     * Endpoint: /api/solicitudes
     *
     * Validates the request and checks that both the
     * student and the course edition exist.
     *
     * Prevents duplicate applications for the same edition.
     *
     * New applications are initialized with PENDIENTE status
     * and the current date.
     *
     * @param request validated application data
     * @return HTTP 201 with the created application,
     *         HTTP 404 if the student or edition does not exist,
     *         or HTTP 409 if the application already exists
     */
    @PostMapping
    public ResponseEntity<SolicitudResponse> crearSolicitud(
            @Valid @RequestBody SolicitudRequest request) {

        // Check whether the student exists.
        if (!alumnoRepository.existsById(request.getAlumnoId())) {
            return ResponseEntity.notFound().build();
        }

        // Check whether the course edition exists.
        if (!edicionRepository.existsById(request.getEdicionId())) {
            return ResponseEntity.notFound().build();
        }

        // Prevent duplicate applications.
        if (solicitudRepository.existsByAlumnoIdAndEdicionId(
                request.getAlumnoId(),
                request.getEdicionId())) {

            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // Obtain references to existing entities.
        Alumno alumno = alumnoRepository.getReferenceById(
                request.getAlumnoId());

        Edicion edicion = edicionRepository.getReferenceById(
                request.getEdicionId());

        // Create and initialize the application.
        Solicitud solicitud = new Solicitud();

        solicitud.setAlumno(alumno);
        solicitud.setEdicion(edicion);
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setSituacionLaboral(request.getSituacionLaboral());
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setObservaciones(request.getObservaciones());

        // Persist the application.
        Solicitud guardada = solicitudRepository.saveAndFlush(solicitud);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertirAResponse(guardada));
    }

    /**
     * Updates the employment status and observations
     * of an existing application.
     *
     * HTTP method: PUT
     * Endpoint: /api/solicitudes/{id}
     *
     * The student, course edition, application date
     * and application status cannot be modified.
     *
     * @param id application identifier
     * @param request updated application data
     * @return HTTP 200 with the updated application,
     *         or HTTP 404 if the application does not exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<SolicitudResponse> actualizarSolicitud(
            @PathVariable Long id,
            @Valid @RequestBody SolicitudRequest request) {

        return solicitudRepository.findById(id)
                .map(solicitud -> {

                    // Update only the permitted fields.
                    solicitud.setSituacionLaboral(
                            request.getSituacionLaboral());

                    solicitud.setObservaciones(
                            request.getObservaciones());

                    // Persist the updated application.
                    Solicitud actualizada =
                            solicitudRepository.save(solicitud);

                    return ResponseEntity.ok(
                            convertirAResponse(actualizada));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes an application by its identifier.
     *
     * HTTP method: DELETE
     * Endpoint: /api/solicitudes/{id}
     *
     * @param id application identifier
     * @return HTTP 204 if successfully deleted,
     *         or HTTP 404 if the application does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSolicitud(
            @PathVariable Long id) {

        // Check whether the application exists.
        if (!solicitudRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Delete the application.
        solicitudRepository.deleteById(id);
        solicitudRepository.flush();

        return ResponseEntity.noContent().build();
    }

    /**
     * Converts a JPA application entity into a response DTO.
     *
     * Exposes related entity identifiers instead of
     * serializing complete student and edition objects.
     *
     * @param solicitud application entity
     * @return application response DTO
     */
    private SolicitudResponse convertirAResponse(Solicitud solicitud) {

        return new SolicitudResponse(
                solicitud.getId(),
                solicitud.getAlumno().getId(),
                solicitud.getEdicion().getId(),
                solicitud.getFechaSolicitud(),
                solicitud.getSituacionLaboral(),
                solicitud.getEstado(),
                solicitud.getObservaciones()
        );
    }
}
