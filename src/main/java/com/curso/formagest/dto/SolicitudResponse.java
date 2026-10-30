
package com.curso.formagest.dto;

import com.curso.formagest.enums.EstadoSolicitud;
import com.curso.formagest.enums.SituacionLaboral;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Data transfer object used to return
 * application information through the REST API.
 *
 * Avoids exposing JPA entities and their relationships.
 */
@Getter
@AllArgsConstructor
public class SolicitudResponse {

    /** Unique application identifier. */
    private Long id;

    /** Identifier of the student. */
    private Long alumnoId;

    /** Identifier of the requested course edition. */
    private Long edicionId;

    /** Date when the application was submitted. */
    private LocalDate fechaSolicitud;

    /** Employment status of the student. */
    private SituacionLaboral situacionLaboral;

    /** Current application status. */
    private EstadoSolicitud estado;

    /** Optional application observations. */
    private String observaciones;
}
