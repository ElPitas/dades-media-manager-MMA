package cat.institutmarianao.mediamanager.persistence.xml;

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

	@XmlElementWrapper(name = "entries")
	@XmlElement(name = "entry")
	List<EntryXml> entries = new ArrayList<>();

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class EntryXml {

		@XmlElement(name = "sourceCode")
		String sourceCode;

		@XmlElement(name = "category")
		String category;

		@XmlElement(name = "generalInfo")
		GeneralInfoXml generalInfo;

		@XmlElement(name = "details")
		DetailsXml details;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class GeneralInfoXml {

		@XmlElement(name = "name")
		String name;

		@XmlElement(name = "director")
		String director;

		@XmlElement(name = "genre")
		String genre;

		@XmlElement(name = "releaseDate")
		String releaseDate;

		@XmlElement(name = "service")
		String service;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class DetailsXml {

		@XmlElement(name = "movie")
		MovieXml movie;

		@XmlElement(name = "show")
		ShowXml show;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class MovieXml {

		@XmlElement(name = "minutes")
		int minutes;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ShowXml {

		@XmlElement(name = "seasonBlocks")
		SeasonBlocksXml seasonBlocks;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class SeasonBlocksXml {

		@XmlElement(name = "block")
		List<BlockXml> blocks = new ArrayList<>();
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class BlockXml {

		@XmlAttribute(name = "number")
		int number;

		@XmlElement(name = "episodeList")
		EpisodeListXml episodeList;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class EpisodeListXml {

		@XmlElement(name = "episode")
		List<EpisodeXml> episodes = new ArrayList<>();
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	public static class EpisodeXml {

		@XmlElement(name = "order")
		int order;

		@XmlElement(name = "name")
		String name;

		@XmlElement(name = "minutes")
		int minutes;
	}
}