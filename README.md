# JavaIDE

A modern Java IDE built with Swing and Java 1.6.

## Features

- **Code Editor**: Syntax highlighting for Java and other languages
- **File Browser**: Navigate your project files with a tree view
- **Build Support**: Integrated Ant and javac support
- **Run Support**: Run Java applications directly from the IDE
- **Multiple Tabs**: Edit multiple files simultaneously
- **Project Management**: Create new Java projects or open existing ones
- **Modern UI**: Built with Swing for cross-platform compatibility

## Requirements

- Java 1.6 or later (JDK)
- Apache Ant 1.8+ (optional, for Ant projects)
- No external dependencies - uses only standard Java libraries

## Installation

### Building from Source

#### Using Ant

```bash
ant jar
java -jar build/jar/javaide.jar
```

#### Using Gradle

```bash
./gradlew build
./gradlew run
```

#### Using Make

```bash
make install
java -jar build/jar/javaide.jar
```

### System Requirements

- **Java 1.6 JDK**: Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
- **Apache Ant** (optional): Download from [Ant website](https://ant.apache.org/bindownload.cgi)

### Platform-Specific Installation

#### Linux

```bash
# Install Java 1.6 JDK (or later)
sudo apt-get install openjdk-6-jdk  # Ubuntu/Debian
sudo dnf install java-1.6.0-openjdk-devel  # Fedora/RHEL
sudo pacman -S jdk6  # Arch Linux

# Install Ant (optional)
sudo apt-get install ant  # Ubuntu/Debian
sudo dnf install ant  # Fedora/RHEL
```

#### macOS

```bash
# Install Java 1.6 JDK (or later)
# Note: Java 1.6 may require older versions or manual installation

# Install Ant (optional)
brew install ant
```

#### Windows

1. Download and install Java 1.6 JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
2. Download and install Apache Ant from [Ant website](https://ant.apache.org/bindownload.cgi)
3. Add Java and Ant to your PATH environment variable

## Building

### Using Ant

```bash
ant compile
ant run
```

Or build a JAR:

```bash
ant jar
java -jar build/jar/javaide.jar
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
# Using Ant
ant run

# Using the JAR
java -jar build/jar/javaide.jar

# Using Gradle
./gradlew run
```

## Troubleshooting

### Error: Java version not supported

Make sure you have Java 1.6 or later installed:

```bash
java -version
```

Should show version 1.6 or higher. If not, install Java 1.6 JDK.

### Error: Ant not found

Ant is optional but recommended for building Java projects. Install Ant:

- **Linux**: `sudo apt-get install ant` (Ubuntu/Debian) or `sudo dnf install ant` (Fedora)
- **macOS**: `brew install ant`
- **Windows**: Download from [Ant website](https://ant.apache.org/bindownload.cgi)

### Error: Cannot find main class

Make sure you're running from the project root directory and the classes are compiled:

```bash
ant compile
```

### Build errors

If you encounter compilation errors, ensure:

1. Java 1.6 JDK is installed and `JAVA_HOME` is set correctly
2. Ant is using Java 1.6 (check with `ant -version`)
3. All source files are in `src/main/java/com/javaide/`


## Usage

### Creating a New Project

1. Go to **File → New Project**
2. Choose a directory for your project
3. The IDE will create a basic Java project template with:
   - `src/main/java/com/example/Main.java` - Main application entry point
   - `build.xml` - Ant build configuration
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
- **Ant projects**: Automatically detects `build.xml` and uses `ant compile`
- **Plain Java projects**: Uses `javac` to compile Java files

### Running Your Application

1. Build your project first
2. Go to **Build → Run** or click the run button in the toolbar
3. The application will run and output will appear in the output panel

For Ant projects, the IDE runs:
```bash
ant run
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
├── build.xml                         # Ant build configuration
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
- Ant/Gradle integration improvements

## License

This project is provided as-is for educational and development purposes.

