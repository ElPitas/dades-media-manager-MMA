package cat.institutmarianao.mediamanager.model;

/**
 * An episode identified by an immutable number within its season.
 * Title and runtime can change without changing that number.
 */
public final class Episode {
    private final int number;
    private String title;
    private int runtime;

    /**
     * Creates an episode.
     * @param number positive episode number; gaps in numbering are allowed
     * @param title nonblank title, preserved without normalization
     * @param runtime positive runtime in minutes
     * @throws NullPointerException if title is null
     * @throws IllegalArgumentException if title is blank or a number is not positive
     */
    public Episode(int number, String title, int runtime) {
        this.number = MediaContent.requirePositive(number, "number");
        this.title = MediaContent.requireText(title, "title");
        this.runtime = MediaContent.requirePositive(runtime, "runtime");
    }

    /**
     * Returns the stable number within the season.
     * @return positive episode number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the episode title.
     * @return nonblank title text
     */
    public String getTitle() {
        return title;
    }

    /**
     * Changes the title, preserving the supplied text.
     * @param title new nonblank title
     * @throws NullPointerException if title is null
     * @throws IllegalArgumentException if title is blank
     */
    public void setTitle(String title) {
        this.title = MediaContent.requireText(title, "title");
    }

    /**
     * Returns the episode duration.
     * @return positive runtime in minutes
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * Changes the duration used by subsequent season and series calculations.
     * @param runtime new positive runtime in minutes
     * @throws IllegalArgumentException if runtime is not positive
     */
    public void setRuntime(int runtime) {
        this.runtime = MediaContent.requirePositive(runtime, "runtime");
    }
}
