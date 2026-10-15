package co.edu.unicauca.patterns.state;

import co.edu.unicauca.domain.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionStateTest {

    private Question question;

    @BeforeEach
    void setUp() {
        question = new Question.Builder().id("P-1").nombre("Pregunta").build();
    }

    // --- Estado inicial ---

    @Test
    @DisplayName("Una pregunta nueva debe nacer en Borrador")
    void testInitialStateIsDraft() {
        assertSame(DraftState.INSTANCE, question.getState());
        assertEquals("Borrador", question.getEstado());
    }

    // --- Transiciones válidas ---

    @Test
    @DisplayName("Borrador → Pendiente de revisión")
    void testDraftToPendingReview() {
        question.submitForReview();
        assertSame(PendingReviewState.INSTANCE, question.getState());
    }

    @Test
    @DisplayName("Borrador → Eliminada")
    void testDraftToDeleted() {
        question.delete();
        assertSame(DeletedState.INSTANCE, question.getState());
    }

    @Test
    @DisplayName("Pendiente de revisión → Borrador")
    void testPendingReviewToDraft() {
        question.submitForReview();
        question.returnToDraft();
        assertSame(DraftState.INSTANCE, question.getState());
    }

    @Test
    @DisplayName("Pendiente de revisión → Eliminada")
    void testPendingReviewToDeleted() {
        question.submitForReview();
        question.delete();
        assertSame(DeletedState.INSTANCE, question.getState());
    }

    @Test
    @DisplayName("Eliminada → Borrador (restaurar)")
    void testDeletedToDraft() {
        question.delete();
        question.restore();
        assertSame(DraftState.INSTANCE, question.getState());
    }

    // --- Transiciones inválidas ---

    @Test
    @DisplayName("Borrador no puede devolverse a Borrador ni restaurarse")
    void testInvalidFromDraft() {
        assertThrows(IllegalStateException.class, question::returnToDraft);
        assertThrows(IllegalStateException.class, question::restore);
        assertSame(DraftState.INSTANCE, question.getState());
    }

    @Test
    @DisplayName("Pendiente de revisión no puede reenviarse a revisión ni restaurarse")
    void testInvalidFromPendingReview() {
        question.submitForReview();
        IllegalStateException ex = assertThrows(IllegalStateException.class, question::submitForReview);
        assertEquals("Solo se puede enviar a revisión una pregunta en estado Borrador.", ex.getMessage());
        assertThrows(IllegalStateException.class, question::restore);
        assertSame(PendingReviewState.INSTANCE, question.getState());
    }

    @Test
    @DisplayName("Eliminada no puede enviarse a revisión, devolverse a Borrador ni eliminarse de nuevo")
    void testInvalidFromDeleted() {
        question.delete();
        assertThrows(IllegalStateException.class, question::submitForReview);
        assertThrows(IllegalStateException.class, question::returnToDraft);
        assertThrows(IllegalStateException.class, question::delete);
        assertSame(DeletedState.INSTANCE, question.getState());
    }

    // --- Permisos por estado ---

    @Test
    @DisplayName("Solo Borrador permite enviar a revisión y solo Pendiente permite asignar revisores")
    void testPermissionsByState() {
        assertTrue(DraftState.INSTANCE.canSubmitForReview());
        assertFalse(PendingReviewState.INSTANCE.canSubmitForReview());
        assertFalse(DeletedState.INSTANCE.canSubmitForReview());

        assertFalse(DraftState.INSTANCE.canAssignReviewers());
        assertTrue(PendingReviewState.INSTANCE.canAssignReviewers());
        assertFalse(DeletedState.INSTANCE.canAssignReviewers());
    }

    // --- Transición por nombre destino (usada por QuestionService) ---

    @Test
    @DisplayName("changeState debe aplicar la transición correspondiente al estado destino")
    void testChangeStateValid() {
        question.changeState(Question.STATE_PENDIENTE_REVISION);
        assertSame(PendingReviewState.INSTANCE, question.getState());
        question.changeState(Question.STATE_BORRADOR);
        assertSame(DraftState.INSTANCE, question.getState());
        question.changeState(Question.STATE_ELIMINADA);
        assertSame(DeletedState.INSTANCE, question.getState());
        question.changeState(Question.STATE_BORRADOR);
        assertSame(DraftState.INSTANCE, question.getState());
    }

    @Test
    @DisplayName("changeState debe rechazar transiciones no permitidas, al mismo estado o a estados desconocidos")
    void testChangeStateInvalid() {
        assertThrows(IllegalStateException.class, () -> question.changeState(Question.STATE_BORRADOR));

        question.delete();
        assertThrows(IllegalStateException.class, () -> question.changeState(Question.STATE_PENDIENTE_REVISION));

        assertThrows(IllegalArgumentException.class, () -> question.changeState("Aprobada"));
        assertSame(DeletedState.INSTANCE, question.getState());
    }

    @Test
    @DisplayName("fromName debe resolver los tres estados y rechazar nombres desconocidos")
    void testFromName() {
        assertSame(DraftState.INSTANCE, QuestionStates.fromName("Borrador"));
        assertSame(PendingReviewState.INSTANCE, QuestionStates.fromName("Pendiente de revisión"));
        assertSame(DeletedState.INSTANCE, QuestionStates.fromName("Eliminada"));
        assertThrows(IllegalArgumentException.class, () -> QuestionStates.fromName("borrador"));
        assertThrows(IllegalArgumentException.class, () -> QuestionStates.fromName(null));
    }
}
