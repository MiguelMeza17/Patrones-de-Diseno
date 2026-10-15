package co.edu.unicauca.patterns.state;

import java.util.List;

/**
 * Utilidades para obtener los estados a partir de su nombre persistido
 * y para calcular la transición hacia un estado destino.
 */
public final class QuestionStates {

    private static final List<QuestionState> ALL = List.of(
            DraftState.INSTANCE, PendingReviewState.INSTANCE, DeletedState.INSTANCE);

    private QuestionStates() {
    }

    public static List<QuestionState> all() {
        return ALL;
    }

    /**
     * @throws IllegalArgumentException si el nombre no corresponde a ningún estado
     */
    public static QuestionState fromName(String name) {
        for (QuestionState state : ALL) {
            if (state.getName().equals(name)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Estado de pregunta desconocido: " + name);
    }

    /**
     * Aplica la transición que lleva de {@code current} al estado llamado {@code targetName}.
     *
     * @throws IllegalArgumentException si el estado destino no existe
     * @throws IllegalStateException    si la transición no está permitida
     */
    public static QuestionState transition(QuestionState current, String targetName) {
        QuestionState target = fromName(targetName);
        if (target == current) {
            throw new IllegalStateException("La pregunta ya está en estado " + current.getName() + ".");
        }
        if (target == PendingReviewState.INSTANCE) {
            return current.submitForReview();
        }
        if (target == DeletedState.INSTANCE) {
            return current.delete();
        }
        return current == DeletedState.INSTANCE ? current.restore() : current.returnToDraft();
    }
}
