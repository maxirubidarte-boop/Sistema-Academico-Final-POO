package modelo;

import java.util.ArrayList;

public class FabricaPlanDeEstudio {

    // 🔥 CORREGIDO: Ahora recibe un único objeto CondicionDeInscripcion global
    public PlanDeEstudio crearPlanDeEstudio(
            ArrayList<Materia> obligatorias,
            ArrayList<Materia> optativas,
            ArrayList<CuatrimestreCurricular> cuatrimestres,
            CondicionDeInscripcion condicionGlobal, // 🚀 Adiós al Map viejo
            int minObli,
            int minOpta,
            Integer codigo,
            String nombre
    ) {
        PlanEstudioBuilder builder = new PlanEstudioBuilder();

        // 1. Configurar datos básicos y la condición única en el Builder
        builder.setMinObligatorias(minObli)
                .setMinOptativas(minOpta)
                .setCodigo(codigo)
                .setNombre(nombre)
                .setCondicionGlobal(condicionGlobal); // 🔥 Se la seteamos al Builder

        // 2. Cargar materias obligatorias de forma simple
        for (Materia m : obligatorias) {
            builder.addMateriaObligatoria(m); // 🚀 Ya no le pasamos ninguna estrategia individual
        }

        // 3. Cargar materias optativas de forma simple
        for (Materia m : optativas) {
            builder.addMateriaOptativa(m);
        }

        // 4. Agregar cuatrimestres
        for (CuatrimestreCurricular c : cuatrimestres) {
            builder.addCuatrimestre(c);
        }

        // Retorna el plan estructurado
        return builder.build();
    }
}