
package com.curso.formagest.enums;

/**
 * Represents the employment status of a student
 * when submitting an application for a course edition.
 *
 * Values must match the PostgreSQL constraint
 * chk_solicitud_situacion.
 */
public enum SituacionLaboral {

    /** Student who is currently unemployed. */
    DESEMPLEADO,

    /** Student who is currently employed. */
    TRABAJADOR
}
