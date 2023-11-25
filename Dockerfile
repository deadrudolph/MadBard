# Use a base image with Java and Android SDK
FROM openjdk:11-jdk-alpine

# Set environment variables
ENV GRADLE_VERSION=7.6.1 \
    ANDROID_COMPILE_SDK=31 \
    ANDROID_BUILD_TOOLS=31.0.0 \
    ANDROID_SDK_ROOT=/sdk

# Install required dependencies
RUN apk --no-cache add curl unzip git

# Download and install Gradle
RUN curl -sLO https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip -q gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && rm gradle-${GRADLE_VERSION}-bin.zip

# Set Gradle home
ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}

# Download and install Android SDK command-line tools
RUN curl -sLO https://dl.google.com/android/repository/commandlinetools-linux-7583922_latest.zip \
    && mkdir /sdk \
    && unzip -q commandlinetools-linux-7583922_latest.zip -d /sdk \
    && rm commandlinetools-linux-7583922_latest.zip

# Set Android SDK paths
ENV PATH=$PATH:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin

# Create a non-root user for running the build
RUN adduser -D builder
USER builder

# Copy project files to the container
COPY . /home/builder/app
WORKDIR /home/builder/app

# Download project dependencies and build the project
RUN ${GRADLE_HOME}/bin/gradle assembleDebug

# Set the default command to run the build command
CMD ["${GRADLE_HOME}/bin/gradle", "assembleDebug"]

