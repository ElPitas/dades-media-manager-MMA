
package cat.institutmarianao.mediamanager.persistence.binary;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import cat.institutmarianao.mediamanager.model.MediaContent;
import cat.institutmarianao.mediamanager.model.Movie;
import cat.institutmarianao.mediamanager.model.Series;

public class BinaryCatalogDao {

	private static final int RECORD_SIZE = 102;
	private static final int TITLE_LENGTH = 40;

	public BinaryCatalogDao() {
	}

	public void createCatalog(File output, Collection<? extends MediaContent> contents) throws IOException {

		Objects.requireNonNull(output, "output");
		Objects.requireNonNull(contents, "contents");

		List<MediaContent> orderedContents = new ArrayList<>(contents);

		for (MediaContent content : orderedContents) {

			Objects.requireNonNull(content, "content");

			if (content.getCode() <= 0) {
				throw new IllegalArgumentException("El contenido no tiene un código asignado: " + content.getTitle());
			}

			if (content.getTitle().isBlank()) {
				throw new IllegalArgumentException("El título no puede estar vacío");
			}

			if (content.getTitle().length() > TITLE_LENGTH) {
				throw new IllegalArgumentException("El título supera los 40 caracteres: " + content.getTitle());
			}

			if (!(content instanceof Movie) && !(content instanceof Series)) {

				throw new IllegalArgumentException("Tipo de contenido no reconocido: " + content.getClass().getName());
			}
		}

		orderedContents.sort(Comparator.comparingInt(MediaContent::getCode));

		for (int i = 0; i < orderedContents.size(); i++) {

			int expectedCode = i + 1;

			if (orderedContents.get(i).getCode() != expectedCode) {
				throw new IllegalArgumentException(
						"Los códigos deben ser únicos y consecutivos " + "desde 1. Código esperado: " + expectedCode);
			}
		}

		try (RandomAccessFile file = new RandomAccessFile(output, "rw")) {

			file.setLength(0);

			for (MediaContent content : orderedContents) {

				long position = (long) (content.getCode() - 1) * RECORD_SIZE;

				file.seek(position);

				file.writeInt(content.getCode());

				String title = content.getTitle();

				for (int i = 0; i < TITLE_LENGTH; i++) {

					if (i < title.length()) {
						file.writeChar(title.charAt(i));
					} else {
						file.writeChar(' ');
					}
				}

				file.writeChar(content.getType());

				file.writeLong(content.getReleaseDate().toEpochDay());

				if (content instanceof Series) {

					Series series = (Series) content;

					file.writeInt(series.getSeasonCount());

					file.writeInt(series.getEpisodeCount());

				} else {

					file.writeInt(0);

					file.writeInt(0);
				}
			}

		} catch (IOException e) {

			throw new IOException("Error al crear el catálogo binario: " + output.getAbsolutePath(), e);
		}
	}

	public Optional<BinaryCatalogRecord> findByCode(File input, int code) throws IOException {

		Objects.requireNonNull(input, "input");

		if (code <= 0) {
			throw new IllegalArgumentException("El código debe ser positivo");
		}

		try (RandomAccessFile file = new RandomAccessFile(input, "r")) {

			long fileLength = file.length();

			if (fileLength % RECORD_SIZE != 0) {
				throw new IOException("El archivo binario tiene una longitud incorrecta");
			}

			long position = (long) (code - 1) * RECORD_SIZE;

			if (position >= fileLength) {
				return Optional.empty();
			}

			file.seek(position);

			int storedCode = file.readInt();

			if (storedCode != code) {
				throw new IOException("El código almacenado no coincide con su posición");
			}

			char[] titleChars = new char[TITLE_LENGTH];

			for (int i = 0; i < TITLE_LENGTH; i++) {
				titleChars[i] = file.readChar();
			}

			int titleLength = TITLE_LENGTH;

			while (titleLength > 0 && titleChars[titleLength - 1] == ' ') {

				titleLength--;
			}

			String title = new String(titleChars, 0, titleLength);

			char type = file.readChar();

			long epochDay = file.readLong();

			int seasons = file.readInt();

			int episodes = file.readInt();

			try {

				LocalDate releaseDate = LocalDate.ofEpochDay(epochDay);

				BinaryCatalogRecord record = new BinaryCatalogRecord(storedCode, title, type, releaseDate, seasons,
						episodes);

				return Optional.of(record);

			} catch (DateTimeException | IllegalArgumentException e) {

				throw new IOException("El registro binario contiene datos incorrectos " + "para el código: " + code, e);
			}

		} catch (IOException e) {

			throw new IOException("Error al consultar el catálogo binario: " + input.getAbsolutePath(), e);
		}
	}
}
