
package cat.institutmarianao.mediamanager.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "movies", "series" })
public class JacksonCatalogExportDTO {

	private final List<MovieDTO> movies;
	private final List<SeriesDTO> series;

	public JacksonCatalogExportDTO(List<MovieDTO> movies, List<SeriesDTO> series) {

		this.movies = movies;
		this.series = series;
	}

	public List<MovieDTO> getMovies() {
		return movies;
	}

	public List<SeriesDTO> getSeries() {
		return series;
	}

	@JsonPropertyOrder({ "code", "title", "director", "genre", "releaseDate", "platform", "runtime" })
	public static class MovieDTO {

		private final int code;
		private final String title;
		private final String director;
		private final String genre;
		private final String releaseDate;
		private final String platform;
		private final int runtime;

		public MovieDTO(int code, String title, String director, String genre, String releaseDate, String platform,
				int runtime) {

			this.code = code;
			this.title = title;
			this.director = director;
			this.genre = genre;
			this.releaseDate = releaseDate;
			this.platform = platform;
			this.runtime = runtime;
		}

		public int getCode() {
			return code;
		}

		public String getTitle() {
			return title;
		}

		public String getDirector() {
			return director;
		}

		public String getGenre() {
			return genre;
		}

		public String getReleaseDate() {
			return releaseDate;
		}

		public String getPlatform() {
			return platform;
		}

		public int getRuntime() {
			return runtime;
		}
	}

	@JsonPropertyOrder({ "code", "title", "director", "genre", "releaseDate", "platform", "seasons" })
	public static class SeriesDTO {

		private final int code;
		private final String title;
		private final String director;
		private final String genre;
		private final String releaseDate;
		private final String platform;
		private final List<SeasonDTO> seasons;

		public SeriesDTO(int code, String title, String director, String genre, String releaseDate, String platform,
				List<SeasonDTO> seasons) {

			this.code = code;
			this.title = title;
			this.director = director;
			this.genre = genre;
			this.releaseDate = releaseDate;
			this.platform = platform;
			this.seasons = seasons;
		}

		public int getCode() {
			return code;
		}

		public String getTitle() {
			return title;
		}

		public String getDirector() {
			return director;
		}

		public String getGenre() {
			return genre;
		}

		public String getReleaseDate() {
			return releaseDate;
		}

		public String getPlatform() {
			return platform;
		}

		public List<SeasonDTO> getSeasons() {
			return seasons;
		}
	}

	@JsonPropertyOrder({ "number", "episodes" })
	public static class SeasonDTO {

		private final int number;
		private final List<EpisodeDTO> episodes;

		public SeasonDTO(int number, List<EpisodeDTO> episodes) {
			this.number = number;
			this.episodes = episodes;
		}

		public int getNumber() {
			return number;
		}

		public List<EpisodeDTO> getEpisodes() {
			return episodes;
		}
	}

	@JsonPropertyOrder({ "number", "title", "runtime" })
	public static class EpisodeDTO {

		private final int number;
		private final String title;
		private final int runtime;

		public EpisodeDTO(int number, String title, int runtime) {
			this.number = number;
			this.title = title;
			this.runtime = runtime;
		}

		public int getNumber() {
			return number;
		}

		public String getTitle() {
			return title;
		}

		public int getRuntime() {
			return runtime;
		}
	}
}
