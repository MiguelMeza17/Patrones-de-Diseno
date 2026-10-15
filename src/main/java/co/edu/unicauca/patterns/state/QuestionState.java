package co.edu.unicauca.patterns.state;

/**
 * Estado del ciclo de vida de una pregunta (State).
 * <p>
 * Cada operación devuelve el estado siguiente. Por defecto las transiciones
 * no están permitidas; cada estado concreto habilita solo las que le aplican.
 * <ul>
 *   <li>Borrador → Pendiente de revisión ({@link #submitForReview()})</li>
 *   <li>Borrador / Pendiente de revisión → Eliminada ({@link #delete()})</li>
 *   <li>Pendiente de revisión → Borrador ({@link #returnToDraft()})</li>
 *   <li>Eliminada → Borrador ({@link #restore()})</li>
 * </ul>
 */
public interface QuestionState {

    String getName();

    default QuestionState submitForReview() {
        throw new IllegalStateException("Solo se puede enviar a revisión una pregunta en estado Borrador.");
    }

    default QuestionState returnToDraft() {
        throw new IllegalStateException("Una pregunta en estado " + getName() + " no puede devolverse a Borrador.");
    }

    default QuestionState delete() {
        throw new IllegalStateException("Una pregunta en estado " + getName() + " no puede eliminarse.");
    }

    default QuestionState restore() {
        throw new IllegalStateException("Solo se puede restaurar una pregunta en estado Eliminada.");
    }

    default boolean canAssignReviewers() {
        return false;
    }

    default boolean canSubmitForReview() {
        return false;
    }
}
