package controlador;

import modelo.ModeloSistemaAcademico;
import vista.VentanaPrincipalUI;
import vista.PanelAlumnosUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorPrincipal implements ActionListener {

    private VentanaPrincipalUI ventanaMadre;
    private ModeloSistemaAcademico modelo;

    public ControladorPrincipal(VentanaPrincipalUI ventanaMadre, ModeloSistemaAcademico modelo) {
        this.ventanaMadre = ventanaMadre;
        this.modelo = modelo;

        // Registramos este controlador para escuchar los 4 botones de arriba
        this.ventanaMadre.escucharMenu(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (comando.equals("Sección Alumnos")) {
            // 🎬 INYECCIÓN EN CALIENTE DE ALUMNOS
            PanelAlumnosUI panelAlumnos = new PanelAlumnosUI();
            ControladorAlumnos ctrlAlumnos = new ControladorAlumnos(panelAlumnos, modelo);

            // Le encajamos el panel de alumnos en el centro a la ventana principal
            ventanaMadre.setPanelCentral(panelAlumnos);

        } else if (comando.equals("Sección Materias") || comando.equals("Sección Carreras") || comando.equals("Planes y Notas")) {
            // Cartelito temporal para las secciones vacías
            JOptionPane.showMessageDialog(ventanaMadre,
                    "La " + comando + " estará disponible en los próximos pasos.",
                    "Módulo en construcción",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}