package cat.institutmarianao.mediamanager.persistence.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cat.institutmarianao.mediamanager.model.Movie;

public class SaxMovieImporter {

	public SaxMovieImporter() {
	}

	public List<Movie> importMovies(File input) throws IOException {

		Objects.requireNonNull(input, "input no puede ser null");

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);

			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			SAXParser parser = factory.newSAXParser();
			MovieSAXHandler handler = new MovieSAXHandler();

			try (InputStream stream = new FileInputStream(input)) {
				InputSource source = new InputSource(stream);
				source.setEncoding("UTF-8");

				parser.parse(source, handler);
			}

			return handler.getPeliculas();

		} catch (ParserConfigurationException | SAXException | IOException | RuntimeException e) {
			throw new IOException("Error al importar películas desde: " + input.getAbsolutePath(), e);
		}
	}
}