package co.edu.unicauca.patterns.templatemethod;

import co.edu.unicauca.domain.IQuestionValidator;
import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionStructuralValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Template Method: define el esqueleto fijo para evaluar una {@link Question}
 * durante el proceso de revisión.
 * <p>
 * El algoritmo siempre ejecuta, en este orden:
 * <ol>
 *   <li>Validación estructural (paso fijo, apoyado en {@link IQuestionValidator}).</li>
 *   <li>Si la pregunta pasa la validación estructural, evaluación de contenido
 *       (paso variable, {@link #evaluateContent(Question)}), que cada subclase
 *       redefine según su criterio de revisión.</li>
 *   <li>Construcción del resultado final (paso con comportamiento por defecto,
 *       que las subclases pueden sobrescribir si lo necesitan).</li>
 * </ol>
 * Si la validación estructural falla, el paso de contenido no se ejecuta: una
 * pregunta estructuralmente inválida nunca se aprueba, sin importar el criterio
 * de la subclase.
 */
public abstract class QuestionEvaluator {
    private final IQuestionValidator structuralValidator;

    protected QuestionEvaluator() {
        this(new QuestionStructuralValidator());
    }

    protected QuestionEvaluator(IQuestionValidator structuralValidator) {
        if (structuralValidator == null) {
            throw new IllegalArgumentException("El validador estructural no puede ser nulo.");
        }
        this.structuralValidator = structuralValidator;
    }

    /**
     * Método plantilla: fija el orden de los pasos del algoritmo de evaluación.
     * No puede ser redefinido por las subclases.
     */
    public final EvaluationResult evaluate(Question question) {
        List<String> errors = new ArrayList<>(validateStructure(question));
        if (errors.isEmpty()) {
            errors.addAll(evaluateContent(question));
        }
        return buildResult(question, errors);
    }

    /**
     * Paso fijo: validación estructural, apoyada en el punto de extensión existente
     * {@link IQuestionValidator}/{@link QuestionStructuralValidator}.
     */
    protected List<String> validateStructure(Question question) {
        return structuralValidator.validate(question);
    }

    /**
     * Paso variable: criterio de evaluación de contenido específico de cada
     * tipo de revisión. Solo se ejecuta si la pregunta pasó la validación
     * estructural.
     */
    protected abstract List<String> evaluateContent(Question question);

    /**
     * Paso con comportamiento por defecto: arma el resultado final a partir de
     * los errores acumulados. Las subclases pueden sobrescribirlo si necesitan
     * un criterio de aprobación distinto al de "sin errores".
     */
    protected EvaluationResult buildResult(Question question, List<String> errors) {
        return new EvaluationResult(question, errors.isEmpty(), errors);
    }
}
