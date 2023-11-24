# Use a base image with Java and Android SDK
FROM openjdk:11-jdk-slim

# Set environment variables
ENV ANDROID_SDK_ZIP commandlinetools-linux-6609375_latest.zip
ENV ANDROID_SDK_ZIP_URL https://dl.google.com/android/repository/$ANDROID_SDK_ZIP
ENV ANDROID_SDK_ROOT /opt/android-sdk
ENV GRADLE_HOME /opt/gradle
# Set Gradle user home to /app/.gradle
ENV GRADLE_USER_HOME /app/.gradle
ENV PATH $PATH:$ANDROID_SDK_ROOT/tools/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator:$GRADLE_HOME/bin

# Debugging statements
RUN echo "ANDROID_SDK_ZIP: $ANDROID_SDK_ZIP" && \
    echo "ANDROID_SDK_ZIP_URL: $ANDROID_SDK_ZIP_URL" && \
    echo "ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT" && \
    echo "GRADLE_HOME: $GRADLE_HOME" && \
    echo "GRADLE_USER_HOME: $GRADLE_USER_HOME"

# Create Android SDK configuration
RUN mkdir -p /root/.android && \
    touch /root/.android/repositories.cfg

# Install necessary tools
RUN apt-get update -qq && \
    apt-get install -y --no-install-recommends \
        unzip \
        curl && \
    rm -rf /var/lib/apt/lists/*

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


