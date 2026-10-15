package co.edu.unicauca.patterns.adapter;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionStructuralValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExternalQuestionAdapterTest {

    private ExternalQuestionAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ExternalQuestionAdapter(List::of);
    }

    private ExternalQuestion validExternal(String code) {
        ExternalQuestion e = new ExternalQuestion();
        e.setCode(code);
        e.setTitle("Suma básica");
        e.setContext("Aritmética elemental");
        e.setStatement("¿Cuánto es 2 + 2?");
        e.setOptions(Arrays.asList("3", "4", "5", "6"));
        e.setCorrectOptionIndex(1);
        e.setRationale("2 + 2 = 4");
        e.setReferences("Baldor");
        e.setCompetency("Razonamiento cuantitativo");
        e.setTopic("Aritmética");
        e.setSubtopic("Suma");
        e.setDifficultyLevel(1);
        return e;
    }

    @Test
    @DisplayName("Debe adaptar todos los campos de una pregunta externa válida")
    void testAdaptValidQuestion() {
        Question q = adapter.adapt(validExternal("EXT-1"));

        assertEquals("EXT-1", q.getId());
        assertEquals("Suma básica", q.getNombre());
        assertEquals("Aritmética elemental", q.getContexto());
        assertEquals("¿Cuánto es 2 + 2?", q.getPregunta());
        assertEquals("3", q.getDistractors().getOptionA());
        assertEquals("4", q.getDistractors().getOptionB());
        assertEquals("5", q.getDistractors().getOptionC());
        assertEquals("6", q.getDistractors().getOptionD());
        assertEquals("2 + 2 = 4", q.getJustificacion());
        assertEquals("Baldor", q.getBibliografia());
        assertEquals("Razonamiento cuantitativo", q.getCompetencia());
        assertEquals("Aritmética", q.getTema());
        assertEquals("Suma", q.getSubtema());
    }

    @Test
    @DisplayName("Debe convertir el índice de respuesta correcta en el texto de la opción")
    void testCorrectAnswerFromIndex() {
        ExternalQuestion e = validExternal("EXT-1");
        e.setCorrectOptionIndex(3);
        assertEquals("6", adapter.adapt(e).getRespuestaCorrecta());
    }

    @Test
    @DisplayName("Debe convertir los niveles numéricos de dificultad a los valores del sistema")
    void testDifficultyMapping() {
        String[] expected = {"Fácil", "Media", "Difícil"};
        for (int level = 1; level <= 3; level++) {
            ExternalQuestion e = validExternal("EXT-" + level);
            e.setDifficultyLevel(level);
            assertEquals(expected[level - 1], adapter.adapt(e).getDificultad());
        }
    }

    @Test
    @DisplayName("La pregunta adaptada debe quedar en Borrador y pasar la validación estructural")
    void testAdaptedQuestionIsValidDraft() {
        Question q = adapter.adapt(validExternal("EXT-1"));
        assertEquals(Question.STATE_BORRADOR, q.getEstado());
        assertTrue(new QuestionStructuralValidator().validate(q).isEmpty());
    }

    @Test
    @DisplayName("Debe rechazar una pregunta externa nula")
    void testRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> adapter.adapt(null));
    }

    @Test
    @DisplayName("Debe rechazar una pregunta externa sin código")
    void testRejectMissingCode() {
        ExternalQuestion e = validExternal("   ");
        assertThrows(IllegalArgumentException.class, () -> adapter.adapt(e));
    }

    @Test
    @DisplayName("Debe rechazar una pregunta externa sin enunciado")
    void testRejectMissingStatement() {
        ExternalQuestion e = validExternal("EXT-1");
        e.setStatement(null);
        assertThrows(IllegalArgumentException.class, () -> adapter.adapt(e));
    }

    @Test
    @DisplayName("Debe rechazar una pregunta externa sin exactamente 4 opciones")
    void testRejectWrongOptionsCount() {
        ExternalQuestion fewer = validExternal("EXT-1");
        fewer.setOptions(Arrays.asList("1", "2"));
        assertThrows(IllegalArgumentException.class, () -> adapter.adapt(fewer));

        ExternalQuestion none = validExternal("EXT-2");
        none.setOptions(null);
        assertThrows(IllegalArgumentException.class, () -> adapter.adapt(none));
    }

    @Test
    @DisplayName("Debe rechazar un índice de respuesta correcta fuera de rango")
    void testRejectInvalidCorrectIndex() {
        ExternalQuestion e = validExternal("EXT-1");
        e.setCorrectOptionIndex(4);
        assertThrows(IllegalArgumentException.class, () -> adapter.adapt(e));
        e.setCorrectOptionIndex(-1);
        assertThrows(IllegalArgumentException.class, () -> adapter.adapt(e));
    }

    @Test
    @DisplayName("Debe rechazar un nivel de dificultad fuera de rango")
    void testRejectInvalidDifficulty() {
        ExternalQuestion e = validExternal("EXT-1");
        e.setDifficultyLevel(0);
        assertThrows(IllegalArgumentException.class, () -> adapter.adapt(e));
        e.setDifficultyLevel(4);
        assertThrows(IllegalArgumentException.class, () -> adapter.adapt(e));
    }

    @Test
    @DisplayName("getQuestions debe importar las válidas y registrar un error por cada malformada")
    void testGetQuestionsSkipsMalformed() {
        ExternalQuestion badOptions = validExternal("EXT-2");
        badOptions.setOptions(Arrays.asList("1"));
        List<ExternalQuestion> external = new ArrayList<>(List.of(validExternal("EXT-1"), badOptions, validExternal("EXT-3")));
        external.add(null);

        IQuestionSource source = new ExternalQuestionAdapter(() -> external);
        List<Question> questions = source.getQuestions();

        assertEquals(2, questions.size());
        assertEquals("EXT-1", questions.get(0).getId());
        assertEquals("EXT-3", questions.get(1).getId());
        List<String> errors = ((ExternalQuestionAdapter) source).getErrors();
        assertEquals(2, errors.size());
        assertTrue(errors.get(0).contains("EXT-2"));
    }

    @Test
    @DisplayName("getQuestions debe limpiar los errores de importaciones anteriores")
    void testErrorsResetBetweenCalls() {
        List<ExternalQuestion> external = new ArrayList<>();
        external.add(null);
        ExternalQuestionAdapter a = new ExternalQuestionAdapter(() -> external);

        a.getQuestions();
        assertEquals(1, a.getErrors().size());

        external.clear();
        a.getQuestions();
        assertTrue(a.getErrors().isEmpty());
    }

    @Test
    @DisplayName("getQuestions debe devolver una lista vacía si la fuente externa devuelve null")
    void testNullProviderResult() {
        ExternalQuestionAdapter a = new ExternalQuestionAdapter(() -> null);
        assertTrue(a.getQuestions().isEmpty());
        assertTrue(a.getErrors().isEmpty());
    }

    @Test
    @DisplayName("No debe permitir construir el adaptador sin fuente externa")
    void testRejectNullProvider() {
        assertThrows(IllegalArgumentException.class, () -> new ExternalQuestionAdapter(null));
    }
}
