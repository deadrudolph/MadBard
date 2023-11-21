FROM openjdk:11-jdk

# Set the working directory to /app
WORKDIR /app

# Install necessary dependencies
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk

# Copy the current directory contents into the container at /app
COPY . /app

# Set environment variables for Android SDK
ENV ANDROID_HOME /usr/local/android-sdk
ENV PATH ${PATH}:${ANDROID_HOME}/tools/bin:${ANDROID_HOME}/platform-tools

# Download and install Android SDK
RUN apt-get install -y wget unzip && \
    mkdir -p ${ANDROID_HOME} && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-6609375_latest.zip -O /tmp/sdk.zip && \
    unzip /tmp/sdk.zip -d ${ANDROID_HOME} && \
    rm /tmp/sdk.zip

# Install required Android components
RUN yes | ${ANDROID_HOME}/tools/bin/sdkmanager --licenses && \
    ${ANDROID_HOME}/tools/bin/sdkmanager "build-tools;33.0.0" "platforms;android-33"

# Install bc for floating point ops and comparisons
RUN apt-get --quiet install --yes bc

# Install tree for CI machine debugging
RUN apt-get --quiet install --yes tree
# Cleaning
RUN apt-get clean
