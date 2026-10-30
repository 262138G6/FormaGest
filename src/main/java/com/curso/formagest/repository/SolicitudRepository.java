package com.curso.formagest.repository;

import com.curso.formagest.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for managing student applications.
 *
 * Provides standard CRUD operations and custom
 * queries for the Solicitud entity.
 */
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    /**
     * Finds all applications submitted by a student.
     *
     * @param alumnoId the student's identifier
     * @return applications submitted by the student
     */
    List<Solicitud> findByAlumnoId(Long alumnoId);

    /**
     * Finds all applications for a course edition.
     *
     * @param edicionId the edition's identifier
     * @return applications for the specified edition
     */
    List<Solicitud> findByEdicionId(Long edicionId);

    /**
     * Checks whether a student has already applied
     * for a specific course edition.
     *
     * @param alumnoId the student's identifier
     * @param edicionId the edition's identifier
     * @return true if the application already exists
     */
    boolean existsByAlumnoIdAndEdicionId(Long alumnoId, Long edicionId);
}
