
package cat.institutmarianao.mediamanager.persistence.text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import cat.institutmarianao.mediamanager.model.MediaContent;

public class TextCatalogExporter {

	public TextCatalogExporter() {
	}

	public void exportCatalog(File output, Collection<? extends MediaContent> contents) throws IOException {

		Objects.requireNonNull(output, "output");
		Objects.requireNonNull(contents, "contents");

		List<MediaContent> orderedContents = new ArrayList<>(contents);

		for (MediaContent content : orderedContents) {

			Objects.requireNonNull(content, "content");

			if (content.getCode() <= 0) {
				throw new IllegalArgumentException("El contenido no tiene un código asignado: " + content.getTitle());
			}
		}

		orderedContents.sort(Comparator.comparing(MediaContent::getTitle, String.CASE_INSENSITIVE_ORDER)
				.thenComparingInt(MediaContent::getCode));

		try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(output, false), StandardCharsets.UTF_8))) {

			for (MediaContent content : orderedContents) {

				writer.write(String.valueOf(content.getCode()));
				writer.write(";");
				writer.write(content.getTitle());
				writer.write(";");
				writer.write(content.getDirector());
				writer.newLine();
			}

		} catch (IOException e) {

			throw new IOException("Error al escribir el catálogo en el archivo: " + output.getAbsolutePath(), e);
		}
	}
}
