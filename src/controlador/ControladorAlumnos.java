package controlador;

import modelo.ModeloSistemaAcademico;
import modelo.Alumno;
import vista.PanelAlumnosUI;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ControladorAlumnos implements ActionListener {

    private PanelAlumnosUI vista;
    private ModeloSistemaAcademico modelo;

    // Variables de Paginación
    private int paginaActual = 1;
    private final int TAMANO_PAGINA = 5; // Muestra de a 5 alumnos por página

    // Modo de formulario activo: true = Alta, false = Edición
    private boolean esAlta = true;

    public ControladorAlumnos(PanelAlumnosUI vista, ModeloSistemaAcademico modelo) {
        this.vista = vista;
        this.modelo = modelo;

        this.vista.escucharComponentes(this);
        refrescarTablaPaginada();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Agregar Alumno":
                esAlta = true;
                vista.mostrarModoAlta();
                break;

            case "Editar Seleccionado":
                int filaSeleccionada = vista.getTablaAlumnos().getSelectedRow();
                if (filaSeleccionada == -1) {
                    JOptionPane.showMessageDialog(vista, "Por favor, seleccione un alumno de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Extraemos los datos directamente de la fila seleccionada de la JTable
                String dni = vista.getModeloTabla().getValueAt(filaSeleccionada, 0).toString();
                String legajo = vista.getModeloTabla().getValueAt(filaSeleccionada, 1).toString();
                String nombre = vista.getModeloTabla().getValueAt(filaSeleccionada, 2).toString();

                esAlta = false; // Modo Edición listo
                vista.mostrarModoEdicion(dni, legajo, nombre);
                break;

            case "Eliminar":
                int filaEliminar = vista.getTablaAlumnos().getSelectedRow();
                if (filaEliminar == -1) {
                    JOptionPane.showMessageDialog(vista, "Seleccione qué alumno desea eliminar del sistema.", "Atención", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String dniEliminar = vista.getModeloTabla().getValueAt(filaEliminar, 0).toString();

                int seguro = JOptionPane.showConfirmDialog(vista, "¿Está seguro de eliminar permanentemente al alumno DNI " + dniEliminar + "?", "Confirmar baja", JOptionPane.YES_NO_OPTION);
                if (seguro == JOptionPane.YES_OPTION) {
                    modelo.eliminarAlumnoDelSistema(dniEliminar); // Método de tu modelo global
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
                int totalAlumnos = modelo.getAlumnosDelPadron().size();
                int totalPaginas = (int) Math.ceil((double) totalAlumnos / TAMANO_PAGINA);
                if (paginaActual < totalPaginas) {
                    paginaActual++;
                    refrescarTablaPaginada();
                }
                break;
        }
    }

    private void ejecutarGuardado() {
        String dni = vista.getTxtDni();
        String legajoStr = vista.getTxtLegajo();
        String nombre = vista.getTxtNombre();

        if (dni.isEmpty() || legajoStr.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int legajo = Integer.parseInt(legajoStr);

        if (esAlta) {
            // Lógica de ALTA: Creamos nueva instancia e ingresamos al sistema
            Alumno nuevoAlumno = new Alumno(nombre, legajo, dni);
            modelo.registrarAlumnoEnSistema(nuevoAlumno);
        } else {
            // Lógica de EDICIÓN: Buscamos el objeto existente en tu backend y mutamos sus campos
            Alumno alumnoExistente = modelo.getAlumno(dni);
            if (alumnoExistente != null) {
                alumnoExistente.editarDatos(nombre,legajo);
            }
        }

        refrescarTablaPaginada();
        vista.mostrarModoLista();
    }

    private void refrescarTablaPaginada() {
        // Obtenemos todos los elementos mapeados en tu backend
        List<Alumno> todosLosAlumnos = new ArrayList<>(modelo.getAlumnosDelPadron().values());
        int totalAlumnos = todosLosAlumnos.size();

        int totalPaginas = (int) Math.ceil((double) totalAlumnos / TAMANO_PAGINA);
        if (totalPaginas == 0) totalPaginas = 1;

        // Ajuste por si eliminamos el último elemento de una página
        if (paginaActual > totalPaginas) paginaActual = totalPaginas;

        // Limpiamos las filas actuales del render
        vista.getModeloTabla().setRowCount(0);

        // Calculamos los índices de corte para la página actual
        int indiceInicio = (paginaActual - 1) * TAMANO_PAGINA;
        int indiceFin = Math.min(indiceInicio + TAMANO_PAGINA, totalAlumnos);

        // Cargamos solo el segmento correspondiente a la página en la JTable
        for (int i = indiceInicio; i < indiceFin; i++) {
            Alumno al = todosLosAlumnos.get(i);
            String carreraStr = (al.getCarreraActual() != null) ? al.getCarreraActual().getNombre() : "Ninguna";

            Object[] fila = { al.getDni(), al.getLegajo(), al.getNombre(), carreraStr };
            vista.getModeloTabla().addRow(fila);
        }

        vista.actualizarEtiquetaPaginacion(paginaActual, totalPaginas);
    }
}