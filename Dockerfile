# Use a base image with Java and Android SDK
FROM circleci/android:api-30-ndk

# Set environment variables
ENV GRADLE_VERSION=7.6.1 \
    ANDROID_COMPILE_SDK=31 \
    ANDROID_BUILD_TOOLS=31.0.0 \
    ANDROID_SDK_ROOT=/sdk

# Update commands
USER root
RUN apt-get update

# Install curl and unzip
RUN apt-get install -y curl unzip


# Download and install Gradle
RUN curl -sLO https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip -q gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && ln -s /opt/gradle-${GRADLE_VERSION}/bin/gradle /usr/bin/gradle \
    && rm gradle-${GRADLE_VERSION}-bin.zip

# Set Gradle home
ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}

# Download and install Android SDK
RUN curl -sLO https://dl.google.com/android/repository/commandlinetools-linux-7583922_latest.zip \
    && mkdir /sdk \
    && unzip -q commandlinetools-linux-7583922_latest.zip -d /sdk \
    && rm commandlinetools-linux-7583922_latest.zip \
    && yes | /sdk/cmdline-tools/bin/sdkmanager --sdk_root=${ANDROID_SDK_ROOT} --licenses

# Set Android SDK paths
ENV PATH=$PATH:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin

# Copy project files to the container
COPY . /app
WORKDIR /app

# Download project dependencies and build the project
RUN gradle assembleDebug

# Set the default command to run the build command
CMD ["gradle", "assembleDebug"]

