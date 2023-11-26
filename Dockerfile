# Use a base image with Java and Android SDK
FROM circleci/android:api-30-ndk

# Set environment variables
ENV GRADLE_VERSION=7.6.1 \
    ANDROID_COMPILE_SDK=31 \
    ANDROID_BUILD_TOOLS=31.0.0 \
    ANDROID_SDK_ROOT=/sdk

ENV ANDROID_SDK_ZIP commandlinetools-linux-6609375_latest.zip
ENV ANDROID_SDK_ZIP_URL https://dl.google.com/android/repository/$ANDROID_SDK_ZIP

# Update commands
USER root
RUN curl https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
RUN apt-get update

# Install curl and unzip
RUN apt-get install -y curl unzip

# Download and install Gradle
RUN curl -sLO https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip -q gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle \
    && rm gradle-${GRADLE_VERSION}-bin.zip

# Download and install Android SDK
RUN curl -sLO "${ANDROID_SDK_ZIP_URL}"
RUN mkdir /sdk
RUN unzip -q "${ANDROID_SDK_ZIP}" -d /sdk
RUN rm "${ANDROID_SDK_ZIP}"
RUN /sdk/cmdline-tools/bin/sdkmanager --sdk_root=${ANDROID_SDK_ROOT} --licenses

# Set Gradle home
ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}

# Set Android SDK paths
ENV PATH=$PATH:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin

# Copy project files to the container
COPY . .
WORKDIR /

