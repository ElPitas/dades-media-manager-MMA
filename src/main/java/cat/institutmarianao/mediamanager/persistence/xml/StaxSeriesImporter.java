package cat.institutmarianao.mediamanager.persistence.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import cat.institutmarianao.mediamanager.model.Episode;
import cat.institutmarianao.mediamanager.model.Season;
import cat.institutmarianao.mediamanager.model.Series;

/**
 * Imports series from an XML file using StAX.
 */
public class StaxSeriesImporter {

	public StaxSeriesImporter() {
	}

	public List<Series> importSeries(File input) throws IOException {

		Objects.requireNonNull(input, "input");

		List<Series> seriesList = new ArrayList<>();

		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();

			factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
			factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

			try (FileInputStream fis = new FileInputStream(input)) {

				XMLStreamReader reader = factory.createXMLStreamReader(fis, "UTF-8");

				Series serie = null;
				Season temporada = null;

				String titulo = null;
				String director = null;
				String genero = null;
				String fechaEstreno = null;
				String plataforma = null;

				String tituloEpisodio = null;
				String numeroEpisodio = null;
				String duracionEpisodio = null;

				StringBuilder text = new StringBuilder();

				try {
					while (reader.hasNext()) {

						int event = reader.next();

						switch (event) {

						case XMLStreamConstants.START_ELEMENT: {
							String element = reader.getLocalName();
							text.setLength(0);

							if ("show".equals(element)) {
								fechaEstreno = reader.getAttributeValue(null, "premiere");

								titulo = null;
								director = null;
								genero = null;
								plataforma = null;
								serie = null;
								temporada = null;

							} else if ("details".equals(element)) {
								genero = reader.getAttributeValue(null, "genre");
								plataforma = reader.getAttributeValue(null, "platform");

							} else if ("season".equals(element)) {
								int numero = Integer.parseInt(reader.getAttributeValue(null, "number"));

								temporada = new Season(numero);

							} else if ("episode".equals(element)) {
								numeroEpisodio = reader.getAttributeValue(null, "number");
								duracionEpisodio = reader.getAttributeValue(null, "minutes");
								tituloEpisodio = null;
							}

							break;
						}

						case XMLStreamConstants.CHARACTERS:
							text.append(reader.getText());
							break;

						case XMLStreamConstants.END_ELEMENT: {
							String endName = reader.getLocalName();
							String value = text.toString().trim();

							switch (endName) {

							case "title":
								if (temporada == null) {
									titulo = value;
								} else {
									tituloEpisodio = value;
								}
								break;

							case "director":
								director = value;
								break;

							case "details":
								serie = new Series(titulo, director, genero, LocalDate.parse(fechaEstreno), plataforma);
								break;

							case "episode":
								temporada.addEpisode(new Episode(Integer.parseInt(numeroEpisodio), tituloEpisodio,
										Integer.parseInt(duracionEpisodio)));
								break;

							case "season":
								serie.addSeason(temporada);
								temporada = null;
								break;

							case "show":
								seriesList.add(serie);
								serie = null;
								break;
							}

							text.setLength(0);
							break;
						}

						default:
							break;
						}
					}
				} finally {
					reader.close();
				}
			}

			return seriesList;

		} catch (IOException | XMLStreamException | RuntimeException e) {
			throw new IOException("Error al importar series desde: " + input.getAbsolutePath(), e);
		}
	}
}