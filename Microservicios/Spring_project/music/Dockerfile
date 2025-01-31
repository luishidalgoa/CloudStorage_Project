# Usar la imagen base más ligera
FROM debian:12-slim

# Instalar dependencias y limpiar caché
RUN apt update && apt install -y --no-install-recommends \
    ffmpeg \
    wget \
    locales \
    ca-certificates \
    python3 \
    python3-pip \
    python3-venv \
    python3-mutagen \
    curl \
    git \
    maven \
    && rm -rf /var/lib/apt/lists/*

# Descargar yt-dlp en una sola capa
RUN wget https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -O /usr/local/bin/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp

# Configurar locales sin archivos temporales
RUN sed -i '/^# en_GB.UTF-8/s/^#//g' /etc/locale.gen \
    && locale-gen en_GB.UTF-8 \
    && update-locale LANG=en_GB.UTF-8

# Variables de entorno (se definen juntas para reducir capas)
ENV LANG=en_GB.UTF-8 \
    LC_ALL=en_GB.UTF-8 \
    CONFIG_SERVER_URI="http://localhost:8888" \
    EUREKA_URI="http://localhost:8761/eureka/" \
    MUSIC_SERVER_PORT="8084" \
    NEXTCLOUD_URI="lb://nextcloud" \
    DEPLOY="local"

# Detectar la arquitectura y descargar el JDK correspondiente
RUN ARCH=$(uname -m) && \
    if [ "$ARCH" = "x86_64" ]; then \
        wget -q https://download.oracle.com/java/21/archive/jdk-21_linux-x64_bin.deb && \
        apt install -y --no-install-recommends ./jdk-21_linux-x64_bin.deb && \
        rm -rf jdk-21_linux-x64_bin.deb /var/lib/apt/lists/*; \
    elif [ "$ARCH" = "aarch64" ]; then \
        wget -q https://download.oracle.com/java/21/archive/jdk-21_linux-aarch64_bin.tar.gz && \
        mkdir -p /usr/lib/jvm && \
        tar -xzf jdk-21_linux-aarch64_bin.tar.gz -C /usr/lib/jvm && \
        rm -rf jdk-21_linux-aarch64_bin.tar.gz; \
    fi

# Establecer el PATH globalmente para todas las capas
ENV PATH="/usr/lib/jvm/jdk-21/bin:$PATH"

# Copiar el archivo JAR
RUN git clone https://github.com/luishidalgoa/CloudStorage_Project.git && \
    cd ./CloudStorage_Project/Microservicios/Spring_project/music && \
    mvn clean package
    
RUN mv /CloudStorage_Project/Microservicios/Spring_project/music/target/*.jar /app.jar

RUN apt-get remove -y maven git wget curl && apt-get autoremove -y && apt-get clean && rm -rf /CloudStorage_Project

# Comando de arranque
CMD ["java", "-jar", "/app.jar"]
