package controlador;

import modelo.ModeloSistemaAcademico;
import modelo.Alumno;
import modelo.PlanDeEstudio;
import vista.PanelAlumnosUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ControladorAlumnos implements ActionListener {

    private PanelAlumnosUI vista;
    private ModeloSistemaAcademico modelo;

    // Variables de control para la paginación
    private int paginaActual = 1;
    private final int FILAS_POR_PAGINA = 5;

    // Banderas de estado para el botón "Guardar"
    private boolean esAlta = true;
    private boolean esInscripcion = false; //  Controla si el guardado es para una carrera

    public ControladorAlumnos(PanelAlumnosUI vista, ModeloSistemaAcademico modelo) {
        this.vista = vista;
        this.modelo = modelo;

        // Ponemos al controlador a escuchar TODOS los componentes de la vista
        this.vista.escucharComponentes(this);

        // Renderizamos la tabla por primera vez
        refrescarTablaPaginada();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Agregar Alumno":
                esAlta = true;
                esInscripcion = false;
                vista.mostrarModoAlta();
                break;

            case "Editar Seleccionado":
                int filaEdicion = vista.getTablaAlumnos().getSelectedRow();
                if (filaEdicion == -1) {
                    JOptionPane.showMessageDialog(vista, "Seleccione un alumno de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String dniEdit = vista.getModeloTabla().getValueAt(filaEdicion, 0).toString();
                String legajoEdit = vista.getModeloTabla().getValueAt(filaEdicion, 1).toString();
                String nombreEdit = vista.getModeloTabla().getValueAt(filaEdicion, 2).toString();

                esAlta = false;
                esInscripcion = false;
                vista.mostrarModoEdicion(dniEdit, legajoEdit, nombreEdit);
                break;

            case "Verificar Egreso":
                int filaEgreso = vista.getTablaAlumnos().getSelectedRow();
                if (filaEgreso == -1) {
                    JOptionPane.showMessageDialog(vista, "Seleccione un alumno para verificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String dniEgreso = vista.getModeloTabla().getValueAt(filaEgreso, 0).toString();
                Alumno alumnoEgreso = modelo.getAlumno(dniEgreso);

                if (alumnoEgreso != null) {
                    boolean estaGraduado = false;

                    // 🛡️ CONTROL DE EGRESO SEGURO
                    // Si el alumno no tiene carrera asignada en el modelo, es imposible que esté graduado
                    if (alumnoEgreso.getCarreraActual() != null) {
                        // Le pedimos al modelo el plan global que armaste en tu simulación (ID: 123 para Sistemas)
                        // O evaluamos dinámicamente según la carrera que tenga
                        PlanDeEstudio planSistemas = modelo.getPlan(123);
                        PlanDeEstudio planTurismo = modelo.getPlan(456);

                        String nombreCarrera = alumnoEgreso.getCarreraActual().getNombre();

                        if (nombreCarrera.contains("Sistemas") && planSistemas != null) {
                            estaGraduado = planSistemas.estaGraduado(alumnoEgreso);
                        } else if (nombreCarrera.contains("Turismo") && planTurismo != null) {
                            estaGraduado = planTurismo.estaGraduado(alumnoEgreso);
                        }
                    }

                    String msj = "🎓 Control de Egreso — UNTDF\n\n";
                    msj += "Alumno: " + alumnoEgreso.getNombre() + "\n";
                    msj += "Carrera: " + (alumnoEgreso.getCarreraActual() != null ? alumnoEgreso.getCarreraActual().getNombre() : "No inscripto") + "\n";
                    msj += "¿Cumple condiciones de graduación?: " + (estaGraduado ? "SÍ 🎉 ¡Graduado!" : "NO 📚 (Cursada incompleta)");

                    JOptionPane.showMessageDialog(vista, msj, "Resultado de Auditoría", JOptionPane.INFORMATION_MESSAGE);
                }
                break;

            case "Inscribir a Carrera":
                int filaInsc = vista.getTablaAlumnos().getSelectedRow();
                if (filaInsc == -1) {
                    JOptionPane.showMessageDialog(vista, "Seleccione un alumno para inscribir.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 1. Sacamos los datos de la fila seleccionada
                String dniInsc = vista.getModeloTabla().getValueAt(filaInsc, 0).toString();
                String nombreInsc = vista.getModeloTabla().getValueAt(filaInsc, 2).toString();
                String carreraDeLaTabla = vista.getModeloTabla().getValueAt(filaInsc, 3).toString();

                // 🛡️ CONTROL DE RE-INSCRIPCIÓN
                // Si la columna 3 dice cualquier cosa que NO sea "No inscripto", significa que ya tiene carrera
                if (!carreraDeLaTabla.equals("No inscripto")) {
                    JOptionPane.showMessageDialog(vista,
                            "El alumno " + nombreInsc + " ya está matriculado en:\n" + carreraDeLaTabla +
                                    "\n\nPara cambiarlo de carrera, primero debe procesarse la baja académica.",
                            "Inscripción Duplicada",
                            JOptionPane.WARNING_MESSAGE);
                    return; // 🛑 Frena acá, no muestra el formulario de inscripción
                }

                // Si está libre, avanzamos al formulario normalmente
                esAlta = false;
                esInscripcion = true;

                String[] carrerasDisponibles = {"Licenciatura en Sistemas", "Licenciatura en Turismo"};
                vista.mostrarModoInscripcionCarrera(dniInsc, nombreInsc, carrerasDisponibles);
                break;

            case "Eliminar":
                int filaEliminar = vista.getTablaAlumnos().getSelectedRow();
                if (filaEliminar == -1) {
                    JOptionPane.showMessageDialog(vista, "Seleccione qué alumno desea dar de baja.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String dniEliminar = vista.getModeloTabla().getValueAt(filaEliminar, 0).toString();
                int seguro = JOptionPane.showConfirmDialog(vista, "¿Está seguro de dar de baja al DNI " + dniEliminar + "?", "Confirmar baja", JOptionPane.YES_NO_OPTION);

                if (seguro == JOptionPane.YES_OPTION) {
                    modelo.eliminarAlumnoDelSistema(dniEliminar); // Usa tu método real
                    refrescarTablaPaginada();
                }
                break;

            case "Guardar":
                ejecutarGuardado();
                break;

            case "Cancelar":
                vista.mostrarModoLista();
                break;

            case "< Anterior":
                if (paginaActual > 1) {
                    paginaActual--;
                    refrescarTablaPaginada();
                }
                break;

            case "Siguiente >":
                // Calcula el total de alumnos en caliente
                int totalAlumnos = modelo.getAlumnosDelPadron().size();
                int maxPaginas = (int) Math.ceil((double) totalAlumnos / FILAS_POR_PAGINA);
                if (paginaActual < maxPaginas) {
                    paginaActual++;
                    refrescarTablaPaginada();
                }
                break;
        }
    }

    private void ejecutarGuardado() {
        // Si estamos inscribiendo a una carrera, la lógica es totalmente distinta
        if (esInscripcion) {
            String dni = vista.getTxtDni();
            String carreraSeleccionada = vista.getComboCarreras().getSelectedItem().toString();

            // Ejecuta tu lógica real del backend
            modelo.inscribirAlumnoACarrera(dni, carreraSeleccionada);

            JOptionPane.showMessageDialog(vista, "¡Inscripción registrada con éxito en " + carreraSeleccionada + "!", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            esInscripcion = false; // Reseteamos la bandera
            refrescarTablaPaginada();
            vista.mostrarModoLista();
            return; // Cortamos el flujo acá
        }

        // --- FLUJO NORMAL: ALTA Y EDICIÓN ---
        String dni = vista.getTxtDni();
        String legajoStr = vista.getTxtLegajo();
        String nombre = vista.getTxtNombre();

        // 1. Validar campos vacíos
        if (dni.isEmpty() || legajoStr.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validar que el legajo sea numérico
        int legajo;
        try {
            legajo = Integer.parseInt(legajoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El Legajo debe ser un número entero válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //  VALIDACIÓN 2.1: Longitud y coherencia del Legajo (Mínimo 4 dígitos)
        if (legajoStr.length() < 4 || legajo <= 0) {
            JOptionPane.showMessageDialog(vista,
                    "El número de Legajo no es válido.\nDebe tener al menos 4 dígitos (Ej: 4001).",
                    "Error en Legajo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        //  VALIDACIÓN 2.2: Formato y longitud del DNI (Solo números, entre 7 y 8 dígitos)

        if (!dni.matches("^[0-9]{7,8}$")) {
            JOptionPane.showMessageDialog(vista,
                    "El DNI debe contener solo números y tener entre 7 y 8 dígitos (sin puntos ni espacios).",
                    "Error en DNI",
                    JOptionPane.ERROR_MESSAGE);
            return; //  Frena el guardado
        }

        //  VALIDACIÓN 2.5: Que el nombre contenga SOLO letras y espacios
        // Usamos una expresión regular: ^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$
        // Esto significa: desde el inicio (^) hasta el final ($), solo letras con tildes, eñes y espacios.
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            JOptionPane.showMessageDialog(vista,
                    "El nombre completo solo puede contener letras y espacios.\nNo se permiten números ni caracteres especiales.",
                    "Error en Nombre",
                    JOptionPane.ERROR_MESSAGE);
            return; //  Frena el guardado
        }

        // 3. Controles de duplicados (solo en Altas)
        if (esAlta) {
            if (modelo.getAlumno(dni) != null) {
                JOptionPane.showMessageDialog(vista, "El DNI " + dni + " ya existe en el sistema.", "Documento Duplicado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (Alumno existente : modelo.getAlumnosDelPadron().values()) {
                if (existente.getLegajo() == legajo) {
                    JOptionPane.showMessageDialog(vista, "El Legajo " + legajo + " ya pertenece a: " + existente.getNombre(), "Legajo Duplicado", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        // 4. Impactar backend
        if (esAlta) {
            Alumno nuevoAlumno = new Alumno(nombre, legajo, dni);
            modelo.registrarAlumnoEnSistema(nuevoAlumno);
            JOptionPane.showMessageDialog(vista, "¡Alumno incorporado con éxito al padrón!", "Alta Exitosa", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Alumno alumnoExistente = modelo.getAlumno(dni);
            if (alumnoExistente != null) {
                alumnoExistente.editarDatos(nombre, legajo); // Tu método encapsulado pro
                JOptionPane.showMessageDialog(vista, "Datos modificados correctamente.", "Edición Exitosa", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        refrescarTablaPaginada();
        vista.mostrarModoLista();
    }

    private void refrescarTablaPaginada() {
        vista.getModeloTabla().setRowCount(0); // Limpia la tabla por completo

        List<Alumno> listaCompleta = new ArrayList<>(modelo.getAlumnosDelPadron().values());
        int totalAlumnos = listaCompleta.size();

        int totalPaginas = (int) Math.ceil((double) totalAlumnos / FILAS_POR_PAGINA);
        if (totalPaginas == 0) totalPaginas = 1;

        int indiceInicio = (paginaActual - 1) * FILAS_POR_PAGINA;
        int indiceFin = Math.min(indiceInicio + FILAS_POR_PAGINA, totalAlumnos);

        // Cargamos las filas correspondientes en la JTable
        for (int i = indiceInicio; i < indiceFin; i++) {
            Alumno al = listaCompleta.get(i);

            // 💡 TU IDEA: Le preguntamos al objeto Alumno si tiene carrera asignada
            String carrera = "No inscripto";

            // (Asumo que tu clase Alumno tiene algo como getCarrera() o getCarreraInscripto())
            // Si devuelve un objeto Carrera, le sacamos el nombre, sino queda "No inscripto"
            if (al.getCarreraActual() != null) {
                carrera = al.getCarreraActual().getNombre(); // O como se llame el método en tu modelo
            }

            Object[] fila = { al.getDni(), al.getLegajo(), al.getNombre(), carrera };
            vista.getModeloTabla().addRow(fila);
        }

        vista.actualizarEtiquetaPaginacion(paginaActual, totalPaginas);
    }
}