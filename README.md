# media-manager

Starter project for the second-year DAM Data Access exercises.

## Clone and personalize

```sh
git clone https://github.com/cara205/media-manager-starter.git
cd media-manager-starter
```

Before importing into Eclipse, edit `pom.xml` and change the project's `artifactId` from `media-manager` to `media-manager-xx`, replacing `xx` with your lowercase initials (for example, `media-manager-ab`). Do not change dependency or plugin identifiers, the `groupId`, or Java package names.

## Import into Eclipse

Use **Java 21 (JDK)** and **Eclipse with Maven integration**.

1. Select **File → Import → Maven → Existing Maven Projects**.
2. Select the cloned folder containing `pom.xml` and click **Finish**.
3. Confirm that the project uses Java 21. If dependencies need refreshing, right-click the project and select **Maven → Update Project**.

## Complete the exercises

Implement the `TODO` methods in the persistence classes and create the required binding classes. Preserve the supplied model, Catalog, Main and public method signatures.

Follow the official assignment specifications provided through **Moodle**. Input files are in `data/input/`; generated files go in `data/output/`.

## Run Main

Open `Main.java` and select **Run As → Java Application**. To choose a mode, open **Run → Run Configurations → Java Application**, select Main and enter one argument under **Arguments → Program arguments**.

Keep the **working directory set to the project root**, where `pom.xml` is located.

| Argument | Action |
| --- | --- |
| No arguments or `all` | Attempt all tasks. |
| `1` | Run the four XML importers. |
| `2` | Run all XML and JSON importers. |
| `3` | Import contents and export text. |
| `4` | Import contents and export XML. |
| `5` | Import contents and export JSON. |
| `6` | Import contents, create the binary file and test lookups. |
| `1-dom` | Test only the DOM importer. |
| `1-sax` | Test only the SAX importer. |
| `1-stax` | Test only the StAX importer. |
| `1-jaxb` | Test only the JAXB importer. |
| `2-gson` | Test only the Gson importer. |
| `2-jackson` | Test only the Jackson importer. |

`NOT_IMPLEMENTED` is expected until the corresponding exercise is implemented.

## Run JUnit tests

Right-click `src/test/java` and select **Run As → JUnit Test**. You can also run an individual test class. Select **JUnit 5** if prompted.

## Submission

Submit your completed assignment through **Moodle**, the official submission channel.
