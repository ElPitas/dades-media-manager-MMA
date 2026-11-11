package cat.institutmarianao.mediamanager.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * JAXB binding classes for the supplierCatalog XML structure.
 *
 * These classes are separate from the application's domain model.
 */
@XmlRootElement(name = "supplierCatalog")
@XmlAccessorType(XmlAccessType.FIELD)
public class SupplierCatalogXml {

	public List<EntryXml> getEntries() {
		return entries;
	}

	public void setEntries(List<EntryXml> entries) {
		this.entries = entries;
	}

	@XmlElementWrapper(name = "entries")
	@XmlElement(name = "entry")
	List<EntryXml> entries = new ArrayList<>();

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class EntryXml {

		@XmlElement(name = "sourceCode")
		String sourceCode;

		@XmlElement(name = "category")
		private
		String category;

		@XmlElement(name = "generalInfo")
		private
		GeneralInfoXml generalInfo;

		@XmlElement(name = "details")
		private
		DetailsXml details;

		public GeneralInfoXml getGeneralInfo() {
			return generalInfo;
		}

		public void setGeneralInfo(GeneralInfoXml generalInfo) {
			this.generalInfo = generalInfo;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public DetailsXml getDetails() {
			return details;
		}

		public void setDetails(DetailsXml details) {
			this.details = details;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class GeneralInfoXml {

		@XmlElement(name = "name")
		private
		String name;

		@XmlElement(name = "director")
		private
		String director;

		@XmlElement(name = "genre")
		private
		String genre;

		@XmlElement(name = "releaseDate")
		private
		String releaseDate;

		@XmlElement(name = "service")
		private
		String service;

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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getReleaseDate() {
			return releaseDate;
		}

		public void setReleaseDate(String releaseDate) {
			this.releaseDate = releaseDate;
		}

		public String getService() {
			return service;
		}

		public void setService(String service) {
			this.service = service;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class DetailsXml {

		@XmlElement(name = "movie")
		private
		MovieXml movie;

		@XmlElement(name = "show")
		private
		ShowXml show;

		public MovieXml getMovie() {
			return movie;
		}

		public void setMovie(MovieXml movie) {
			this.movie = movie;
		}

		public ShowXml getShow() {
			return show;
		}

		public void setShow(ShowXml show) {
			this.show = show;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MovieXml {

		@XmlElement(name = "minutes")
		private
		int minutes;

		public int getMinutes() {
			return minutes;
		}

		public void setMinutes(int minutes) {
			this.minutes = minutes;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ShowXml {

		@XmlElement(name = "seasonBlocks")
		private
		SeasonBlocksXml seasonBlocks;

		public SeasonBlocksXml getSeasonBlocks() {
			return seasonBlocks;
		}

		public void setSeasonBlocks(SeasonBlocksXml seasonBlocks) {
			this.seasonBlocks = seasonBlocks;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class SeasonBlocksXml {

		@XmlElement(name = "block")
		private
		List<BlockXml> blocks = new ArrayList<>();

		public List<BlockXml> getBlocks() {
			return blocks;
		}

		public void setBlocks(List<BlockXml> blocks) {
			this.blocks = blocks;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class BlockXml {

		@XmlAttribute(name = "number")
		private
		int number;

		@XmlElement(name = "episodeList")
		private
		EpisodeListXml episodeList;

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}

		public EpisodeListXml getEpisodeList() {
			return episodeList;
		}

		public void setEpisodeList(EpisodeListXml episodeList) {
			this.episodeList = episodeList;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class EpisodeListXml {

		@XmlElement(name = "episode")
		private
		List<EpisodeXml> episodes = new ArrayList<>();

		public List<EpisodeXml> getEpisodes() {
			return episodes;
		}

		public void setEpisodes(List<EpisodeXml> episodes) {
			this.episodes = episodes;
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class EpisodeXml {

		@XmlElement(name = "order")
		private
		int order;

		@XmlElement(name = "name")
		private
		String name;

		@XmlElement(name = "minutes")
		private
		int minutes;

		public int getOrder() {
			return order;
		}

		public void setOrder(int order) {
			this.order = order;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getMinutes() {
			return minutes;
		}

		public void setMinutes(int minutes) {
			this.minutes = minutes;
		}
	}
}