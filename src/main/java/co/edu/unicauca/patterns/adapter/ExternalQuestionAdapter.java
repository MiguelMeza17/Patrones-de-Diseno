package co.edu.unicauca.patterns.adapter;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter: convierte las preguntas de una {@link ExternalQuestionProvider}
 * al modelo de dominio {@link Question}, exponiéndolas como {@link IQuestionSource}.
 * <p>
 * Las preguntas externas malformadas no se importan; el motivo queda
 * registrado en {@link #getErrors()}. Las preguntas adaptadas quedan en
 * estado Borrador para que pasen por el flujo normal de validación y revisión.
 */
public class ExternalQuestionAdapter implements IQuestionSource {
    private static final int OPTIONS_COUNT = 4;
    private static final String[] DIFFICULTIES = {"Fácil", "Media", "Difícil"};

    private final ExternalQuestionProvider provider;
    private final List<String> errors = new ArrayList<>();

    public ExternalQuestionAdapter(ExternalQuestionProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("La fuente externa no puede ser nula.");
        }
        this.provider = provider;
    }

    @Override
    public List<Question> getQuestions() {
        errors.clear();
        List<Question> questions = new ArrayList<>();
        List<ExternalQuestion> external = provider.fetchQuestions();
        if (external == null) {
            return questions;
        }
        for (ExternalQuestion item : external) {
            try {
                questions.add(adapt(item));
            } catch (IllegalArgumentException e) {
                errors.add(e.getMessage());
            }
        }
        return questions;
    }

    /**
     * Convierte una pregunta externa al modelo de dominio.
     *
     * @throws IllegalArgumentException si la pregunta externa está malformada
     */
    public Question adapt(ExternalQuestion external) {
        if (external == null) {
            throw new IllegalArgumentException("La pregunta externa no puede ser nula.");
        }
        String code = external.getCode();
        if (isBlank(code)) {
            throw new IllegalArgumentException("Pregunta externa sin código.");
        }
        if (isBlank(external.getStatement())) {
            throw new IllegalArgumentException("Pregunta externa " + code + ": falta el enunciado.");
        }

        List<String> options = external.getOptions();
        if (options == null || options.size() != OPTIONS_COUNT) {
            throw new IllegalArgumentException("Pregunta externa " + code + ": debe tener exactamente "
                    + OPTIONS_COUNT + " opciones.");
        }
        int correct = external.getCorrectOptionIndex();
        if (correct < 0 || correct >= OPTIONS_COUNT) {
            throw new IllegalArgumentException("Pregunta externa " + code
                    + ": índice de respuesta correcta inválido (" + correct + ").");
        }

        return new Question.Builder()
                .id(code.trim())
                .nombre(external.getTitle())
                .contexto(external.getContext())
                .pregunta(external.getStatement())
                .distractors(new QuestionDistractors(options.get(0), options.get(1), options.get(2), options.get(3)))
                .respuestaCorrecta(options.get(correct))
                .justificacion(external.getRationale())
                .bibliografia(external.getReferences())
                .competencia(external.getCompetency())
                .tema(external.getTopic())
                .subtema(external.getSubtopic())
                .dificultad(toDifficulty(code, external.getDifficultyLevel()))
                .estado(Question.STATE_BORRADOR)
                .build();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    private String toDifficulty(String code, int level) {
        if (level < 1 || level > DIFFICULTIES.length) {
            throw new IllegalArgumentException("Pregunta externa " + code
                    + ": nivel de dificultad inválido (" + level + ").");
        }
        return DIFFICULTIES[level - 1];
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
