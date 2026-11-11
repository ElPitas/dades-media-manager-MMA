
package cat.institutmarianao.mediamanager.persistence.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cat.institutmarianao.mediamanager.model.Episode;
import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.model.Movie;
import cat.institutmarianao.mediamanager.model.Season;
import cat.institutmarianao.mediamanager.model.Series;

public class DomCatalogExporter {

	public DomCatalogExporter() {
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

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.newDocument();

			Element root = document.createElement("catalog");
			document.appendChild(root);

			Element moviesElement = document.createElement("movies");
			root.appendChild(moviesElement);

			Element seriesElement = document.createElement("series");
			root.appendChild(seriesElement);

			for (Movie movie : movies) {
				appendMovie(document, moviesElement, movie);
			}

			for (Series serie : series) {
				appendSeries(document, seriesElement, serie);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();

			transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			try (FileOutputStream stream = new FileOutputStream(output, false)) {

				transformer.transform(new DOMSource(document), new StreamResult(stream));
			}

		} catch (ParserConfigurationException | TransformerException e) {

			throw new IOException("Error al construir o generar el XML: " + output.getAbsolutePath(), e);

		} catch (IOException e) {

			throw new IOException("Error al escribir el archivo XML: " + output.getAbsolutePath(), e);
		}
	}

	private void appendMovie(Document document, Element moviesElement, Movie movie) {

		Element movieElement = document.createElement("movie");
		movieElement.setAttribute("code", String.valueOf(movie.getCode()));

		moviesElement.appendChild(movieElement);

		appendTextElement(document, movieElement, "title", movie.getTitle());

		appendTextElement(document, movieElement, "director", movie.getDirector());

		appendTextElement(document, movieElement, "genre", movie.getGenre());

		appendTextElement(document, movieElement, "releaseDate", movie.getReleaseDate().toString());

		appendTextElement(document, movieElement, "platform", movie.getPlatform());

		appendTextElement(document, movieElement, "runtime", String.valueOf(movie.getRuntime()));
	}

	private void appendSeries(Document document, Element seriesElement, Series serie) {

		Element seriesItem = document.createElement("seriesItem");
		seriesItem.setAttribute("code", String.valueOf(serie.getCode()));

		seriesElement.appendChild(seriesItem);

		appendTextElement(document, seriesItem, "title", serie.getTitle());

		appendTextElement(document, seriesItem, "director", serie.getDirector());

		appendTextElement(document, seriesItem, "genre", serie.getGenre());

		appendTextElement(document, seriesItem, "releaseDate", serie.getReleaseDate().toString());

		appendTextElement(document, seriesItem, "platform", serie.getPlatform());

		Element seasonsElement = document.createElement("seasons");
		seriesItem.appendChild(seasonsElement);

		List<Season> seasons = new ArrayList<>(serie.getSeasons());

		seasons.sort(Comparator.comparingInt(Season::getNumber));

		for (Season season : seasons) {

			Element seasonElement = document.createElement("season");

			seasonElement.setAttribute("number", String.valueOf(season.getNumber()));

			seasonsElement.appendChild(seasonElement);

			Element episodesElement = document.createElement("episodes");
			seasonElement.appendChild(episodesElement);

			List<Episode> episodes = new ArrayList<>(season.getEpisodes());

			episodes.sort(Comparator.comparingInt(Episode::getNumber));

			for (Episode episode : episodes) {

				Element episodeElement = document.createElement("episode");

				episodeElement.setAttribute("number", String.valueOf(episode.getNumber()));

				episodesElement.appendChild(episodeElement);

				appendTextElement(document, episodeElement, "title", episode.getTitle());

				appendTextElement(document, episodeElement, "runtime", String.valueOf(episode.getRuntime()));
			}
		}
	}

	private void appendTextElement(Document document, Element parent, String tagName, String value) {

		Element element = document.createElement(tagName);
		element.setTextContent(value);
		parent.appendChild(element);
	}

}
