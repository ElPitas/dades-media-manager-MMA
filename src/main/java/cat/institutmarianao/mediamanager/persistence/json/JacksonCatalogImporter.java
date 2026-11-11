
package cat.institutmarianao.mediamanager.persistence.json;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import cat.institutmarianao.mediamanager.dto.JacksonCatalogDTO;
import cat.institutmarianao.mediamanager.dto.JacksonCatalogDTO.ChapterDTO;
import cat.institutmarianao.mediamanager.dto.JacksonCatalogDTO.ContentRecordDTO;
import cat.institutmarianao.mediamanager.dto.JacksonCatalogDTO.MovieDetailsDTO;
import cat.institutmarianao.mediamanager.dto.JacksonCatalogDTO.SeasonDTO;
import cat.institutmarianao.mediamanager.dto.JacksonCatalogDTO.SeriesDetailsDTO;
import cat.institutmarianao.mediamanager.model.Episode;
import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.model.Movie;
import cat.institutmarianao.mediamanager.model.Season;
import cat.institutmarianao.mediamanager.model.Series;

public class JacksonCatalogImporter {

	private final ObjectMapper mapper;

	public JacksonCatalogImporter() {
		mapper = new ObjectMapper();
	}

	public List<MediaContent> importContents(File input) throws IOException {

		Objects.requireNonNull(input, "input");

		List<MediaContent> contents = new ArrayList<>();

		JacksonCatalogDTO catalog = mapper.readValue(input, JacksonCatalogDTO.class);

		if (catalog == null || catalog.getRecords() == null) {
			throw new IllegalArgumentException("El archivo JSON no contiene registros");
		}

		for (ContentRecordDTO record : catalog.getRecords()) {

			if (record == null) {
				throw new IllegalArgumentException("El catálogo contiene un registro nulo");
			}

			MediaContent content = convertRecord(record);

			contents.add(content);
		}

		return contents;
	}

	private MediaContent convertRecord(ContentRecordDTO record) {

		requireText(record.getExternalId(), "externalId");

		String title = requireText(record.getTitle(), "title");

		String director = requireText(record.getDirector(), "director");

		String genre = requireText(record.getGenre(), "genre");

		String platform = requireText(record.getPlatform(), "platform");

		LocalDate releaseDate;

		try {

			releaseDate = LocalDate.parse(requireText(record.getReleaseDate(), "releaseDate"));

		} catch (DateTimeParseException e) {

			throw new IllegalArgumentException("Fecha de estreno incorrecta: " + record.getReleaseDate(), e);
		}

		String mediaType = requireText(record.getMediaType(), "mediaType");

		switch (mediaType) {

		case "MOVIE":

			MovieDetailsDTO movieDetails = record.getMovieDetails();

			if (movieDetails == null) {
				throw new IllegalArgumentException("Faltan los detalles de la película: " + title);
			}

			int duration = requirePositive(movieDetails.getDurationMinutes(), "durationMinutes");

			return new Movie(title, director, genre, releaseDate, platform, duration);

		case "SERIES":

			SeriesDetailsDTO seriesDetails = record.getSeriesDetails();

			if (seriesDetails == null || seriesDetails.getSeasons() == null) {
				throw new IllegalArgumentException("Faltan las temporadas de la serie: " + title);
			}

			Series series = new Series(title, director, genre, releaseDate, platform);

			for (SeasonDTO seasonDTO : seriesDetails.getSeasons()) {

				if (seasonDTO == null) {
					throw new IllegalArgumentException("La serie contiene una temporada nula");
				}

				Season season = new Season(requirePositive(seasonDTO.getSeasonNumber(), "seasonNumber"));

				if (seasonDTO.getChapters() == null) {
					throw new IllegalArgumentException("La temporada no contiene capítulos");
				}

				for (ChapterDTO chapterDTO : seasonDTO.getChapters()) {

					if (chapterDTO == null) {
						throw new IllegalArgumentException("La temporada contiene un capítulo nulo");
					}

					Episode episode = new Episode(requirePositive(chapterDTO.getChapterNumber(), "chapterNumber"),
							requireText(chapterDTO.getTitle(), "chapter.title"),
							requirePositive(chapterDTO.getDurationMinutes(), "chapter.durationMinutes"));

					season.addEpisode(episode);
				}

				series.addSeason(season);
			}

			return series;

		default:

			throw new IllegalArgumentException("Tipo de contenido desconocido: " + mediaType);
		}
	}

	private static String requireText(String value, String field) {

		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("El campo " + field + " está vacío o no existe");
		}

		return value;
	}

	private static int requirePositive(Integer value, String field) {

		if (value == null || value <= 0) {
			throw new IllegalArgumentException("El campo " + field + " debe ser positivo");
		}

		return value;
	}

}
