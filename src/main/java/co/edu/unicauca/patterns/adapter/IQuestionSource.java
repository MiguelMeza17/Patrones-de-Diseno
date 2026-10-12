package co.edu.unicauca.patterns.adapter;

import co.edu.unicauca.domain.Question;

import java.util.List;

/**
 * Interfaz que espera el sistema para obtener preguntas (Target).
 */
public interface IQuestionSource {

    List<Question> getQuestions();
}
