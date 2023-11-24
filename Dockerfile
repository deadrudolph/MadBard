# Use a base image with Java and Android SDK
FROM openjdk:11-jdk-slim

ENV SDK_URL="https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip" \
    ANDROID_HOME="/usr/local/android-sdk" \
    ANDROID_VERSION=28 \
    ANDROID_BUILD_TOOLS_VERSION=28.0.3

# Update commands
RUN apt-get update

# Install curl
RUN apt-get install -y curl

#Install unzip
RUN apt-get install -y unzip

# Create a script to set up Android SDK
RUN mkdir -p "$ANDROID_HOME" \
    && mkdir -p "/root/.android" \
    && cd "$ANDROID_HOME"

#Download SDK
RUN curl -o sdk.zip $SDK_URL

RUN unzip sdk.zip && rm sdk.zip

# Accept Android SDK licenses
RUN mkdir -p "$ANDROID_HOME/licenses" || true \
    && echo "24333f8a63b6825ea9c5514f83c2829b004d1" > "$ANDROID_HOME/licenses/android-sdk-license" \
    && echo "84831b9409646a918e30573bab4c9c91346d8" > "$ANDROID_HOME/licenses/android-sdk-preview-license"

# Run the script to set up Android SDK
RUN /tmp/setup-android-sdk.sh

RUN $ANDROID_HOME/tools/bin/sdkmanager --update
RUN $ANDROID_HOME/tools/bin/sdkmanager "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" \
    "platforms;android-${ANDROID_VERSION}" \
    "platform-tools"

# Download and install Gradle
RUN curl -sSL https://services.gradle.org/distributions/gradle-7.6.1-bin.zip -o gradle.zip \
    && unzip -q gradle.zip -d /opt \
    && rm gradle.zip

# Set the working directory
WORKDIR /app

# Copy the Android project into the container
COPY . /app

# Grant execute permission for gradlew
RUN chmod +x gradlew


