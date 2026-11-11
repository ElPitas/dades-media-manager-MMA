package cat.institutmarianao.mediamanager.persistence.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cat.institutmarianao.mediamanager.dto.SupplierCatalogXml;
import cat.institutmarianao.mediamanager.model.Episode;
import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.model.Movie;
import cat.institutmarianao.mediamanager.model.Season;
import cat.institutmarianao.mediamanager.model.Series;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class JaxbCatalogImporter {

	public JaxbCatalogImporter() {
	}

	public List<MediaContent> importContents(File input) throws IOException {

		Objects.requireNonNull(input, "input");

		try (Reader reader = new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8)) {

			JAXBContext context = JAXBContext.newInstance(SupplierCatalogXml.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			SupplierCatalogXml catalog = (SupplierCatalogXml) unmarshaller.unmarshal(reader);

			List<MediaContent> contents = new ArrayList<>();

			if (catalog.getEntries() != null) {
				for (SupplierCatalogXml.EntryXml entry : catalog.getEntries()) {
					contents.add(convertEntry(entry));
				}
			}

			return contents;

		} catch (JAXBException | IOException | RuntimeException e) {
			throw new IOException("Error al importar contenidos desde: " + input.getAbsolutePath(), e);
		}
	}

	private MediaContent convertEntry(SupplierCatalogXml.EntryXml entry) {

		SupplierCatalogXml.GeneralInfoXml info = entry.getGeneralInfo();

		if ("movie".equals(entry.getCategory())) {
			return new Movie(info.getName(), info.getDirector(), info.getGenre(), LocalDate.parse(info.getReleaseDate()), info.getService(),
					entry.getDetails().getMovie().getMinutes());
		}

		if ("show".equals(entry.getCategory())) {
			return convertSeries(entry);
		}

		throw new IllegalArgumentException("Categoría de contenido desconocida: " + entry.getCategory());
	}

	private Series convertSeries(SupplierCatalogXml.EntryXml entry) {

		SupplierCatalogXml.GeneralInfoXml info = entry.getGeneralInfo();

		Series series = new Series(info.getName(), info.getDirector(), info.getGenre(), LocalDate.parse(info.getReleaseDate()),
				info.getService());

		for (SupplierCatalogXml.BlockXml block : entry.getDetails().getShow().getSeasonBlocks().getBlocks()) {

			Season season = new Season(block.getNumber());

			for (SupplierCatalogXml.EpisodeXml data : block.getEpisodeList().getEpisodes()) {
				season.addEpisode(new Episode(data.getOrder(), data.getName(), data.getMinutes()));
			}

			series.addSeason(season);
		}

		return series;
	}
}