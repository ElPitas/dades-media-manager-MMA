package cat.institutmarianao.mediamanager.persistence.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cat.institutmarianao.mediamanager.model.Episode;
import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.model.Movie;
import cat.institutmarianao.mediamanager.model.Season;
import cat.institutmarianao.mediamanager.model.Series;
import cat.institutmarianao.mediamanager.persistence.ExerciseNotImplementedException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Student exercise for importing catalog_jaxb.xml using JAXB with
 * student-created binding classes. File access must use traditional Java I/O.
 * This class does not manage a Catalog.
 */
public class JaxbCatalogImporter {
	/**
	 * Creates a stateless importer without opening any files.
	 */
	public JaxbCatalogImporter() {
	}

	/**
	 * Imports the source using JAXB with student-created binding classes, as
	 * required by Task 1.
	 *
	 * <p>
	 * Return a non-null list of newly constructed domain objects in document order,
	 * with internal codes left at zero. Preserve all required descriptive
	 * information and any complete series, season and episode hierarchy. A valid
	 * empty source produces an empty list. Malformed or invalid data must be
	 * reported, not silently discarded.
	 *
	 * <p>
	 * Use traditional Java I/O, respect the source encoding (UTF-8 for the supplied
	 * files), close opened resources and do not print to the console. Do not depend
	 * on Catalog or assign internal codes. Binding classes, where required, are
	 * part of the student exercise and must remain outside the model.
	 *
	 * @param input source file; never null
	 * @return newly constructed, uncoded contents in source order; never null
	 * @throws NullPointerException            if input is null after implementation
	 * @throws IOException                     if reading, parsing, binding,
	 *                                         configuration or data validation
	 *                                         fails; preserve the cause and useful
	 *                                         file context
	 * @throws ExerciseNotImplementedException while this exercise is unfinished
	 */
	public List<MediaContent> importContents(File input) throws IOException {
		Objects.requireNonNull(input, "input");

		try (FileInputStream stream = new FileInputStream(input)) {

			JAXBContext context = JAXBContext.newInstance(SupplierCatalogXml.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			SupplierCatalogXml catalog = (SupplierCatalogXml) unmarshaller.unmarshal(stream);

			List<MediaContent> contents = new ArrayList<>();

			if (catalog.entries != null) {
				for (SupplierCatalogXml.EntryXml entry : catalog.entries) {
					contents.add(convertEntry(entry));
				}
			}

			return contents;
		} catch (JAXBException | IOException | RuntimeException e) {
			throw new IOException("Error al importar contenidos desde: " + input.getAbsolutePath(), e);
		}
	}

	private MediaContent convertEntry(SupplierCatalogXml.EntryXml entry) {

		SupplierCatalogXml.GeneralInfoXml info = entry.generalInfo;

		if ("movie".equals(entry.category)) {
			return new Movie(info.name, info.director, info.genre, LocalDate.parse(info.releaseDate), info.service,
					entry.details.movie.minutes);
		}

		if ("show".equals(entry.category)) {
			return convertSeries(info, entry.details.show);
		}

		throw new IllegalArgumentException("Categoría de contenido desconocida: " + entry.category);
	}

	private Series convertSeries(SupplierCatalogXml.GeneralInfoXml info, SupplierCatalogXml.ShowXml show) {

		Series series = new Series(info.name, info.director, info.genre, LocalDate.parse(info.releaseDate),
				info.service);

		if (show.seasonBlocks != null && show.seasonBlocks.blocks != null) {

			for (SupplierCatalogXml.BlockXml block : show.seasonBlocks.blocks) {

				Season season = new Season(block.number);

				if (block.episodeList != null && block.episodeList.episodes != null) {

					for (SupplierCatalogXml.EpisodeXml data : block.episodeList.episodes) {

						season.addEpisode(new Episode(data.order, data.name, data.minutes));
					}
				}

				series.addSeason(season);
			}
		}

		return series;
	}

}