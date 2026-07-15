package controlador;

import modelo.ModeloSistemaAcademico;
import vista.PanelCarrerasUI;
import vista.PanelMateriasUI;
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

        switch (comando) {
            case "Sección Alumnos": // 💡 Corregido: Agregamos la tilde para que coincida con el botón
                // 🎬 INYECCIÓN EN CALIENTE DE ALUMNOS
                PanelAlumnosUI panelAlumnos = new PanelAlumnosUI();
                ControladorAlumnos ctrlAlumnos = new ControladorAlumnos(panelAlumnos, modelo);

                // Le encajamos el panel de alumnos en el centro a la ventana principal
                ventanaMadre.setPanelCentral(panelAlumnos);
                break;

            case "Sección Materias":
                // 🎬 INYECCIÓN EN CALIENTE DE MATERIAS
                PanelMateriasUI panelMaterias = new PanelMateriasUI();
                ControladorMaterias ctrlMaterias = new ControladorMaterias(panelMaterias, modelo); // 💡 Corregido: Se agregó el ';' faltante

                // Le encajamos el panel de materias en el centro a la ventana principal
                ventanaMadre.setPanelCentral(panelMaterias);
                break;

            case "Sección Carreras":
                PanelCarrerasUI panelCarrerasUI = new PanelCarrerasUI();
                ControladorCarreras ctrlCarreras = new ControladorCarreras(panelCarrerasUI, modelo);

                ventanaMadre.setPanelCentral(panelCarrerasUI);
                break;

            case "Planes y Notas":
                // Cartelito temporal para las secciones vacías
                JOptionPane.showMessageDialog(ventanaMadre,
                        "La " + comando + " estará disponible en los próximos pasos.",
                        "Módulo en construcción",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }
}