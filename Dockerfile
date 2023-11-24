# Use a base image with Java and Android SDK
FROM openjdk:11-jdk-slim

# Set environment variables
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV GRADLE_HOME=/opt/gradle
# Set Gradle user home to /app/.gradle
ENV GRADLE_USER_HOME=/app/.gradle
ENV PATH=$PATH:$ANDROID_SDK_ROOT/tools/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator:$GRADLE_HOME/bin

# Install necessary tools and Android SDK components
RUN apt-get update -qq && \
    apt-get install -y --no-install-recommends \
        unzip \
        curl && \
    rm -rf /var/lib/apt/lists/* && \
    mkdir -p $ANDROID_SDK_ROOT && \
    curl -sSL $ANDROID_SDK_ZIP_URL -o $ANDROID_SDK_ROOT/$ANDROID_SDK_ZIP && \
    unzip -q $ANDROID_SDK_ROOT/$ANDROID_SDK_ZIP -d $ANDROID_SDK_ROOT && \
    rm $ANDROID_SDK_ROOT/$ANDROID_SDK_ZIP && \
    yes | $ANDROID_SDK_ROOT/cmdline-tools/bin/sdkmanager --licenses && \
    $ANDROID_SDK_ROOT/cmdline-tools/bin/sdkmanager --update && \
    $ANDROID_SDK_ROOT/cmdline-tools/bin/sdkmanager "build-tools;30.0.3" "platforms;android-30"

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


