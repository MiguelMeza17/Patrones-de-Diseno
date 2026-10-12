package co.edu.unicauca.patterns.facade;

import co.edu.unicauca.domain.Question;

import java.util.Collections;
import java.util.List;

/**
 * Resultado de importar preguntas desde una fuente externa a través del Facade.
 * Separa las preguntas efectivamente importadas de los motivos de rechazo,
 * tanto por conversión (formato externo malformado) como por validación de negocio.
 */
public class ImportResult {
    private final List<Question> imported;
    private final List<String> errors;

    public ImportResult(List<Question> imported, List<String> errors) {
        this.imported = Collections.unmodifiableList(imported);
        this.errors = Collections.unmodifiableList(errors);
    }

    public List<Question> getImported() {
        return imported;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int getImportedCount() {
        return imported.size();
    }
}
