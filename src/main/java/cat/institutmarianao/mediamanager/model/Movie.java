package cat.institutmarianao.mediamanager.model;

import java.time.LocalDate;

/**
 * A movie with a runtime in whole minutes.
 * Its catalogue identifier is assigned separately from construction.
 */
public final class Movie extends MediaContent {
    private int runtime;

    /**
     * Creates an uncoded movie.
     * @param title nonblank title
     * @param director nonblank director name
     * @param genre nonblank genre
     * @param releaseDate release date, including future dates
     * @param platform nonblank platform name
     * @param runtime positive runtime in minutes
     * @throws NullPointerException if any reference argument is null
     * @throws IllegalArgumentException if text is blank or runtime is not positive
     */
    public Movie(String title, String director, String genre,
            LocalDate releaseDate, String platform, int runtime) {
        super(title, director, genre, releaseDate, platform);
        this.runtime = requirePositive(runtime, "runtime");
    }

    /**
     * Returns the movie duration.
     * @return positive runtime in minutes
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * Changes the movie duration.
     * @param runtime new positive runtime in minutes
     * @throws IllegalArgumentException if runtime is not positive
     */
    public void setRuntime(int runtime) {
        this.runtime = requirePositive(runtime, "runtime");
    }

    /**
     * Returns the movie type required by the binary format.
     * @return 'P'
     */
    @Override
    public char getType() {
        return 'P';
    }

    /**
     * Returns the current movie duration as a long.
     * @return runtime in minutes
     */
    @Override
    public long getTotalRuntime() {
        return runtime;
    }
}
