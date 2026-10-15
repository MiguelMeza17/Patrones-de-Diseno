package co.edu.unicauca.patterns.builder;

import co.edu.unicauca.domain.Question;
import co.edu.unicauca.domain.QuestionDistractors;

/**
 * Builder: construye instancias de {@link Question} de forma fluida, como clase
 * independiente del producto (a diferencia de {@link Question.Builder}, que sigue
 * existiendo como builder anidado para no romper a los consumidores actuales).
 * <p>
 * No valida reglas de negocio: esa responsabilidad es de
 * {@link co.edu.unicauca.domain.IQuestionValidator}. Este builder solo ensambla
 * los datos y, si no se indica un estado, construye la pregunta en estado Borrador,
 * listo para integrarse con QuestionBankFacade.
 */
public class QuestionBuilder {
    private String id;
    private String nombre;
    private String contexto;
    private String pregunta;
    private QuestionDistractors distractors;
    private String respuestaCorrecta;
    private String justificacion;
    private String bibliografia;
    private String competencia;
    private String tema;
    private String subtema;
    private String dificultad;
    private String estado;
    private String autorLogin;

    public QuestionBuilder id(String id) {
        this.id = id;
        return this;
    }

    public QuestionBuilder nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public QuestionBuilder contexto(String contexto) {
        this.contexto = contexto;
        return this;
    }

    public QuestionBuilder pregunta(String pregunta) {
        this.pregunta = pregunta;
        return this;
    }

    public QuestionBuilder distractors(QuestionDistractors distractors) {
        this.distractors = distractors;
        return this;
    }

    public QuestionBuilder opciones(String optionA, String optionB, String optionC, String optionD) {
        this.distractors = new QuestionDistractors(optionA, optionB, optionC, optionD);
        return this;
    }

    public QuestionBuilder respuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
        return this;
    }

    public QuestionBuilder justificacion(String justificacion) {
        this.justificacion = justificacion;
        return this;
    }

    public QuestionBuilder bibliografia(String bibliografia) {
        this.bibliografia = bibliografia;
        return this;
    }

    public QuestionBuilder competencia(String competencia) {
        this.competencia = competencia;
        return this;
    }

    public QuestionBuilder tema(String tema) {
        this.tema = tema;
        return this;
    }

    public QuestionBuilder subtema(String subtema) {
        this.subtema = subtema;
        return this;
    }

    public QuestionBuilder dificultad(String dificultad) {
        this.dificultad = dificultad;
        return this;
    }

    public QuestionBuilder estado(String estado) {
        this.estado = estado;
        return this;
    }

    public QuestionBuilder autorLogin(String autorLogin) {
        this.autorLogin = autorLogin;
        return this;
    }

    public Question build() {
        Question question = new Question();
        question.setId(id);
        question.setNombre(nombre);
        question.setContexto(contexto);
        question.setPregunta(pregunta);
        question.setDistractors(distractors);
        question.setRespuestaCorrecta(respuestaCorrecta);
        question.setJustificacion(justificacion);
        question.setBibliografia(bibliografia);
        question.setCompetencia(competencia);
        question.setTema(tema);
        question.setSubtema(subtema);
        question.setDificultad(dificultad);
        question.setAutorLogin(autorLogin);
        question.setEstado(estado != null ? estado : Question.STATE_BORRADOR);
        return question;
    }
}
