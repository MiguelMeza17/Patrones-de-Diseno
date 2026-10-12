package co.edu.unicauca.patterns.templatemethod;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.patterns.builder.QuestionBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionEvaluatorTest {

    /**
     * Doble de prueba que registra si el paso variable fue invocado, para
     * verificar el orden fijo del esqueleto del Template Method.
     */
    private static class RecordingEvaluator extends QuestionEvaluator {
        boolean contentEvaluated = false;
        final List<String> contentErrors;

        RecordingEvaluator(List<String> contentErrors) {
            this.contentErrors = contentErrors;
        }

        @Override
        protected List<String> evaluateContent(Question question) {
            contentEvaluated = true;
            return contentErrors;
        }
    }

    private Question validQuestion(String id) {
        return new QuestionBuilder()
                .id(id)
                .nombre("Pregunta completa")
                .contexto("Contexto suficiente")
                .pregunta("¿Cuál es la respuesta correcta?")
                .opciones("A", "B", "C", "D")
                .respuestaCorrecta("A")
                .justificacion("Esta es una justificación suficientemente larga y detallada.")
                .bibliografia("Autor, Título, Editorial, 2024.")
                .competencia("Competencia de prueba")
                .tema("Tema de prueba")
                .subtema("Subtema de prueba")
                .dificultad("Media")
                .autorLogin("autor1")
                .build();
    }

    private Question incompleteQuestion(String id) {
        return new QuestionBuilder().id(id).build();
    }

    @Test
    @DisplayName("Si la validación estructural falla, el paso de contenido no debe ejecutarse")
    void testStructuralErrorsShortCircuitContentStep() {
        RecordingEvaluator evaluator = new RecordingEvaluator(new ArrayList<>());

        EvaluationResult result = evaluator.evaluate(incompleteQuestion("TM-001"));

        assertFalse(result.isApproved());
        assertFalse(result.getErrors().isEmpty());
        assertFalse(evaluator.contentEvaluated, "El paso variable no debe ejecutarse si la estructura es inválida");
    }

    @Test
    @DisplayName("Si la validación estructural pasa, el esqueleto debe ejecutar el paso de contenido")
    void testFixedSkeletonRunsContentStepWhenStructureIsValid() {
        RecordingEvaluator evaluator = new RecordingEvaluator(new ArrayList<>());

        EvaluationResult result = evaluator.evaluate(validQuestion("TM-002"));

        assertTrue(result.isApproved());
        assertTrue(evaluator.contentEvaluated, "El paso variable debe ejecutarse cuando la estructura es válida");
    }

    @Test
    @DisplayName("El resultado debe quedar no aprobado si el paso de contenido reporta errores")
    void testContentStepErrorsPreventApproval() {
        RecordingEvaluator evaluator = new RecordingEvaluator(List.of("Error de contenido simulado"));

        EvaluationResult result = evaluator.evaluate(validQuestion("TM-003"));

        assertFalse(result.isApproved());
        assertTrue(result.getErrors().contains("Error de contenido simulado"));
    }

    @Test
    @DisplayName("StructuralOnlyEvaluator debe aprobar una pregunta completa sin aplicar criterios extra")
    void testStructuralOnlyEvaluatorApprovesCompleteQuestion() {
        QuestionEvaluator evaluator = new StructuralOnlyEvaluator();

        EvaluationResult result = evaluator.evaluate(validQuestion("TM-004"));

        assertTrue(result.isApproved());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("RubricQuestionEvaluator debe rechazar una justificación demasiado corta")
    void testRubricEvaluatorRejectsShortJustification() {
        Question question = validQuestion("TM-005");
        question.setJustificacion("Muy corta");

        EvaluationResult result = new RubricQuestionEvaluator().evaluate(question);

        assertFalse(result.isApproved());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("justificación")));
    }

    @Test
    @DisplayName("RubricQuestionEvaluator debe rechazar una bibliografía genérica 'N/A'")
    void testRubricEvaluatorRejectsPlaceholderBibliography() {
        Question question = validQuestion("TM-006");
        question.setBibliografia("N/A");

        EvaluationResult result = new RubricQuestionEvaluator().evaluate(question);

        assertFalse(result.isApproved());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("bibliografía")));
    }

    @Test
    @DisplayName("RubricQuestionEvaluator debe rechazar una dificultad fuera del catálogo permitido")
    void testRubricEvaluatorRejectsInvalidDifficulty() {
        Question question = validQuestion("TM-007");
        question.setDificultad("Extrema");

        EvaluationResult result = new RubricQuestionEvaluator().evaluate(question);

        assertFalse(result.isApproved());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("dificultad")));
    }

    @Test
    @DisplayName("RubricQuestionEvaluator debe aprobar una pregunta completa que cumple el rubro")
    void testRubricEvaluatorApprovesCompleteQuestion() {
        EvaluationResult result = new RubricQuestionEvaluator().evaluate(validQuestion("TM-008"));

        assertTrue(result.isApproved());
        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Subclases distintas deben poder producir resultados distintos para la misma pregunta")
    void testDifferentSubclassesRedefineContentStepDifferently() {
        Question question = validQuestion("TM-009");
        question.setJustificacion("Corta");

        EvaluationResult structuralOnly = new StructuralOnlyEvaluator().evaluate(question);
        EvaluationResult rubric = new RubricQuestionEvaluator().evaluate(question);

        assertTrue(structuralOnly.isApproved(), "El evaluador solo-estructural no aplica criterios de contenido");
        assertFalse(rubric.isApproved(), "El evaluador con rúbrica sí exige una justificación más extensa");
    }
}
