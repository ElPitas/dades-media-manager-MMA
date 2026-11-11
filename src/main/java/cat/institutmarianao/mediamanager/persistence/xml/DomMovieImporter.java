package cat.institutmarianao.mediamanager.persistence.xml;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cat.institutmarianao.mediamanager.model.Movie;

/**
 * Student exercise for importing movies_dom.xml using DOM. File access must use
 * traditional Java I/O. This class does not manage a Catalog.
 */
public class DomMovieImporter {
	/**
	 * Creates a stateless importer without opening any files.
	 */
	public DomMovieImporter() {
	}

	public List<Movie> importMovies(File input) throws IOException {

		List<Movie> peliculas = new ArrayList<>();

		final String ETIQUETA_MOVIES = "movies";
		final String ETIQUETA_MOVIE = "movie";
		final String ETIQUETA_SOURCEID = "sourceId";
		final String ETIQUETA_NAME = "name";
		final String ETIQUETA_DIRECTOR = "director";
		final String ETIQUETA_GENRE = "genre";
		final String ETIQUETA_RELEASEDATE = "releaseDate";
		final String ETIQUETA_PLATFORM = "platform";
		final String ETIQUETA_RUNTIME = "runtime";

		try {
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			f.setNamespaceAware(true);
			f.setIgnoringElementContentWhitespace(true);
			f.setIgnoringComments(true);
			DocumentBuilder b = f.newDocumentBuilder();

			Document doc = b.parse(input);
			doc.getDocumentElement().normalize();

			NodeList movies = doc.getElementsByTagName(ETIQUETA_MOVIE);

			for (int i = 0; i < movies.getLength(); i++) {
				Element e = (Element) movies.item(i);
				String nombre = e.getElementsByTagName(ETIQUETA_NAME).item(0).getTextContent();
				Element director = (Element) e.getElementsByTagName(ETIQUETA_DIRECTOR).item(0);
				String nombreDirector = director.getElementsByTagName(ETIQUETA_NAME).item(0).getTextContent();
				String genero = e.getElementsByTagName(ETIQUETA_GENRE).item(0).getTextContent();
				String fechaEstreno = e.getElementsByTagName(ETIQUETA_RELEASEDATE).item(0).getTextContent().trim();
				String plataforma = e.getElementsByTagName(ETIQUETA_PLATFORM).item(0).getTextContent();
				String duracion = e.getElementsByTagName(ETIQUETA_RUNTIME).item(0).getTextContent().trim();

				LocalDate fecha = LocalDate.parse(fechaEstreno);
				int runtime = Integer.parseInt(duracion);

				Movie pelicula = new Movie(nombre, nombreDirector, genero, fecha, plataforma, runtime);

				peliculas.add(pelicula);
			}

			return peliculas;

		} catch (IOException e) {
			throw new IOException("Error al leer el archivo: " + input.getAbsolutePath(), e);
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException("Error al procesar el archivo XML: " + input.getAbsolutePath(), e);
		} catch (java.time.DateTimeException | IllegalArgumentException e) {
			throw new IOException("Los datos de las películas no son válidos: " + input.getAbsolutePath(), e);
		}

	}
}
