# media-manager

Starter project for the second-year DAM Data Access exercises.

## Clone and personalize

### Option 1: Clone from Eclipse (recommended)

1. Open Eclipse and select **Window → Perspective → Open Perspective → Other... → Git**.
2. In the **Git Repositories** view, click **Clone a Git Repository**.
3. Enter the repository URI: `https://github.com/cara205/media-manager-starter.git`
4. Click **Next**, select the `main` branch and click **Next**.
5. Choose a local destination folder and click **Finish**.
6. If Eclipse offers to import the project automatically, skip the import for now.

### Option 2: Clone using Git

Alternatively, open a terminal and run:

```sh
git clone https://github.com/cara205/media-manager-starter.git
cd media-manager-starter
```

### Personalize the project

**Before importing into Eclipse**, open `pom.xml` in a text editor and change the project's `artifactId` from `media-manager` to `media-manager-xxx`, replacing `xxx` with your lowercase initials (surnames+name).

For example in my case (Cristina Ara Mirabet) it should be:

```xml
<artifactId>media-manager-amc</artifactId>
```

Do not change dependency or plugin identifiers, the `groupId`, or Java package names.

## Import into Eclipse

Use **Java 21 (JDK)** and **Eclipse with Maven integration**.

1. Select **File → Import → Maven → Existing Maven Projects**.
2. Select the cloned folder containing `pom.xml` and click **Finish**.
3. Confirm that the project uses Java 21. If dependencies need refreshing, right-click the project and select **Maven → Update Project**.

## Complete the exercises

Implement the `TODO` methods in the persistence classes and create the required binding classes. Preserve the supplied model, Catalog, Main and public method signatures.

Replace each `throw new ExerciseNotImplementedException(...)` statement with your implementation of the corresponding method.

Follow the official assignment specifications provided through **Moodle**. Input files are in `data/input/`; generated files go in `data/output/`.

## Run Main

Open `Main.java` and select **Run As → Java Application**.

To choose a mode, open **Run → Run Configurations → Java Application**, select Main and enter one argument under **Arguments → Program arguments**.

**Available execution modes:**

- **No arguments or `all`:** Attempt all tasks.
- **`1`:** Run the four XML importers.
- **`2`:** Run all XML and JSON importers.
- **`3`:** Import contents and export text.
- **`4`:** Import contents and export XML.
- **`5`:** Import contents and export JSON.
- **`6`:** Import contents, create the binary file and test lookups.

**Individual importer tests:**

- **`1-dom`:** Test only the DOM importer.
- **`1-sax`:** Test only the SAX importer.
- **`1-stax`:** Test only the StAX importer.
- **`1-jaxb`:** Test only the JAXB importer.
- **`2-gson`:** Test only the Gson importer.
- **`2-jackson`:** Test only the Jackson importer.

Keep the **working directory set to the project root**, where `pom.xml` is located.

`NOT_IMPLEMENTED` is expected until the corresponding exercise is implemented.

## Run JUnit tests

Right-click `src/test/java` and select **Run As → JUnit Test**. You can also run an individual test class. Select **JUnit 5** if prompted.

## Submission

Submit your completed assignment through **Moodle**, the official submission channel.