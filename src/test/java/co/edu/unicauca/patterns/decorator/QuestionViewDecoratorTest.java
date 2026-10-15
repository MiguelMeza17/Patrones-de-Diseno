package co.edu.unicauca.patterns.decorator;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionViewDecoratorTest {

    private static final String BASE =
            "[P-1] Suma básica\n¿Cuánto es 2 + 2?\nA. 3\nB. 4\nC. 5\nD. 6";

    private Question question;
    private QuestionView base;

    @BeforeEach
    void setUp() {
        question = new Question.Builder()
                .id("P-1")
                .nombre("Suma básica")
                .contexto("Aritmética elemental")
                .pregunta("¿Cuánto es 2 + 2?")
                .distractors(new QuestionDistractors("3", "4", "5", "6"))
                .respuestaCorrecta("4")
                .justificacion("2 + 2 = 4")
                .bibliografia("Baldor")
                .competencia("Razonamiento cuantitativo")
                .tema("Aritmética")
                .subtema("Suma")
                .dificultad("Fácil")
                .build();
        base = new BasicQuestionView(question);
    }

    // --- Comportamiento base ---

    @Test
    @DisplayName("La vista base debe mostrar solo título, enunciado y opciones")
    void testBaseView() {
        assertEquals(BASE, base.render());
        assertSame(question, base.getQuestion());
    }

    @Test
    @DisplayName("Decorar no debe alterar la vista base ni la pregunta")
    void testDecoratingDoesNotAlterBase() {
        QuestionView full = new BibliographyDecorator(new MetadataDecorator(
                new AnswerKeyDecorator(new ContextDecorator(base))));
        full.render();

        assertEquals(BASE, base.render());
        assertEquals("4", question.getRespuestaCorrecta());
        assertEquals("Aritmética elemental", question.getContexto());
    }

    // --- Cada decorador por separado ---

    @Test
    @DisplayName("ContextDecorator debe anteponer el contexto")
    void testContextDecorator() {
        assertEquals("Contexto: Aritmética elemental\n" + BASE, new ContextDecorator(base).render());
    }

    @Test
    @DisplayName("AnswerKeyDecorator debe agregar respuesta correcta y justificación")
    void testAnswerKeyDecorator() {
        assertEquals(BASE + "\nRespuesta correcta: 4\nJustificación: 2 + 2 = 4",
                new AnswerKeyDecorator(base).render());
    }

    @Test
    @DisplayName("MetadataDecorator debe agregar competencia, tema/subtema y dificultad")
    void testMetadataDecorator() {
        assertEquals(BASE + "\nCompetencia: Razonamiento cuantitativo | Tema: Aritmética / Suma | Dificultad: Fácil",
                new MetadataDecorator(base).render());
    }

    @Test
    @DisplayName("BibliographyDecorator debe agregar la bibliografía")
    void testBibliographyDecorator() {
        assertEquals(BASE + "\nBibliografía: Baldor", new BibliographyDecorator(base).render());
    }

    // --- Composición ---

    @Test
    @DisplayName("Los decoradores deben poder componerse para armar una vista de revisor")
    void testComposition() {
        QuestionView reviewerView = new BibliographyDecorator(new MetadataDecorator(
                new AnswerKeyDecorator(new ContextDecorator(base))));

        assertEquals("Contexto: Aritmética elemental\n" + BASE
                        + "\nRespuesta correcta: 4\nJustificación: 2 + 2 = 4"
                        + "\nCompetencia: Razonamiento cuantitativo | Tema: Aritmética / Suma | Dificultad: Fácil"
                        + "\nBibliografía: Baldor",
                reviewerView.render());
        assertSame(question, reviewerView.getQuestion());
    }

    @Test
    @DisplayName("El orden de composición debe determinar el orden de las secciones")
    void testCompositionOrder() {
        String keyThenBib = new BibliographyDecorator(new AnswerKeyDecorator(base)).render();
        String bibThenKey = new AnswerKeyDecorator(new BibliographyDecorator(base)).render();

        assertTrue(keyThenBib.indexOf("Respuesta correcta") < keyThenBib.indexOf("Bibliografía"));
        assertTrue(bibThenKey.indexOf("Bibliografía") < bibThenKey.indexOf("Respuesta correcta"));
    }

    @Test
    @DisplayName("Una vista de estudiante no debe revelar la respuesta correcta")
    void testStudentViewHidesAnswer() {
        String studentView = new ContextDecorator(base).render();
        assertFalse(studentView.contains("Respuesta correcta"));
        assertFalse(studentView.contains("Justificación"));
    }

    // --- Datos faltantes ---

    @Test
    @DisplayName("Los decoradores deben omitir su sección si el dato está vacío")
    void testBlankFieldsAreSkipped() {
        Question minimal = new Question.Builder()
                .id("P-2").nombre("Mínima").pregunta("¿?")
                .distractors(new QuestionDistractors("a", "b", "c", "d"))
                .build();
        QuestionView minimalBase = new BasicQuestionView(minimal);
        String expected = minimalBase.render();

        QuestionView decorated = new BibliographyDecorator(new MetadataDecorator(
                new AnswerKeyDecorator(new ContextDecorator(minimalBase))));

        assertEquals(expected, decorated.render());
    }

    @Test
    @DisplayName("MetadataDecorator debe mostrar el tema sin subtema si este falta")
    void testMetadataWithoutSubtopic() {
        question.setSubtema(null);
        assertTrue(new MetadataDecorator(base).render().contains("Tema: Aritmética | Dificultad: Fácil"));
    }

    @Test
    @DisplayName("No debe permitir vistas o decoradores sobre valores nulos")
    void testNullArguments() {
        assertThrows(IllegalArgumentException.class, () -> new BasicQuestionView(null));
        assertThrows(IllegalArgumentException.class, () -> new ContextDecorator(null));
        assertThrows(IllegalArgumentException.class, () -> new AnswerKeyDecorator(null));
        assertThrows(IllegalArgumentException.class, () -> new MetadataDecorator(null));
        assertThrows(IllegalArgumentException.class, () -> new BibliographyDecorator(null));
    }
}
