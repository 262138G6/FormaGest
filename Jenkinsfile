pipeline {

    // Cualquier agente disponible: en el aula, el propio Jenkins.
    agent any

    // tools le dice a Jenkins qué JDK y qué Maven debe poner en el PATH durante la construcción.
    // El servicio de Jenkins no ve el PATH del usuario que lo instaló, así que sin este bloque
    // el comando mvn no se encontraría.
    // Los nombres entre comillas son los de Manage Jenkins > Tools (S13-2) y deben
    // coincidir letra por letra con ellos; si no coinciden, Jenkins no podrá encontrar la herramienta.
    tools {
        jdk 'jdk-21'
        maven 'maven-3.9'
    }

    stages {

        stage('Compilar') {
            steps {
                // -B (batch mode) hace que Maven escriba una salida limpia, sin colores
                // ni barras de progreso, pensada para leerse en un registro y no en una terminal.
                // clean borra target/ antes de empezar: así no quedan informes de
                // pruebas de una construcción anterior que confundan a Jenkins.
                bat 'mvn -B clean compile'
            }
        }

        stage('Probar') {
            steps {
                // Inyectamos la contraseña esperada por tu application.properties
                withEnv(["POSTGRES_PASSWORD=1234"]) {}

                // mvn test compila las pruebas y las ejecuta con Surefire.
                // Si una prueba falla, Maven termina con error y esta etapa se pone en rojo.
                bat 'mvn -B test'
                }
            }
            // post define qué hacer cuando termina la etapa, según su resultado.
            post {
                // always se ejecuta siempre, también cuando las pruebas han fallado.
                // Sin él, el informe solo se publicaría con las construcciones en verde,
                // que es justo cuando menos falta hace.
                always {
                    // junit lee los informes XML de Surefire y los muestra en Jenkins.
                    // allowEmptyResults evita un segundo error si no llegó a generarse ningún informe
                    // (por ejemplo, si las pruebas ni siquiera compilan).
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Empaquetar') {
            steps {
                // Las pruebas ya se ejecutaron en la etapa anterior, así que aquí las saltamos
                // con -DskipTests para no hacerlas dos veces.
                // Maven genera target/contactos-0.0.1-SNAPSHOT.jar.
                bat 'mvn -B package -DskipTests'
                // archiveArtifacts guarda el JAR dentro de Jenkins, asociado a esta construcción,
                // para poder descargarlo después. El patrón target/*.jar no incluye el
                // fichero .jar.original, porque este termina en .original.
                archiveArtifacts artifacts: 'target/*.jar'
            }
        }
    }
}