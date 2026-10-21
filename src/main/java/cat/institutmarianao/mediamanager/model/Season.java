package cat.institutmarianao.mediamanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A numbered season containing episodes in insertion order.
 * Episode numbers are unique within this season, but need not be consecutive.
 */
public final class Season {
    private final int number;
    private final List<Episode> episodes = new ArrayList<>();

    /**
     * Creates an empty season.
     * @param number positive season number
     * @throws IllegalArgumentException if number is not positive
     */
    public Season(int number) {
        this.number = MediaContent.requirePositive(number, "number");
    }

    /**
     * Returns the stable season number.
     * @return positive season number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns an unmodifiable snapshot in insertion order.
     * Later additions do not change this list; its episode objects remain mutable.
     * @return a shallow snapshot of the episodes
     */
    public List<Episode> getEpisodes() {
        return List.copyOf(episodes);
    }

    /**
     * Appends an episode without sorting the season.
     * @param episode episode with a number not already present in this season
     * @throws NullPointerException if episode is null
     * @throws IllegalArgumentException if its number is already present
     */
    public void addEpisode(Episode episode) {
        Objects.requireNonNull(episode, "episode");
        for (Episode existing : episodes) {
            if (existing.getNumber() == episode.getNumber()) {
                throw new IllegalArgumentException("Duplicate episode number: " + episode.getNumber());
            }
        }
        episodes.add(episode);
    }

    /**
     * Counts the episodes currently stored, rather than the highest episode number.
     * @return current episode count
     */
    public int getEpisodeCount() {
        return episodes.size();
    }

    /**
     * Sums current episode durations, so edits are reflected immediately.
     * @return total runtime in minutes, or zero for an empty season
     * @throws ArithmeticException if the total exceeds the long range
     */
    public long getTotalRuntime() {
        long total = 0;
        for (Episode episode : episodes) {
            total = Math.addExact(total, episode.getRuntime());
        }
        return total;
    }
}
