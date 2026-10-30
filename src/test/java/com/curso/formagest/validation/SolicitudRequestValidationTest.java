
package com.curso.formagest.validation;

import com.curso.formagest.dto.SolicitudRequest;
import com.curso.formagest.enums.SituacionLaboral;

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
 * Bean Validation tests for SolicitudRequest.
 *
 * Verifies the validation constraints applied to
 * incoming course application requests in FormaGest.
 *
 * Uses Jakarta Bean Validation without starting
 * Spring Boot or connecting to PostgreSQL.
 */
class SolicitudRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    /**
     * Initializes the Bean Validation provider
     * before executing the tests.
     */
    @BeforeAll
    static void setUp() {

        validatorFactory =
                Validation.buildDefaultValidatorFactory();

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
     * Creates a request containing valid application data.
     *
     * @return a valid application request
     */
    private SolicitudRequest crearSolicitudValida() {

        SolicitudRequest request = new SolicitudRequest();

        request.setAlumnoId(1L);
        request.setEdicionId(2L);
        request.setSituacionLaboral(
                SituacionLaboral.DESEMPLEADO
        );
        request.setObservaciones("Application test");

        return request;
    }

    /**
     * Checks whether the validation results contain
     * a violation for the specified property.
     *
     * @param violations validation results
     * @param property property name
     * @return true if the property has a violation
     */
    private boolean tieneError(
            Set<ConstraintViolation<SolicitudRequest>> violations,
            String property) {

        return violations.stream()
                .anyMatch(v ->
                        v.getPropertyPath()
                                .toString()
                                .equals(property));
    }

    // =========================================================
    // VALID REQUEST
    // =========================================================

    /**
     * Verifies that a correctly populated request
     * passes all validation constraints.
     */
    @Test
    void solicitudValida_noTieneErrores() {

        SolicitudRequest request = crearSolicitudValida();

        Set<ConstraintViolation<SolicitudRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    // =========================================================
    // STUDENT IDENTIFIER
    // =========================================================

    /**
     * Verifies that a missing student identifier
     * produces a validation error.
     */
    @Test
    void alumnoIdNulo_produceError() {

        SolicitudRequest request = crearSolicitudValida();
        request.setAlumnoId(null);

        assertTrue(tieneError(
                validator.validate(request),
                "alumnoId"
        ));
    }

    // =========================================================
    // COURSE EDITION IDENTIFIER
    // =========================================================

    /**
     * Verifies that a missing course edition identifier
     * produces a validation error.
     */
    @Test
    void edicionIdNulo_produceError() {

        SolicitudRequest request = crearSolicitudValida();
        request.setEdicionId(null);

        assertTrue(tieneError(
                validator.validate(request),
                "edicionId"
        ));
    }

    // =========================================================
    // EMPLOYMENT STATUS
    // =========================================================

    /**
     * Verifies that a missing employment status
     * produces a validation error.
     */
    @Test
    void situacionLaboralNula_produceError() {

        SolicitudRequest request = crearSolicitudValida();
        request.setSituacionLaboral(null);

        assertTrue(tieneError(
                validator.validate(request),
                "situacionLaboral"
        ));
    }

    /**
     * Verifies that the DESEMPLEADO employment status
     * is accepted.
     */
    @Test
    void situacionLaboralDesempleado_esValida() {

        SolicitudRequest request = crearSolicitudValida();

        request.setSituacionLaboral(
                SituacionLaboral.DESEMPLEADO
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    /**
     * Verifies that the TRABAJADOR employment status
     * is accepted.
     */
    @Test
    void situacionLaboralTrabajador_esValida() {

        SolicitudRequest request = crearSolicitudValida();

        request.setSituacionLaboral(
                SituacionLaboral.TRABAJADOR
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    // =========================================================
    // OBSERVATIONS
    // =========================================================

    /**
     * Verifies that observations are optional.
     */
    @Test
    void observacionesNulas_sonValidas() {

        SolicitudRequest request = crearSolicitudValida();
        request.setObservaciones(null);

        assertTrue(validator.validate(request).isEmpty());
    }

    /**
     * Verifies that observations containing exactly
     * 255 characters are accepted.
     */
    @Test
    void observaciones255Caracteres_sonValidas() {

        SolicitudRequest request = crearSolicitudValida();

        request.setObservaciones("A".repeat(255));

        assertTrue(validator.validate(request).isEmpty());
    }

    /**
     * Verifies that observations exceeding
     * 255 characters are rejected.
     */
    @Test
    void observaciones256Caracteres_producenError() {

        SolicitudRequest request = crearSolicitudValida();

        request.setObservaciones("A".repeat(256));

        assertTrue(tieneError(
                validator.validate(request),
                "observaciones"
        ));
    }
}
