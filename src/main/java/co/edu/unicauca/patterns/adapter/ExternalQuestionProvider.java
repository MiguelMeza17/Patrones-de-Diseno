package co.edu.unicauca.patterns.adapter;

import java.util.List;

/**
 * Interfaz de la fuente externa de preguntas (Adaptee).
 * Entrega las preguntas en su propio formato {@link ExternalQuestion}.
 */
@FunctionalInterface
public interface ExternalQuestionProvider {

    List<ExternalQuestion> fetchQuestions();
}
