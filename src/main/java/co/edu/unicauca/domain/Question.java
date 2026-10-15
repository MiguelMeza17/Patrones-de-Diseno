package co.edu.unicauca.domain;

import co.edu.unicauca.patterns.state.DeletedState;
import co.edu.unicauca.patterns.state.DraftState;
import co.edu.unicauca.patterns.state.PendingReviewState;
import co.edu.unicauca.patterns.state.QuestionState;
import co.edu.unicauca.patterns.state.QuestionStates;

import java.util.ArrayList;
import java.util.List;

/**
 * Pregunta del banco. Es el contexto del patrón State: su ciclo de vida
 * se delega en un {@link QuestionState}.
 */
public class Question {
    public static final String STATE_BORRADOR = DraftState.NAME;
    public static final String STATE_PENDIENTE_REVISION = PendingReviewState.NAME;
    public static final String STATE_ELIMINADA = DeletedState.NAME;

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
    private QuestionState state = DraftState.INSTANCE;
    private String autorLogin;
    private List<String> revisoresAsignados = new ArrayList<>();

    public Question() {
    }

    public Question(String id, String nombre, String pregunta, QuestionDistractors distractors, String respuestaCorrecta, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.pregunta = pregunta;
        this.distractors = distractors;
        this.respuestaCorrecta = respuestaCorrecta;
        setEstado(estado);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public QuestionDistractors getDistractors() {
        return distractors;
    }

    public void setDistractors(QuestionDistractors distractors) {
        this.distractors = distractors;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getBibliografia() {
        return bibliografia;
    }

    public void setBibliografia(String bibliografia) {
        this.bibliografia = bibliografia;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getSubtema() {
        return subtema;
    }

    public void setSubtema(String subtema) {
        this.subtema = subtema;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public String getEstado() {
        return state.getName();
    }

    /**
     * Fija el estado a partir de su nombre, sin validar la transición.
     * Pensado para reconstruir preguntas desde la persistencia; para cambiar
     * de estado en el flujo de negocio use las operaciones de transición.
     *
     * @param estado nombre del estado; si es nulo o vacío queda en Borrador
     * @throws IllegalArgumentException si el nombre no corresponde a ningún estado
     */
    public void setEstado(String estado) {
        this.state = estado == null || estado.isBlank() ? DraftState.INSTANCE : QuestionStates.fromName(estado);
    }

    public QuestionState getState() {
        return state;
    }

    /** Borrador → Pendiente de revisión. */
    public void submitForReview() {
        state = state.submitForReview();
    }

    /** Pendiente de revisión → Borrador. */
    public void returnToDraft() {
        state = state.returnToDraft();
    }

    /** Borrador o Pendiente de revisión → Eliminada. */
    public void delete() {
        state = state.delete();
    }

    /** Eliminada → Borrador. */
    public void restore() {
        state = state.restore();
    }

    /**
     * Aplica la transición necesaria para llegar al estado indicado.
     *
     * @throws IllegalArgumentException si el estado destino no existe
     * @throws IllegalStateException    si la transición no está permitida desde el estado actual
     */
    public void changeState(String targetEstado) {
        state = QuestionStates.transition(state, targetEstado);
    }

    public String getAutorLogin() {
        return autorLogin;
    }

    public void setAutorLogin(String autorLogin) {
        this.autorLogin = autorLogin;
    }

    public List<String> getRevisoresAsignados() {
        return revisoresAsignados;
    }

    public void setRevisoresAsignados(List<String> revisoresAsignados) {
        this.revisoresAsignados = revisoresAsignados != null ? revisoresAsignados : new ArrayList<>();
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }

    public static class Builder {
        private final Question question = new Question();

        public Builder id(String id) {
            question.id = id;
            return this;
        }

        public Builder nombre(String nombre) {
            question.nombre = nombre;
            return this;
        }

        public Builder contexto(String contexto) {
            question.contexto = contexto;
            return this;
        }

        public Builder pregunta(String pregunta) {
            question.pregunta = pregunta;
            return this;
        }

        public Builder distractors(QuestionDistractors distractors) {
            question.distractors = distractors;
            return this;
        }

        public Builder respuestaCorrecta(String respuestaCorrecta) {
            question.respuestaCorrecta = respuestaCorrecta;
            return this;
        }

        public Builder justificacion(String justificacion) {
            question.justificacion = justificacion;
            return this;
        }

        public Builder bibliografia(String bibliografia) {
            question.bibliografia = bibliografia;
            return this;
        }

        public Builder competencia(String competencia) {
            question.competencia = competencia;
            return this;
        }

        public Builder tema(String tema) {
            question.tema = tema;
            return this;
        }

        public Builder subtema(String subtema) {
            question.subtema = subtema;
            return this;
        }

        public Builder dificultad(String dificultad) {
            question.dificultad = dificultad;
            return this;
        }

        public Builder estado(String estado) {
            question.setEstado(estado);
            return this;
        }

        public Builder autorLogin(String autorLogin) {
            question.autorLogin = autorLogin;
            return this;
        }

        public Question build() {
            return question;
        }
    }
}
