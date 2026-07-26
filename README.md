# Sistema de Gestión Académica - UNTDF

Este sistema permite la gestión de alumnos, carreras, materias y actas de examen, estructurado bajo el patrón arquitectónico **MVC (Modelo-Vista-Controlador)** y con soporte de persistencia local.

---

## 🚀 Instrucciones de Inicialización (Primer Uso)

Para poder evaluar el sistema con datos de prueba precargados (alumnos, carreras y materias), siga estrictamente estos pasos en su entorno de desarrollo:

### Paso 1: Precarga de Datos (Poblador)
1. Abra la clase de entrada principal del sistema (clase `Demo` o `Main`).
2. Localice la línea del método encargado de inicializar los datos de prueba y **descoméntela**:
   ```java
   // Descomentar únicamente para el primer inicio:
   popularDatosIniciales(ModeloSistemaAcademico.getInstancia());

Ejecute el programa. Verá la interfaz gráfica con datos cargados.

Cierre la ventana del sistema normalmente. Esto activará el GestorPersistencia para salvar el estado inicial en el disco.

Paso 2: Modo de Uso Normal
Regrese a la clase principal y vuelva a comentar el método poblador para evitar duplicaciones o sobreescrituras en los próximos inicios:

Java
// Comentar después del primer uso:
// popularDatosIniciales(ModeloSistemaAcademico.getInstancia());
Ejecute el sistema nuevamente. A partir de este momento, el programa leerá de forma automática los datos guardados en el archivo local a través del gestor de persistencia.

🛠️ Tecnologías Utilizadas
Lenguaje: Java 17 / 21

Interfaz Gráfica: Java Swing

Arquitectura: MVC (Model-View-Controller)

Persistencia: Serialización de objetos (Serializable)

