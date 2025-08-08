# Android Development Docker Environment

This directory contains Docker configuration files for setting up an Android development environment in a container. This is useful for CI/CD pipelines and for developers who want to use a containerized development environment.

## Features

- Ubuntu 22.04 base image
- JDK 17 (or 11 as fallback)
- Android SDK with command line tools, platform tools, build tools, and platforms
- Android NDK for native code development
- Gradle for building Android projects
- Non-root user for running the container

## Prerequisites

- Docker
- Docker Compose (optional, for using docker-compose.yml)

## Building the Docker Image

To build the Docker image, run:

```bash
cd /workspace/android-dev-setup
docker build -t android-dev -f docker/Dockerfile .
```

## Running the Docker Container

To run the Docker container, use:

```bash
docker run -it --rm -v $(pwd):/workspace android-dev
```

This will start a bash shell in the container with the current directory mounted as `/workspace`.

## Using Docker Compose

Alternatively, you can use Docker Compose:

```bash
cd /workspace/android-dev-setup
docker-compose -f docker/docker-compose.yml up -d
docker-compose -f docker/docker-compose.yml exec android-dev bash
```

This will start the container and open a bash shell in it.

## Environment Variables

The following environment variables are set in the container:

- `ANDROID_DEV_HOME`: `/opt/android-dev`
- `JAVA_HOME`: `/opt/android-dev/jdk`
- `ANDROID_SDK_ROOT`: `/opt/android-dev/android-sdk`
- `ANDROID_HOME`: `/opt/android-dev/android-sdk`
- `ANDROID_NDK_HOME`: `/opt/android-dev/android-sdk/ndk/25.2.9519653`
- `GRADLE_HOME`: `/opt/android-dev/gradle`

## Using the Container for CI/CD

You can use this Docker image in your CI/CD pipelines. For example, in GitHub Actions:

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
    container:
      image: android-dev
    steps:
      - uses: actions/checkout@v2
      - name: Build Android app
        run: |
          cd your-android-project
          ./gradlew assembleDebug
```

## Customizing the Docker Image

You can customize the Docker image by modifying the Dockerfile. For example, you can:

- Change the base image
- Install additional packages
- Change the Android SDK, NDK, or Gradle versions
- Add custom configuration files

After making changes, rebuild the Docker image:

```bash
docker build -t android-dev-custom -f docker/Dockerfile .
```

## Troubleshooting

If you encounter any issues with the Docker container, check the following:

- Make sure Docker is installed and running
- Check if the container has enough resources (CPU, memory, disk space)
- Verify that the environment variables are set correctly
- Check if the mounted volumes have the correct permissions

If you still have issues, you can try rebuilding the Docker image with the `--no-cache` option:

```bash
docker build --no-cache -t android-dev -f docker/Dockerfile .
```