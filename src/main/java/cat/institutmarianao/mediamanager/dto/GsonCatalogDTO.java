
package cat.institutmarianao.mediamanager.dto;

import java.util.List;

public class GsonCatalogDTO {

	// Representa el objeto raíz del JSON
	public static class CatalogDTO {

		public String source;

		public List<ItemDTO> items;
	}

	// Representa cada película o serie
	public static class ItemDTO {

		public int ref;

		public String kind;

		public String name;

		public CreditsDTO credits;

		public MetadataDTO metadata;

		public ContentDTO content;
	}

	// Representa los créditos
	public static class CreditsDTO {

		public String director;
	}

	// Representa los metadatos
	public static class MetadataDTO {

		public String genre;

		public String releaseDate;

		public String availableOn;
	}

	// Representa el contenido de una película o serie
	public static class ContentDTO {

		public Integer minutes;

		public List<SeasonDTO> seasons;
	}

	// Representa una temporada
	public static class SeasonDTO {

		public int index;

		public List<EpisodeDTO> episodes;
	}

	// Representa un episodio
	public static class EpisodeDTO {

		public int index;

		public String name;

		public int runtime;
	}

}
