package cat.institutmarianao.mediamanager.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class HierarchyTest {
    private Series series() {
        return new Series("Breaking Bad", "Vince Gilligan", "Drama",
                LocalDate.of(2008, 1, 20), "Netflix");
    }

    @Test
    void emptySeriesAndSeasonsHaveZeroCountsAndRuntime() {
        Series series = series();
        Season season = new Season(3);
        assertEquals('S', series.getType());
        assertEquals(0, series.getSeasonCount());
        assertEquals(0, series.getEpisodeCount());
        assertEquals(0L, series.getTotalRuntime());
        assertEquals(0, season.getEpisodeCount());
        assertEquals(0L, season.getTotalRuntime());
        series.addSeason(season);
        assertEquals(1, series.getSeasonCount());
        assertEquals(0, series.getEpisodeCount());
        assertEquals(0L, series.getTotalRuntime());
    }

    @Test
    void runtimeAndCountsFollowAllCurrentEpisodes() {
        Series series = series();
        Season first = new Season(1);
        Episode pilot = new Episode(1, "Pilot", 58);
        first.addEpisode(pilot);
        first.addEpisode(new Episode(2, "Cat's in the Bag...", 48));
        Season second = new Season(2);
        second.addEpisode(new Episode(1, "Seven Thirty-Seven", 47));
        series.addSeason(first);
        series.addSeason(second);

        assertEquals(106L, first.getTotalRuntime());
        assertEquals(153L, series.getTotalRuntime());
        assertEquals(2, series.getSeasonCount());
        assertEquals(3, series.getEpisodeCount());

        pilot.setRuntime(60);
        assertEquals(108L, first.getTotalRuntime());
        assertEquals(155L, series.getTotalRuntime());
        second.addEpisode(new Episode(4, "Later episode", 45));
        assertEquals(4, series.getEpisodeCount());
        assertEquals(200L, series.getTotalRuntime());
    }

    @Test
    void aggregateRuntimesExceedIntRangeWithoutWrapping() {
        Series series = series();
        Season season = new Season(1);
        season.addEpisode(new Episode(1, "First", Integer.MAX_VALUE));
        season.addEpisode(new Episode(2, "Second", Integer.MAX_VALUE));
        series.addSeason(season);
        assertEquals(2L * Integer.MAX_VALUE, season.getTotalRuntime());
        assertEquals(2L * Integer.MAX_VALUE, series.getTotalRuntime());
        Season second = new Season(2);
        second.addEpisode(new Episode(1, "Third", Integer.MAX_VALUE));
        series.addSeason(second);
        assertEquals(3L * Integer.MAX_VALUE, series.getTotalRuntime());
    }

    @Test
    void rejectsDuplicateNumbersWithinTheirParentButAllowsGaps() {
        Series series = series();
        Season season = new Season(8);
        Episode episode = new Episode(9, "Later", 20);
        season.addEpisode(episode);
        season.addEpisode(new Episode(2, "Earlier", 30));
        assertThrows(IllegalArgumentException.class, () -> season.addEpisode(episode));
        assertThrows(IllegalArgumentException.class,
                () -> season.addEpisode(new Episode(9, "Duplicate", 40)));
        assertEquals(List.of(9, 2), season.getEpisodes().stream().map(Episode::getNumber).toList());

        series.addSeason(season);
        series.addSeason(new Season(2));
        assertThrows(IllegalArgumentException.class, () -> series.addSeason(season));
        assertThrows(IllegalArgumentException.class, () -> series.addSeason(new Season(8)));
        assertEquals(List.of(8, 2), series.getSeasons().stream().map(Season::getNumber).toList());
        assertEquals(2, series.getSeasonCount());
        assertEquals(2, series.getEpisodeCount());
    }

    @Test
    void numberingIsLocalToEachSeasonAndSeries() {
        Series first = series();
        Series second = series();
        Season firstSeason = new Season(1);
        Season otherSeason = new Season(1);
        firstSeason.addEpisode(new Episode(1, "First", 20));
        otherSeason.addEpisode(new Episode(1, "Other", 30));
        first.addSeason(firstSeason);
        second.addSeason(otherSeason);
        assertEquals(20L, first.getTotalRuntime());
        assertEquals(30L, second.getTotalRuntime());
    }

    @Test
    void rejectsNullChildrenWithoutChangingHierarchy() {
        Series series = series();
        Season season = new Season(1);
        assertThrows(NullPointerException.class, () -> series.addSeason(null));
        assertThrows(NullPointerException.class, () -> season.addEpisode(null));
        assertTrue(series.getSeasons().isEmpty());
        assertTrue(season.getEpisodes().isEmpty());
    }

    @Test
    void hierarchySnapshotsAreUnmodifiableAndDoNotTrackLaterAdditions() {
        Series series = series();
        Season first = new Season(4);
        List<Season> emptySeasons = series.getSeasons();
        List<Episode> emptyEpisodes = first.getEpisodes();
        Episode episode = new Episode(3, "Original", 20);
        first.addEpisode(episode);
        series.addSeason(first);

        List<Season> seasons = series.getSeasons();
        List<Episode> episodes = first.getEpisodes();
        series.addSeason(new Season(2));
        first.addEpisode(new Episode(1, "Another", 25));

        assertTrue(emptySeasons.isEmpty());
        assertTrue(emptyEpisodes.isEmpty());
        assertEquals(List.of(first), seasons);
        assertEquals(List.of(episode), episodes);
        assertThrows(UnsupportedOperationException.class, () -> seasons.add(new Season(5)));
        assertThrows(UnsupportedOperationException.class, () -> seasons.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> seasons.set(0, new Season(6)));
        assertThrows(UnsupportedOperationException.class,
                () -> episodes.add(new Episode(4, "New", 20)));
        assertThrows(UnsupportedOperationException.class, () -> episodes.remove(0));
        assertThrows(UnsupportedOperationException.class,
                () -> episodes.set(0, new Episode(4, "New", 20)));
        episode.setTitle("Edited");
        episode.setRuntime(35);
        assertEquals("Edited", episodes.getFirst().getTitle());
        assertEquals(60L, seasons.getFirst().getTotalRuntime());
        assertEquals(List.of(4, 2), series.getSeasons().stream().map(Season::getNumber).toList());
    }

    @Test
    void seasonAndEpisodeNumbersHaveNoPublicSetters() {
        assertThrows(NoSuchMethodException.class,
                () -> Season.class.getMethod("setNumber", int.class));
        assertThrows(NoSuchMethodException.class,
                () -> Episode.class.getMethod("setNumber", int.class));
    }
}
