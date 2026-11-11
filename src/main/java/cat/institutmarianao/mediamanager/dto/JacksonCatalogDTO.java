
package cat.institutmarianao.mediamanager.dto;

import java.util.List;

public class JacksonCatalogDTO {

	private String provider;
	private List<ContentRecordDTO> records;

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public List<ContentRecordDTO> getRecords() {
		return records;
	}

	public void setRecords(List<ContentRecordDTO> records) {
		this.records = records;
	}

	// Representa cada película o serie
	public static class ContentRecordDTO {

		private String externalId;
		private String mediaType;
		private String title;
		private String director;
		private String genre;
		private String releaseDate;
		private String platform;

		private MovieDetailsDTO movieDetails;
		private SeriesDetailsDTO seriesDetails;

		public String getExternalId() {
			return externalId;
		}

		public void setExternalId(String externalId) {
			this.externalId = externalId;
		}

		public String getMediaType() {
			return mediaType;
		}

		public void setMediaType(String mediaType) {
			this.mediaType = mediaType;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDirector() {
			return director;
		}

		public void setDirector(String director) {
			this.director = director;
		}

		public String getGenre() {
			return genre;
		}

		public void setGenre(String genre) {
			this.genre = genre;
		}

		public String getReleaseDate() {
			return releaseDate;
		}

		public void setReleaseDate(String releaseDate) {
			this.releaseDate = releaseDate;
		}

		public String getPlatform() {
			return platform;
		}

		public void setPlatform(String platform) {
			this.platform = platform;
		}

		public MovieDetailsDTO getMovieDetails() {
			return movieDetails;
		}

		public void setMovieDetails(MovieDetailsDTO movieDetails) {
			this.movieDetails = movieDetails;
		}

		public SeriesDetailsDTO getSeriesDetails() {
			return seriesDetails;
		}

		public void setSeriesDetails(SeriesDetailsDTO seriesDetails) {
			this.seriesDetails = seriesDetails;
		}
	}

	// Representa los detalles específicos de una película
	public static class MovieDetailsDTO {

		private Integer durationMinutes;

		public Integer getDurationMinutes() {
			return durationMinutes;
		}

		public void setDurationMinutes(Integer durationMinutes) {
			this.durationMinutes = durationMinutes;
		}
	}

	// Representa los detalles específicos de una serie
	public static class SeriesDetailsDTO {

		private List<SeasonDTO> seasons;

		public List<SeasonDTO> getSeasons() {
			return seasons;
		}

		public void setSeasons(List<SeasonDTO> seasons) {
			this.seasons = seasons;
		}
	}

	// Representa una temporada
	public static class SeasonDTO {

		private Integer seasonNumber;
		private List<ChapterDTO> chapters;

		public Integer getSeasonNumber() {
			return seasonNumber;
		}

		public void setSeasonNumber(Integer seasonNumber) {
			this.seasonNumber = seasonNumber;
		}

		public List<ChapterDTO> getChapters() {
			return chapters;
		}

		public void setChapters(List<ChapterDTO> chapters) {
			this.chapters = chapters;
		}
	}

	// Representa un capítulo
	public static class ChapterDTO {

		private Integer chapterNumber;
		private String title;
		private Integer durationMinutes;

		public Integer getChapterNumber() {
			return chapterNumber;
		}

		public void setChapterNumber(Integer chapterNumber) {
			this.chapterNumber = chapterNumber;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Integer getDurationMinutes() {
			return durationMinutes;
		}

		public void setDurationMinutes(Integer durationMinutes) {
			this.durationMinutes = durationMinutes;
		}
	}
}
