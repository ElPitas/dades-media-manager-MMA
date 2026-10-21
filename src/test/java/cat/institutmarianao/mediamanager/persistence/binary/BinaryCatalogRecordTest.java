package cat.institutmarianao.mediamanager.persistence.binary;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Modifier;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class BinaryCatalogRecordTest {
    private final LocalDate date = LocalDate.of(2014, 11, 7);

    @Test
    void movieGettersAndReadableDescriptionExposeAllSixFields() {
        BinaryCatalogRecord record = new BinaryCatalogRecord(1, "Interstellar", 'P', date, 0, 0);
        assertEquals(1, record.getCode());
        assertEquals("Interstellar", record.getTitle());
        assertEquals('P', record.getType());
        assertEquals(date, record.getReleaseDate());
        assertEquals(0, record.getSeasons());
        assertEquals(0, record.getEpisodes());
        for (String field : new String[] {"code=1", "Interstellar", "type=P",
                "releaseDate=2014-11-07", "seasons=0", "episodes=0"}) {
            assertTrue(record.toString().contains(field));
        }
    }

    @Test
    void representsSeriesCountsAndAllowsEmptySeriesAndFutureDates() {
        BinaryCatalogRecord series = new BinaryCatalogRecord(12, "Breaking Bad", 'S', date, 2, 6);
        assertEquals(2, series.getSeasons());
        assertEquals(6, series.getEpisodes());
        LocalDate future = LocalDate.of(2100, 1, 1);
        BinaryCatalogRecord empty = new BinaryCatalogRecord(13, "Future", 'S', future, 0, 0);
        assertEquals(future, empty.getReleaseDate());
        assertEquals(0, empty.getSeasons());
        assertEquals(0, empty.getEpisodes());
    }

    @Test
    void isAConventionalImmutableClassWithExactlySixFields() {
        assertFalse(BinaryCatalogRecord.class.isRecord());
        assertTrue(Modifier.isFinal(BinaryCatalogRecord.class.getModifiers()));
        assertEquals(6, BinaryCatalogRecord.class.getDeclaredFields().length);
        for (var field : BinaryCatalogRecord.class.getDeclaredFields()) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    @Test
    void titleValidationCountsUtf16UnitsAndNeverNormalizesText() {
        String boundary = "😀".repeat(20);
        assertEquals(40, boundary.length());
        assertEquals(boundary, new BinaryCatalogRecord(1, boundary, 'P', date, 0, 0).getTitle());
        String spaced = "  Merlí  ";
        assertEquals(spaced, new BinaryCatalogRecord(1, spaced, 'S', date, 0, 0).getTitle());
        assertThrows(IllegalArgumentException.class,
                () -> new BinaryCatalogRecord(1, "x".repeat(41), 'P', date, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new BinaryCatalogRecord(1, boundary + "x", 'P', date, 0, 0));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsMissingOrBlankTitles(String title) {
        Class<? extends RuntimeException> expected =
                title == null ? NullPointerException.class : IllegalArgumentException.class;
        assertThrows(expected, () -> new BinaryCatalogRecord(1, title, 'P', date, 0, 0));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void rejectsNonpositiveCodes(int code) {
        assertThrows(IllegalArgumentException.class,
                () -> new BinaryCatalogRecord(code, "Title", 'P', date, 0, 0));
    }

    @Test
    void rejectsNullDatesUnknownTypesAndInvalidCounts() {
        assertThrows(NullPointerException.class,
                () -> new BinaryCatalogRecord(1, "Title", 'P', null, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new BinaryCatalogRecord(1, "Title", 'M', date, 0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new BinaryCatalogRecord(1, "Title", 'S', date, -1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new BinaryCatalogRecord(1, "Title", 'S', date, 0, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new BinaryCatalogRecord(1, "Title", 'P', date, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new BinaryCatalogRecord(1, "Title", 'P', date, 0, 1));
    }
}
