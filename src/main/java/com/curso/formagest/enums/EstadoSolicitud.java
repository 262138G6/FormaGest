
package com.curso.formagest.enums;

/**
 * Represents the possible states of a student's
 * application for a course edition.
 *
 * Values must match the PostgreSQL constraint
 * chk_solicitud_estado.
 */
public enum EstadoSolicitud {

    /** Application submitted and awaiting review. */
    PENDIENTE,

    /** Application placed on the waiting list. */
    RESERVA,

    /** Application accepted for the requested edition. */
    ADMITIDA,

    /** Application rejected during the review process. */
    RECHAZADA,

    /** Application cancelled by the student or administration. */
    CANCELADA
}
