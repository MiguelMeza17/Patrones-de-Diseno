package co.edu.unicauca.patterns.decorator;

import co.edu.unicauca.domain.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Agrega la clasificación de la pregunta: competencia, tema/subtema y dificultad.
 */
public class MetadataDecorator extends QuestionViewDecorator {

    public MetadataDecorator(QuestionView wrapped) {
        super(wrapped);
    }

    @Override
    public String render() {
        Question q = getQuestion();
        List<String> parts = new ArrayList<>();
        if (!isBlank(q.getCompetencia())) {
            parts.add("Competencia: " + q.getCompetencia());
        }
        if (!isBlank(q.getTema())) {
            parts.add("Tema: " + q.getTema() + (isBlank(q.getSubtema()) ? "" : " / " + q.getSubtema()));
        }
        if (!isBlank(q.getDificultad())) {
            parts.add("Dificultad: " + q.getDificultad());
        }
        if (parts.isEmpty()) {
            return super.render();
        }
        return super.render() + "\n" + String.join(" | ", parts);
    }
}
