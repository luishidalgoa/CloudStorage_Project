# Usa una imagen base de Debian para mayor flexibilidad
FROM debian:12.8

# Actualiza los paquetes e instala Apache, MySQL, PHP y las extensiones necesarias
RUN apt-get update && apt-get install -y \
    apache2 \
    libapache2-mod-php \
    php-mysql \
    default-mysql-client-core \
    php \
    php-mbstring \
    php-xml \
    php-curl \
    php-zip \
    php-gd \
    php-intl \
    php-bcmath \
    php-imagick \
    php-gmp \
    wget \
    php8.2-apcu \
    python3 \
    python3-pip \
    python3-venv \
    ffmpeg \
    git \
    openssh-server && \
    apt-get clean

# Configurar módulos de PHP
RUN phpenmod mbstring xml apcu

# Activar módulos de Apache
RUN a2enmod rewrite headers env dir mime

# Crear entorno virtual de Python e instalar dependencias
RUN python3 -m venv /env && \
    /env/bin/pip install yt-dlp eyed3

# Configurar OpenSSH
RUN mkdir /var/run/sshd && \
    echo 'root:1234' | chpasswd && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config

# Clonar el repositorio de tu proyecto
RUN mkdir /WorkSpace && \
    git clone https://github.com/luishidalgoa/CloudStorage_Project.git /WorkSpace/CloudStorage_Project

# Copiar contenido de Nextcloud y configurar Apache
COPY ./nextcloud /var/www/html/nextcloud
COPY nextcloud.conf /etc/apache2/sites-available/000-default.conf
RUN chown -R www-data:www-data /var/www/html/nextcloud

# Reiniciar Apache
RUN service apache2 restart

# Exponer los puertos 80 (Apache) y 22 (SSH)
EXPOSE 80 22

# Comando para iniciar Apache y SSH en primer plano
CMD ["sh", "-c", "service ssh start && apachectl -D FOREGROUND"]
