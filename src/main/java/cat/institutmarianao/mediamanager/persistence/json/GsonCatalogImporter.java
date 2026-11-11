
package cat.institutmarianao.mediamanager.persistence.json;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cat.institutmarianao.mediamanager.dto.GsonCatalogDTO;
import cat.institutmarianao.mediamanager.model.Episode;
import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.model.Movie;
import cat.institutmarianao.mediamanager.model.Season;
import cat.institutmarianao.mediamanager.model.Series;

public class GsonCatalogImporter {

	public GsonCatalogImporter() {

	}

	public List<MediaContent> importContents(File input) throws IOException {

		Objects.requireNonNull(input, "input");

		// Lista donde guardaremos las películas y series importadas
		List<MediaContent> contenidos = new ArrayList<>();

		// 1. Leer el archivo utilizando Java I/O tradicional
		try (Reader reader = new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8)) {

			// 2. Deserializar el JSON en objetos DTO mediante Gson
			Gson gson = new GsonBuilder().create();

			GsonCatalogDTO.CatalogDTO catalogo = gson.fromJson(reader, GsonCatalogDTO.CatalogDTO.class);

			if (catalogo == null || catalogo.items == null) {
				throw new IllegalArgumentException("El archivo JSON no contiene una lista de items");
			}

			// 3. Recorrer todos los elementos del catálogo
			for (GsonCatalogDTO.ItemDTO item : catalogo.items) {

				if (item == null) {
					throw new IllegalArgumentException("El catálogo contiene un elemento nulo");
				}

				// 4. Convertir cada elemento en Movie o Series
				MediaContent contenido = convertItem(item);

				// Añadir el contenido a la lista
				contenidos.add(contenido);
			}
		}

		// 5. Devolver todos los contenidos importados
		return contenidos;
	}

	/**
	 * Converts an ItemDTO into a Movie or Series.
	 */
	private MediaContent convertItem(GsonCatalogDTO.ItemDTO item) {

		// Comprobar que existen los objetos necesarios
		if (item.credits == null || item.metadata == null || item.content == null) {

			throw new IllegalArgumentException("Faltan datos obligatorios en: " + item.name);
		}

		// Obtener los datos comunes
		String title = requireText(item.name, "name");

		String director = requireText(item.credits.director, "director");

		String genre = requireText(item.metadata.genre, "genre");

		String platform = requireText(item.metadata.availableOn, "availableOn");

		// Convertir la fecha String a LocalDate
		LocalDate releaseDate;

		try {

			releaseDate = LocalDate.parse(requireText(item.metadata.releaseDate, "releaseDate"));

		} catch (java.time.format.DateTimeParseException e) {

			throw new IllegalArgumentException("Fecha de estreno incorrecta: " + item.metadata.releaseDate, e);
		}

		// 5. Identificar el tipo de contenido
		switch (requireText(item.kind, "kind")) {

		case "film":

			// Obtener la duración de la película
			int minutes = requirePositive(item.content.minutes, "minutes");

			// Crear el objeto Movie
			return new Movie(title, director, genre, releaseDate, platform, minutes);

		case "show":

			// Crear el objeto Series
			Series series = new Series(title, director, genre, releaseDate, platform);

			if (item.content.seasons == null) {
				throw new IllegalArgumentException("La serie no contiene temporadas: " + title);
			}

			// 6. Recorrer las temporadas
			for (GsonCatalogDTO.SeasonDTO seasonDTO : item.content.seasons) {

				if (seasonDTO == null) {
					throw new IllegalArgumentException("La serie contiene una temporada nula");
				}

				// Crear una temporada
				Season season = new Season(requirePositive(seasonDTO.index, "season.index"));

				if (seasonDTO.episodes == null) {
					throw new IllegalArgumentException("La temporada no contiene episodios");
				}

				// 7. Recorrer los episodios
				for (GsonCatalogDTO.EpisodeDTO episodeDTO : seasonDTO.episodes) {

					if (episodeDTO == null) {
						throw new IllegalArgumentException("La temporada contiene un episodio nulo");
					}

					// Crear un episodio
					Episode episode = new Episode(requirePositive(episodeDTO.index, "episode.index"),
							requireText(episodeDTO.name, "episode.name"),
							requirePositive(episodeDTO.runtime, "episode.runtime"));

					// Añadir el episodio a su temporada
					season.addEpisode(episode);
				}

				// Añadir la temporada a la serie
				series.addSeason(season);
			}

			return series;

		default:

			throw new IllegalArgumentException("Tipo de contenido desconocido: " + item.kind);
		}
	}

	// =====================================================
	// MÉTODOS AUXILIARES DE VALIDACIÓN
	// =====================================================

	/**
	 * Comprueba que un texto no sea nulo ni esté vacío.
	 */
	private static String requireText(String value, String field) {

		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("El campo " + field + " está vacío o no existe");
		}

		return value;
	}

	/**
	 * Comprueba que un número sea positivo.
	 */
	private static int requirePositive(Integer value, String field) {

		if (value == null || value <= 0) {
			throw new IllegalArgumentException("El campo " + field + " debe ser positivo");
		}

		return value;
	}

}
