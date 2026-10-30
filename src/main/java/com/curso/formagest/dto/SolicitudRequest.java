
package com.curso.formagest.dto;

import com.curso.formagest.enums.SituacionLaboral;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object used to create or update
 * a student's application for a course edition.
 *
 * Contains only the information provided by the client.
 */
@Getter
@Setter
@NoArgsConstructor
public class SolicitudRequest {

    /**
     * Identifier of the student submitting the application.
     */
    @NotNull(message = "El alumno es obligatorio")
    private Long alumnoId;

    /**
     * Identifier of the requested course edition.
     */
    @NotNull(message = "La edición es obligatoria")
    private Long edicionId;

    /**
     * Employment status of the student.
     */
    @NotNull(message = "La situación laboral es obligatoria")
    private SituacionLaboral situacionLaboral;

    /**
     * Optional observations about the application.
     */
    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;
}
