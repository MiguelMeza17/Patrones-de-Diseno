package co.edu.unicauca.patterns.facade;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionService;
import co.edu.unicauca.domain.User;
import co.edu.unicauca.patterns.adapter.IQuestionSource;
import co.edu.unicauca.patterns.builder.QuestionBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Facade: unifica el acceso a {@link QuestionService} (y, a través de él, al
 * {@link co.edu.unicauca.domain.QuestionRepository}) junto con la importación
 * de preguntas externas vía {@link IQuestionSource}, detrás de una API simple.
 * <p>
 * El cliente no necesita conocer QuestionService, el repositorio ni el Adapter
 * por separado: crea preguntas con {@link QuestionBuilder}, importa fuentes
 * externas, y realiza las demás operaciones del banco de preguntas que requiere
 * el taller, todo a través de esta única clase.
 */
public class QuestionBankFacade {
    private final QuestionService questionService;

    public QuestionBankFacade(QuestionService questionService) {
        if (questionService == null) {
            throw new IllegalArgumentException("El QuestionService no puede ser nulo.");
        }
        this.questionService = questionService;
    }

    /**
     * Punto de entrada para crear una pregunta vía Builder.
     */
    public QuestionBuilder newQuestionBuilder() {
        return new QuestionBuilder();
    }

    /**
     * Construye la pregunta a partir del builder, la valida y la guarda (HU01).
     *
     * @throws IllegalArgumentException si la pregunta no cumple la validación estructural
     */
    public Question createQuestion(QuestionBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException("El builder no puede ser nulo.");
        }
        Question question = builder.build();
        questionService.createQuestion(question);
        return question;
    }

    /**
     * Importa preguntas desde una fuente externa (p. ej. {@code ExternalQuestionAdapter})
     * y las guarda aplicando la misma validación estructural que una pregunta creada
     * manualmente. Las preguntas que no pasan la validación de negocio se registran en
     * {@link ImportResult#getErrors()} y no se guardan; los errores de conversión del
     * propio Adapter (formato externo malformado) no los ve este método y deben
     * consultarse directamente en la fuente si la expone (p. ej. {@code getErrors()}).
     */
    public ImportResult importExternalQuestions(IQuestionSource source) {
        if (source == null) {
            throw new IllegalArgumentException("La fuente de preguntas no puede ser nula.");
        }
        List<Question> candidates = source.getQuestions();
        List<Question> imported = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Question candidate : candidates) {
            try {
                questionService.createQuestion(candidate);
                imported.add(candidate);
            } catch (IllegalArgumentException e) {
                String id = candidate.getId() != null ? candidate.getId() : "(sin id)";
                errors.add(id + ": " + e.getMessage());
            }
        }
        return new ImportResult(imported, errors);
    }

    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    public Question getQuestionById(String id) {
        return questionService.getQuestionById(id);
    }

    public boolean submitForReview(String questionId, String autorLogin) {
        return questionService.submitForReview(questionId, autorLogin);
    }

    public List<Question> getQuestionsByAuthor(String autorLogin) {
        return questionService.getQuestionsByAuthor(autorLogin);
    }

    public List<Question> getQuestionsPendingReview() {
        return questionService.getQuestionsPendingReview();
    }

    public boolean assignReviewers(String questionId, List<User> reviewers) {
        return questionService.assignReviewers(questionId, reviewers);
    }

    public boolean updateQuestionState(String questionId, String newState) {
        return questionService.updateQuestionState(questionId, newState);
    }
}
