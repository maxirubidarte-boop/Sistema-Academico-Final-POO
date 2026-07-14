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
    // Bandera de estado para el botón "Guardar"
    private boolean esAlta = true;

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
            case "Editar Materia":
                this.editarMateria();
                break;
        }
    }

    // 4. MÉTODOS OPERATIVOS (El cerebro del controlador)

    private void guardarMateria() {

        // 1. EXTRACCIÓN DE DATOS DESDE LA VISTA

        String txtCodigo = vista.getTxtCodigo();
        String nombre = vista.getTxtNombre();
        Integer cuatri = vista.getCuatrimestreSeleccionado();

        // 2. VALIDACIONES GENERALES

        // Validación A: Que no dejen campos vacíos
        if (txtCodigo.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Todos los campos son obligatorios.", "Error de Validación", JOptionPane.WARNING_MESSAGE);
            return; // Corta la ejecución si hay error
        }

        // Validación B: Intentar convertir el String del código a un Integer real
        int codigo;
        try {
            codigo = Integer.parseInt(txtCodigo);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El código de la materia debe ser un número entero válido.", "Error de Tipo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validación C: Que el número sea positivo y coherente
        if (codigo <= 0) {
            JOptionPane.showMessageDialog(vista, "El código debe ser un número mayor a cero.", "Error de Rango", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. BIFURCACIÓN DE LÓGICA DE NEGOCIO (El "if" de esAlta)

        if (esAlta) {

            //  CAMINO A: REGISTRAR MATERIA NUEVA

            // Control de código duplicado
            if (existeCodigoMateria(codigo)) {
                JOptionPane.showMessageDialog(vista, "Ya existe una materia registrada con el código " + codigo, "Materia Duplicada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Control de nombre duplicado
            if (modelo.getMapaMaterias().containsKey(nombre)) {
                JOptionPane.showMessageDialog(vista, "Ya existe una materia registrada con el nombre '" + nombre + "'", "Materia Duplicada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Si pasó todos los controles,la mandamos al modelo
            modelo.registrarMateria(codigo, nombre, cuatri);
            JOptionPane.showMessageDialog(vista, "Materia guardada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);

        } else {
            //  CAMINO B: MODIFICAR MATERIA EXISTENTE

            // Buscamos la materia original en el modelo usando su código (que no cambió)
            Materia materiaAEditar = null;
            for (Materia m : modelo.getMapaMaterias().values()) {
                if (m.getCodigoMateria() == codigo) {
                    materiaAEditar = m;
                    break;
                }
            }

            if (materiaAEditar != null) {
                String nombreViejo = materiaAEditar.getNombre();

                // ¿El usuario cambió el nombre de la materia?
                if (!nombreViejo.equals(nombre)) {
                    // Validamos que el nuevo nombre no lo tenga otra materia ya cargada
                    if (modelo.getMapaMaterias().containsKey(nombre)) {
                        JOptionPane.showMessageDialog(vista, "No se puede renombrar. Ya existe otra materia llamada '" + nombre + "'", "Nombre Duplicado", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Si todo está ok, removemos la clave vieja del HashMap para que no queden hilos sueltos
                    modelo.getMapaMaterias().remove(nombreViejo);
                }

                // Actualizamos los datos del objeto
                materiaAEditar.setNombre(nombre);
                materiaAEditar.setCuatrimestre(cuatri);

                // Volvemos a meter el objeto en el mapa con la clave (nueva o vieja)
                modelo.getMapaMaterias().put(nombre, materiaAEditar);

                JOptionPane.showMessageDialog(vista, "Materia modificada con éxito.", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error crítico: No se encontró la materia original.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        // 4. LIMPIEZA Y RESTABLECIMIENTO DE LA UI

        esAlta = true; // Reseteamos el semáforo para que la próxima por defecto sea un Alta
        vista.mostrarModoLista(); // Cerramos el formulario de la derecha y volvemos a mostrar botones
        this.actualizarTabla(); // Recargamos la tabla con los cambios frescos
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

    private void editarMateria(){

        // 1. Verificamos si hay una fila seleccionada en la JTable
        int filaEdicion = vista.getTablaMaterias().getSelectedRow();
        if (filaEdicion == -1){
            JOptionPane.showMessageDialog(vista, "Seleccione una materia de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Extraemos los datos de la fila seleccionada (según el orden de tus columnas en la vista)
        // Columnas: {"Código", "Nombre de Materia", "Cuatrimestre"}
        String codigoEdit = vista.getModeloTabla().getValueAt(filaEdicion, 0).toString();
        String nombreEdit = vista.getModeloTabla().getValueAt(filaEdicion, 1).toString();
        String cuatrimestreStr = vista.getModeloTabla().getValueAt(filaEdicion, 2).toString();

        // Parseamos el cuatrimestre (ej: "1º Cuatrimestre" -> nos quedamos con el número 1)
        int cuatrimestreEdit = Character.getNumericValue(cuatrimestreStr.charAt(0));

        // 3. Marcamos que NO es un alta (es una edición)
        esAlta = false;

        // 4. Mandamos los datos a la vista para que arme el formulario bloqueado
        vista.mostrarModoEdicion(codigoEdit, nombreEdit, cuatrimestreEdit);

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