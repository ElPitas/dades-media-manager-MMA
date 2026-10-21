package cat.institutmarianao.mediamanager.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An in-memory catalogue that assigns stable codes in insertion order.
 *
 * <p>Each instance starts empty at code one. No state is static, restored from
 * files, or saved between application executions. Creating fresh objects in the
 * same order therefore produces the same codes in each fresh catalogue.
 *
 * <p>This class is intended for single-threaded use. Persistence and output-file
 * management belong to separate classes.
 */
public final class Catalog {
    private final LinkedHashMap<Integer, MediaContent> contents = new LinkedHashMap<>();
    private long nextCode = 1;

    /**
     * Creates an empty catalogue whose first assigned code will be one.
     */
    public Catalog() {
    }

    /**
     * Adds previously uncoded content and assigns its permanent internal code.
     * Distinct objects with identical metadata are accepted. Validation failures
     * leave both the catalogue and the next available code unchanged.
     *
     * @param content content whose current code is zero
     * @return the newly assigned positive code
     * @throws NullPointerException if content is null
     * @throws IllegalStateException if content already has a code, including one
     *         assigned by another catalogue, or no positive int codes remain
     */
    public int addContent(MediaContent content) {
        Objects.requireNonNull(content, "content");
        if (content.getCode() != 0) {
            throw new IllegalStateException("Content already belongs to a catalogue");
        }
        // A long counter can represent the exhausted state without wrapping to a
        // negative int after assigning Integer.MAX_VALUE.
        if (nextCode > Integer.MAX_VALUE) {
            throw new IllegalStateException("No internal catalogue codes remain");
        }
        int code = (int) nextCode;
        content.assignCode(code);
        contents.put(code, content);
        nextCode++;
        return code;
    }

    /**
     * Looks up content without changing catalogue state.
     * @param code requested internal code; nonpositive codes cannot be assigned
     * @return the matching content, or empty for any code not present
     */
    public Optional<MediaContent> findByCode(int code) {
        return Optional.ofNullable(contents.get(code));
    }

    /**
     * Returns an unmodifiable snapshot in insertion order.
     * Later insertions do not change the list; descriptive edits to the contained
     * objects remain visible because the snapshot is not a deep copy.
     * @return a shallow snapshot of all catalogue contents
     */
    public List<MediaContent> getContents() {
        return List.copyOf(contents.values());
    }

    /**
     * Returns the number of contents successfully added.
     * @return current catalogue size
     */
    public int size() {
        return contents.size();
    }
}
