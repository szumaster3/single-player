### [Emulation](https://szumaster3.github.io/530-content/)

This repository is a fork of the original [2009scape project](https://gitlab.com/2009scape/2009scape), licensed under the **GNU Affero General Public License v3.0 (AGPL-3.0)**.

___

#### Essentials

_For Windows users_ - Enable developer mode in Windows settings first.

Make sure you have the following installed:

- **Java 11**: You can download it from [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
  or [AdoptOpenJDK](https://adoptium.net/temurin/releases/?version=11).
- **IntelliJ IDEA**: Download and install from [JetBrains](https://www.jetbrains.com/idea/download/).

---

#### Forking the Repository

1. Click the **Fork** button at the top right to create a personal copy of the repository.

___

#### Cloning the Repository

1. After forking the repository, go to your fork’s page.
2. Click the **Clone** button and copy the URL (either HTTPS or SSH).
3. Open a terminal or command prompt and run the following command to clone the repository.

```bash
git clone <your-fork-url>
```

#### Import into IntelliJ IDEA

1. Open **IntelliJ IDEA**.
2. Go to **File > New > Project from Version Control > Git**.
3. Paste the cloned repository URL into the **URL** field.
4. Select the directory where you want to store the project and click **Clone**.

---

#### GitLab VCS in IntelliJ IDEA

**Generate an SSH key** (if you don’t already have one):

```bash
ssh-keygen -t ed25519 -C "<comment>"
```

Add the **SSH key** to your **GitLab** account.

**Configure Git**:

- Set your GitLab username: `git config --global user.name "Your Name"`
- Set your GitLab email: `git config --global user.email "your_email@example.com"`

---

#### Building the Project

1. Open the **Terminal** inside IntelliJ IDEA (or use an external terminal).
2. Navigate to the project’s root directory if you're not already there.
3. Run the following command to build the project:

```bash
mvn clean install
```

#### Setting Up the Project

1. Open project in IntelliJ IDEA, navigate to **File > Settings** (or **Preferences** on macOS).
2. Under **Build, Execution, Deployment > Build Tools**, make sure **Maven** is properly configured to use your
   installed version.
3. Ensure that Java 11 or later is selected as the **Project SDK**.
4. Sync the Maven project by clicking on the **Maven** tab on the right and then clicking the **Refresh** button.

---

#### Launching

To launch the project, you can use Maven with the following command:

```bash
mvn exec:java -f pom.xml
```

This will compile and run the Java application based on the configuration in the `pom.xml` file.

___

#### License

- This project is licensed under the **AGPL 3.0**. This means you are free to modify, distribute, and use this code,
  provided you comply with the terms of the AGPL 3.0 license.

- As this is a fork of the [2009scape](https://gitlab.com/2009scape/2009scape) project, all modifications to this code, including derivative works, must also be licensed under the **AGPL 3.0**.

- You can find more information about the license [here](https://www.gnu.org/licenses/agpl-3.0.html).

- **Source Code**: You must make the source code available to anyone who interacts with this software, especially in online services.

- The license applies to the entire repository unless otherwise stated.
