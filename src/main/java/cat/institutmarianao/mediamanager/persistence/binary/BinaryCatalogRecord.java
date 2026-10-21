package cat.institutmarianao.mediamanager.persistence.binary;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable summary of one binary catalogue record.
 *
 * <p>This conventional data class performs no I/O, byte conversion, positioning
 * or catalogue management. The student DAO supplies already decoded values.
 * Title width is a restriction of this representation, not of the domain model.
 */
public final class BinaryCatalogRecord {
    private final int code;
    private final String title;
    private final char type;
    private final LocalDate releaseDate;
    private final int seasons;
    private final int episodes;

    /**
     * Creates a summary from decoded record values, preserving supplied text.
     * Empty series are allowed; movie season and episode counts must be zero.
     *
     * @param code positive internal code
     * @param title nonblank title of at most 40 UTF-16 code units
     * @param type P for movie or S for series
     * @param releaseDate non-null release date, including future dates
     * @param seasons nonnegative season count
     * @param episodes nonnegative total episode count
     * @throws NullPointerException if title or releaseDate is null
     * @throws IllegalArgumentException if code, title, type or counts violate the contract
     */
    public BinaryCatalogRecord(int code, String title, char type, LocalDate releaseDate,
            int seasons, int episodes) {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(releaseDate, "releaseDate");
        if (code <= 0) {
            throw new IllegalArgumentException("code must be positive");
        }
        if (title.isBlank() || title.length() > 40) {
            throw new IllegalArgumentException("title must be nonblank and at most 40 UTF-16 code units");
        }
        if (type != 'P' && type != 'S') {
            throw new IllegalArgumentException("type must be P or S");
        }
        if (seasons < 0 || episodes < 0) {
            throw new IllegalArgumentException("season and episode counts must not be negative");
        }
        if (type == 'P' && (seasons != 0 || episodes != 0)) {
            throw new IllegalArgumentException("movie season and episode counts must be zero");
        }
        this.code = code;
        this.title = title;
        this.type = type;
        this.releaseDate = releaseDate;
        this.seasons = seasons;
        this.episodes = episodes;
    }

    /**
     * Returns the internal identifier.
     * @return positive catalogue code
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the supplied title without normalization.
     * @return nonblank title of at most 40 UTF-16 code units
     */
    public String getTitle() {
        return title;
    }

    /**
     * Identifies the content category.
     * @return P for a movie or S for a series
     */
    public char getType() {
        return type;
    }

    /**
     * Returns the decoded release date.
     * @return non-null release date
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * Returns the stored season count.
     * @return nonnegative season count, zero for movies
     */
    public int getSeasons() {
        return seasons;
    }

    /**
     * Returns the stored total episode count.
     * @return nonnegative episode count, zero for movies
     */
    public int getEpisodes() {
        return episodes;
    }

    /**
     * Presents all summary fields for human-readable console output.
     * @return a description of this record; not a persistence format
     */
    @Override
    public String toString() {
        return "BinaryCatalogRecord{code=" + code + ", title='" + title + "', type=" + type
                + ", releaseDate=" + releaseDate + ", seasons=" + seasons
                + ", episodes=" + episodes + "}";
    }
}
