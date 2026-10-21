package cat.institutmarianao.mediamanager.persistence;

import java.util.Objects;

/**
 * Marks an intentionally unfinished student exercise.
 * Main handles this marker separately from accidental runtime failures, including
 * ordinary UnsupportedOperationException instances.
 */
public final class ExerciseNotImplementedException extends UnsupportedOperationException {
    private static final long serialVersionUID = 1L;

    /**
     * Identifies an operation that the student has not implemented yet.
     * @param operation nonblank description identifying the task and operation
     * @throws NullPointerException if operation is null
     * @throws IllegalArgumentException if operation is blank
     */
    public ExerciseNotImplementedException(String operation) {
        super("Student exercise not implemented: " + requireOperation(operation));
    }

    private static String requireOperation(String operation) {
        Objects.requireNonNull(operation, "operation");
        if (operation.isBlank()) {
            throw new IllegalArgumentException("operation must not be blank");
        }
        return operation;
    }
}
