package co.edu.unicauca.patterns.templatemethod;

import co.edu.unicauca.domain.Question;

import java.util.Collections;
import java.util.List;

/**
 * Resultado de evaluar una {@link Question} a través de {@link QuestionEvaluator}.
 */
public class EvaluationResult {
    private final Question question;
    private final boolean approved;
    private final List<String> errors;

    public EvaluationResult(Question question, boolean approved, List<String> errors) {
        this.question = question;
        this.approved = approved;
        this.errors = Collections.unmodifiableList(errors);
    }

    public Question getQuestion() {
        return question;
    }

    public boolean isApproved() {
        return approved;
    }

    public List<String> getErrors() {
        return errors;
    }
}
