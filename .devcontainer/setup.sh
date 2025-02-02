#!/bin/bash
set -e

sudo apt update && sudo apt install -y --no-install-recommends \
    ffmpeg \
    locales \
    ca-certificates \
    python3-mutagen \
    maven \
    git \
    wget \
    curl

wget https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -O /usr/local/bin/yt-dlp
chmod a+rx /usr/local/bin/yt-dlp

echo "en_GB.UTF-8 UTF-8" | sudo tee -a /etc/locale.gen
sudo locale-gen en_GB.UTF-8
echo "export LANG=en_GB.UTF-8" >> ~/.bashrc

git clone https://github.com/luishidalgoa/CloudStorage_Project.git
cd CloudStorage_Project/Microservicios/Spring_project/music
mvn clean package
mv target/*.jar /app.jar

sudo apt-get remove -y maven git wget curl && sudo apt-get autoremove -y && sudo apt-get clean
rm -rf ~/CloudStorage_Project

echo "Setup completed!"
