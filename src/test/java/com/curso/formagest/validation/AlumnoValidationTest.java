
package com.curso.formagest.validation;

import com.curso.formagest.model.Alumno;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bean Validation tests for the Alumno entity.
 *
 * Verifies the validation constraints defined
 * for student information in FormaGest.
 *
 * Uses Jakarta Bean Validation without starting
 * the Spring Boot application or accessing PostgreSQL.
 */
class AlumnoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    /**
     * Initializes the Bean Validation provider.
     */
    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    /**
     * Releases validation resources after all tests.
     */
    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    /**
     * Creates a student containing valid test data.
     *
     * @return a valid student entity
     */
    private Alumno crearAlumnoValido() {

        Alumno alumno = new Alumno();
        alumno.setNombre("Javier");
        alumno.setApellidos("Romero");
        alumno.setEmail("javier@example.com");
        alumno.setTelefono("600123456");

        return alumno;
    }

    /**
     * Checks whether a validation violation exists
     * for the specified property.
     *
     * @param violations validation results
     * @param property property name
     * @return true if the property has a violation
     */
    private boolean tieneError(
            Set<ConstraintViolation<Alumno>> violations,
            String property) {

        return violations.stream()
                .anyMatch(v ->
                        v.getPropertyPath().toString().equals(property));
    }

    /**
     * Verifies that valid student data passes validation.
     */
    @Test
    void alumnoValido_noTieneErrores() {

        Alumno alumno = crearAlumnoValido();

        Set<ConstraintViolation<Alumno>> violations =
                validator.validate(alumno);

        assertTrue(violations.isEmpty());
    }

    /**
     * Verifies that a missing first name is rejected.
     */
    @Test
    void nombreNulo_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setNombre(null);

        assertTrue(tieneError(
                validator.validate(alumno), "nombre"));
    }

    /**
     * Verifies that a blank first name is rejected.
     */
    @Test
    void nombreVacio_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setNombre("   ");

        assertTrue(tieneError(
                validator.validate(alumno), "nombre"));
    }

    /**
     * Verifies that a first name exceeding 80 characters
     * is rejected.
     */
    @Test
    void nombreDemasiadoLargo_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setNombre("A".repeat(81));

        assertTrue(tieneError(
                validator.validate(alumno), "nombre"));
    }

    /**
     * Verifies that missing surnames are rejected.
     */
    @Test
    void apellidosNulos_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setApellidos(null);

        assertTrue(tieneError(
                validator.validate(alumno), "apellidos"));
    }

    /**
     * Verifies that blank surnames are rejected.
     */
    @Test
    void apellidosVacios_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setApellidos("   ");

        assertTrue(tieneError(
                validator.validate(alumno), "apellidos"));
    }

    /**
     * Verifies that surnames exceeding 120 characters
     * are rejected.
     */
    @Test
    void apellidosDemasiadoLargos_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setApellidos("A".repeat(121));

        assertTrue(tieneError(
                validator.validate(alumno), "apellidos"));
    }

    /**
     * Verifies that a missing email address is rejected.
     */
    @Test
    void emailNulo_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setEmail(null);

        assertTrue(tieneError(
                validator.validate(alumno), "email"));
    }

    /**
     * Verifies that a blank email address is rejected.
     */
    @Test
    void emailVacio_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setEmail("   ");

        assertTrue(tieneError(
                validator.validate(alumno), "email"));
    }

    /**
     * Verifies that an incorrectly formatted email
     * address is rejected.
     */
    @Test
    void emailFormatoIncorrecto_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setEmail("correo-invalido");

        assertTrue(tieneError(
                validator.validate(alumno), "email"));
    }

    /**
     * Verifies that an email address exceeding
     * 150 characters is rejected.
     */
    @Test
    void emailDemasiadoLargo_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setEmail("a".repeat(145) + "@test.com");

        assertTrue(tieneError(
                validator.validate(alumno), "email"));
    }

    /**
     * Verifies that the telephone number is optional.
     */
    @Test
    void telefonoNulo_esValido() {

        Alumno alumno = crearAlumnoValido();
        alumno.setTelefono(null);

        assertTrue(validator.validate(alumno).isEmpty());
    }

    /**
     * Verifies that a telephone number exceeding
     * 20 characters is rejected.
     */
    @Test
    void telefonoDemasiadoLargo_produceError() {

        Alumno alumno = crearAlumnoValido();
        alumno.setTelefono("1".repeat(21));

        assertTrue(tieneError(
                validator.validate(alumno), "telefono"));
    }
}
