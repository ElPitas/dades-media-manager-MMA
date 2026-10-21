package cat.institutmarianao.mediamanager.model;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class CatalogTest {
    private Movie movie(String title) {
        return new Movie(title, "Bong Joon-ho", "Thriller",
                LocalDate.of(2019, 5, 30), "Filmin", 132);
    }

    @Test
    void assignsCodesToMoviesAndSeriesInInsertionOrder() {
        Catalog catalog = new Catalog();
        Movie first = movie("Parasite");
        Series second = new Series("Dark", "Baran bo Odar", "Misteri",
                LocalDate.of(2017, 12, 1), "Netflix");
        assertEquals(0, first.getCode());
        assertEquals(0, second.getCode());
        assertEquals(1, catalog.addContent(first));
        assertEquals(2, catalog.addContent(second));
        assertEquals(1, first.getCode());
        assertEquals(2, second.getCode());
        assertEquals(List.of(first, second), catalog.getContents());
        assertEquals(2, catalog.size());
    }

    @Test
    void independentCatalogsStartEmptyAndReproduceCodesForFreshObjects() {
        Catalog previousRun = new Catalog();
        assertEquals(0, previousRun.size());
        assertTrue(previousRun.getContents().isEmpty());
        for (String title : List.of("Parasite", "Arrival", "Parasite")) {
            previousRun.addContent(movie(title));
        }

        Catalog nextRun = new Catalog();
        assertEquals(0, nextRun.size());
        assertTrue(nextRun.findByCode(1).isEmpty());
        for (MediaContent oldContent : previousRun.getContents()) {
            Movie freshContent = movie(oldContent.getTitle());
            assertEquals(oldContent.getCode(), nextRun.addContent(freshContent));
            assertNotSame(oldContent, nextRun.findByCode(freshContent.getCode()).orElseThrow());
        }
        assertEquals(3, previousRun.size());
        assertEquals(3, nextRun.size());
    }

    @Test
    void rejectsAlreadyCodedObjectsWithoutConsumingCodes() {
        Catalog first = new Catalog();
        Movie content = movie("Parasite");
        first.addContent(content);
        assertThrows(IllegalStateException.class, () -> first.addContent(content));
        assertThrows(NullPointerException.class, () -> first.addContent(null));
        assertEquals(2, first.addContent(movie("Arrival")));

        Catalog second = new Catalog();
        assertThrows(IllegalStateException.class, () -> second.addContent(content));
        assertTrue(second.getContents().isEmpty());
        assertEquals(1, second.addContent(movie("Parasite")));
        assertEquals(1, content.getCode());
    }

    @Test
    void allowsDistinctObjectsWithIdenticalMetadata() {
        Catalog catalog = new Catalog();
        Movie first = movie("Parasite");
        Movie second = movie("Parasite");
        assertEquals(1, catalog.addContent(first));
        assertEquals(2, catalog.addContent(second));
        assertSame(first, catalog.findByCode(1).orElseThrow());
        assertSame(second, catalog.findByCode(2).orElseThrow());
    }

    @Test
    void missingCodesReturnEmptyWithoutChangingState() {
        Catalog catalog = new Catalog();
        catalog.addContent(movie("Arrival"));
        for (int code : new int[] {Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE}) {
            assertTrue(catalog.findByCode(code).isEmpty());
        }
        assertEquals(1, catalog.size());
        assertEquals(2, catalog.addContent(movie("Parasite")));
    }

    @Test
    void snapshotsAreUnmodifiableOrderedAndIndependentOfLaterInsertions() {
        Catalog catalog = new Catalog();
        List<MediaContent> emptySnapshot = catalog.getContents();
        Movie first = movie("Z");
        Movie second = movie("A");
        catalog.addContent(first);
        List<MediaContent> snapshot = catalog.getContents();
        catalog.addContent(second);

        assertTrue(emptySnapshot.isEmpty());
        assertEquals(List.of(first), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(second));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> snapshot.set(0, second));
        assertEquals(List.of(first, second), catalog.getContents());

        first.setTitle("Edited");
        assertEquals("Edited", snapshot.getFirst().getTitle());
        assertEquals(1, first.getCode());
        assertSame(first, catalog.findByCode(1).orElseThrow());
    }

    @Test
    void codeAssignmentRejectsInvalidValuesAndReassignment() {
        Movie content = movie("Parasite");
        assertThrows(IllegalArgumentException.class, () -> content.assignCode(0));
        assertThrows(IllegalArgumentException.class, () -> content.assignCode(-1));
        assertEquals(0, content.getCode());
        content.assignCode(7);
        assertThrows(IllegalStateException.class, () -> content.assignCode(8));
        assertEquals(7, content.getCode());
    }

    @Test
    void assignmentIsNotPublicAndCodeGetterCannotBeOverridden() throws Exception {
        int modifiers = MediaContent.class.getDeclaredMethod("assignCode", int.class).getModifiers();
        assertFalse(Modifier.isPublic(modifiers));
        assertFalse(Modifier.isProtected(modifiers));
        assertFalse(Modifier.isPrivate(modifiers));
        assertTrue(Modifier.isFinal(MediaContent.class.getMethod("getCode").getModifiers()));
        assertThrows(NoSuchMethodException.class,
                () -> MediaContent.class.getMethod("setCode", int.class));
    }

    @Test
    void maximumIntCodeCanBeAssignedOnceWithoutWrapping() throws Exception {
        Catalog catalog = new Catalog();
        // Reach the boundary without allocating billions of objects or adding
        // a production API that lets callers change the catalogue counter.
        Field counter = Catalog.class.getDeclaredField("nextCode");
        counter.setAccessible(true);
        counter.setLong(catalog, Integer.MAX_VALUE);

        Movie last = movie("Last");
        assertEquals(Integer.MAX_VALUE, catalog.addContent(last));
        assertSame(last, catalog.findByCode(Integer.MAX_VALUE).orElseThrow());

        Movie rejected = movie("Too late");
        assertThrows(IllegalStateException.class, () -> catalog.addContent(rejected));
        assertThrows(IllegalStateException.class, () -> catalog.addContent(rejected));
        assertEquals(0, rejected.getCode());
        assertEquals(1, catalog.size());
        assertEquals(Integer.MAX_VALUE, last.getCode());
    }
}
