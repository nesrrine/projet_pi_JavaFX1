# Get the current directory
$currentDir = Get-Location

# JavaFX path - change this to your JavaFX SDK location if needed
# Download JavaFX SDK from https://openjfx.io/
$javaFXPath = "$env:USERPROFILE\Downloads\javafx-sdk-20\lib"

# If JavaFX path doesn't exist, try to download it
if (-not (Test-Path $javaFXPath)) {
    Write-Host "JavaFX SDK not found. Please download it from https://openjfx.io/"
    Write-Host "Extract it to your system and update the javaFXPath variable in this script."
    exit
}

# Make sure Twilio JARs are downloaded
if (-not (Test-Path "lib\twilio-9.14.0.jar")) {
    Write-Host "Downloading Twilio dependencies..."
    .\download_twilio_jars.ps1
}

# Run the application
Write-Host "Running the application..."

try {
    # Create the classpath with all JAR files in the lib directory
    $libPath = "$currentDir\lib"
    $classesPath = "$currentDir\target\classes"
    
    # Create the classpath string
    $libJars = Get-ChildItem "$libPath\*.jar" | ForEach-Object { $_.FullName }
    $classpath = "$classesPath;$($libJars -join ';')"
    
    # Run the application
    & java --module-path $javaFXPath --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media -cp $classpath test.Main
}
catch {
    Write-Host "Error running the application: $_"
    exit 1
} 