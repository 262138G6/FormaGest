
-- FormaGest
-- 02_datos_prueba.sql
-- Ejecutar después de 01_crear_esquema.sql.

BEGIN;

-- =========================================
-- CURSOS
-- =========================================
INSERT INTO curso
(id, codigo, nombre, descripcion, horas, activo)
VALUES
    (1, 'CUR001', 'Ofimatica avanzada',
     'Uso avanzado de herramientas ofimaticas', 40, TRUE),

    (2, 'CUR002', 'Excel profesional',
     'Hojas de calculo y analisis de datos', 30, TRUE),

    (3, 'CUR003', 'Desarrollo web',
     'Fundamentos de desarrollo web', 60, TRUE),

    (4, 'CUR004', 'Bases de datos',
     'Introduccion a las bases de datos relacionales', 45, TRUE),

    (5, 'CUR005', 'Competencias digitales',
     'Herramientas y competencias digitales', 25, FALSE);

-- =========================================
-- EDICIONES
-- =========================================
INSERT INTO edicion
(id, curso_id, fecha_inicio, fecha_fin, plazas, modalidad)
VALUES
    (1, 1, '2027-01-10', '2027-02-10', 15, 'PRESENCIAL'),
    (2, 2, '2027-02-15', '2027-03-15', 20, 'ONLINE'),
    (3, 3, '2027-04-01', '2027-05-15', 12, 'MIXTA'),
    (4, 4, '2027-05-20', '2027-06-20', 18, 'PRESENCIAL'),
    (5, 5, '2027-09-01', '2027-09-30', 25, 'ONLINE');

-- =========================================
-- ALUMNOS
-- =========================================
INSERT INTO alumno
(id, nombre, apellidos, email, telefono)
VALUES
    (1, 'Ana', 'Garcia Lopez',
     'ana.garcia@example.com', '600100001'),

    (2, 'Luis', 'Martinez Perez',
     'luis.martinez@example.com', '600100002'),

    (3, 'Marta', 'Sanchez Ruiz',
     'marta.sanchez@example.com', '600100003'),

    (4, 'Carlos', 'Fernandez Diaz',
     'carlos.fernandez@example.com', '600100004'),

    (5, 'Elena', 'Lopez Martin',
     'elena.lopez@example.com', NULL);

-- =========================================
-- SOLICITUDES
-- =========================================
INSERT INTO solicitud
(id, alumno_id, edicion_id, fecha_solicitud,
 situacion_laboral, estado, observaciones)
VALUES
    (1, 1, 1, '2026-10-20',
     'DESEMPLEADO', 'PENDIENTE', 'Primera solicitud'),

    (2, 2, 2, '2026-10-21',
     'TRABAJADOR', 'ADMITIDA', 'Documentacion revisada'),

    (3, 3, 3, '2026-10-22',
     'DESEMPLEADO', 'RESERVA', 'En lista de espera'),

    (4, 4, 4, '2026-10-23',
     'TRABAJADOR', 'RECHAZADA', 'No cumple los requisitos'),

    (5, 5, 5, '2026-10-24',
     'DESEMPLEADO', 'CANCELADA', NULL);

-- =========================================
-- SINCRONIZAR IDENTIDADES
-- Necesario porque hemos insertado IDs explícitos.
-- =========================================
SELECT setval(
               pg_get_serial_sequence('curso', 'id'),
               (SELECT MAX(id) FROM curso),
               TRUE
       );

SELECT setval(
               pg_get_serial_sequence('edicion', 'id'),
               (SELECT MAX(id) FROM edicion),
               TRUE
       );

SELECT setval(
               pg_get_serial_sequence('alumno', 'id'),
               (SELECT MAX(id) FROM alumno),
               TRUE
       );

SELECT setval(
               pg_get_serial_sequence('solicitud', 'id'),
               (SELECT MAX(id) FROM solicitud),
               TRUE
       );

COMMIT;
