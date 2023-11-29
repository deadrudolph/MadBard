# Use a base image with Java and Android SDK
FROM mobiledevops/android-sdk-image:28.0.3

# Set environment variables
ENV GRADLE_VERSION=7.6.1 \
    ANDROID_COMPILE_SDK=28 \
    ANDROID_BUILD_TOOLS=28.0.3 \
    ANDROID_SDK_ROOT="/opt/android-sdk" \
    ANDROID_HOME="/opt/android-sdk"

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

# Download and install Android SDK tools
RUN mkdir -p "$ANDROID_HOME" \
    && cd "$ANDROID_HOME" \
    && curl -o sdk.zip $ANDROID_SDK_ZIP_URL \
    && unzip sdk.zip \
    && rm sdk.zip

# Accept Android SDK licenses
RUN mkdir -p "$ANDROID_HOME/licenses" || true \
    && echo "24333f8a63b6825ea9c5514f83c2829b004d1" > "$ANDROID_HOME/licenses/android-sdk-license" \
    && echo "84831b9409646a918e30573bab4c9c91346d8" > "$ANDROID_HOME/licenses/android-sdk-preview-license"

# Check if sdkmanager exists and is not empty
RUN test -s "$ANDROID_HOME/tools/bin/sdkmanager" && { \
        echo "sdkmanager exists and is not empty"; \
        ls -l "$ANDROID_HOME/tools/bin"; \
    } || { \
        echo "Error: sdkmanager does not exist or is empty"; \
        exit 1; \
    }

RUN yes | $ANDROID_HOME/tools/bin/sdkmanager --sdk_root=${ANDROID_HOME} --licenses

# Update the Android SDK
RUN $ANDROID_HOME/tools/bin/sdkmanager --sdk_root=${ANDROID_HOME} --update

# Install necessary Android components
RUN $ANDROID_HOME/tools/bin/sdkmanager --sdk_root=${ANDROID_HOME} "build-tools;${ANDROID_BUILD_TOOLS}" \
    "platforms;android-${ANDROID_COMPILE_SDK}" \
    "platform-tools"

# Set Gradle home
ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}

ENV PATH $PATH:$ANDROID_HOME/tools/bin
ENV PATH $PATH:$ANDROID_HOME/platform-tools
ENV PATH $PATH:$GRADLE_HOME/bin

# Copy project files to root directory of the image
COPY . /app

WORKDIR /app

