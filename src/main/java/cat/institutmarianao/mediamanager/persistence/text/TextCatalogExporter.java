package cat.institutmarianao.mediamanager.persistence.text;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.persistence.ExerciseNotImplementedException;

/**
 * Student exercise for generating list.txt using sequential text output.
 * The class is independent of Main and must use traditional Java I/O.
 */
public class TextCatalogExporter {
    /**
     * Creates a stateless exporter without opening any files.
     */
    public TextCatalogExporter() {
    }

    /**
     * Replaces the requested file with the catalogue representation for Task 3.
     * Write only code, title and director separated by semicolons, one content per line.
     * Sort all contents together by ascending title, ignoring case, then by
     * ascending internal code for equal titles.
     *
     * <p>Receive already-coded objects and preserve their values, relationships
     * and original collection orders. Write UTF-8 using traditional Java I/O,
     * close resources, and do not print to the console. Replace previous output
     * completely; do not append. The caller creates missing parent directories.
     *
     * @param output destination file; never null
     * @param contents non-null catalogue collection with non-null, coded objects
     * @throws NullPointerException if an argument or content is null after implementation
     * @throws IllegalArgumentException if contents contain unassigned codes
     * @throws IOException if output, configuration or serialization fails;
     *         preserve the cause and useful file context
     * @throws ExerciseNotImplementedException while this exercise is unfinished
     */
    public void exportCatalog(File output, Collection<? extends MediaContent> contents)
            throws IOException {
        // TODO Task 3: Implement the TextCatalogExporter operation.
        throw new ExerciseNotImplementedException("Task 3: TextCatalogExporter");
    }
}
