package cat.institutmarianao.mediamanager.persistence.xml;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import cat.institutmarianao.mediamanager.model.Movie;

public class MovieSAXHandler extends DefaultHandler {

	private List<Movie> peliculas;
	private StringBuilder contenido;

	private String titulo;
	private String director;
	private String genero;
	private String fechaEstreno;
	private String plataforma;
	private String runtime;

	private boolean dentroCreators;
	private boolean dentroClassification;
	private boolean dentroAvailability;

	public MovieSAXHandler() {
	}

	public List<Movie> getPeliculas() {
		return peliculas;
	}

	@Override
	public void startDocument() {
		peliculas = new ArrayList<>();
		contenido = new StringBuilder();

		dentroCreators = false;
		dentroClassification = false;
		dentroAvailability = false;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {

		contenido.setLength(0);

		if ("film".equals(qName)) {
			titulo = attributes.getValue("name");
			fechaEstreno = attributes.getValue("released");

			director = null;
			genero = null;
			plataforma = null;
			runtime = null;

			dentroCreators = false;
			dentroClassification = false;
			dentroAvailability = false;
		}

		if ("creators".equals(qName)) {
			dentroCreators = true;
		}

		if ("classification".equals(qName)) {
			dentroClassification = true;
		}

		if ("availability".equals(qName)) {
			dentroAvailability = true;
		}

		if ("footage".equals(qName)) {
			runtime = attributes.getValue("minutes");
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		contenido.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		String texto = contenido.toString().trim();

		switch (qName) {

		case "name":
			if (dentroCreators) {
				director = texto;
			} else if (dentroClassification) {
				genero = texto;
			} else if (dentroAvailability) {
				plataforma = texto;
			}
			break;

		case "creators":
			dentroCreators = false;
			break;

		case "classification":
			dentroClassification = false;
			break;

		case "availability":
			dentroAvailability = false;
			break;

		case "film":
			Movie peliculaActual = new Movie(titulo, director, genero, LocalDate.parse(fechaEstreno), plataforma,
					Integer.parseInt(runtime));

			peliculas.add(peliculaActual);
			break;
		}

		contenido.setLength(0);
	}
}