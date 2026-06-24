package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelAlumnosUI extends JPanel {

    private CardLayout navegador;
    private JPanel contenedorDinamico;

    // Componentes Modo LISTA
    private JPanel panelLista;
    private JTable tablaAlumnos;
    private DefaultTableModel modeloTabla;
    private JButton btnAgregar, btnEditar, btnEliminar;
    private JButton btnAnterior, btnSiguiente;
    private JLabel lblPaginacion;

    // Componentes Modo FORMULARIO (Reutilizable)
    private JPanel panelFormulario;
    private JTextField txtNombre, txtDni, txtLegajo;
    private JButton btnGuardar, btnCancelar;
    private JLabel lblTituloFormulario;

    public PanelAlumnosUI() {
        setLayout(new BorderLayout());
        navegador = new CardLayout();
        contenedorDinamico = new JPanel(navegador);

        armarPanelLista();
        armarPanelFormulario();

        contenedorDinamico.add(panelLista, "LISTA");
        contenedorDinamico.add(panelFormulario, "FORMULARIO");

        add(contenedorDinamico, BorderLayout.CENTER);
        mostrarModoLista();
    }

    private void armarPanelLista() {
        panelLista = new JPanel(new BorderLayout());

        // Configuración de la Tabla
        String[] columnas = {"DNI", "Legajo", "Nombre", "Carrera Actual"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // No editable in-line
        };
        tablaAlumnos = new JTable(modeloTabla);
        panelLista.add(new JScrollPane(tablaAlumnos), BorderLayout.CENTER);

        // Barra inferior: Paginación y Botones de Acción
        JPanel panelInferior = new JPanel(new BorderLayout());

        // Sub-panel de paginación (Izquierda/Centro)
        JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAnterior = new JButton("< Anterior");
        btnSiguiente = new JButton("Siguiente >");
        lblPaginacion = new JLabel("Página 1 de 1");
        panelPaginacion.add(btnAnterior);
        panelPaginacion.add(lblPaginacion);
        panelPaginacion.add(btnSiguiente);

        // Sub-panel de acciones CRUD (Derecha)
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAgregar = new JButton("Agregar Alumno");
        btnEditar = new JButton("Editar Seleccionado");
        btnEliminar = new JButton("Eliminar");
        panelAcciones.add(btnAgregar);
        panelAcciones.add(btnEditar);
        panelAcciones.add(btnEliminar);

        panelInferior.add(panelPaginacion, BorderLayout.WEST);
        panelInferior.add(panelAcciones, BorderLayout.EAST);
        panelLista.add(panelInferior, BorderLayout.SOUTH);
    }

    private void armarPanelFormulario() {
        panelFormulario = new JPanel(new BorderLayout());

        lblTituloFormulario = new JLabel("Formulario de Alumno", SwingConstants.CENTER);
        lblTituloFormulario.setFont(new Font("Arial", Font.BOLD, 16));
        lblTituloFormulario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelFormulario.add(lblTituloFormulario, BorderLayout.NORTH);

        JPanel panelCampos = new JPanel(new GridLayout(3, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panelCampos.add(new JLabel("Documento (DNI):"));
        txtDni = new JTextField();
        panelCampos.add(txtDni);

        panelCampos.add(new JLabel("Número de Legajo:"));
        txtLegajo = new JTextField();
        panelCampos.add(txtLegajo);

        panelCampos.add(new JLabel("Nombre Completo:"));
        txtNombre = new JTextField();
        panelCampos.add(txtNombre);

        panelFormulario.add(panelCampos, BorderLayout.CENTER);

        // Botones Formulario
        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnCancelar);
        panelFormulario.add(panelBotonesForm, BorderLayout.SOUTH);
    }

    // --- MÉTODOS DE CONTROL VISUAL ---

    public void mostrarModoLista() {
        navegador.show(contenedorDinamico, "LISTA");
    }

    // Configura el formulario listo para un ALTA
    public void mostrarModoAlta() {
        lblTituloFormulario.setText("Registrar Nuevo Alumno");
        txtDni.setText("");
        txtDni.setEditable(true); // Se puede escribir el DNI
        txtLegajo.setText("");
        txtNombre.setText("");
        navegador.show(contenedorDinamico, "FORMULARIO");
    }

    // Configura el formulario cargado listo para una EDICIÓN
    public void mostrarModoEdicion(String dni, String legajo, String nombre) {
        lblTituloFormulario.setText("Modificar Datos del Alumno");
        txtDni.setText(dni);
        txtDni.setEditable(false); // 🔒 ¡Bloqueado! No se edita la clave primaria
        txtLegajo.setText(legajo);
        txtNombre.setText(nombre);
        navegador.show(contenedorDinamico, "FORMULARIO");
    }

    // --- GETTERS DE INFORMACIÓN ---
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JTable getTablaAlumnos() { return tablaAlumnos; }
    public String getTxtDni() { return txtDni.getText().trim(); }
    public String getTxtLegajo() { return txtLegajo.getText().trim(); }
    public String getTxtNombre() { return txtNombre.getText().trim(); }

    public void actualizarEtiquetaPaginacion(int paginaActual, int totalPaginas) {
        lblPaginacion.setText("Página " + paginaActual + " de " + totalPaginas);
    }

    // Enlace centralizado de eventos
    public void escucharComponentes(ActionListener listener) {
        btnAgregar.addActionListener(listener);
        btnEditar.addActionListener(listener);
        btnEliminar.addActionListener(listener);
        btnAnterior.addActionListener(listener);
        btnSiguiente.addActionListener(listener);
        btnGuardar.addActionListener(listener);
        btnCancelar.addActionListener(listener);
    }
}