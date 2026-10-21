package cat.institutmarianao.mediamanager.persistence.xml;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.persistence.ExerciseNotImplementedException;

/**
 * Student exercise for generating unified_catalog.xml using DOM.
 * The class is independent of Main and must use traditional Java I/O.
 */
public class DomCatalogExporter {
    /**
     * Creates a stateless exporter without opening any files.
     */
    public DomCatalogExporter() {
    }

    /**
     * Replaces the requested file with the catalogue representation for Task 4.
     * Use the exact catalog, movies and series structure in Task 4, including empty
     * groups, all required fields, and the complete seasons and episodes.
     *
     * <p>Sort movies and series separately by ascending, case-insensitive title,
     * then ascending internal code for ties. Seasons and episodes must appear in
     * ascending numerical order within their parent. Dates use yyyy-MM-dd and
     * runtimes are whole minutes without units.
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
        // TODO Task 4: Implement the DomCatalogExporter operation.
        throw new ExerciseNotImplementedException("Task 4: DomCatalogExporter");
    }
}
