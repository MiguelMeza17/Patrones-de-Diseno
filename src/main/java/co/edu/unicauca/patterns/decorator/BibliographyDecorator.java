package co.edu.unicauca.patterns.decorator;

/**
 * Agrega la bibliografía de referencia de la pregunta.
 */
public class BibliographyDecorator extends QuestionViewDecorator {

    public BibliographyDecorator(QuestionView wrapped) {
        super(wrapped);
    }

    @Override
    public String render() {
        String bibliografia = getQuestion().getBibliografia();
        if (isBlank(bibliografia)) {
            return super.render();
        }
        return super.render() + "\nBibliografía: " + bibliografia;
    }
}
