package cat.institutmarianao.mediamanager.persistence.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cat.institutmarianao.mediamanager.model.Movie;
import cat.institutmarianao.mediamanager.persistence.ExerciseNotImplementedException;

/**
 * Student exercise for importing films_sax.xml using SAX.
 * File access must use traditional Java I/O. This class does not manage a Catalog.
 */
public class SaxMovieImporter {
    /**
     * Creates a stateless importer without opening any files.
     */
    public SaxMovieImporter() {
    }

    /**
     * Imports the source using SAX, as required by Task 1.
     *
     * <p>Return a non-null list of newly constructed domain objects in document
     * order, with internal codes left at zero. Preserve all required descriptive
     * information and any complete series, season and episode hierarchy.
     * A valid empty source produces an empty list. Malformed or invalid data
     * must be reported, not silently discarded.
     *
     * <p>Use traditional Java I/O, respect the source encoding (UTF-8 for the
     * supplied files), close opened resources and do not print to the console.
     * Do not depend on Catalog or assign internal codes. Binding classes, where
     * required, are part of the student exercise and must remain outside the model.
     *
     * @param input source file; never null
     * @return newly constructed, uncoded contents in source order; never null
     * @throws NullPointerException if input is null after implementation
     * @throws IOException if reading, parsing, binding, configuration or data
     *         validation fails; preserve the cause and useful file context
     * @throws ExerciseNotImplementedException while this exercise is unfinished
     */
    public List<Movie> importMovies(File input) throws IOException {
    	
    	
    	
        // TODO Task 1: Implement the SaxMovieImporter operation.
        throw new ExerciseNotImplementedException("Task 1: SaxMovieImporter");
    }
}
