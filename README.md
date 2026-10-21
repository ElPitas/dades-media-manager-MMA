# media-manager

Starter project for the second-year DAM Data Access assignment. The official Catalan assignment specifications are provided separately through **Moodle** and define the requirements for each task.

## Requirements

- Java 21 (JDK).
- Eclipse with Maven integration (m2e).
- Internet access to download dependencies initially, if they are not already available.

You do not need to install Maven separately or use Maven Wrapper when using Eclipse's Maven integration. The exercises use traditional **java.io**, not Java NIO file APIs. Text, XML and JSON files use UTF-8.

## Clone and personalize the project

Clone the public starter repository using Git:

```sh
git clone https://github.com/cara205/media-manager-starter.git
cd media-manager-starter
```

Alternatively, extract the student ZIP into a suitable working folder. Git is optional when using the ZIP.

**Before importing into Eclipse**, open `pom.xml` in a text editor and change the project's `artifactId` from `media-manager` to **`media-manager-xx`**, replacing `xx` with your lowercase initials. For example, a student with initials AB would use:

```xml
<artifactId>media-manager-ab</artifactId>
```

Change only the project's own `artifactId`, not any dependency or plugin identifiers. The supplied starter keeps `media-manager`; this personalization is part of your local setup. Keep the existing `groupId`, Java package names and public APIs unchanged.

## Importing into Eclipse

1. Open Eclipse and select **File → Import → Maven → Existing Maven Projects**.
2. Under **Root Directory**, select the cloned or extracted folder containing your personalized `pom.xml`.
3. Select the project and click **Finish**.
4. If dependencies are missing or configuration needs refreshing, right-click the project and select **Maven → Update Project**.
5. Check that the project uses Java 21 under **Properties → Java Build Path** and **Java Compiler**. If necessary, configure a JDK 21 under **Window → Preferences → Java → Installed JREs** and associate it with the JavaSE-21 environment.
6. Check that the project encoding is **UTF-8** under **Properties → Resource → Text file encoding**.

Eclipse reconstructs the project configuration from `pom.xml`; you do not need to recreate the Java project manually.

## Project structure and student work

| Location | Contents |
| --- | --- |
| `src/main/java` | Domain model, Catalog, Main and persistence classes. |
| `src/test/java` | JUnit tests for the model, Catalog and BinaryCatalogRecord. |
| `data/input` | Six supplied XML and JSON input files. |
| `data/output` | Generated results; Main creates the directory when needed. |
| `pom.xml` | Java 21, dependencies and Maven configuration. |

The base package is `cat.institutmarianao.mediamanager`. The teaching staff provides fully implemented **MediaContent, Movie, Series, Season, Episode, Catalog, Main, BinaryCatalogRecord** and **ExerciseNotImplementedException**.

Students must complete the operations marked `TODO` in the ten `persistence` classes and create the required intermediate binding classes. Preserve the supplied public interfaces, domain model and Main coordination logic.

The six tasks cover XML import (DOM, SAX, StAX and JAXB), JSON import (Gson and Jackson), text export, DOM XML export, Jackson JSON export and a binary file with direct access. See **Moodle** for the complete official formats and requirements.

## Running Main

Open `Main.java` and select **Run As → Java Application**. To select a mode, open **Run → Run Configurations → Java Application**, select the Main configuration and enter the argument under **Arguments → Program arguments**. Use one argument per execution.

| Argument | Behavior |
| --- | --- |
| No arguments or `all` | Attempts all tasks. |
| `1` | Attempts the four XML importers. |
| `2` | Attempts all six XML and JSON importers. |
| `3` | Attempts all six importers and text export. |
| `4` | Attempts all six importers and XML export. |
| `5` | Attempts all six importers and JSON export. |
| `6` | Attempts all six importers, binary creation and lookups. |
| `1-dom` | Tests only the DOM importer. |
| `1-sax` | Tests only the SAX importer. |
| `1-stax` | Tests only the StAX importer. |
| `1-jaxb` | Tests only the JAXB importer. |
| `2-gson` | Tests only the Gson importer. |
| `2-jackson` | Tests only the Jackson importer. |

### Working directory

The application uses paths relative to the **project root**, containing `pom.xml`. This must be the working directory so Main can find `data/input/` and generate results in `data/output/`.

Under **Run Configurations → Arguments → Working directory**, check that the project folder is selected. If necessary, select **Other → Workspace** and choose the imported project. Do not select `src`, `bin` or `target`.

### Execution statuses

| Status | Meaning |
| --- | --- |
| `COMPLETED` | The operation returned successfully and passed Main's basic checks. |
| `NOT_IMPLEMENTED` | The operation is still an unfinished exercise; this is expected initially. |
| `ERROR` | An error occurred or a checked contract was violated. |
| `BLOCKED` | No contents are available for export, or binary creation did not succeed in this execution and lookups cannot run. |
| `NOT_SELECTED` | The operation was not requested; this can also identify a binary lookup for a content type absent from the catalogue. |

**COMPLETED does not guarantee that every aspect of the exercise is correct.** Always check results against the official specification.

## Progressive development

Importers run independently: an unfinished or failing importer does not prevent testing the others. Main registers all successfully imported contents in Catalog and displays the assigned codes and actual catalogue summary.

Selected exports can run with **any nonempty catalogue**; completing all importers first is not required. With no contents, Main skips exports. Binary lookups are attempted only if binary creation succeeded during the current execution. The final implementation must satisfy all official requirements and support all six input files.

Every execution creates a **new, empty Catalog**. Existing output files do not restore contents or codes and are not automatically deleted at startup. Export operations must replace their corresponding output files.

## Running JUnit tests

Right-click `src/test/java` and select **Run As → JUnit Test** to run the supplied tests. You can also run an individual test class with the same option. Select **JUnit 5** if Eclipse asks.

The 54 tests check the model, Catalog and BinaryCatalogRecord and can run before and after implementing persistence. They do not validate file formats or replace verification of the six tasks.

## Optional private GitHub backup

You may create your own **PRIVATE**, empty GitHub repository to back up your work. Do not initialize it with a README or other files. Cloning this starter initially names the public repository's remote `origin`; rename it to `upstream`, then add your private repository as the new `origin`:

```sh
git remote rename origin upstream
git remote add origin https://github.com/YOUR_USERNAME/YOUR_PRIVATE_REPOSITORY.git
git remote -v
```

Replace the placeholders with your private repository's owner and name. `upstream` retains the starter URL for reference; `origin` must now point to your private backup. Check both URLs before pushing.

Save your work in your own commits and push them to the private repository:

```sh
git add .
git commit -m "Save assignment progress"
git push -u origin main
```

The push uploads your commits to your private `origin` and establishes `main` as its tracked branch. Configure Git authentication if requested; never store credentials or tokens in project files.

Students do not have permission to push changes to the teaching staff's public starter repository. Do not push to `upstream`.

## Official submission

Submit the assignment through **Moodle**, following the official instructions provided there. Your personal GitHub repository, if used, is only a backup and **does not replace the Moodle submission**. The official assignment specifications are distributed separately through Moodle.
