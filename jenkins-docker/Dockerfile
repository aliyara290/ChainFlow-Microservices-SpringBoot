FROM jenkins/jenkins:lts

# Install Docker CLI and setup
USER root

# Install Docker CLI
RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean

# Check if docker group exists and add jenkins user to it
RUN getent group docker || groupadd -g 993 docker
RUN usermod -aG docker jenkins

# Verify installations
RUN docker --version
RUN groups jenkins

# Switch back to jenkins user
USER jenkins