# Use a base image with Java and Android SDK
FROM openjdk:11-jdk-slim

# Set environment variables
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV GRADLE_HOME=/opt/gradle
# Set Gradle user home to /app/.gradle
ENV GRADLE_USER_HOME=/app/.gradle
ENV PATH=$PATH:$ANDROID_SDK_ROOT/tools/bin:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/emulator:$GRADLE_HOME/bin

RUN mkdir /root/.android
RUN touch /root/.android/repositories.cfg

ADD $ANDROID_SDK_ZIP_URL /opt/android/
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


