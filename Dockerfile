# Use a base image with Java and Android SDK
FROM mobiledevops/android-sdk-image:28.0.3

# Copy project files to root directory of the image
COPY . /app

WORKDIR /app

