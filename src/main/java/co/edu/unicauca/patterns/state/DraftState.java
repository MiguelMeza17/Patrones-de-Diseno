package co.edu.unicauca.patterns.state;

/**
 * Estado Borrador: la pregunta está en edición por su autor.
 */
public final class DraftState implements QuestionState {
    public static final String NAME = "Borrador";
    public static final DraftState INSTANCE = new DraftState();

    private DraftState() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public QuestionState submitForReview() {
        return PendingReviewState.INSTANCE;
    }

    @Override
    public QuestionState delete() {
        return DeletedState.INSTANCE;
    }

    @Override
    public boolean canSubmitForReview() {
        return true;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
