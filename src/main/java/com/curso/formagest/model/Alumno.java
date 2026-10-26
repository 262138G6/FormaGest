package com.curso.formagest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Represents a student registered in FormaGest.
 * Maps to the "alumno" table in PostgreSQL.
 * Lombok generates getters, setters and a no-args constructor.
 */
@Entity
@Table(name = "alumno")
@Getter
@Setter
@NoArgsConstructor
public class Alumno {

    // Primary key generated automatically by PostgreSQL.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String apellidos;

    // Email must be unique for each student.
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Optional contact phone number.
    @Column(length = 20)
    private String telefono;
}