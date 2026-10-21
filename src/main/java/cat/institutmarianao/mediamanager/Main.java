package cat.institutmarianao.mediamanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import cat.institutmarianao.mediamanager.model.Catalog;
import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.model.Movie;
import cat.institutmarianao.mediamanager.model.Series;
import cat.institutmarianao.mediamanager.persistence.ExerciseNotImplementedException;
import cat.institutmarianao.mediamanager.persistence.binary.BinaryCatalogDao;
import cat.institutmarianao.mediamanager.persistence.binary.BinaryCatalogRecord;
import cat.institutmarianao.mediamanager.persistence.json.GsonCatalogImporter;
import cat.institutmarianao.mediamanager.persistence.json.JacksonCatalogExporter;
import cat.institutmarianao.mediamanager.persistence.json.JacksonCatalogImporter;
import cat.institutmarianao.mediamanager.persistence.text.TextCatalogExporter;
import cat.institutmarianao.mediamanager.persistence.xml.DomCatalogExporter;
import cat.institutmarianao.mediamanager.persistence.xml.DomMovieImporter;
import cat.institutmarianao.mediamanager.persistence.xml.JaxbCatalogImporter;
import cat.institutmarianao.mediamanager.persistence.xml.SaxMovieImporter;
import cat.institutmarianao.mediamanager.persistence.xml.StaxSeriesImporter;

/**
 * Teacher-provided smoke-test runner for the six persistence assignments.
 *
 * <p>Run from the Maven project root. Every invocation creates a fresh in-memory
 * Catalog and imports the selected source files again. Existing output files
 * never initialize application state and are not deleted at startup.
 * Only successfully imported objects enter the catalogue. Selected exports use
 * its actual contents, even when other importers are unfinished or fail.
 *
 * <p>This runner coordinates public APIs and checks basic contracts. It contains
 * no student persistence algorithms and does not prove detailed output-format
 * correctness; that belongs to a later solution-validation phase.
 */
public final class Main {
    private static final String NOT_REQUESTED = "Not requested.";

    private final Catalog catalog = new Catalog();
    private final File inputDirectory;
    private final File outputDirectory;
    private final Map<ImportTask, Status> importStatuses = new EnumMap<>(ImportTask.class);

    private Main(File projectRoot) {
        inputDirectory = new File(projectRoot, "data/input");
        outputDirectory = new File(projectRoot, "data/output");
        for (ImportTask task : ImportTask.values()) {
            importStatuses.put(task, Status.NOT_SELECTED);
        }
    }

    /**
     * Runs all tasks, one numbered task, or one individual importer.
     * Failures are reported per operation so independent exercises can continue.
     *
     * @param args no arguments, all, 1 through 6, 1-dom, 1-sax, 1-stax,
     *         1-jaxb, 2-gson or 2-jackson; all paths are relative to the project root
     */
    public static void main(String[] args) {
        String mode = null;
        if (args != null && args.length == 0) {
            mode = "all";
        } else if (args != null && args.length == 1) {
            mode = args[0];
        }
        if (!isValidMode(mode)) {
            System.out.println("Usage: Main [all|1|2|3|4|5|6|1-dom|1-sax|1-stax|1-jaxb|2-gson|2-jackson]");
            return;
        }
        new Main(new File(".")).run(mode);
    }

    private static boolean isValidMode(String mode) {
        return mode != null && List.of("all", "1", "2", "3", "4", "5", "6",
                "1-dom", "1-sax", "1-stax", "1-jaxb", "2-gson", "2-jackson").contains(mode);
    }

    private void run(String mode) {
        System.out.println("Media Manager - mode " + mode);
        System.out.println("Starting Catalog: " + catalog.size() + " contents (fresh in-memory state).");
        for (ImportTask task : ImportTask.values()) {
            File input = new File(inputDirectory, task.filename);
            if (isImportSelected(mode, task)) {
                importStatuses.put(task, runImport(task, input));
            } else {
                report(task.id + " import", input, Status.NOT_SELECTED, NOT_REQUESTED);
            }
        }
        printCatalogSummary();
        printImportSummary();

        File text = new File(outputDirectory, "list.txt");
        runExport(mode, "3", "Task 3 text export", text,
                () -> new TextCatalogExporter().exportCatalog(text, catalog.getContents()));
        File xml = new File(outputDirectory, "unified_catalog.xml");
        runExport(mode, "4", "Task 4 DOM XML export", xml,
                () -> new DomCatalogExporter().exportCatalog(xml, catalog.getContents()));
        File json = new File(outputDirectory, "unified_catalog.json");
        runExport(mode, "5", "Task 5 Jackson JSON export", json,
                () -> new JacksonCatalogExporter().exportCatalog(json, catalog.getContents()));
        runBinary(mode);
    }

    private boolean isImportSelected(String mode, ImportTask task) {
        if (mode.equals(task.id)) {
            return true;
        }
        if (mode.equals("1")) {
            return task.id.startsWith("1-");
        }
        return List.of("all", "2", "3", "4", "5", "6").contains(mode);
    }

    private Status runImport(ImportTask task, File input) {
        String label = task.id + " import";
        try {
            List<? extends MediaContent> imported = switch (task) {
                case DOM -> new DomMovieImporter().importMovies(input);
                case SAX -> new SaxMovieImporter().importMovies(input);
                case STAX -> new StaxSeriesImporter().importSeries(input);
                case JAXB -> new JaxbCatalogImporter().importContents(input);
                case GSON -> new GsonCatalogImporter().importContents(input);
                case JACKSON -> new JacksonCatalogImporter().importContents(input);
            };
            validateImported(task, imported);
            // Check the whole batch before registration: a malformed importer
            // result must not consume catalogue codes during basic validation.
            for (MediaContent content : imported) {
                catalog.addContent(content);
            }
            report(label, input, Status.COMPLETED, "Imported " + imported.size() + " contents.");
            for (MediaContent content : imported) {
                System.out.printf("  code=%d type=%s title=%s director=%s runtime=%d min%n",
                        content.getCode(), content.getType(), content.getTitle(),
                        content.getDirector(), content.getTotalRuntime());
            }
            return Status.COMPLETED;
        } catch (ExerciseNotImplementedException unfinished) {
            report(label, input, Status.NOT_IMPLEMENTED, unfinished.getMessage());
            return Status.NOT_IMPLEMENTED;
        } catch (IOException | RuntimeException error) {
            report(label, input, Status.ERROR, describe(error));
            return Status.ERROR;
        }
    }

    private void validateImported(ImportTask task, List<? extends MediaContent> imported) {
        Objects.requireNonNull(imported, "Importer returned a null list");
        Set<MediaContent> identities = Collections.newSetFromMap(new IdentityHashMap<>());
        for (MediaContent content : imported) {
            Objects.requireNonNull(content, "Importer returned a null content");
            require(content.getCode() == 0, "Imported content already has a code");
            require(identities.add(content), "Importer returned the same object more than once");
            require(content instanceof Movie || content instanceof Series, "Unknown content class");
            if (task == ImportTask.DOM || task == ImportTask.SAX) {
                require(content instanceof Movie, "Movie importer returned a series");
            } else if (task == ImportTask.STAX) {
                require(content instanceof Series, "Series importer returned a movie");
            }
        }
    }

    private void printCatalogSummary() {
        long movies = catalog.getContents().stream().filter(Movie.class::isInstance).count();
        long series = catalog.getContents().stream().filter(Series.class::isInstance).count();
        System.out.printf("Catalog: %d contents (%d movies, %d series).%n", catalog.size(), movies, series);
    }

    private void printImportSummary() {
        List<String> results = new ArrayList<>();
        boolean incomplete = false;
        for (ImportTask task : ImportTask.values()) {
            Status status = importStatuses.get(task);
            results.add(task.id + "=" + status);
            if (status == Status.NOT_IMPLEMENTED || status == Status.ERROR) {
                incomplete = true;
            }
        }
        System.out.println("Imports: " + String.join(", ", results));
        if (incomplete && catalog.size() > 0) {
            System.out.println("Partial import: selected exports will use the "
                    + catalog.size() + " available contents.");
        }
    }

    private boolean runExport(String mode, String task, String label, File output,
            IOAction operation) {
        if (!mode.equals("all") && !mode.equals(task)) {
            report(label, output, Status.NOT_SELECTED, NOT_REQUESTED);
            return false;
        }
        if (catalog.size() == 0) {
            report(label, output, Status.BLOCKED, "No contents are available: Catalog is empty.");
            return false;
        }
        return execute(label, output, () -> {
            ensureOutputDirectory();
            operation.run();
        });
    }

    private void ensureOutputDirectory() throws IOException {
        if (!outputDirectory.isDirectory() && !outputDirectory.mkdirs()
                && !outputDirectory.isDirectory()) {
            throw new IOException("Cannot create output directory: " + outputDirectory);
        }
    }

    private boolean execute(String label, File file, IOAction operation) {
        try {
            operation.run();
            // A current invocation must succeed. The presence of an old output
            // file is never used as evidence that an operation was completed.
            report(label, file, Status.COMPLETED, "Operation and basic checks completed.");
            return true;
        } catch (ExerciseNotImplementedException unfinished) {
            report(label, file, Status.NOT_IMPLEMENTED, unfinished.getMessage());
        } catch (IOException | RuntimeException error) {
            report(label, file, Status.ERROR, describe(error));
        }
        return false;
    }

    private void runBinary(String mode) {
        File binary = new File(outputDirectory, "catalog.dat");
        BinaryCatalogDao dao = new BinaryCatalogDao();
        boolean created = runExport(mode, "6", "Task 6 binary creation", binary,
                () -> dao.createCatalog(binary, catalog.getContents()));
        if (!mode.equals("all") && !mode.equals("6")) {
            report("Task 6 binary lookup", binary, Status.NOT_SELECTED, NOT_REQUESTED);
            return;
        }
        if (!created) {
            report("Task 6 binary lookup", binary, Status.BLOCKED,
                    "Binary creation did not complete in this execution.");
            return;
        }

        runRepresentativeLookup(dao, binary, 'P');
        runRepresentativeLookup(dao, binary, 'S');
        int absentCode = Math.addExact(catalog.size(), 1);
        execute("Task 6 lookup absent code " + absentCode, binary, () -> {
            Optional<BinaryCatalogRecord> found = dao.findByCode(binary, absentCode);
            Objects.requireNonNull(found, "Binary lookup returned a null Optional");
            require(found.isEmpty(), "Absent code must return Optional.empty()");
            System.out.println("  No record found, as expected.");
        });
    }

    private void runRepresentativeLookup(BinaryCatalogDao dao, File binary, char type) {
        Optional<MediaContent> representative = catalog.getContents().stream()
                .filter(content -> content.getType() == type).findFirst();
        if (representative.isEmpty()) {
            report("Task 6 " + type + " lookup", binary, Status.NOT_SELECTED,
                    "No " + (type == 'P' ? "movie" : "series") + " content is available in Catalog.");
            return;
        }
        MediaContent expected = representative.orElseThrow();
        execute("Task 6 lookup code " + expected.getCode(), binary, () -> {
            Optional<BinaryCatalogRecord> found = dao.findByCode(binary, expected.getCode());
            verifyRecord(expected, found);
            System.out.println("  " + found.orElseThrow());
        });
    }

    private void verifyRecord(MediaContent expected, Optional<BinaryCatalogRecord> found) {
        Objects.requireNonNull(found, "Binary lookup returned a null Optional");
        require(found.isPresent(), "Expected binary record was not returned");
        BinaryCatalogRecord binaryRecord = found.orElseThrow();
        int seasons = expected instanceof Series series ? series.getSeasonCount() : 0;
        int episodes = expected instanceof Series series ? series.getEpisodeCount() : 0;
        String context = " for code " + expected.getCode();
        require(binaryRecord.getCode() == expected.getCode(), "Binary code mismatch" + context);
        require(binaryRecord.getTitle().equals(expected.getTitle()), "Binary title mismatch" + context);
        require(binaryRecord.getType() == expected.getType(), "Binary type mismatch" + context);
        require(binaryRecord.getReleaseDate().equals(expected.getReleaseDate()), "Binary date mismatch" + context);
        require(binaryRecord.getSeasons() == seasons, "Binary season count mismatch" + context);
        require(binaryRecord.getEpisodes() == episodes, "Binary episode count mismatch" + context);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static String describe(Exception error) {
        return error.getClass().getSimpleName() + ": " + error.getMessage();
    }

    private static void report(String operation, File file, Status status, String detail) {
        System.out.printf("[%s] %s | %s | %s%n", status, operation, file, detail);
    }

    private enum Status {
        COMPLETED, NOT_IMPLEMENTED, ERROR, BLOCKED, NOT_SELECTED
    }

    private enum ImportTask {
        DOM("1-dom", "movies_dom.xml"),
        SAX("1-sax", "films_sax.xml"),
        STAX("1-stax", "series_stax.xml"),
        JAXB("1-jaxb", "catalog_jaxb.xml"),
        GSON("2-gson", "contents_gson.json"),
        JACKSON("2-jackson", "contents_jackson.json");

        private final String id;
        private final String filename;

        ImportTask(String id, String filename) {
            this.id = id;
            this.filename = filename;
        }
    }

    @FunctionalInterface
    private interface IOAction {
        void run() throws IOException;
    }
}
