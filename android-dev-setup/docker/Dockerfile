FROM ubuntu:22.04

# Prevent interactive prompts during package installation
ENV DEBIAN_FRONTEND=noninteractive

# Set environment variables
ENV ANDROID_DEV_HOME=/opt/android-dev
ENV JAVA_HOME=${ANDROID_DEV_HOME}/jdk
ENV ANDROID_SDK_ROOT=${ANDROID_DEV_HOME}/android-sdk
ENV ANDROID_HOME=${ANDROID_SDK_ROOT}
ENV ANDROID_NDK_HOME=${ANDROID_SDK_ROOT}/ndk/25.2.9519653
ENV GRADLE_HOME=${ANDROID_DEV_HOME}/gradle
ENV PATH=${JAVA_HOME}/bin:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_NDK_HOME}:${GRADLE_HOME}/bin:${PATH}

# Install system dependencies
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    zip \
    curl \
    git \
    build-essential \
    libz-dev \
    libncurses-dev \
    libbz2-dev \
    liblzma-dev \
    libssl-dev \
    libreadline-dev \
    libsqlite3-dev \
    libgl1-mesa-dev \
    libxrandr-dev \
    libxxf86vm-dev \
    libxinerama-dev \
    libxcursor-dev \
    libxi-dev \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create installation directory
RUN mkdir -p ${ANDROID_DEV_HOME}

# Copy setup scripts
COPY *.sh ${ANDROID_DEV_HOME}/

# Make scripts executable
RUN chmod +x ${ANDROID_DEV_HOME}/*.sh

# Run CI setup script
RUN cd ${ANDROID_DEV_HOME} && ./ci-setup.sh ${ANDROID_DEV_HOME}

# Set working directory
WORKDIR /workspace

# Create a non-root user for running the container
RUN groupadd -r android && useradd -r -g android -m -d /home/android android
RUN chown -R android:android ${ANDROID_DEV_HOME}
RUN chown -R android:android /workspace

# Switch to non-root user
USER android

# Set up environment
RUN echo "source ${ANDROID_DEV_HOME}/env-setup.sh" >> /home/android/.bashrc

# Default command
CMD ["bash"]