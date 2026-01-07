# JavaIDE

A modern Java IDE built with Swing and Java 22.

## Features

- **Code Editor**: Syntax highlighting for Java and other languages
- **File Browser**: Navigate your project files with a tree view
- **Build Support**: Integrated Maven and javac support
- **Run Support**: Run Java applications directly from the IDE
- **Multiple Tabs**: Edit multiple files simultaneously
- **Project Management**: Create new Java projects or open existing ones
- **Modern UI**: Built with Swing for cross-platform compatibility

## Requirements

- Java 22 or later (JDK)
- Maven 3.6+ (optional, for Maven projects)
- No external dependencies - uses only standard Java libraries

## Installation

### Building from Source

#### Using Maven

```bash
mvn clean package
java -jar target/javaide-1.0.0.jar
```

#### Using Gradle

```bash
./gradlew build
./gradlew run
```

#### Using Make

```bash
make install
java -jar target/javaide-1.0.0.jar
```

### System Requirements

- **Java 22 JDK**: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
- **Maven** (optional): Download from [Maven website](https://maven.apache.org/download.cgi)

### Platform-Specific Installation

#### Linux

```bash
# Install Java 22
sudo apt-get install openjdk-22-jdk  # Ubuntu/Debian
sudo dnf install java-22-openjdk-devel  # Fedora/RHEL
sudo pacman -S jdk-openjdk  # Arch Linux

# Install Maven (optional)
sudo apt-get install maven  # Ubuntu/Debian
sudo dnf install maven  # Fedora/RHEL
```

#### macOS

```bash
# Install Java 22 using Homebrew
brew install openjdk@22

# Install Maven (optional)
brew install maven
```

#### Windows

1. Download and install Java 22 JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
2. Download and install Maven from [Maven website](https://maven.apache.org/download.cgi)
3. Add Java and Maven to your PATH environment variable

## Building

### Using Maven

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.javaide.JavaIDE"
```

Or build a JAR:

```bash
mvn clean package
java -jar target/javaide-1.0.0.jar
```

### Using Gradle

```bash
./gradlew build
./gradlew run
```

### Using Make

```bash
make compile    # Compile only
make run        # Compile and run
make package    # Build JAR
make install    # Build and show instructions
```

## Running

After building, you can run the IDE:

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.javaide.JavaIDE"

# Using the JAR
java -jar target/javaide-1.0.0.jar

# Using Gradle
./gradlew run
```

## Troubleshooting

### Error: Java version not supported

Make sure you have Java 22 installed:

```bash
java -version
```

Should show version 22 or higher. If not, install Java 22 JDK.

### Error: Maven not found

Maven is optional but recommended for building Java projects. Install Maven:

- **Linux**: `sudo apt-get install maven` (Ubuntu/Debian) or `sudo dnf install maven` (Fedora)
- **macOS**: `brew install maven`
- **Windows**: Download from [Maven website](https://maven.apache.org/download.cgi)

### Error: Cannot find main class

Make sure you're running from the project root directory and the classes are compiled:

```bash
mvn clean compile
```

### Build errors

If you encounter compilation errors, ensure:

1. Java 22 JDK is installed and `JAVA_HOME` is set correctly
2. Maven is using Java 22 (check with `mvn -version`)
3. All source files are in `src/main/java/com/javaide/`

## Running

```bash
./valaide
```

Or if built with Meson:

```bash
./build/valaide
```

Or install it:

```bash
meson install -C build
valaide
```

## Usage

### Creating a New Project

1. Go to **File → New Project**
2. Choose a directory for your project
3. The IDE will create a basic Java project template with:
   - `src/main/java/com/example/Main.java` - Main application entry point
   - `pom.xml` - Maven build configuration
   - `README.md` - Project documentation

### Opening a Project

1. Go to **File → Open Project**
2. Select your project directory
3. The file browser will show your project structure

### Building Your Project

1. Open your Java project
2. Go to **Build → Build Project** or click the build button in the toolbar
3. The build output will appear in the output panel

The IDE supports:
- **Maven projects**: Automatically detects `pom.xml` and uses `mvn compile`
- **Plain Java projects**: Uses `javac` to compile Java files

### Running Your Application

1. Build your project first
2. Go to **Build → Run** or click the run button in the toolbar
3. The application will run and output will appear in the output panel

For Maven projects, the IDE runs:
```bash
mvn exec:java -Dexec.mainClass="com.example.Main"
```

### Editing Files

- Click on files in the file browser to open them in the editor
- Use **File → New File** to create a new file
- Use **File → Save** or **Ctrl+S** to save the current file
- Multiple files can be open in tabs
- Syntax highlighting is automatically applied for Java files

### Supported File Types

The IDE supports syntax highlighting for:
- **Java** (`.java`) - Full syntax highlighting with keywords, strings, and comments
- Other file types can be edited but without syntax highlighting

## Keyboard Shortcuts

- **Ctrl+N**: New File
- **Ctrl+O**: Open File
- **Ctrl+S**: Save File
- **Ctrl+Shift+S**: Save As
- **Ctrl+Z**: Undo
- **Ctrl+Shift+Z**: Redo
- **Ctrl+X**: Cut
- **Ctrl+C**: Copy
- **Ctrl+V**: Paste
- **Ctrl+R**: Run
- **Ctrl+Q**: Quit

## Project Structure

A typical Java project created with JavaIDE looks like:

```
myproject/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   └── Main.java    # Main application entry point
├── pom.xml                           # Maven build configuration
└── README.md                          # Project documentation
```

## Contributing

This is a basic IDE implementation. Feel free to extend it with:
- Code completion and IntelliSense
- Error highlighting and linting
- Debugger integration (JDWP)
- Git integration
- Plugin system
- Themes and customization
- Advanced syntax highlighting for more languages
- Code formatting
- Refactoring tools
- Maven/Gradle integration improvements

## License

This project is provided as-is for educational and development purposes.

