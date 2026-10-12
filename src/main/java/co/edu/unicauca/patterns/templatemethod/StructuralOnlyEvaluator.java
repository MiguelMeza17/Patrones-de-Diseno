package co.edu.unicauca.patterns.templatemethod;

import co.edu.unicauca.domain.Question;

import java.util.Collections;
import java.util.List;

/**
 * Evaluador que solo aplica el paso fijo de validación estructural, sin
 * criterios de contenido adicionales. Útil como revisión rápida (p. ej. para
 * el rol Revisor) o como caso base del Template Method.
 */
public class StructuralOnlyEvaluator extends QuestionEvaluator {

    @Override
    protected List<String> evaluateContent(Question question) {
        return Collections.emptyList();
    }
}
