package cat.institutmarianao.mediamanager.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ModelValidationTest {
    private Movie movie() {
        return new Movie("Interstellar", "Christopher Nolan", "Ciència-ficció",
                LocalDate.of(2014, 11, 7), "Max", 169);
    }

    @Test
    void movieRuntimeAndMutableMetadataRemainConsistentWithStableCode() {
        Movie movie = movie();
        new Catalog().addContent(movie);
        movie.setTitle("  La vida és bella  ");
        movie.setDirector("Roberto Benigni");
        movie.setGenre("Comèdia dramàtica");
        movie.setPlatform("Filmin");
        movie.setReleaseDate(LocalDate.of(2100, 1, 1));
        movie.setRuntime(116);
        assertEquals("  La vida és bella  ", movie.getTitle());
        assertEquals("Roberto Benigni", movie.getDirector());
        assertEquals("Comèdia dramàtica", movie.getGenre());
        assertEquals("Filmin", movie.getPlatform());
        assertEquals(LocalDate.of(2100, 1, 1), movie.getReleaseDate());
        assertEquals(116, movie.getRuntime());
        assertEquals(116L, movie.getTotalRuntime());
        assertEquals('P', movie.getType());
        assertEquals(1, movie.getCode());
    }

    @Test
    void longUnicodeTitlesAndFutureDatesAreNotRestrictedByBinaryLayout() {
        String title = "  Les de l’hoquei — ".repeat(5);
        Movie movie = new Movie(title, "Patricia Font", "Drama",
                LocalDate.of(2100, 1, 1), "3Cat", 100);
        Episode episode = new Episode(1, title, 50);
        assertTrue(title.length() > 40);
        assertEquals(title, movie.getTitle());
        assertEquals(title, episode.getTitle());
        assertEquals(LocalDate.of(2100, 1, 1), movie.getReleaseDate());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t\n", "\u2003"})
    void descriptiveSettersRejectInvalidTextWithoutChangingExistingValues(String invalid) {
        Movie movie = movie();
        List<Consumer<String>> setters = List.of(
                movie::setTitle, movie::setDirector, movie::setGenre, movie::setPlatform);
        Class<? extends RuntimeException> error =
                invalid == null ? NullPointerException.class : IllegalArgumentException.class;
        for (Consumer<String> setter : setters) {
            assertThrows(error, () -> setter.accept(invalid));
        }
        assertEquals("Interstellar", movie.getTitle());
        assertEquals("Christopher Nolan", movie.getDirector());
        assertEquals("Ciència-ficció", movie.getGenre());
        assertEquals("Max", movie.getPlatform());
        Episode episode = new Episode(1, "Pilot", 58);
        assertThrows(error, () -> episode.setTitle(invalid));
        assertEquals("Pilot", episode.getTitle());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t\n"})
    void constructorsValidateEveryRequiredTextField(String invalid) {
        Class<? extends RuntimeException> error =
                invalid == null ? NullPointerException.class : IllegalArgumentException.class;
        LocalDate date = LocalDate.of(2024, 3, 1);
        assertThrows(error, () -> new Movie(invalid, "Director", "Genre", date, "Max", 100));
        assertThrows(error, () -> new Movie("Title", invalid, "Genre", date, "Max", 100));
        assertThrows(error, () -> new Movie("Title", "Director", invalid, date, "Max", 100));
        assertThrows(error, () -> new Movie("Title", "Director", "Genre", date, invalid, 100));
        assertThrows(error, () -> new Series(invalid, "Director", "Genre", date, "Max"));
        assertThrows(error, () -> new Series("Title", invalid, "Genre", date, "Max"));
        assertThrows(error, () -> new Series("Title", "Director", invalid, date, "Max"));
        assertThrows(error, () -> new Series("Title", "Director", "Genre", date, invalid));
        assertThrows(error, () -> new Episode(1, invalid, 50));
    }

    @Test
    void requiredDatesCannotBeNullAndFailedEditsPreserveTheDate() {
        Movie movie = movie();
        assertThrows(NullPointerException.class,
                () -> new Movie("Title", "Director", "Genre", null, "Max", 100));
        assertThrows(NullPointerException.class,
                () -> new Series("Title", "Director", "Genre", null, "Max"));
        assertThrows(NullPointerException.class, () -> movie.setReleaseDate(null));
        assertEquals(LocalDate.of(2014, 11, 7), movie.getReleaseDate());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void numberingAndRuntimesMustBePositive(int invalid) {
        assertThrows(IllegalArgumentException.class, () -> new Season(invalid));
        assertThrows(IllegalArgumentException.class, () -> new Episode(invalid, "Pilot", 58));
        assertThrows(IllegalArgumentException.class, () -> new Episode(1, "Pilot", invalid));
        assertThrows(IllegalArgumentException.class, () -> new Movie(
                "Title", "Director", "Genre", LocalDate.of(2020, 1, 1), "Max", invalid));

        Movie movie = movie();
        Episode episode = new Episode(1, "Pilot", 58);
        assertThrows(IllegalArgumentException.class, () -> movie.setRuntime(invalid));
        assertThrows(IllegalArgumentException.class, () -> episode.setRuntime(invalid));
        assertEquals(169L, movie.getTotalRuntime());
        assertEquals(58, episode.getRuntime());
    }

    @Test
    void episodeTextEditsPreserveNumberAndExactText() {
        Episode episode = new Episode(7, "Old", 20);
        episode.setTitle("  Estació de servei  ");
        episode.setRuntime(18);
        assertEquals(7, episode.getNumber());
        assertEquals("  Estació de servei  ", episode.getTitle());
        assertEquals(18, episode.getRuntime());
    }

    // Literal examples from the input documents check domain construction without
    // implementing any of the importers reserved for students.
    @ParameterizedTest
    @CsvSource({
        "Interstellar, Christopher Nolan, Ciència-ficció, 2014-11-07, Max, 169",
        "Arrival, Denis Villeneuve, Ciència-ficció, 2016-11-11, Filmin, 116",
        "La vida és bella, Roberto Benigni, Comèdia dramàtica, 1997-12-20, Filmin, 116",
        "Dune: Part Two, Denis Villeneuve, Ciència-ficció, 2024-03-01, Max, 166",
        "Arrival, Denis Villeneuve, Science fiction, 2016-11-11, Prime Video, 116"
    })
    void sampleMovieMetadataCanBeRepresented(String title, String director,
            String genre, String date, String platform, int runtime) {
        Movie movie = new Movie(title, director, genre, LocalDate.parse(date), platform, runtime);
        assertEquals(0, movie.getCode());
        assertEquals(title, movie.getTitle());
        assertEquals(genre, movie.getGenre());
        assertEquals(platform, movie.getPlatform());
        assertEquals(runtime, movie.getTotalRuntime());
    }

    private static Stream<Series> sampleSeries() {
        Series stax = new Series("Breaking Bad", "Vince Gilligan", "Drama",
                LocalDate.parse("2008-01-20"), "Netflix");
        Season first = new Season(1);
        first.addEpisode(new Episode(1, "Pilot", 58));
        first.addEpisode(new Episode(2, "Cat's in the Bag...", 48));
        stax.addSeason(first);

        Series jaxb = new Series("Merlí", "Eduard Cortés", "Drama",
                LocalDate.parse("2015-09-14"), "3Cat");
        Season merli = new Season(1);
        merli.addEpisode(new Episode(1, "Els peripatètics", 50));
        merli.addEpisode(new Episode(2, "Plató", 50));
        jaxb.addSeason(merli);

        Series gson = new Series("Chernobyl", "Johan Renck", "Drama històric",
                LocalDate.parse("2019-05-06"), "Max");
        Season chernobyl = new Season(1);
        chernobyl.addEpisode(new Episode(1, "1:23:45", 58));
        chernobyl.addEpisode(new Episode(2, "Please Remain Calm", 65));
        gson.addSeason(chernobyl);

        Series jackson = new Series("The Queen's Gambit", "Scott Frank", "Drama",
                LocalDate.parse("2020-10-23"), "Netflix");
        Season gambit = new Season(1);
        gambit.addEpisode(new Episode(1, "Openings", 59));
        gambit.addEpisode(new Episode(2, "Exchanges", 65));
        jackson.addSeason(gambit);
        return Stream.of(stax, jaxb, gson, jackson);
    }

    @ParameterizedTest
    @MethodSource("sampleSeries")
    void sampleSeriesHierarchiesCanBeRepresented(Series series) {
        assertEquals(0, series.getCode());
        assertEquals('S', series.getType());
        assertEquals(1, series.getSeasonCount());
        assertEquals(2, series.getEpisodeCount());
        Season season = series.getSeasons().getFirst();
        long expected = (long) season.getEpisodes().get(0).getRuntime()
                + season.getEpisodes().get(1).getRuntime();
        assertEquals(expected, series.getTotalRuntime());
        assertEquals(1, new Catalog().addContent(series));
    }
}
