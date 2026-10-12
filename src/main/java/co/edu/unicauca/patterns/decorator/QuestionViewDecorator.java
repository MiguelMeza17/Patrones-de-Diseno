package co.edu.unicauca.patterns.decorator;

import co.edu.unicauca.domain.Question;

/**
 * Decorador base (Decorator): envuelve otra {@link QuestionView} y delega en ella.
 * Las subclases agregan su característica antes o después de la vista envuelta.
 */
public abstract class QuestionViewDecorator implements QuestionView {
    protected final QuestionView wrapped;

    protected QuestionViewDecorator(QuestionView wrapped) {
        if (wrapped == null) {
            throw new IllegalArgumentException("La vista a decorar no puede ser nula.");
        }
        this.wrapped = wrapped;
    }

    @Override
    public Question getQuestion() {
        return wrapped.getQuestion();
    }

    @Override
    public String render() {
        return wrapped.render();
    }

    protected static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
