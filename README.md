# 🎓 FormaGest

### Sistema de Gestión Académica

**Plataforma web para la gestión de centros de formación**

**Grupo 6 · Curso 26/2138 · Geor y Javier**

**Estado:** 🟦 Planificación  ·  **Tecnologías previstas:** Java 21 · Maven · Spring Boot · PostgreSQL

> Proyecto académico de desarrollo web. Implementación progresiva durante la especialización de 100 horas.

---

## 📖 1. Presentación

**FormaGest** es una aplicación web concebida para centralizar y simplificar la gestión de una academia de formación. Su objetivo es organizar en un único sistema los cursos ofertados, sus ediciones, los alumnos y las matrículas.

El proyecto está inspirado en las necesidades de centros que imparten formación profesional y formación para el empleo, incluidos cursos vinculados a convocatorias de administraciones públicas como la Comunidad de Madrid. **La primera versión no pretende implementar los procedimientos administrativos oficiales** de dichas convocatorias.

Se desarrolla como práctica del **Grupo 6 del curso 26/2138**, de manera incremental y siguiendo las tecnologías estudiadas en la especialización de 100 horas.

> **Estado actual:** análisis y planificación. Las funcionalidades descritas son objetivos previstos, no prestaciones ya implementadas.

## 🎯 2. Objetivos

### Objetivo general

Desarrollar una aplicación web sencilla y mantenible que permita gestionar la oferta formativa y el proceso de inscripción de alumnos de una academia.

### Objetivos específicos

- Centralizar la información de cursos y alumnos.
- Programar diferentes ediciones de un mismo curso.
- Registrar, consultar y cancelar matrículas.
- Controlar las plazas disponibles y evitar matrículas duplicadas.
- Proporcionar una interfaz web comprensible para el personal de gestión.
- Exponer consultas básicas mediante una API REST propia.
- Aplicar progresivamente Maven, Spring Boot, JPA/Hibernate, Thymeleaf, Jenkins, Docker y Kubernetes.
- Practicar Git, GitHub, pruebas y documentación técnica en equipo.

## 🚀 3. Fase 1 — Producto mínimo viable (MVP)

La **primera entrega funcional** se centrará exclusivamente en cuatro módulos:

| Módulo | Funcionalidades previstas |
| --- | --- |
| **Cursos** | Crear, listar, consultar y modificar cursos; indicar si están activos; eliminar solo si no tienen ediciones asociadas. |
| **Ediciones** | Programar fechas, modalidad y número máximo de plazas para un curso. |
| **Alumnos** | Registrar, consultar y modificar datos básicos de contacto. |
| **Matrículas** | Inscribir alumnos, listar inscritos, cancelar matrículas y comprobar plazas disponibles. |

### Flujo principal de uso

```text
Crear curso
    ↓
Programar una edición
    ↓
Registrar un alumno
    ↓
Matricularlo en la edición
    ↓
Consultar inscritos y plazas disponibles
```

### Reglas básicas de negocio

1. Una edición siempre pertenece a un curso existente.
2. La fecha de finalización no puede ser anterior a la fecha de inicio.
3. Las plazas deben ser mayores que cero.
4. Un alumno no puede tener dos matrículas para la misma edición.
5. No se pueden crear nuevas matrículas activas si la edición no tiene plazas disponibles.
6. Las matrículas canceladas no ocupan plaza.

**Criterio de éxito del MVP:** poder completar todo el flujo anterior desde el navegador, almacenar los datos en PostgreSQL y consultar información básica mediante la API REST.

### Fuera del alcance de la primera versión

No se desarrollarán inicialmente autenticación y roles, profesores, aulas, horarios detallados, asistencia, evaluaciones, certificados, facturación, notificaciones ni integración con sistemas oficiales. Se reservan para futuras fases porque no son necesarios para validar el flujo básico de inscripción.

## 🗄️ 4. Modelo de datos inicial

El MVP utilizará **cuatro entidades**:

| Entidad | Datos principales | Relación |
| --- | --- | --- |
| **Curso** | ID, código, nombre, descripción, horas, activo | Un curso tiene muchas ediciones. |
| **Edición** | ID, curso, inicio, fin, plazas, modalidad | Una edición pertenece a un curso y tiene muchas matrículas. |
| **Alumno** | ID, nombre, apellidos, correo, teléfono | Un alumno puede tener muchas matrículas. |
| **Matrícula** | ID, alumno, edición, fecha, estado | Relaciona un alumno con una edición. |

### Relaciones entre entidades

```text
CURSO (1) ──────── (N) EDICION
                           │
                          (1)
                           │
                          (N)
                        MATRICULA
                          (N)
                           │
                          (1)
                        ALUMNO
```

**Interpretación:** un curso tiene varias ediciones; cada edición admite varias matrículas; y un alumno puede realizar varias matrículas. Cada matrícula corresponde a un solo alumno y una sola edición.


Se establecerá una restricción de unicidad para la pareja **alumno + edición**. Las plazas disponibles se calcularán restando las matrículas activas de la capacidad de la edición; no será necesario almacenar ese dato duplicado.

Para las pruebas se utilizarán **datos ficticios**.

## 🛠️ 5. Tecnologías y herramientas previstas

| Tecnología | Propósito |
| --- | --- |
| **Java 21** | Lenguaje de programación. |
| **Maven** | Dependencias, compilación y ejecución de pruebas. |
| **Spring / Spring Boot** | Estructura de la aplicación y lógica de negocio. |
| **Spring Data JPA / Hibernate** | Persistencia y relaciones entre entidades. |
| **PostgreSQL (Supabase)** | Base de datos relacional. |
| **Thymeleaf, HTML y CSS** | Interfaz web del personal de gestión. |
| **API REST** | Consulta de información mediante HTTP y JSON. |
| **JUnit** | Pruebas automatizadas. |
| **Jenkins** | Integración continua. |
| **Docker** | Ejecución de la aplicación en un contenedor. |
| **Kubernetes** | Despliegue de aprendizaje en un clúster local. |
| **IntelliJ IDEA** | Entorno de desarrollo. |
| **Git y GitHub** | Versionado y trabajo colaborativo. |

> Las herramientas se incorporarán conforme avance el temario; no todas estarán presentes desde el primer commit.

## 🧭 6. Plan de implementación técnica

El desarrollo seguirá **exactamente el orden del curso**:

| Etapa | Objetivo | Evidencia de finalización |
| --- | --- | --- |
| **1. Maven** | Crear y organizar el proyecto Java. | `mvn clean verify` termina correctamente y pasan las pruebas iniciales. |
| **2. Spring / Spring Boot** | Crear la aplicación y sus primeros controladores y servicios. | La aplicación arranca y responde a una petición HTTP con datos temporales. |
| **3. JPA / Hibernate** | Conectar PostgreSQL y persistir las cuatro entidades. | Los registros sobreviven al reinicio y se validan las reglas de matrícula. |
| **4. Thymeleaf y API REST** | Implementar formularios, listados y consultas JSON. | Se completa el flujo del MVP desde el navegador y se consulta mediante REST. |
| **5. Jenkins** | Automatizar compilación y pruebas. | El pipeline se ejecuta y detecta pruebas fallidas. |
| **6. Docker** | Empaquetar la aplicación. | La aplicación funciona desde un contenedor. |
| **7. Kubernetes** | Desplegar el contenedor en un entorno local. | Un Deployment y un Service permiten ejecutar y acceder a la aplicación. |

Cada etapa incluirá sus cambios en GitHub, pruebas y actualización de la documentación.

## 🔭 7. Posibles fases futuras

Estas ampliaciones **no forman parte del MVP** y se valorarán según el tiempo disponible y las necesidades detectadas.

| Fase futura | Funcionalidades | Necesidad que cubriría |
| --- | --- | --- |
| **2. Profesores y recursos** | Docentes, asignaciones, aulas y horarios. | Coordinar la impartición y evitar solapamientos. |
| **3. Seguimiento académico** | Asistencia, evaluaciones y certificados internos. | Conocer el progreso y la finalización de la formación. |
| **4. Gestión administrativa** | Convocatorias, documentación e informes. | Organizar los expedientes de acciones formativas. |
| **5. Portal de usuarios** | Autenticación, perfiles de alumnos y profesores. | Permitir el acceso seguro a información según el usuario. |
| **6. Automatización** | Notificaciones, listas de espera y exportaciones. | Reducir tareas manuales cuando el sistema crezca. |

La eventual gestión de formación subvencionada conforme a requisitos oficiales necesitará un análisis normativo y funcional específico antes de su implementación.

## 📁 8. Organización prevista del repositorio

La estructura crecerá progresivamente, sin crear componentes innecesarios antes de utilizarlos.

```text
formagest-academia/
├── README.md
├── pom.xml
├── .gitignore
├── src/
│   ├── main/
│   │   ├── java/com/formagest/
│   │   │   ├── FormaGestApplication.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   └── model/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── templates/
│   │       └── static/
│   └── test/java/
├── Jenkinsfile              # Etapa Jenkins
├── Dockerfile               # Etapa Docker
└── k8s/                     # Etapa Kubernetes
```

**Seguridad:** nunca se subirán contraseñas, claves de Supabase, archivos `.env` con secretos ni artefactos generados como `target/`. La conexión a PostgreSQL se configurará mediante variables de entorno.

## 👥 9. Equipo de desarrollo

**Grupo 6 — Curso 26/2138**

| Integrante | Participación |
| --- | --- |
| **Geor** | Análisis, diseño, desarrollo, pruebas y documentación. |
| **Javier** | Análisis, diseño, desarrollo, pruebas y documentación. |

El proyecto se realizará de forma colaborativa, utilizando Git y GitHub para registrar cambios y coordinar el trabajo.

## 📌 10. Estado del proyecto

**🟦 En planificación**

- [x] Definición de la idea y los objetivos.
- [x] Delimitación del MVP.
- [x] Propuesta del modelo de datos inicial.
- [x] Identificación de futuras ampliaciones.
- [ ] Creación del proyecto Maven.
- [ ] Implementación con Spring Boot.
- [ ] Persistencia con PostgreSQL.
- [ ] Interfaz web y API REST.
- [ ] Automatización con Jenkins.
- [ ] Contenerización con Docker.
- [ ] Despliegue local con Kubernetes.

---

**FormaGest · Sistema de Gestión Académica**  
*Grupo 6 · Curso 26/2138 · Geor y Javier*  
*Aprender construyendo una solución útil, paso a paso.*
