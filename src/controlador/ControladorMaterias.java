package controlador;

import modelo.Materia;
import modelo.ModeloSistemaAcademico;
import vista.PanelMateriasUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorMaterias implements ActionListener {

    private PanelMateriasUI vista;
    private ModeloSistemaAcademico modelo;

    // Variables para controlar la paginación de a 5 filas
    private int paginaActual = 1;
    private final int FILAS_POR_PAGINA = 10;

    public ControladorMaterias(PanelMateriasUI vista, ModeloSistemaAcademico modelo) {
        this.vista = vista;
        this.modelo = modelo;

        // Enlazamos este controlador para que escuche los botones de la vista
        this.vista.escucharComponentes(this);

        // Cargamos la tabla por primera vez al arrancar
        this.actualizarTabla();
    }

    // 3. LA CENTRAL DE EVENTOS
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Agregar Materia":
                vista.mostrarModoAlta();
                break;
            case "Cancelar":
                vista.mostrarModoLista();
                break;
            case "Guardar":
                this.guardarMateria();
                break;
            case "Eliminar Materia":
                this.eliminarMateria();
                break;
            case "< Anterior":
                this.paginaAnterior();
                break;
            case "Siguiente >":
                this.paginaSiguiente();
                break;
        }
    }

    // 4. MÉTODOS OPERATIVOS (El cerebro del controlador)

    private void guardarMateria() {
        // Extraemos la información de la pantalla usando los getters de tu vista
        String txtCodigo = vista.getTxtCodigo();
        String nombre = vista.getTxtNombre();
        Integer cuatri = vista.getCuatrimestreSeleccionado();

        // Validación 1: Campos vacíos
        if (txtCodigo.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int codigo;
        // Validación 2: Intentar parsear el código a Integer (Uso estricto de try-catch)
        try {
            codigo = Integer.parseInt(txtCodigo);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El código de la materia debe ser un número entero válido.", "Error de Tipo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validación 3: Número positivo
        if (codigo <= 0) {
            JOptionPane.showMessageDialog(vista, "El código debe ser un número mayor a cero.", "Error de Rango", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validación 4: Duplicados en el Backend
        // (Asumo que tu modelo tiene un método para verificar si ya existe esa clave primaria)
        if (existeCodigoMateria(codigo)) {
            JOptionPane.showMessageDialog(vista, "Ya existe una materia registrada con el código " + codigo, "Materia Duplicada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //  Se lo enviamos al modelo
        modelo.registrarMateria(codigo, nombre, cuatri);

        JOptionPane.showMessageDialog(vista, "Materia guardada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);

        // Volvemos al modo lista, limpiamos el formulario y refrescamos la JTable
        vista.mostrarModoLista();
        this.actualizarTabla();
    }

    public boolean existeCodigoMateria(Integer codigoBuscado) {
        // Recorremos todos los objetos Materia guardados en los valores del HashMap
        for (Materia m : modelo.getMapaMaterias().values()) {
            if (m.getCodigoMateria() == codigoBuscado) {
                return true; // Lo encontramos, el código ya está usado
            }
        }
        return false; // Terminó el bucle y nadie tenía ese código
    }


    private void eliminarMateria() {
        // 1. Capturamos la fila seleccionada de la tabla
        int filaSeleccionada = vista.getTablaMaterias().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione una materia de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Recuperamos el nombre de la materia (asumiendo que está en la columna 1)
        String nombreParaBorrar = (String) vista.getTablaMaterias().getValueAt(filaSeleccionada, 1);

        // 3. Pedimos confirmación al usuario antes de hacer el lío
        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Está seguro de que desea eliminar la materia '" + nombreParaBorrar + "' del sistema?\nEsto la quitará de todos los planes de estudio.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {

            //  Guardamos el booleano que devuelve el modelo
            boolean exito = modelo.eliminarMateriaDelSistema(nombreParaBorrar);

            if (exito) {
                // todo joya: avisamos y refrescamos la pantalla
                JOptionPane.showMessageDialog(
                        vista,
                        "La materia '" + nombreParaBorrar + "' fue eliminada correctamente del sistema.",
                        "Baja Exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

                //  método que limpia y vuelve a cargar la tabla en la vista
                this.actualizarTabla();

            } else {
                // SI DEVOLVIÓ FALSO: Saltaba el filtro del plan de estudios
                JOptionPane.showMessageDialog(
                        vista,
                        "No se puede eliminar la materia '" + nombreParaBorrar + "'.\n\n" +
                                "Motivo de seguridad: Al quitarla, uno o más Planes de Estudio quedarían\n" +
                                "con menos materias obligatorias que el mínimo requerido para que los alumnos se gradúen.",
                        "Operación Rechazada",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }

    // 5. LÓGICA DE PAGINACIÓN Y REFRESCO DE TABLA
    private void actualizarTabla() {
        DefaultTableModel dtm = vista.getModeloTabla();
        dtm.setRowCount(0); // Vaciamos la tabla por completo

        // Le pedimos al modelo TODAS las materias registradas y le pasamos los values() adentro del constructor de ArrayList para crear una lista real
        List<Materia> todasLasMaterias = new ArrayList<>(modelo.getMapaMaterias().values());

        // Ordenamos la lista por código de menor a mayor
        // Así el orden es predecible y la matemática de páginas no falla jamás.
        todasLasMaterias.sort((m1, m2) -> Integer.compare(m1.getCodigoMateria(), m2.getCodigoMateria()));

        int totalRegistros = todasLasMaterias.size();

        // Calculamos cuántas páginas máximas hay en base a la cantidad de elementos
        // Usamos Math.ceil para redondear hacia arriba (ej: 6 materias / 5 por pág = 2 páginas)
        int paginasMaximas = (int) Math.ceil((double) totalRegistros / FILAS_POR_PAGINA);
        if (paginasMaximas == 0) paginasMaximas = 1;

        // Si por eliminaciones nos quedamos en una página inexistente, recalculamos
        if (paginaActual > paginasMaximas) {
            paginaActual = paginasMaximas;
        }

        // Calculamos los índices de la lista global para saber qué pedacito renderizar
        int indiceInicio = (paginaActual - 1) * FILAS_POR_PAGINA;
        int indiceFin = Math.min(indiceInicio + FILAS_POR_PAGINA, totalRegistros);

        // Cargamos solo las filas correspondientes a la página actual
        for (int i = indiceInicio; i < indiceFin; i++) {
            Materia mat = todasLasMaterias.get(i);
            Object[] fila = { mat.getCodigoMateria(), mat.getNombre(), mat.getCuatrimestre() };
            dtm.addRow(fila);
        }

        // Actualizamos el cartelito "Página X de Y" en la UI
        vista.getLblPaginacion().setText("Página " + paginaActual + " de " + paginasMaximas);

        // Habilitamos o deshabilitamos los botones de navegación según corresponda
        vista.getBtnAnterior().setEnabled(paginaActual > 1);
        vista.getBtnSiguiente().setEnabled(paginaActual < paginasMaximas);
    }

    private void paginaAnterior() {
        if (paginaActual > 1) {
            paginaActual--;
            this.actualizarTabla();
        }
    }

    private void paginaSiguiente() {
        // Verificamos dinámicamente si hay más elementos adelante antes de avanzar
        int totalRegistros = modelo.getMapaMaterias().size();
        int paginasMaximas = (int) Math.ceil((double) totalRegistros / FILAS_POR_PAGINA);

        if (paginaActual < paginasMaximas) {
            paginaActual++;
            this.actualizarTabla();
        }
    }
}