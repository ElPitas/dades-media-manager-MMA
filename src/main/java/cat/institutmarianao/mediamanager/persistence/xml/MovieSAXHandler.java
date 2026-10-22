package cat.institutmarianao.mediamanager.persistence.xml;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import cat.institutmarianao.mediamanager.model.Movie;

public class MovieSAXHandler extends DefaultHandler {

	private List<Movie> peliculas;
	private Movie peliculaActual;
	private StringBuilder contenido;

	private String sourceId;
	private String nombre;
	private String nombreDirector;
	private String genero;
	private String fechaEstreno;
	private String plataforma;
	private String duracion;

	private boolean dentroDirector;

	public MovieSAXHandler() {
	}

	public List<Movie> getPeliculas() {
		return peliculas;
	}

	@Override
	public void startDocument() {
		peliculas = new ArrayList<>();
		contenido = new StringBuilder();

		peliculaActual = null;
		dentroDirector = false;
	}

	// Aqui termina el trabajo humano, tlabaja tiene que tlabajá

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {

		String etiqueta = obtenerNombreEtiqueta(localName, qName);
		contenido.setLength(0);

		if ("movie".equals(etiqueta)) {
			peliculaActual = null;

			sourceId = null;
			nombre = null;
			nombreDirector = null;
			genero = null;
			fechaEstreno = null;
			plataforma = null;
			duracion = null;

			dentroDirector = false;
		}

		if ("director".equals(etiqueta)) {
			dentroDirector = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {

		// El texto puede llegar fragmentado en varios eventos.
		contenido.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		String etiqueta = obtenerNombreEtiqueta(localName, qName);
		String texto = contenido.toString().trim();

		switch (etiqueta) {

		case "sourceId":
			sourceId = texto;
			break;

		case "name":
			// Hay dos etiquetas <name> en el XML.
			if (dentroDirector) {
				nombreDirector = texto;
			} else {
				nombre = texto;
			}
			break;

		case "genre":
			genero = texto;
			break;

		case "releaseDate":
			fechaEstreno = texto;
			break;

		case "platform":
			plataforma = texto;
			break;

		case "runtime":
			duracion = texto;
			break;

		case "director":
			dentroDirector = false;
			break;

		case "movie":
			// Hemos terminado de leer una película.
			crearPelicula();
			break;
		}

		contenido.setLength(0);
	}

	private void crearPelicula() throws SAXException {

		// Comprobar que estén presentes los campos obligatorios.
		comprobarCampo(sourceId, "sourceId");
		comprobarCampo(nombre, "name");
		comprobarCampo(nombreDirector, "director/name");
		comprobarCampo(genero, "genre");
		comprobarCampo(fechaEstreno, "releaseDate");
		comprobarCampo(plataforma, "platform");
		comprobarCampo(duracion, "runtime");

		try {
			// Convertir los datos a los tipos que necesita Movie.
			LocalDate fecha = LocalDate.parse(fechaEstreno);
			int runtime = Integer.parseInt(duracion);

			// Crear el objeto Movie.
			peliculaActual = new Movie(nombre, nombreDirector, genero, fecha, plataforma, runtime);

			// Añadir la película a la lista.
			peliculas.add(peliculaActual);

		} catch (DateTimeException | IllegalArgumentException e) {
			throw new SAXException("Datos inválidos en la película con sourceId: " + sourceId, e);
		}
	}

	private void comprobarCampo(String valor, String campo) throws SAXException {

		if (valor == null || valor.trim().isEmpty()) {
			throw new SAXException("Falta el campo obligatorio o está vacío: " + campo);
		}
	}

	private String obtenerNombreEtiqueta(String localName, String qName) {

		// Con namespaces desactivados normalmente utilizaremos qName.
		// Si qName está vacío, recurrimos a localName.
		if (qName != null && !qName.isEmpty()) {
			return qName;
		}

		return localName;
	}

}
