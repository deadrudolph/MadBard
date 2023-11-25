# Use CircleCI Android NDK image as the base
FROM circleci/android:api-30-ndk

# Set environment variables
ENV GRADLE_VERSION=7.6.1 \
    ANDROID_SDK_ROOT=/home/circleci/android-sdk-linux

# Download and install Gradle
RUN curl -sLO https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip -q gradle-${GRADLE_VERSION}-bin.zip -d /opt \
    && rm gradle-${GRADLE_VERSION}-bin.zip

# Set Gradle home and path
ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}
ENV PATH=$PATH:$GRADLE_HOME/bin

# Copy project files to the container
COPY . /app
WORKDIR /app

# Download project dependencies and build the project
RUN gradle assembleDebug

# Set the default command to run the build command
CMD ["gradle", "assembleDebug"]

