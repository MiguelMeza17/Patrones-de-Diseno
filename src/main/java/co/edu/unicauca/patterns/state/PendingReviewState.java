package co.edu.unicauca.patterns.state;

/**
 * Estado Pendiente de revisión: la pregunta espera la asignación de revisores.
 */
public final class PendingReviewState implements QuestionState {
    public static final String NAME = "Pendiente de revisión";
    public static final PendingReviewState INSTANCE = new PendingReviewState();

    private PendingReviewState() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public QuestionState returnToDraft() {
        return DraftState.INSTANCE;
    }

    @Override
    public QuestionState delete() {
        return DeletedState.INSTANCE;
    }

    @Override
    public boolean canAssignReviewers() {
        return true;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
