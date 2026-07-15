package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class PanelCarrerasUI extends JPanel{

        private JTable tablaCarreras;
        private DefaultTableModel modeloTabla;

        // Campos del formulario
        private JTextField txtCodigo;
        private JTextField txtNombre;

        // Botones de control (Lista y Formulario)
        private JButton btnAgregar;
        private JButton btnEliminar;
        private JButton btnGuardar;
        private JButton btnCancelar;
        private JButton btnEditar;
        private JButton btnPlanDeEstudio;
        private JButton btnVolver;


        // Botones y etiquetas de paginación
        private JButton btnAnterior;
        private JButton btnSiguiente;
        private JLabel lblPaginacion;

        // Paneles que mutan o necesitan ser accedidos externamente
        private JPanel panelDerechoContenedor;
        private JPanel panelFormulario;
        private JLabel lblTituloFormulario;
        private JLabel lblTituloModulo;


        public PanelCarrerasUI() {
            // Configuramos nuestro panel principal con un BorderLayout
            this.setLayout(new BorderLayout(10, 10));
            this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // --- NORTE: El título del módulo ---
            lblTituloModulo = new JLabel("Administración de Carreras", JLabel.CENTER);
            lblTituloModulo.setFont(new Font("Arial", Font.BOLD, 18));
            this.add(lblTituloModulo, BorderLayout.NORTH);

            // --- CENTRO: Tabla y Paginación ---
            JPanel panelContenedorTabla = new JPanel(new BorderLayout(5, 5));

            String[] columnas = {"Nombre de Carrera", "Codigo de Carrera", "Plan de Estudio", "Numero de Inscriptos"};
            modeloTabla = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; //  Evita que editen tipeando directo en la celda
                }
            };

            tablaCarreras = new JTable(modeloTabla);
            tablaCarreras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(tablaCarreras);
            panelContenedorTabla.add(scrollPane, BorderLayout.CENTER);

            // Sub-panel de paginación (Sur del panel centro)
            JPanel panelPaginacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            btnAnterior = new JButton("< Anterior");
            btnSiguiente = new JButton("Siguiente >");
            lblPaginacion = new JLabel("Página 1 de 1");

            panelPaginacion.add(btnAnterior);
            panelPaginacion.add(lblPaginacion);
            panelPaginacion.add(btnSiguiente);
            panelContenedorTabla.add(panelPaginacion, BorderLayout.SOUTH);

            this.add(panelContenedorTabla, BorderLayout.CENTER);

            // --- ESTE: Panel Lateral Intercambiable (CardLayout) ---
            // Este es el contenedor "padre" de la derecha que maneja las cartas
            panelDerechoContenedor = new JPanel(new CardLayout());
            panelDerechoContenedor.setPreferredSize(new Dimension(300, 0));

            // CARTA 1: Botones de Acción (Modo Lista)
            JPanel panelBotonesAccion = new JPanel(new GridLayout(6, 1, 0, 10));
            panelBotonesAccion.setBorder(BorderFactory.createTitledBorder("Acciones"));

            btnAgregar = new JButton("Agregar Carrera");
            btnEliminar = new JButton("Eliminar Carrera");
            btnEditar = new JButton("Editar Carrera");
            btnEditar.setFocusPainted(false);
            btnPlanDeEstudio = new JButton("Plan de Estudio");

            btnVolver = new JButton("Volver");
            btnVolver.setVisible(false);

            panelBotonesAccion.add(btnAgregar);
            panelBotonesAccion.add(btnEliminar);
            panelBotonesAccion.add(btnEditar);
            panelBotonesAccion.add(btnPlanDeEstudio);
            panelBotonesAccion.add(btnVolver);


            // Con un solo casillero vacío ya completamos las 6 filas del GridLayout
            panelBotonesAccion.add(new JLabel(""));

            // CARTA 2: Formulario de Carga (Modo Alta)
            panelFormulario = new JPanel(new GridBagLayout());
            panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de la Carrera"));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5); // Margen interno entre componentes

            // Fila 0: Título interno del formulario
            lblTituloFormulario = new JLabel("Nueva Carrera");
            lblTituloFormulario.setFont(new Font("Arial", Font.BOLD, 14));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            panelFormulario.add(lblTituloFormulario, gbc);

            // Fila 1: Campo Código
            gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
            panelFormulario.add(new JLabel("Código (Nº):"), gbc);
            txtCodigo = new JTextField();
            gbc.gridx = 1; gbc.gridy = 1;
            panelFormulario.add(txtCodigo, gbc);

            // Fila 2: Campo Nombre
            gbc.gridx = 0; gbc.gridy = 2;
            panelFormulario.add(new JLabel("Nombre:"), gbc);
            txtNombre = new JTextField();
            gbc.gridx = 1; gbc.gridy = 2;
            panelFormulario.add(txtNombre, gbc);


            // otra Fila : Botones Guardar/Cancelar (Metidos en un sub-panel para ir juntos)
            JPanel panelBotonesForm = new JPanel(new GridLayout(1, 2, 5, 0));
            btnGuardar = new JButton("Guardar");
            btnCancelar = new JButton("Cancelar");
            panelBotonesForm.add(btnGuardar);
            panelBotonesForm.add(btnCancelar);

            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
            panelFormulario.add(panelBotonesForm, gbc);

            //  Agregamos las dos cartas al mazo contenedor identificándolas con un String
            panelDerechoContenedor.add(panelBotonesAccion, "Botones");
            panelDerechoContenedor.add(panelFormulario, "Formulario");

            this.add(panelDerechoContenedor, BorderLayout.EAST);
        }
        //  MÉTODOS DE CONTROL VISUAL (Intercambio de cartas)

        public void mostrarModoLista() {
            txtCodigo.setText("");
            txtNombre.setText("");
            txtCodigo.setEditable(true);

            btnAgregar.setText("Agregar Carrera");
            btnEliminar.setText("Eliminar Carrera");

            btnEditar.setVisible(true);
            btnPlanDeEstudio.setVisible(true);
            btnVolver.setVisible(false);
            btnEliminar.setVisible(true);

            lblTituloModulo.setText("Administración de Carreras");

            CardLayout cl = (CardLayout) panelDerechoContenedor.getLayout();
            cl.show(panelDerechoContenedor, "Botones");

            tablaCarreras.setEnabled(true);
        }

        public void mostrarModoAlta() {
            lblTituloFormulario.setText("Nueva Carrera");

            CardLayout cl = (CardLayout) panelDerechoContenedor.getLayout();
            cl.show(panelDerechoContenedor, "Formulario");

            tablaCarreras.setEnabled(false);
        }

        public void mostrarModoEdicion(String codigo, String nombre) {
            lblTituloFormulario.setText("Editar Carrera");

            // Cargamos los campos con los datos actuales
            txtCodigo.setText(codigo);
            txtCodigo.setEditable(false); // Bloqueamos el código para que no lo alteren
            txtNombre.setText(nombre);

            // Mostramos el formulario de la derecha
            CardLayout cl = (CardLayout) panelDerechoContenedor.getLayout();
            cl.show(panelDerechoContenedor, "Formulario");

            tablaCarreras.setEnabled(false); // Deshabilitamos la tabla mientras edita
        }

        public void mostrarModoGestionPlanDeEstudio(String nombreCarrera,String nombrePlanDeEstudio){
            lblTituloModulo.setText("Plan de Estudio asignado a "+nombreCarrera+": "+nombrePlanDeEstudio);

            // Mutamos el texto de los mismos botones
            btnAgregar.setText("Agregar Plan de Estudio");
            btnEliminar.setText("Eliminar Plan de Estudio");

            btnEditar.setVisible(false); // Ocultamos Editar porque acá no tiene sentido usarlo
            btnPlanDeEstudio.setVisible(false);
            btnVolver.setVisible(true);
            btnEliminar.setVisible(true);

            CardLayout cl = (CardLayout) panelDerechoContenedor.getLayout();
            cl.show(panelDerechoContenedor, "Botones");

            tablaCarreras.setEnabled(true);
        }

        public void mostrarModoSeleccionPlanDeEstudio(String nombreCarrera) {
            lblTituloModulo.setText("Planes disponibles para asignar a: " + nombreCarrera);

            // Cambiamos el texto del botón de agregar para que actúe como confirmación
            btnAgregar.setText("Confirmar Selección");

            // Ocultamos lo que no sirve en este sub-modo
            btnEliminar.setVisible(false);
            btnEditar.setVisible(false);
            btnPlanDeEstudio.setVisible(false);

            // Dejamos únicamente el botón para confirmar y el de volver por si se arrepiente
            btnAgregar.setVisible(true);
            btnVolver.setVisible(true);

            tablaCarreras.setEnabled(true);
        }

        public void escucharComponentes(ActionListener listener) {
            btnAgregar.addActionListener(listener);
            btnEliminar.addActionListener(listener);
            btnGuardar.addActionListener(listener);
            btnCancelar.addActionListener(listener);
            btnEditar.addActionListener(listener);
            btnAnterior.addActionListener(listener);
            btnSiguiente.addActionListener(listener);
            btnPlanDeEstudio.addActionListener(listener);
            btnVolver.addActionListener(listener);
        }

        // =========================================================================
        // GETTERS
        // =========================================================================

        public JTable getTablaCarreras() { return tablaCarreras; }
        public DefaultTableModel getModeloTabla() { return modeloTabla; }
        public String getTxtCodigo() { return txtCodigo.getText().trim(); }
        public String getTxtNombre() { return txtNombre.getText().trim(); }
        public JButton getBtnAnterior() { return btnAnterior; }
        public JButton getBtnSiguiente() { return btnSiguiente; }
        public JLabel getLblPaginacion() { return lblPaginacion; }

        public JButton getBtnAgregar() { return btnAgregar; }
        public JButton getBtnEliminar() { return btnEliminar; }
        public JButton getBtnPlanDeEstudio() { return btnPlanDeEstudio;}
        public JButton getBtnVolver() { return btnVolver; }

        public JButton getBtnGuardar(){return btnGuardar;}
        public JButton getBtnCancelar(){return btnCancelar;}
        public JButton getBtnEditar(){return btnEditar;}

}
