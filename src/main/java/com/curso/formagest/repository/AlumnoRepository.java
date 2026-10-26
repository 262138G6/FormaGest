package com.curso.formagest.repository;

import com.curso.formagest.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database access for Alumno entities.
 * Inherits standard CRUD operations from JpaRepository.
 *
 * Entity: Alumno
 * Primary key: Long
 */
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
}