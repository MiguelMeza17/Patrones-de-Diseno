package co.edu.unicauca.patterns.state;

/**
 * Estado Eliminada: la pregunta fue retirada del banco; solo puede restaurarse a Borrador.
 */
public final class DeletedState implements QuestionState {
    public static final String NAME = "Eliminada";
    public static final DeletedState INSTANCE = new DeletedState();

    private DeletedState() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public QuestionState restore() {
        return DraftState.INSTANCE;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
