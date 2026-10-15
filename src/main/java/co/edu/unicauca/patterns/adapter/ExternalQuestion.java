package co.edu.unicauca.patterns.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Formato de pregunta de una fuente externa (Adaptee).
 * <p>
 * Su estructura es incompatible con {@link co.edu.unicauca.domain.Question}:
 * las opciones llegan como lista, la respuesta correcta como índice (base 0)
 * y la dificultad como nivel numérico (1 = fácil, 2 = media, 3 = difícil).
 */
public class ExternalQuestion {
    private String code;
    private String title;
    private String context;
    private String statement;
    private List<String> options = new ArrayList<>();
    private int correctOptionIndex = -1;
    private String rationale;
    private String references;
    private String competency;
    private String topic;
    private String subtopic;
    private int difficultyLevel;

    public ExternalQuestion() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getCompetency() {
        return competency;
    }

    public void setCompetency(String competency) {
        this.competency = competency;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubtopic() {
        return subtopic;
    }

    public void setSubtopic(String subtopic) {
        this.subtopic = subtopic;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }
}
