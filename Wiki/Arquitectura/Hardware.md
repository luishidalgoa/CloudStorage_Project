# 📑 Índice de Contenidos - NAS Home Server

---

- [1. 🧱 Hardware y Componentes](#-hardware-y-componentes)
- [2. 💾 Gestión de Unidades de Disco](#-gestión-de-unidades-de-disco)
- [3. 🧠 Arquitectura de Almacenamiento](#-arquitectura-de-almacenamiento)
    - [3.1. Configuración de almacenamiento](#-configuración-de-almacenamiento)
    - [3.2. Implementación y Configuración ZFS](#-configuración-zfs)
    - [3.3. Análisis de Capacidad Estimada](#-capacidad-estimada)
- [4. 🌐 Configuración de Red y Conectividad](#-configuración-de-red)
- [5. 👥 Gestión de Accesos y Usuarios](#-gestión-de-accesos-y-usuarios)
    - [5.1. Administración de Usuarios](#-usuarios)
    - [5.2. Espacio asignado por usuario (Cuotas)](#-espacio-por-usuario-6-personas)
- [6. 🧩 Ecosistema de Servicios](#-servicios)
- [7. 🔐 Seguridad y Mitigación de Riesgos](#-seguridad)
    - [7.1. Riesgos Críticos Conocidos](#-riesgos-conocidos)
        - [7.1.1. ❌ Ausencia de backups externos](#-sin-backups-externos)
        - [7.1.2. ❌ Ausencia de SAI (UPS)](#-sin-sai-ups)
- [8. 📈 Escalabilidad y Crecimiento](#-escalabilidad)
    - [8.1. Estrategia general de crecimiento](#-estrategia-general-de-crecimiento)
    - [8.2. Modelo de expansión principal (Recomendado)](#-modelo-de-expansión-principal-recomendado)
    - [8.3. La Regla de Crecimiento](#-regla-de-crecimiento)
    - [8.4. Flujo de arquitectura actual y futura](#-flujo-de-arquitectura-actual)
    - [8.5. Impacto en servicios (Nextcloud)](#-impacto-en-nextcloud)
    - [8.6. 🔥 Principios fundamentales de escalabilidad](#-principios-de-escalabilidad)
- [9. 📊 Análisis de Rendimiento y Uso](#-análisis-de-rendimiento-y-uso)
    - [9.1. Uso recomendado del sistema](#-uso-recomendado)
    - [9.2. Comparativa de carga real (6 usuarios)](#-comparativa-real-6-usuarios)
- [10. 🔧 Mantenimiento y Notas Futuras](#-notas-futuras)
- [11. 🧨 Resumen Ejecutivo](#-resumen)

# 🧱 NAS Home Server - Documentación

---

## 📦 Hardware

| Componente | Detalle |
|--|--|
| NAS | Asustor Lockerstor 6 AS6706T |
| Precio NAS | 874€ |
| RAM | De serie |
| SSD NVMe | 250GB (apps / cache) |

---

## 💾 Discos

| Concepto | Valor |
|--|--|
| Modelo | Seagate IronWolf 6TB 7200RPM |
| Cantidad | 6 discos |
| Precio unitario | 229,99€ |
| Total discos | 1379,94€ |

---

## 🧠 Configuración de almacenamiento

| Parámetro | Valor |
|--|--|
| Sistema de archivos | ZFS |
| Tipo RAID | RAIDZ2 |

### Características

- ✔ Se pierden 2 discos en paridad  
- ✔ Alta tolerancia a fallos (hasta 2 discos)  
- ✔ Protección contra corrupción de datos  

---

## 📊 Capacidad estimada

| Tipo | Capacidad |
|--|--|
| Bruto | 36 TB |
| Útil teórico | ~24 TB |
| Útil real seguro | ~18–20 TB |

---

## ⚙️ Configuración ZFS

| Parámetro | Estado |
|--|--|
| Compresión | Activada (LZ4) |
| Snapshots | Pendiente |

### 📌 Nota sobre compresión

- Reduce espacio en datos comprimibles  
- Mejora rendimiento en muchos casos  
- Impacto CPU despreciable  

---

## 🌐 Red

| Parámetro | Valor |
|--|--|
| Velocidad | 1 Gbps |
| IP local | 192.168.0.24 |
| IP pública | Dinámica |

---

## 👥 Usuarios

| Concepto | Valor |
|--|--|
| Usuarios reales | 4 |
| Máximo previsto | 6 |

### Distribución

- Espacio individual por usuario  
- Carpeta compartida:
  - Tamaño: 380 GB  
  - Compartida entre varios usuarios  
  - Descuento proporcional en 4 usuarios  

---

## 🧩 Servicios

| Servicio | Uso |
|--|--|
| Nextcloud | Almacenamiento y sincronización |
| Docker | Contenedores |
| Jellyfin | Multimedia |
| Navidrome | Música |

---

## 🔐 Seguridad

| Elemento | Estado |
|--|--|
| Dominio | CloudDNS |
| Acceso externo | Sí |

### ⚠️ Pendiente

- Configurar port forwarding  
- Revisar exposición  
- Forzar HTTPS  

---

## ⚠️ Riesgos conocidos

### ❌ Sin backups externos
- Posible pérdida total de datos  
- RAID ≠ backup  

### ❌ Sin SAI (UPS)
- Riesgo de corrupción en cortes de luz  

---

## 📊 Uso recomendado

- No superar 80% de uso  
- Monitorizar SMART  
- Revisar estado del pool  

---

## 🔧 Notas futuras

- Snapshots automáticos  
- Backup externo  
- Ampliación de RAM  
- Red 2.5GbE  

---

## 🧨 Resumen

Sistema NAS con:
- 6 discos en RAIDZ2  
- Alta tolerancia a fallos  
- Servicios multimedia y cloud  

⚠️ Riesgos:
- Sin backup  
- Sin protección eléctrica



## 📊 Comparativa real (6 usuarios)

| Concepto | 24 TB (6×4TB) | 32 TB (ej: 8×4TB) |
|--|--|--|
| Bruto (marketing) | 24 TB | 32 TB |
| Real en TiB | ~21.8 TiB | ~29.1 TiB |
| RAIDZ2 útil | ~14.5 TiB | ~21.8 TiB |
| Tras overhead (~8%) | ~13.3 TiB | ~20.0 TiB |
| Uso seguro (~80%) | ~10.6 TiB | ~16.0 TiB |
| − sistema (50GB) | ~10.55 TiB | ~15.95 TiB |

---

## 👥 Espacio por usuario (6 personas)

| Configuración | Espacio por persona |
|--|--|
| 24 TB total | ~1.75 TB (~1750 GB) |
| 32 TB total | ~2.65 TB (~2650 GB) |


## Escalabilidad

### 🧱 Estrategia general de crecimiento

El sistema está diseñado para crecer de forma **horizontal mediante vdevs ZFS**, manteniendo un único pool lógico accesible por los servicios (Nextcloud y demás).

---

### ⚙️ Modelo de expansión principal (recomendado)

- El almacenamiento se estructura en bloques de discos (vdevs)
- Cada bloque puede ser:
  - 6 discos internos
  - 6 discos en DAS externo

👉 Todos los bloques se añaden al mismo pool ZFS

---

### 📦 Regla de crecimiento

- Crecer siempre en **módulos completos (RAIDZ2 de 6 discos)**
- No mezclar configuraciones distintas dentro del mismo pool
- Mantener homogeneidad de rendimiento y fiabilidad

---

### 🧠 Flujo de arquitectura actual

*
Hogares (clientes)
        │
     VPN / HTTPS
        │
[Servidor en tu casa]
   ├── ZFS Pool
   │    ├── 6 discos internos (RAIDZ2)
   │    └── DAS 6 discos (RAIDZ2)
   ├── Nextcloud
   └── servicios
*

---

### 📈 Expansión futura

#### ➕ Escenario 1: añadir nuevo bloque de discos
- Se añade un tercer RAIDZ2 (6 discos)
- Se integra como nuevo vdev en el pool existente
- Nextcloud no requiere cambios

✔ expansión transparente  
✔ sin migraciones de datos  
❌ no se puede revertir  

---

#### ⚠️ Escenario 2: crecimiento desordenado (no recomendado)
- Mezcla de USB, DAS inestable o redes lentas
- vdevs heterogéneos

❌ riesgo de degradación del pool completo  
❌ rendimiento inconsistente  

---

### 🌐 Impacto en Nextcloud

- Nextcloud solo ve `/data`
- No conoce la estructura interna del pool
- La expansión es completamente transparente

---

### 🔥 Principios de escalabilidad

- Escalar por **bloques completos de discos**
- Evitar dependencias en red dentro del pool
- Mantener consistencia de hardware
- Priorizar fiabilidad sobre flexibilidad extrema
