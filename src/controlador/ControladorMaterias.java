package controlador;

import modelo.Materia;
import modelo.ModeloSistemaAcademico;
import modelo.Prerrequisito;
import vista.PanelMateriasUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorMaterias implements ActionListener {

    private PanelMateriasUI vista;
    private ModeloSistemaAcademico modelo;
    // Bandera de estado para el botón "Guardar"
    private boolean esAlta = true;

    // VARIABLES DE ESTADO PARA GESTIONAR CORRELATIVAS
    private boolean enModoCorrelativas = false;
    private Materia materiaSeleccionadaCorrelativas = null;
    private boolean enModoSeleccionCorrelativa = false;

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
            case "Correlativas":
                this.correlativas();
                break;
            case "Volver":
                this.procesarVolver();
                break;
            case "Agregar Correlativa":
                this.agregarCorrelativa();
                break;
            case "Eliminar Correlativa":
                this.eliminarCorrelativa();
                break;
            case "Confirmar Selección":
                this.confirmarSeleccionCorrelativa();
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

    public void correlativas(){
        int filaSeleccionada = vista.getTablaMaterias().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Por favor, seleccione una materia de la tabla para gestionar sus correlativas.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreMateria = (String) vista.getTablaMaterias().getValueAt(filaSeleccionada, 1);

        // Buscamos el objeto real en el mapa del modelo
        materiaSeleccionadaCorrelativas = modelo.getMapaMaterias().get(nombreMateria);

        if (materiaSeleccionadaCorrelativas != null) {
            enModoCorrelativas = true;
            paginaActual = 1; // Reseteamos el paginado al ingresar

            // Transicionamos la interfaz visual
            vista.mostrarModoGestionCorrelativas(nombreMateria);

            // Forzamos el refresco para mostrar únicamente las correlativas de esta materia
            this.actualizarTabla();
        } else {
            JOptionPane.showMessageDialog(vista, "Error: No se pudo encontrar los datos de la materia en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarCorrelativa(){
        int filaSeleccionada = vista.getTablaMaterias().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione la correlativa que desea remover de la lista.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreCorrelativa = (String) vista.getTablaMaterias().getValueAt(filaSeleccionada, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
                vista,
                "¿Está seguro de quitar a '" + nombreCorrelativa + "' como correlativa de '" + materiaSeleccionadaCorrelativas.getNombre() + "'?",
                "Confirmar Desvinculación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            // Buscamos el objeto de la correlativa para removerlo de la lista interna de la materia
            Prerrequisito correlativaARemover = null;


            for (Prerrequisito m : materiaSeleccionadaCorrelativas.getCorrelativas()) {
                if (m.getMateriaRequerida().getNombre().equals(nombreCorrelativa)) {
                    correlativaARemover = m;
                    break;
                }
            }

            if (correlativaARemover != null) {
                materiaSeleccionadaCorrelativas.getCorrelativas().remove(correlativaARemover);
                JOptionPane.showMessageDialog(vista, "Correlativa eliminada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                this.actualizarTabla();
            }
        }

    }

    public void agregarCorrelativa() {
        // 1. Verificamos si realmente hay materias que se puedan agregar
        boolean hayDisponibles = false;
        for (Materia m : modelo.getMapaMaterias().values()) {
            if (m.getCodigoMateria().equals(materiaSeleccionadaCorrelativas.getCodigoMateria())) {
                continue;
            }
            boolean yaEsCorrelativa = false;
            for (Prerrequisito p : materiaSeleccionadaCorrelativas.getCorrelativas()) {
                if (p.getMateriaRequerida().getCodigoMateria().equals(m.getCodigoMateria())) {
                    yaEsCorrelativa = true;
                    break;
                }
            }
            if (!yaEsCorrelativa) {
                hayDisponibles = true;
                break;
            }
        }

        if (!hayDisponibles) {
            JOptionPane.showMessageDialog(vista, "No hay más materias disponibles en el sistema para asignar como correlativas.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 2. Activamos el modo selección
        enModoSeleccionCorrelativa = true;

        // 3. Le avisamos a la vista que cambie los botones (ej: "Agregar" pasa a "Confirmar Selección")
        vista.mostrarModoSeleccionCorrelativa(materiaSeleccionadaCorrelativas.getNombre());

        // 4. Refrescamos la tabla para que dibuje los candidatos
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

    public void confirmarSeleccionCorrelativa() {
        int filaSeleccionada = vista.getTablaMaterias().getSelectedRow();

        // 1. Validamos que realmente haya hecho clic en alguna fila de la lista de disponibles
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Por favor, seleccione la materia que desea asignar de la tabla.",
                    "Atención",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // 2. Obtenemos el nombre de la materia elegida
        String nombreMateriaRequerida = (String) vista.getTablaMaterias().getValueAt(filaSeleccionada, 1);

        // 3. Buscamos el objeto Materia real en nuestro modelo
        Materia materiaRequerida = modelo.getMapaMaterias().get(nombreMateriaRequerida);

        if (materiaRequerida != null) {
            // 4. Le preguntamos el tipo de condición (REGULAR o APROBADA) usando los enums de Prerrequisito
            Prerrequisito.TipoPrerrequisito[] opciones = Prerrequisito.TipoPrerrequisito.values();
            Prerrequisito.TipoPrerrequisito tipoElegido = (Prerrequisito.TipoPrerrequisito) JOptionPane.showInputDialog(
                    vista,
                    "Seleccione la condición requerida para '" + materiaRequerida.getNombre() + "':",
                    "Condición de Correlativa",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]
            );

            // 5. Si no canceló el cartelito, creamos la correlativa y la guardamos
            if (tipoElegido != null) {
                Prerrequisito nuevoPrerrequisito = new Prerrequisito(materiaRequerida, tipoElegido);
                materiaSeleccionadaCorrelativas.addCorrelativa(nuevoPrerrequisito);

                JOptionPane.showMessageDialog(
                        vista,
                        "¡Se asignó '" + materiaRequerida.getNombre() + "' como correlativa con éxito!",
                        "Operación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // 6. Desactivamos el modo selección, volvemos al panel de gestión y redibujamos la tabla
                enModoSeleccionCorrelativa = false;
                vista.mostrarModoGestionCorrelativas(materiaSeleccionadaCorrelativas.getNombre());
                this.actualizarTabla();
            }
        } else {
            JOptionPane.showMessageDialog(
                    vista,
                    "Error crítico: No se pudo recuperar la materia seleccionada desde el sistema.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void procesarVolver(){
        // Si estoy seleccionando una materia para correlacionar (Estado 3)
        if (enModoSeleccionCorrelativa) {
            enModoSeleccionCorrelativa = false;
            // Volvemos al Estado 2 (Ver Correlativas de la materia que estábamos editando)
            vista.mostrarModoGestionCorrelativas(materiaSeleccionadaCorrelativas.getNombre());
            this.actualizarTabla();
            return; // Cortamos acá para no seguir retrocediendo al menú principal
        }

        // Si estoy en la gestión de correlativas (Estado 2)
        if (enModoCorrelativas) {
            enModoCorrelativas = false;
            materiaSeleccionadaCorrelativas = null;
            // Volvemos al Estado 1 (Menú Principal)
            vista.mostrarModoLista(); // <- Acuérdate de agregarle a este método el "btnEliminar.setVisible(true)" que vimos antes
            this.actualizarTabla();
        }
    }

    // 5. LÓGICA DE PAGINACIÓN Y REFRESCO DE TABLA
    private void actualizarTabla() {
        DefaultTableModel dtm = vista.getModeloTabla();
        dtm.setRowCount(0); // Vaciamos la tabla por completo

        // =====================================================================
        // CASO A: ESTAMOS EN MODO SELECCIÓN DE NUEVA CORRELATIVA
        // =====================================================================
        if (enModoCorrelativas && enModoSeleccionCorrelativa && materiaSeleccionadaCorrelativas != null) {

            // 1. Aseguramos las cabeceras para la selección
            dtm.setColumnIdentifiers(new String[]{"Código", "Nombre de Materia", "Cuatrimestre"});

            for (Materia m : modelo.getMapaMaterias().values()) {
                // Filtro 1: No auto-correlacionarse
                if (m.getCodigoMateria().equals(materiaSeleccionadaCorrelativas.getCodigoMateria())) {
                    continue;
                }

                // Filtro 2: Que no sea una correlativa ya existente
                boolean yaEsCorrelativa = false;
                for (Prerrequisito p : materiaSeleccionadaCorrelativas.getCorrelativas()) {
                    if (p.getMateriaRequerida().getCodigoMateria().equals(m.getCodigoMateria())) {
                        yaEsCorrelativa = true;
                        break;
                    }
                }

                // Si pasa los filtros, la mostramos en la tabla para que el usuario la cliquee
                if (!yaEsCorrelativa) {
                    Object[] fila = {
                            m.getCodigoMateria(),
                            m.getNombre(),
                            m.getCuatrimestre() + "º Cuatrimestre"
                    };
                    dtm.addRow(fila);
                }
            }

            vista.getLblPaginacion().setText("Seleccione una materia de la lista para correlacionar");
            vista.getBtnAnterior().setEnabled(false);
            vista.getBtnSiguiente().setEnabled(false);
            return; // Cortamos acá
        }

        // =====================================================================
        // CASO B: ESTAMOS EN MODO GESTIÓN DE CORRELATIVAS
        // =====================================================================
        if (enModoCorrelativas && materiaSeleccionadaCorrelativas != null) {

            // 2.  Cambiamos la cabecera para que la tercera columna diga "Condición Requerida"
            dtm.setColumnIdentifiers(new String[]{"Código", "Nombre de Materia", "Condición Requerida"});

            for (Prerrequisito p : materiaSeleccionadaCorrelativas.getCorrelativas()) {
                Materia mat = p.getMateriaRequerida();
                Object[] fila = {
                        mat.getCodigoMateria(),
                        mat.getNombre(),
                        p.getTipo().toString()
                };
                dtm.addRow(fila);
            }
            vista.getLblPaginacion().setText("Mostrando correlativas de: " + materiaSeleccionadaCorrelativas.getNombre());
            vista.getBtnAnterior().setEnabled(false);
            vista.getBtnSiguiente().setEnabled(false);
            return;
        }

        // =====================================================================
        // CASO C: MODO NORMAL (LISTADO GENERAL DE MATERIAS)
        // =====================================================================

        // 3.  Restauramos las cabeceras originales de la lista general
        dtm.setColumnIdentifiers(new String[]{"Código", "Nombre de Materia", "Cuatrimestre"});

        List<Materia> todasLasMaterias = new ArrayList<>(modelo.getMapaMaterias().values());

        // Ordenamos la lista por código de menor a mayor para que el paginado sea predecible
        todasLasMaterias.sort((m1, m2) -> Integer.compare(m1.getCodigoMateria(), m2.getCodigoMateria()));

        int totalRegistros = todasLasMaterias.size();

        // Calculamos las páginas en base a las materias totales
        int paginasMaximas = (int) Math.ceil((double) totalRegistros / FILAS_POR_PAGINA);
        if (paginasMaximas == 0) paginasMaximas = 1;

        // Si borramos materias y la página actual quedó fuera de rango, recalculamos
        if (paginaActual > paginasMaximas) {
            paginaActual = paginasMaximas;
        }

        // Calculamos los índices para mostrar solo las 10 materias de la página actual
        int indiceInicio = (paginaActual - 1) * FILAS_POR_PAGINA;
        int indiceFin = Math.min(indiceInicio + FILAS_POR_PAGINA, totalRegistros);

        for (int i = indiceInicio; i < indiceFin; i++) {
            Materia mat = todasLasMaterias.get(i);
            Object[] fila = { mat.getCodigoMateria(), mat.getNombre(), mat.getCuatrimestre() + "º Cuatrimestre" };
            dtm.addRow(fila);
        }

        // Actualizamos controles de paginación en la vista
        vista.getLblPaginacion().setText("Página " + paginaActual + " de " + paginasMaximas);
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