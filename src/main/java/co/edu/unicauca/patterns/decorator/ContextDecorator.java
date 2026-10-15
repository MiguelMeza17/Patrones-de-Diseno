package co.edu.unicauca.patterns.decorator;

/**
 * Antepone el contexto de la pregunta (situación o texto base) a la vista envuelta.
 */
public class ContextDecorator extends QuestionViewDecorator {

    public ContextDecorator(QuestionView wrapped) {
        super(wrapped);
    }

    @Override
    public String render() {
        String contexto = getQuestion().getContexto();
        if (isBlank(contexto)) {
            return super.render();
        }
        return "Contexto: " + contexto + "\n" + super.render();
    }
}
