package cat.institutmarianao.mediamanager.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A series containing seasons and their episodes.
 * Counts and runtime are calculated from the hierarchy, never stored separately.
 */
public final class Series extends MediaContent {
    private final List<Season> seasons = new ArrayList<>();

    /**
     * Creates an uncoded series with no seasons.
     * @param title nonblank title
     * @param director nonblank director name
     * @param genre nonblank genre
     * @param releaseDate release date, including future dates
     * @param platform nonblank platform name
     * @throws NullPointerException if any argument is null
     * @throws IllegalArgumentException if any text argument is blank
     */
    public Series(String title, String director, String genre,
            LocalDate releaseDate, String platform) {
        super(title, director, genre, releaseDate, platform);
    }

    /**
     * Appends a season without sorting the series.
     * @param season season with a number not already present in this series
     * @throws NullPointerException if season is null
     * @throws IllegalArgumentException if its number is already present
     */
    public void addSeason(Season season) {
        Objects.requireNonNull(season, "season");
        for (Season existing : seasons) {
            if (existing.getNumber() == season.getNumber()) {
                throw new IllegalArgumentException("Duplicate season number: " + season.getNumber());
            }
        }
        seasons.add(season);
    }

    /**
     * Returns an unmodifiable snapshot in insertion order.
     * Later additions do not change this list; its season objects remain mutable.
     * @return a shallow snapshot of the seasons
     */
    public List<Season> getSeasons() {
        return List.copyOf(seasons);
    }

    /**
     * Counts stored seasons, including empty ones.
     * @return current season count
     */
    public int getSeasonCount() {
        return seasons.size();
    }

    /**
     * Counts episodes across all current seasons.
     * @return total episode count, or zero for an empty series
     * @throws ArithmeticException if the count exceeds the int range
     */
    public int getEpisodeCount() {
        int total = 0;
        for (Season season : seasons) {
            total = Math.addExact(total, season.getEpisodeCount());
        }
        return total;
    }

    /**
     * Returns the series type required by the binary format.
     * @return 'S'
     */
    @Override
    public char getType() {
        return 'S';
    }

    /**
     * Sums current season runtimes without caching the result.
     * @return total runtime in minutes, or zero for an empty series
     * @throws ArithmeticException if the total exceeds the long range
     */
    @Override
    public long getTotalRuntime() {
        long total = 0;
        for (Season season : seasons) {
            total = Math.addExact(total, season.getTotalRuntime());
        }
        return total;
    }
}
