# info de la materia: ST0263-1715 Tópicos Especiales de Telemática
#
# Estudiante(s): Juan José Gabriel Cañón Díaz, jjcanond@eafit.edu.co
#
# Profesor: Juan Carlos Montoya Mendoza, jcmontoy@eafit.edu.co
#
# <para borrar: EL OBJETIVO DE ESTA DOCUMENTACÍON ES QUE CUALQUIER LECTOR CON EL REPO, EN ESPECIAL EL PROFESOR, ENTIENDA EL ALCANCE DE LO DESARROLLADO Y QUE PUEDA REPRODUCIR SIN EL ESTUDIANTE EL AMBIENTE DE DESARROLLO Y EJECUTAR Y USAR LA APLICACIÓN SIN PROBLEMAS>

# Reto 1: Arquitectura P2P y Comunicación entre proceso 
#
# 1. descripción de la actividad
#
<texto descriptivo> Se diseñó y desarrolló un sistema distribuido con arquitectura P2P, cuyo objetivo es la transferencia de archivos (doomie) entre peers. Utilizando un protocolo de comunicación, los peers coordinan sus acciones mediante una secuencia de mensajes para cumplir eficientemente con la transferencia.

 ## 1.1. Que aspectos cumplió o desarrolló de la actividad propuesta por el profesor (requerimientos funcionales y no funcionales)
 <texto descriptivo> Diseño de arquitectura basada en bitTorrent no estructurada, implementación del protocólo gRPC para el paso de mensajes entre los peers y transferencia de archivos, implementación en aws academy.
 
## 1.2. Que aspectos NO cumplió o desarrolló de la actividad propuesta por el profesor (requerimientos funcionales y no funcionales)
<texto descriptivo> Particionamiento de archivos, implementacion de docker.

# 2. información general de diseño de alto nivel, arquitectura, patrones, mejores prácticas utilizadas.

## Diseño de Alto Nivel
- El sistema utiliza una arquitectura P2P, donde todos los nodos pueden actuar como clientes y/o servidores, permitiendo la distribución de archivos sin necesidad de un servidor central.
- Se hace uso de un **Tracker/Indexador** centralizado para coordinar y localizar los archivos disponibles en los peers, optimizando el proceso de busqueda y conexión.

## Arquitectura del Sistema

![image](https://github.com/user-attachments/assets/dc6b1fe7-6bc0-46fc-8468-fbfed0657cd6)

<texto descriptivo> El sistema distribuido sigue una arquitectura P2P, donde los clientes y servidores interactuan entre si para la transferecia de archivos a través de un protocolo gRPC. la arquitectura se organiza en los siguientes componentes principales
1. **Tracker/Indexer**
   - Es el nucleo del sistema, encargado de mantener un registro de los archivos disponibles y las ubicaciones de los peers.
   - Actua como intermediario inicial para que de los clientes localicen los archivos que desean descargar.
2. **Clientes-Servidores/Peers**
   - Cada cliente puede funcionar tanto como un **leecher** (cliente que solicita archivos) como un **seeder** (servidor que ofrece archivos para descarga).
   - Utilizan el protocolo **gRPC** para coordinar la transferencia de archivos entre peers.
3. **Transferencia de Archivos**
   - Una vez que un cliente solicita un archivo al tracker, este devuelve la ubicación del archivo en otro peer
   - Los clientes se comunican directamente entre si para completar la transferencia del archivo a través de **gRPC**.
4. **Protocolo gRPC**
   -El protocolo de comunicación entre peers se basa en gRPC, optimizando el intercambio de mensajes y archivos con baja latencia y soporte para conexciones simultaneas.

## Patrones de Diseño
1. **Patron de Proxy**
   - El tracker actua como un proxy para localizar recursos sin participar directamente en la transferecia de archivos.
2. **Cliente/Servidor**
   - Aunque los peers se pueden actual tanto cliente como servidor, la relación entre ellos sigue un patrón de cliente-servidor en cada transacción de archivo.
3. **Patron de Microservicios**
   -El sistema está dividido en componentes aislados (tracker,cliente,servidor), que pueden ser contenerizados y desplegados de forma independiente, lo que facilita la escalabilidad y el mantenimiento.

## Mejores Practicas Utilizadas
1. **Modularidad y Escalabilidad**: La separación de roles entre el tracker y peers permite que el sistema escale facilmente a medidad que mas clientes y servidores se unen a la red
2. **Uso de gRPC**: gRPC Facilita la comunicación eficiente entre los peers y el tracker, mejorando la latencia y capacidad de transmición de grandes cantidades de datos.


# 3. Descripción del ambiente de desarrollo y técnico: lenguaje de programación, librerias, paquetes, etc, con sus numeros de versiones.

## 1. **Lenguaje de Programación**
   - **Java**: `22.0.1`

## 2. **Librerías y Paquetes**
   - **gRPC**:
     1. `grpc-netty-shaded` -> `1.56.0`
     2. `grpc-protobuf` -> `1.56.0`
     3. `grpc-stub` -> `1.56.0`
     4. `protoc-gen-grpc-java` -> `1.56.0`
   - **Protobuf**: `com.google.protobuf:protoc` -> `3.21.12`
   - **JUnit**: `junit:junit` -> `4.13.2`
   - **JSON**: `org.json:json` -> `20210307`
   - **javax.annotation**: `javax.annotation:javax.annotation-api` -> `1.3.2`
   - **Maven Compiler Plugin**: `org.apache.maven.plugins:maven-compiler-plugin` -> `3.8.1`
   - **Protobuf Maven Plugin**: `org.xolstice.maven.plugins:protobuf-maven-plugin` -> `0.6.1`
   - **OS Maven Plugin**: `kr.motd.maven:os-maven-plugin` -> `1.7.0`
   - **Exec Maven Plugin**: `org.codehaus.mojo:exec-maven-plugin` -> `3.0.0`

## 3. **Manejador de Dependencias**
   - **Maven**: `3.9.9`

## 4. **Herramientas de Desarrollo**
   - **Maven**: para la gestión de dependencias y la construcción del proyecto.
   - **gRPC**: para la comunicación remota entre cliente y servidor.
   - **VSCode**: entorno de desarrollo integrado (IDE) utilizado en el proyecto.
   - **Protobuf**: para la generación de código basado en archivos `.proto`.
   - **JUnit**: para la realización de pruebas unitarias.

## 5. **Sistema Operativo**
   - **Windows 11**
   - **Ubuntu** (AWS Academy)

## 6. **Control de Versiones**
   - **GitHub**
## Cómo se compila y ejecuta
- **Compilar el proyecto**: 
  ```bash
  mvn clean install
- **Run Tracker**
  ```bash
  mvn exec:java -Dexec.mainClass="com.challenge.FileTransferTracker"
- **Run Server**
  ```bash
  mvn exec:java -Dexec.mainClass="com.challenge.Main"
- **Run Client**
  ```bash
  mvn exec:java -Dexec.mainClass="com.challenge.FileTransferClient"

## detalles del desarrollo.
<texto descriptivo> El desarrollo se enfocó en contruir un sistema distribuido con arquitectura P2P para la transferecia de archivos, utilizando P2P como protocolo de comunicación. El sistema consta de un Tracker centralizado que permite la ubicación de archivos en la red, y peers que actuan tanto como clientes solicitando archivos, como servidores compartiendolos.
### principales Caracteristicas:
1. Arquitectura P2P: Todos los peers pueden ser clientes y servidores simultaneamente.
2. No-Estructurada Hibrida: se utiliza un Tracker para facilitar la ubicación de archivos en lugar de un sistema completamente descentralizado.
3. Transferecia de archivos: El sistema permite la transferencia directa de archivos entre los peers una vez se localiza el archivo solicitado en otro nodo.
4. Comunicación eficiente: Se utiliza gRPC para manejar la comunicación entre los peers y entre los peers y el Tracker, permitiendo manejo de multiples conexiones simultaneas (bajo ciertas condiciones) y baja latencia.
5. AWS Academy: se probó la implementación en entornos de nube, utilizando instancias de Ubuntu en AWS para simular la comunicación y transferencia de archivos entre nodos distribuidos.
### detalles técnicos
<texto descriptivo> El proyecto utiliza varias tecnologías y librerías para lograr una eficiente comunicación entre los peers y la transferencia de archivos, algunas de las cuales son:
1. Java 22.0.1: Como lenguaje de programación principal.
2. gRPC: Para manejar la comunicación entre el tracker y los peers, y la transferencia de archivos.
3. Protobuf: Para la serialización y deserialización de mensajes que se intercambian entre los nodos.
### Servicios Utilizados
- gRPC: Para la comunicación entre los procesos distribuidos.
- Maven: Para la gestión de dependencias y contrucción del proyecto.

## descripción y como se configura los parámetros del proyecto (ej: ip, puertos, conexión a bases de datos, variables de ambiente, parámetros, etc)
### Parametros de Configuración
El proyecto puede ser configurado mediante diferentes parámetros que definen aspectos críticos como la IP del Tracker, los puertos de comunicación, y las rutas de los archivos.
- IP y Puerto del Tracker: La IP y el puerto donde el tracker escucha peticiones deben ser definidos tanto en el tracker como en los peers que quieran conectarse a él.
- IPs y Puertos de los Peers: Cada peer debe ser lanzado con una IP y un puerto únicos para recibir las conexiones de otros peers.
- Directorio de Archivos: Cada peer tiene que tener un directorio específico de donde compartirá o descargará archivos.
### Variables de Ambiente
Se pueden utilizar variables de ambiente para controlar ciertos aspectos del comportamiento del sistema:
- TRACKER_IP: IP donde el tracker estará escuchando.
- TRACKER_PORT: Puerto donde el tracker estará escuchando.
- PEER_PORT: Puerto donde el peer recibirá conexiones de otros peers.
- FILES_DIR: Directorio local donde el peer guarda y comparte archivos.

## opcional - detalles de la organización del código por carpetas o descripción de algún archivo. (ESTRUCTURA DE DIRECTORIOS Y ARCHIVOS IMPORTANTE DEL PROYECTO, comando 'tree' de linux)
## Modulos del Proyecto
- tracker: Mantiene un registro de los archivos disponibles y facilita la conexión entre peers.
- Peers: Actuan como clientes o servidores segun las necesidades, comunicandose con el tracker y entre si para compartir los archivos.
- Transferencia: se implementa una logica para la transferencia de archivos entre los nodos una vez se optiene la dirección IP del peer que lo posee.

## opcionalmente - si quiere mostrar resultados o pantallazos 

# 4. Descripción del ambiente de EJECUCIÓN (en producción) lenguaje de programación, librerias, paquetes, etc, con sus numeros de versiones.
## 1. **Lenguaje de Programación**
   - **Java**: `22.0.1`

## 2. **Librerías y Paquetes**
   - **gRPC**:
     1. `grpc-netty-shaded` -> `1.56.0`
     2. `grpc-protobuf` -> `1.56.0`
     3. `grpc-stub` -> `1.56.0`
     4. `protoc-gen-grpc-java` -> `1.56.0`
   - **Protobuf**: `com.google.protobuf:protoc` -> `3.21.12`
   - **JUnit**: `junit:junit` -> `4.13.2`
   - **JSON**: `org.json:json` -> `20210307`
   - **javax.annotation**: `javax.annotation:javax.annotation-api` -> `1.3.2`
   - **Maven Compiler Plugin**: `org.apache.maven.plugins:maven-compiler-plugin` -> `3.8.1`
   - **Protobuf Maven Plugin**: `org.xolstice.maven.plugins:protobuf-maven-plugin` -> `0.6.1`
   - **OS Maven Plugin**: `kr.motd.maven:os-maven-plugin` -> `1.7.0`
   - **Exec Maven Plugin**: `org.codehaus.mojo:exec-maven-plugin` -> `3.0.0`

## 3. **Manejador de Dependencias**
   - **Maven**: `3.9.9`
     
## 4. **Sistema Operativo**
   - **Windows 11**
   - **Ubuntu** (AWS Academy)

      
# IP o nombres de dominio en nube o en la máquina servidor.
- IP publica tracker:
- IP publica peer: 18.209.236.113
- IP publica peer: 98.83.131.229
- IP publca peer: 44.209.228.210

## como se lanza el servidor.
Para lanzar el Tracker:
 1. Clona el repositorio del proyecto.
 2. Asegúrate de tener Maven instalado y configurado.
 3. Usa el siguiente comando para compilar el proyecto:
    ```
     mvn clean install
    ```
 4. Usa el siguiente comando para ejecutar el Tracker:
    ```
     mvn exec:java -Dexec.mainClass="com.challenge.FileTransferTracker"
    ```
Este comando lanza el tracker, que comenzará a escuchar en el puerto especificado para recibir solicitudes de los peers.

## una mini guia de como un usuario utilizaría el software o la aplicación
 1. Clona el repositorio del proyecto.
 2. Asegúrate de tener Maven instalado y configurado.
 3. Usa el siguiente comando para compilar el proyecto en la carpeta Reto 1/challenge:
    ```
     mvn clean install
    ```
 4. Usa el siguiente comando para ejecutar el Server en una terminal:
    ```
     mvn exec:java -Dexec.mainClass="com.challenge.Main"
    ```
 5. Abre una nueva terminal y usa el siguiente comando:
    ```
     mvn exec:java -Dexec.mainClass="com.challenge.FileTransferClient"
    ```
Este comando inicia el peer que se conecta al tracker y comienza a compartir o descargar archivos.

![Reto11111](https://github.com/user-attachments/assets/fcb881cb-5c10-4998-b5f2-8d7a13316d6a)

    
## opcionalmente - si quiere mostrar resultados o pantallazos 

# 5. otra información que considere relevante para esta actividad.

# referencias:
<debemos siempre reconocer los créditos de partes del código que reutilizaremos, así como referencias a youtube, o referencias bibliográficas utilizadas para desarrollar el proyecto o la actividad>
## sitio1-url 
## sitio2-url
## url de donde tomo info para desarrollar este proyecto
- https://youtu.be/eUu29SrGYTA
- https://grpc.io/docs/languages/java/
- https://chatgpt.com/
