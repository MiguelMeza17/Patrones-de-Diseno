package co.edu.unicauca.patterns.decorator;

import co.edu.unicauca.domain.Question;

/**
 * Vista base de una pregunta (ConcreteComponent): título, enunciado y las 4 opciones.
 */
public class BasicQuestionView implements QuestionView {
    private final Question question;

    public BasicQuestionView(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("La pregunta no puede ser nula.");
        }
        this.question = question;
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(question.getId()).append("] ").append(question.getNombre()).append("\n");
        sb.append(question.getPregunta());
        if (question.getDistractors() != null) {
            sb.append("\n").append(question.getDistractors().getFormattedText());
        }
        return sb.toString();
    }
}
