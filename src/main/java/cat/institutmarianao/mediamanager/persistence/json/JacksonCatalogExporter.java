
package cat.institutmarianao.mediamanager.persistence.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import cat.institutmarianao.mediamanager.dto.JacksonCatalogExportDTO;
import cat.institutmarianao.mediamanager.dto.JacksonCatalogExportDTO.EpisodeDTO;
import cat.institutmarianao.mediamanager.dto.JacksonCatalogExportDTO.MovieDTO;
import cat.institutmarianao.mediamanager.dto.JacksonCatalogExportDTO.SeasonDTO;
import cat.institutmarianao.mediamanager.dto.JacksonCatalogExportDTO.SeriesDTO;
import cat.institutmarianao.mediamanager.model.Episode;
import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.model.Movie;
import cat.institutmarianao.mediamanager.model.Season;
import cat.institutmarianao.mediamanager.model.Series;

public class JacksonCatalogExporter {

	public JacksonCatalogExporter() {
	}

	public void exportCatalog(File output, Collection<? extends MediaContent> contents) throws IOException {

		Objects.requireNonNull(output, "output");
		Objects.requireNonNull(contents, "contents");

		List<Movie> movies = new ArrayList<>();
		List<Series> series = new ArrayList<>();

		for (MediaContent content : contents) {

			Objects.requireNonNull(content, "content");

			if (content.getCode() <= 0) {
				throw new IllegalArgumentException("El contenido no tiene un código asignado: " + content.getTitle());
			}

			if (content instanceof Movie) {

				movies.add((Movie) content);

			} else if (content instanceof Series) {

				series.add((Series) content);

			} else {

				throw new IllegalArgumentException("Tipo de contenido no reconocido: " + content.getClass().getName());
			}
		}

		Comparator<MediaContent> comparator = Comparator
				.comparing(MediaContent::getTitle, String.CASE_INSENSITIVE_ORDER)
				.thenComparingInt(MediaContent::getCode);

		movies.sort(comparator);
		series.sort(comparator);

		List<MovieDTO> movieDTOs = new ArrayList<>();

		for (Movie movie : movies) {

			MovieDTO movieDTO = new MovieDTO(movie.getCode(), movie.getTitle(), movie.getDirector(), movie.getGenre(),
					movie.getReleaseDate().toString(), movie.getPlatform(), movie.getRuntime());

			movieDTOs.add(movieDTO);
		}

		List<SeriesDTO> seriesDTOs = new ArrayList<>();

		for (Series serie : series) {

			List<SeasonDTO> seasonDTOs = new ArrayList<>();

			List<Season> seasons = new ArrayList<>(serie.getSeasons());

			seasons.sort(Comparator.comparingInt(Season::getNumber));

			for (Season season : seasons) {

				List<EpisodeDTO> episodeDTOs = new ArrayList<>();

				List<Episode> episodes = new ArrayList<>(season.getEpisodes());

				episodes.sort(Comparator.comparingInt(Episode::getNumber));

				for (Episode episode : episodes) {

					EpisodeDTO episodeDTO = new EpisodeDTO(episode.getNumber(), episode.getTitle(),
							episode.getRuntime());

					episodeDTOs.add(episodeDTO);
				}

				SeasonDTO seasonDTO = new SeasonDTO(season.getNumber(), episodeDTOs);

				seasonDTOs.add(seasonDTO);
			}

			SeriesDTO seriesDTO = new SeriesDTO(serie.getCode(), serie.getTitle(), serie.getDirector(),
					serie.getGenre(), serie.getReleaseDate().toString(), serie.getPlatform(), seasonDTOs);

			seriesDTOs.add(seriesDTO);
		}

		JacksonCatalogExportDTO catalogDTO = new JacksonCatalogExportDTO(movieDTOs, seriesDTOs);

		ObjectMapper mapper = new ObjectMapper();

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(output, false), StandardCharsets.UTF_8))) {

			mapper.writerWithDefaultPrettyPrinter().writeValue(writer, catalogDTO);

		} catch (IOException e) {

			throw new IOException("Error al exportar el catálogo JSON: " + output.getCanonicalPath(), e);
		}
	}
}
