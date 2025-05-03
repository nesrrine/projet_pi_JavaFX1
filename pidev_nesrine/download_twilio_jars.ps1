# Script to download Twilio JAR files
$libDir = "lib"

# Create lib directory if it doesn't exist
if (-not (Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir
}

# URLs for Twilio JAR files
$urls = @(
    "https://repo1.maven.org/maven2/com/twilio/sdk/twilio/9.14.0/twilio-9.14.0.jar",
    "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar",
    "https://repo1.maven.org/maven2/org/apache/httpcomponents/httpcore/4.4.13/httpcore-4.4.13.jar",
    "https://repo1.maven.org/maven2/commons-codec/commons-codec/1.15/commons-codec-1.15.jar",
    "https://repo1.maven.org/maven2/commons-logging/commons-logging/1.2/commons-logging-1.2.jar",
    "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.14.0/jackson-core-2.14.0.jar",
    "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.14.0/jackson-annotations-2.14.0.jar",
    "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.14.0/jackson-databind-2.14.0.jar",
    "https://repo1.maven.org/maven2/javax/xml/bind/jaxb-api/2.3.1/jaxb-api-2.3.1.jar",
    "https://repo1.maven.org/maven2/io/jsonwebtoken/jjwt-api/0.11.2/jjwt-api-0.11.2.jar",
    "https://repo1.maven.org/maven2/io/jsonwebtoken/jjwt-impl/0.11.2/jjwt-impl-0.11.2.jar",
    "https://repo1.maven.org/maven2/io/jsonwebtoken/jjwt-jackson/0.11.2/jjwt-jackson-0.11.2.jar"
)

# Download each JAR file
foreach ($url in $urls) {
    $fileName = $url.Split("/")[-1]
    $outputPath = Join-Path $libDir $fileName
    
    Write-Host "Downloading $fileName..."
    try {
        Invoke-WebRequest -Uri $url -OutFile $outputPath
        Write-Host "Downloaded $fileName successfully."
    } catch {
        Write-Host "Failed to download $fileName. Error: $_"
    }
}

Write-Host "Download complete. JAR files are in the $libDir directory."
