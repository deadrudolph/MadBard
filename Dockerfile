# Use CircleCI Android NDK image as the base
FROM circleci/android:api-30-ndk

# Set environment variables
ENV ANDROID_SDK_ROOT=/home/circleci/android-sdk-linux

# Install required packages
RUN apt-get update && apt-get install -y curl unzip

# Set Gradle version
ENV GRADLE_VERSION=7.6.1

# Download and install Gradle
RUN curl -sLO "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
    && unzip -q "gradle-${GRADLE_VERSION}-bin.zip" -d /opt \
    && rm "gradle-${GRADLE_VERSION}-bin.zip"

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

