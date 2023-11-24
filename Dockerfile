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

# Download Android SDK
RUN mkdir -p $ANDROID_SDK_ROOT && \
    curl -sSL $ANDROID_SDK_ZIP_URL -o $ANDROID_SDK_ROOT/$ANDROID_SDK_ZIP

# Unzip Android SDK
RUN unzip -q $ANDROID_SDK_ROOT/$ANDROID_SDK_ZIP -d $ANDROID_SDK_ROOT && \
    rm $ANDROID_SDK_ROOT/$ANDROID_SDK_ZIP

# Accept Android SDK licenses
RUN yes | $ANDROID_SDK_ROOT/cmdline-tools/bin/sdkmanager --licenses

# Update Android SDK
RUN $ANDROID_SDK_ROOT/cmdline-tools/bin/sdkmanager --update

# Install Android SDK components
RUN $ANDROID_SDK_ROOT/cmdline-tools/bin/sdkmanager "build-tools;30.0.3" "platforms;android-30"

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


