package co.edu.unicauca.patterns.facade;

import co.edu.unicauca.access.QuestionImplRepository;
import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionRepository;
import co.edu.unicauca.domain.QuestionService;
import co.edu.unicauca.domain.Role;
import co.edu.unicauca.domain.User;
import co.edu.unicauca.domain.UserStatus;
import co.edu.unicauca.patterns.adapter.ExternalQuestion;
import co.edu.unicauca.patterns.adapter.ExternalQuestionAdapter;
import co.edu.unicauca.patterns.adapter.IQuestionSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionBankFacadeTest {

    private QuestionRepository repository;
    private QuestionService questionService;
    private QuestionBankFacade facade;

    @BeforeEach
    void setUp() {
        repository = new QuestionImplRepository();
        questionService = new QuestionService(repository);
        facade = new QuestionBankFacade(questionService);
    }

    @Test
    @DisplayName("No debe permitir construir el facade con un QuestionService nulo")
    void testConstructorRejectsNullService() {
        assertThrows(IllegalArgumentException.class, () -> new QuestionBankFacade(null));
    }

    @Test
    @DisplayName("Debe crear y guardar una pregunta válida a través del Builder")
    void testCreateQuestionViaBuilder() {
        Question created = facade.createQuestion(
                facade.newQuestionBuilder()
                        .id("FAC-001")
                        .nombre("Pregunta vía facade")
                        .contexto("Contexto")
                        .pregunta("¿Pregunta?")
                        .opciones("A", "B", "C", "D")
                        .respuestaCorrecta("A")
                        .justificacion("Justificación")
                        .bibliografia("Bibliografía")
                        .competencia("Competencia")
                        .tema("Tema")
                        .subtema("Subtema")
                        .dificultad("Media")
                        .autorLogin("autor1")
        );

        assertEquals(Question.STATE_BORRADOR, created.getEstado());
        assertNotNull(facade.getQuestionById("FAC-001"));
    }

    @Test
    @DisplayName("Debe propagar el error de validación si la pregunta construida es inválida")
    void testCreateQuestionPropagatesValidationErrors() {
        assertThrows(IllegalArgumentException.class, () ->
                facade.createQuestion(facade.newQuestionBuilder().id("FAC-002")));
    }

    @Test
    @DisplayName("Debe rechazar un builder nulo al crear pregunta")
    void testCreateQuestionRejectsNullBuilder() {
        assertThrows(IllegalArgumentException.class, () -> facade.createQuestion(null));
    }

    @Test
    @DisplayName("Debe listar todas las preguntas delegando en QuestionService")
    void testGetAllQuestionsDelegatesToService() {
        assertEquals(questionService.getAllQuestions().size(), facade.getAllQuestions().size());
        assertFalse(facade.getAllQuestions().isEmpty());
    }

    @Test
    @DisplayName("Debe enviar a revisión una pregunta propia en Borrador")
    void testSubmitForReviewDelegatesToService() {
        Question created = facade.createQuestion(
                facade.newQuestionBuilder()
                        .id("FAC-003")
                        .nombre("Pregunta a revisar")
                        .contexto("Contexto")
                        .pregunta("¿Pregunta?")
                        .opciones("A", "B", "C", "D")
                        .respuestaCorrecta("B")
                        .justificacion("Justificación")
                        .bibliografia("Bibliografía")
                        .competencia("Competencia")
                        .tema("Tema")
                        .subtema("Subtema")
                        .dificultad("Fácil")
                        .autorLogin("autor1")
        );

        boolean result = facade.submitForReview(created.getId(), "autor1");

        assertTrue(result);
        assertEquals(Question.STATE_PENDIENTE_REVISION, facade.getQuestionById("FAC-003").getEstado());
    }

    @Test
    @DisplayName("Debe asignar revisores a una pregunta pendiente de revisión")
    void testAssignReviewersDelegatesToService() {
        Question created = facade.createQuestion(
                facade.newQuestionBuilder()
                        .id("FAC-004")
                        .nombre("Pregunta para asignar revisor")
                        .contexto("Contexto")
                        .pregunta("¿Pregunta?")
                        .opciones("A", "B", "C", "D")
                        .respuestaCorrecta("C")
                        .justificacion("Justificación")
                        .bibliografia("Bibliografía")
                        .competencia("Competencia")
                        .tema("Tema")
                        .subtema("Subtema")
                        .dificultad("Difícil")
                        .autorLogin("autor1")
        );
        facade.submitForReview(created.getId(), "autor1");

        User revisor = new User("revisor1", "Revisor Uno", "revisor1@test.com", Role.REVISOR, UserStatus.ACTIVO, "hash");
        boolean assigned = facade.assignReviewers(created.getId(), List.of(revisor));

        assertTrue(assigned);
    }

    @Test
    @DisplayName("Debe importar preguntas externas válidas a través del Adapter")
    void testImportExternalQuestionsAllValid() {
        ExternalQuestion valid = new ExternalQuestion();
        valid.setCode("EXT-001");
        valid.setTitle("Externa válida");
        valid.setContext("Contexto externo");
        valid.setStatement("¿Pregunta externa?");
        valid.setOptions(Arrays.asList("Op1", "Op2", "Op3", "Op4"));
        valid.setCorrectOptionIndex(0);
        valid.setRationale("Justificación externa");
        valid.setReferences("Referencia externa");
        valid.setCompetency("Competencia externa");
        valid.setTopic("Tema externo");
        valid.setSubtopic("Subtema externo");
        valid.setDifficultyLevel(2);

        IQuestionSource source = new ExternalQuestionAdapter(() -> Collections.singletonList(valid));

        ImportResult result = facade.importExternalQuestions(source);

        assertFalse(result.hasErrors());
        assertEquals(1, result.getImportedCount());
        assertNotNull(facade.getQuestionById("EXT-001"));
    }

    @Test
    @DisplayName("Debe registrar en ImportResult las preguntas externas que no pasan la validación de negocio")
    void testImportExternalQuestionsReportsValidationErrors() {
        ExternalQuestion sinJustificacion = new ExternalQuestion();
        sinJustificacion.setCode("EXT-002");
        sinJustificacion.setTitle("Externa incompleta");
        sinJustificacion.setContext("Contexto externo");
        sinJustificacion.setStatement("¿Pregunta externa incompleta?");
        sinJustificacion.setOptions(Arrays.asList("Op1", "Op2", "Op3", "Op4"));
        sinJustificacion.setCorrectOptionIndex(1);
        // Sin rationale/references/competency/topic/subtopic: debe fallar la validación estructural.
        sinJustificacion.setDifficultyLevel(1);

        IQuestionSource source = new ExternalQuestionAdapter(() -> Collections.singletonList(sinJustificacion));

        ImportResult result = facade.importExternalQuestions(source);

        assertTrue(result.hasErrors());
        assertEquals(0, result.getImportedCount());
        assertTrue(result.getErrors().get(0).contains("EXT-002"));
    }

    @Test
    @DisplayName("Debe rechazar una fuente externa nula al importar")
    void testImportExternalQuestionsRejectsNullSource() {
        assertThrows(IllegalArgumentException.class, () -> facade.importExternalQuestions(null));
    }

    @Test
    @DisplayName("Con una fuente vacía, el resultado de importación no debe tener preguntas ni errores")
    void testImportExternalQuestionsWithEmptySource() {
        IQuestionSource emptySource = ArrayList::new;

        ImportResult result = facade.importExternalQuestions(emptySource);

        assertFalse(result.hasErrors());
        assertEquals(0, result.getImportedCount());
    }
}
