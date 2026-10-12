package co.edu.unicauca.patterns.templatemethod;

import co.edu.unicauca.domain.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluador que, además del paso fijo de validación estructural, aplica un
 * criterio de contenido más estricto (pensado para el rol Docente): justificación
 * con sustento mínimo, bibliografía real (no un marcador genérico) y dificultad
 * dentro de los niveles reconocidos por el banco de preguntas.
 */
public class RubricQuestionEvaluator extends QuestionEvaluator {
    private static final int MIN_JUSTIFICACION_LENGTH = 20;
    private static final List<String> DIFICULTADES_VALIDAS = List.of("Fácil", "Media", "Difícil");
    private static final String PLACEHOLDER_BIBLIOGRAFIA = "n/a";

    @Override
    protected List<String> evaluateContent(Question question) {
        List<String> errors = new ArrayList<>();

        String justificacion = question.getJustificacion();
        if (justificacion == null || justificacion.trim().length() < MIN_JUSTIFICACION_LENGTH) {
            errors.add("La justificación debe tener al menos " + MIN_JUSTIFICACION_LENGTH
                    + " caracteres para sustentar la respuesta correcta.");
        }

        String bibliografia = question.getBibliografia();
        if (bibliografia != null && bibliografia.trim().equalsIgnoreCase(PLACEHOLDER_BIBLIOGRAFIA)) {
            errors.add("La bibliografía no puede ser un marcador genérico como 'N/A'.");
        }

        String dificultad = question.getDificultad();
        boolean dificultadValida = dificultad != null
                && DIFICULTADES_VALIDAS.stream().anyMatch(d -> d.equalsIgnoreCase(dificultad.trim()));
        if (!dificultadValida) {
            errors.add("La dificultad debe ser una de: " + String.join(", ", DIFICULTADES_VALIDAS) + ".");
        }

        return errors;
    }
}
