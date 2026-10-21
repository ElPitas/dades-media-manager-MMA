package cat.institutmarianao.mediamanager.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Common descriptive data for a movie or series.
 *
 * <p>New contents have code zero. Only the catalogue assigns a positive internal
 * code, independently of identifiers in input documents. Descriptive data remains
 * editable after assignment. Instances are intended for single-threaded use.
 */
public abstract class MediaContent {
    private int code;
    private String title;
    private String director;
    private String genre;
    private LocalDate releaseDate;
    private String platform;

    /**
     * Creates content without an internal catalogue code.
     *
     * @param title nonblank title, preserved without normalization
     * @param director nonblank director name
     * @param genre nonblank genre
     * @param releaseDate release date; future dates are allowed
     * @param platform nonblank platform name
     * @throws NullPointerException if any argument is null
     * @throws IllegalArgumentException if any text argument is blank
     */
    protected MediaContent(String title, String director, String genre,
            LocalDate releaseDate, String platform) {
        this.title = requireText(title, "title");
        this.director = requireText(director, "director");
        this.genre = requireText(genre, "genre");
        this.releaseDate = Objects.requireNonNull(releaseDate, "releaseDate");
        this.platform = requireText(platform, "platform");
    }

    /**
     * Returns the stable internal identifier.
     * @return zero before catalogue insertion, otherwise a positive code
     */
    public final int getCode() {
        return code;
    }

    /**
     * Assigns the internal identifier once. Package visibility keeps this operation
     * outside the API used by persistence classes.
     * @param code positive internal code
     * @throws IllegalArgumentException if the supplied code is not positive
     * @throws IllegalStateException if this content already has a code
     */
    final void assignCode(int code) {
        requirePositive(code, "code");
        if (this.code != 0) {
            throw new IllegalStateException("Content already has an internal code");
        }
        this.code = code;
    }

    /**
     * Returns the content title.
     * @return the original, nonblank title text
     */
    public String getTitle() {
        return title;
    }

    /**
     * Changes the title without trimming or limiting its length.
     * @param title new nonblank title
     * @throws NullPointerException if title is null
     * @throws IllegalArgumentException if title is blank
     */
    public void setTitle(String title) {
        this.title = requireText(title, "title");
    }

    /**
     * Returns the director name.
     * @return nonblank director name
     */
    public String getDirector() {
        return director;
    }

    /**
     * Changes the director name, preserving its text.
     * @param director new nonblank director name
     * @throws NullPointerException if director is null
     * @throws IllegalArgumentException if director is blank
     */
    public void setDirector(String director) {
        this.director = requireText(director, "director");
    }

    /**
     * Returns the genre.
     * @return nonblank genre text
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Changes the genre without restricting it to a predefined vocabulary.
     * @param genre new nonblank genre
     * @throws NullPointerException if genre is null
     * @throws IllegalArgumentException if genre is blank
     */
    public void setGenre(String genre) {
        this.genre = requireText(genre, "genre");
    }

    /**
     * Returns the release date.
     * @return non-null release date
     */
    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    /**
     * Changes the release date; dates in the future are valid.
     * @param releaseDate new release date
     * @throws NullPointerException if releaseDate is null
     */
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = Objects.requireNonNull(releaseDate, "releaseDate");
    }

    /**
     * Returns the platform.
     * @return nonblank platform name
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Changes the platform, preserving its text.
     * @param platform new nonblank platform name
     * @throws NullPointerException if platform is null
     * @throws IllegalArgumentException if platform is blank
     */
    public void setPlatform(String platform) {
        this.platform = requireText(platform, "platform");
    }

    /**
     * Identifies the content using the codes required by the binary assignment.
     * @return 'P' for a movie or 'S' for a series
     */
    public abstract char getType();

    /**
     * Calculates the current runtime without caching derived values.
     * @return total runtime in minutes; zero for an empty series
     * @throws ArithmeticException if an aggregated runtime exceeds the long range
     */
    public abstract long getTotalRuntime();

    /**
     * Validates shared textual fields without altering the supplied value.
     */
    static String requireText(String value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    /**
     * Validates the positive numbers used for runtimes and identifiers.
     */
    static int requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }
}
