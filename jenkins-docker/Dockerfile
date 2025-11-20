FROM jenkins/jenkins:lts

USER root

RUN apt-get update && \
    apt-get install -y docker.io && \
    apt-get clean

# Create docker group with fixed GID to match host (usually 993 on Ubuntu/Debian)
RUN groupadd -g 993 docker

# Add jenkins user to docker group
RUN usermod -aG docker jenkins

RUN docker --version

# Switch back to jenkins user
USER jenkins