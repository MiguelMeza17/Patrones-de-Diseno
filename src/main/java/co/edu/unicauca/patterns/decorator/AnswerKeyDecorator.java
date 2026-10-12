package co.edu.unicauca.patterns.decorator;

import co.edu.unicauca.domain.Question;

/**
 * Agrega la clave de respuesta (respuesta correcta y justificación).
 * Pensado para vistas de autores y revisores, no de estudiantes.
 */
public class AnswerKeyDecorator extends QuestionViewDecorator {

    public AnswerKeyDecorator(QuestionView wrapped) {
        super(wrapped);
    }

    @Override
    public String render() {
        Question q = getQuestion();
        StringBuilder sb = new StringBuilder(super.render());
        if (!isBlank(q.getRespuestaCorrecta())) {
            sb.append("\nRespuesta correcta: ").append(q.getRespuestaCorrecta());
        }
        if (!isBlank(q.getJustificacion())) {
            sb.append("\nJustificación: ").append(q.getJustificacion());
        }
        return sb.toString();
    }
}
