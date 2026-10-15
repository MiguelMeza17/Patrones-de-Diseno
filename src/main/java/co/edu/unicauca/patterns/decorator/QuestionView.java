package co.edu.unicauca.patterns.decorator;

import co.edu.unicauca.domain.Question;

/**
 * Representación textual de una pregunta (Component).
 * Los decoradores le agregan características dinámicamente sin modificar {@link Question}.
 */
public interface QuestionView {

    Question getQuestion();

    String render();
}
