# Use a base image with Java and Android SDK
FROM openjdk:11-jdk-slim

ENV SDK_URL="https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip" \
    ANDROID_HOME="/usr/local/android-sdk" \
    ANDROID_VERSION=28 \
    ANDROID_BUILD_TOOLS_VERSION=28.0.3

RUN mkdir "$ANDROID_HOME" .android \
    && cd "$ANDROID_HOME" \
    && curl -o sdk.zip $SDK_URL \
    && unzip sdk.zip \
    && rm sdk.zip \
    && mkdir "$ANDROID_HOME/licenses" || true \
    && echo "24333f8a63b6825ea9c5514f83c2829b004d1" > "$ANDROID_HOME/licenses/android-sdk-license" \
    && echo "84831b9409646a918e30573bab4c9c91346d8" > "$ANDROID_HOME/licenses/android-sdk-preview-license"

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


