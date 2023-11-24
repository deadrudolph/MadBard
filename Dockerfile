# Use a base image with Java and Android SDK
FROM openjdk:11-jdk-slim

# Set environment variables
ENV ANDROID_SDK_ZIP commandlinetools-linux-6609375_latest.zip
ENV ANDROID_SDK_ZIP_URL https://dl.google.com/android/repository/$ANDROID_SDK_ZIP
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV GRADLE_HOME=/opt/gradle
# Set Gradle user home to /app/.gradle
ENV GRADLE_USER_HOME=/app/.gradle
ENV PATH=$PATH:$ANDROID_SDK_ROOT/tools/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator:$GRADLE_HOME/bin

# Debugging statements
RUN echo "ANDROID_SDK_ZIP: $ANDROID_SDK_ZIP"
RUN echo "ANDROID_SDK_ZIP_URL: $ANDROID_SDK_ZIP_URL"
RUN echo "ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"
RUN echo "GRADLE_HOME: $GRADLE_HOME"
RUN echo "GRADLE_USER_HOME: $GRADLE_USER_HOME"

# Create Android SDK configuration
RUN mkdir /root/.android
RUN touch /root/.android/repositories.cfg

# Download and unzip Android SDK
ADD $ANDROID_SDK_ZIP_URL /opt/android/
RUN curl -L $ANDROID_SDK_ZIP_URL -o /opt/android/$ANDROID_SDK_ZIP
RUN unzip -q /opt/android/$ANDROID_SDK_ZIP -d $ANDROID_SDK_ROOT && rm /opt/android/$ANDROID_SDK_ZIP

## Install Android SDK into Image
RUN mkdir /opt/gradle
ADD $GRADLE_ZIP_URL /opt/gradle
RUN ls /opt/gradle/
RUN unzip /opt/gradle/$GRADLE_ZIP -d /opt/gradle

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


