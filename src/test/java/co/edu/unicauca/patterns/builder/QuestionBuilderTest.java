package co.edu.unicauca.patterns.builder;

import co.edu.unicauca.domain.IQuestionValidator;
import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionStructuralValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionBuilderTest {

    @Test
    @DisplayName("Debe construir una pregunta válida de forma fluida con todos los campos")
    void testBuildValidQuestionFluently() {
        Question question = new QuestionBuilder()
                .id("P-100")
                .nombre("Pregunta de prueba")
                .contexto("Contexto de prueba")
                .pregunta("¿Cuál es la respuesta correcta?")
                .opciones("A", "B", "C", "D")
                .respuestaCorrecta("A")
                .justificacion("Justificación de prueba")
                .bibliografia("Bibliografía de prueba")
                .competencia("Competencia de prueba")
                .tema("Tema de prueba")
                .subtema("Subtema de prueba")
                .dificultad("Media")
                .autorLogin("autor1")
                .build();

        assertEquals("P-100", question.getId());
        assertEquals("Pregunta de prueba", question.getNombre());
        assertEquals("Contexto de prueba", question.getContexto());
        assertEquals("¿Cuál es la respuesta correcta?", question.getPregunta());
        assertNotNull(question.getDistractors());
        assertEquals("A", question.getDistractors().getOptionA());
        assertEquals("D", question.getDistractors().getOptionD());
        assertEquals("A", question.getRespuestaCorrecta());
        assertEquals("Justificación de prueba", question.getJustificacion());
        assertEquals("Bibliografía de prueba", question.getBibliografia());
        assertEquals("Competencia de prueba", question.getCompetencia());
        assertEquals("Tema de prueba", question.getTema());
        assertEquals("Subtema de prueba", question.getSubtema());
        assertEquals("Media", question.getDificultad());
        assertEquals("autor1", question.getAutorLogin());

        IQuestionValidator validator = new QuestionStructuralValidator();
        assertTrue(validator.validate(question).isEmpty(), "Una pregunta completa no debe tener errores estructurales");
    }

    @Test
    @DisplayName("Sin indicar estado, la pregunta debe nacer en Borrador")
    void testDefaultStateIsBorrador() {
        Question question = new QuestionBuilder()
                .id("P-101")
                .nombre("Pregunta sin estado")
                .build();

        assertEquals(Question.STATE_BORRADOR, question.getEstado());
    }

    @Test
    @DisplayName("Si se indica explícitamente un estado, debe respetarse en lugar del valor por defecto")
    void testExplicitStateOverridesDefault() {
        Question question = new QuestionBuilder()
                .id("P-102")
                .nombre("Pregunta con estado explícito")
                .estado(Question.STATE_PENDIENTE_REVISION)
                .build();

        assertEquals(Question.STATE_PENDIENTE_REVISION, question.getEstado());
    }

    @Test
    @DisplayName("Una pregunta construida sin campos obligatorios debe fallar la validación estructural")
    void testMissingRequiredFieldsFailStructuralValidation() {
        Question question = new QuestionBuilder()
                .id("P-103")
                .build();

        IQuestionValidator validator = new QuestionStructuralValidator();
        List<String> errors = validator.validate(question);

        assertFalse(errors.isEmpty(), "Una pregunta incompleta debe reportar errores de validación");
        assertTrue(errors.stream().anyMatch(e -> e.contains("título")));
        assertTrue(errors.stream().anyMatch(e -> e.contains("distractores") || e.contains("opciones")));
    }

    @Test
    @DisplayName("El método opciones() debe armar correctamente los 4 distractores")
    void testOpcionesHelperBuildsDistractors() {
        Question question = new QuestionBuilder()
                .id("P-104")
                .opciones("Uno", "Dos", "Tres", "Cuatro")
                .build();

        assertEquals("Uno", question.getDistractors().getOptionA());
        assertEquals("Dos", question.getDistractors().getOptionB());
        assertEquals("Tres", question.getDistractors().getOptionC());
        assertEquals("Cuatro", question.getDistractors().getOptionD());
    }
}
