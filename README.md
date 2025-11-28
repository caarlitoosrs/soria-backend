# Experiencias Soria Backend

Backend REST API para la aplicación de experiencias turísticas en Soria. Permite gestionar experiencias, usuarios, pasaportes digitales, comentarios y rankings.

## Tecnologías

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Security** - Autenticación JWT
- **Spring Data JPA** - Persistencia
- **MySQL** - Base de datos
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

## Configuración

### Variables de Entorno

Crear un archivo `.env` o configurar las siguientes variables:

```bash
# Base de datos
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=soria
MYSQL_USER=root
MYSQL_PASSWORD=tu_password

# JWT
JWT_SECRET=tu_clave_secreta_super_larga_y_segura
JWT_EXPIRATION=3600000  # 1 hora en milisegundos

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173

# JPA
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false

# Servidor
PORT=8080

# Logging
LOG_LEVEL=INFO
LOG_LEVEL_SECURITY=INFO
```

### application.yml

El archivo `application.yml` ya está configurado con valores por defecto que pueden ser sobrescritos por variables de entorno.

## Ejecución

### Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+

### Pasos

1. Clonar el repositorio
2. Configurar las variables de entorno
3. Crear la base de datos MySQL:
   ```sql
   CREATE DATABASE soria;
   ```
4. Ejecutar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

La aplicación estará disponible en `http://localhost:8080`

## Estructura del Proyecto

```
src/main/java/com/experienciassoria/
├── config/          # Configuración (CORS, OpenAPI, Properties)
├── controller/      # Controladores REST
├── dto/            # Data Transfer Objects
├── exception/      # Manejo de excepciones
├── model/          # Entidades JPA
├── repository/     # Repositorios JPA
├── security/       # Configuración de seguridad y JWT
└── service/        # Lógica de negocio
```

## Endpoints de la API

### Autenticación (`/api/auth`)

#### POST `/api/auth/register`
Registrar nuevo usuario
- **Permiso**: Público
- **Body**:
  ```json
  {
    "nombre": "Juan",
    "email": "juan@example.com",
    "password": "123456"
  }
  ```
- **Response**: `{ "token": "..." }`

#### POST `/api/auth/login`
Iniciar sesión
- **Permiso**: Público
- **Body**:
  ```json
  {
    "email": "juan@example.com",
    "password": "123456"
  }
  ```
- **Response**: `{ "token": "..." }`

#### GET `/api/auth/me`
Obtener usuario autenticado
- **Permiso**: Usuario autenticado
- **Headers**: `Authorization: Bearer {token}`
- **Response**: `{ "id": "...", "nombre": "...", "email": "...", "role": "USER", "puntos": 0 }`

### Experiencias (`/api/experiencias`)

#### GET `/api/experiencias`
Listar todas las experiencias (solo visibles)
- **Permiso**: Público
- **Response**: Lista de experiencias con coordenadas

#### GET `/api/experiencias/{id}`
Obtener detalle de experiencia
- **Permiso**: Público
- **Response**: Detalle completo de la experiencia

#### GET `/api/experiencias/uid/{uid}`
Obtener experiencia por UID
- **Permiso**: Público
- **Response**: Detalle completo de la experiencia

#### POST `/api/experiencias`
Crear nueva experiencia
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Body**:
  ```json
  {
    "titulo": "Museo Numantino",
    "descripcion": "Descripción...",
    "categoria": "MUSEO",
    "imagenPortadaUrl": "https://...",
    "direccion": "Calle...",
    "ubicacionLat": 41.7667,
    "ubicacionLng": -2.4667,
    "puntosOtorgados": 10
  }
  ```

#### PUT `/api/experiencias/{id}`
Actualizar experiencia
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Body**: Campos opcionales a actualizar

#### DELETE `/api/experiencias/{id}`
Eliminar experiencia
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`

#### GET `/api/experiencias/{id}/uids`
Listar UIDs de una experiencia
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`

#### POST `/api/experiencias/{id}/generar-uid?cantidad=5`
Generar UIDs para una experiencia
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Query params**: `cantidad` (1-100)
- **Response**: `{ "experienciaId": "...", "cantidadGenerada": 5, "uids": [...] }`

### Pasaporte (`/api/pasaporte`)

#### GET `/api/pasaporte`
Obtener pasaporte del usuario
- **Permiso**: Usuario autenticado
- **Headers**: `Authorization: Bearer {token}`
- **Response**: Pasaporte con registros de experiencias

#### POST `/api/pasaporte/registrar`
Registrar experiencia desde QR
- **Permiso**: Usuario autenticado
- **Headers**: `Authorization: Bearer {token}`
- **Body**:
  ```json
  {
    "uidScaneado": "ABC123...",
    "opinion": "Excelente experiencia"
  }
  ```

### Comentarios (`/api/experiencias/{experienciaId}/comentarios`)

#### GET `/api/experiencias/{experienciaId}/comentarios`
Listar comentarios de una experiencia
- **Permiso**: Público

#### POST `/api/experiencias/{experienciaId}/comentarios`
Crear comentario
- **Permiso**: Usuario autenticado
- **Headers**: `Authorization: Bearer {token}`
- **Body**:
  ```json
  {
    "texto": "Muy buena experiencia"
  }
  ```

### Ranking (`/api/top`)

#### GET `/api/top`
Obtener top 10 usuarios por puntos
- **Permiso**: Público

### Administración de Usuarios (`/api/admin/usuarios`)

Todos los endpoints de administración requieren rol ADMIN y JWT válido.

#### GET `/api/admin/usuarios`
Listar todos los usuarios
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Response**: Lista de usuarios con información básica (id, nombre, email, role, puntos, fechaCreacion, activo)

#### GET `/api/admin/usuarios/{id}`
Obtener detalles completos de un usuario
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Response**: Detalles del usuario incluyendo estadísticas (totalExperiencias, totalComentarios)

#### PUT `/api/admin/usuarios/{id}`
Actualizar usuario (cambiar rol o estado activo)
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Body**:
  ```json
  {
    "role": "ADMIN",
    "activo": true
  }
  ```
- **Nota**: Ambos campos son opcionales. El rol debe ser "USER" o "ADMIN"

#### DELETE `/api/admin/usuarios/{id}`
Eliminar usuario (soft delete - marca como inactivo)
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Validación**: No se puede eliminar el último administrador del sistema

#### GET `/api/admin/usuarios/{id}/pasaporte`
Ver pasaporte completo de un usuario
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Response**: Pasaporte con todos los registros de experiencias del usuario

#### GET `/api/admin/usuarios/{id}/experiencias`
Ver experiencias registradas por un usuario
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Response**: Lista de experiencias registradas con detalles (titulo, categoria, fechaRegistro, opinion, puntosOtorgados)

#### GET `/api/admin/usuarios/{id}/comentarios`
Ver comentarios realizados por un usuario
- **Permiso**: ADMIN
- **Headers**: `Authorization: Bearer {token_admin}`
- **Response**: Lista de comentarios del usuario ordenados por fecha descendente

## Modelo de Datos

### Usuario
- `id` (UUID)
- `nombre` (String)
- `email` (String, único)
- `passwordHash` (String)
- `role` (USER, ADMIN)
- `puntos` (int)
- `fechaCreacion` (Instant)
- `activo` (boolean)

### Experiencia
- `id` (UUID)
- `titulo` (String)
- `descripcion` (String)
- `categoria` (RESTAURANTE, AIRE_LIBRE, MUSEO, MONUMENTO)
- `imagenPortadaUrl` (String)
- `direccion` (String)
- `ubicacionLat` (BigDecimal)
- `ubicacionLng` (BigDecimal)
- `puntosOtorgados` (int) - Puntos que otorga al registrarse
- `visible` (boolean)

### ExperienciaUID
- `id` (UUID)
- `experiencia` (Experiencia)
- `uid` (String, único) - Código QR único
- `activo` (boolean)
- `fechaGeneracion` (Instant)

### RegistroExperiencia
- `id` (UUID)
- `usuario` (Usuario)
- `experiencia` (Experiencia)
- `experienciaUID` (ExperienciaUID)
- `fechaRegistro` (Instant)
- `opinion` (String)
- `imgPortada` (String)
- `puntosOtorgados` (int)

### Comentario
- `id` (UUID)
- `usuario` (Usuario)
- `experiencia` (Experiencia)
- `texto` (String)
- `fecha` (Instant)

## Seguridad

- Autenticación basada en JWT (JSON Web Tokens)
- Tokens expiran después de 1 hora (configurable)
- Endpoints protegidos por roles (USER, ADMIN)
- CORS configurado para orígenes permitidos
- Validación de datos con Bean Validation

## Manejo de Errores

El backend utiliza un `GlobalExceptionHandler` centralizado que devuelve respuestas consistentes:

```json
{
  "timestamp": "2024-01-01T00:00:00Z",
  "status": 404,
  "error": "Recurso no encontrado",
  "message": "Experiencia no encontrada",
  "path": "/api/experiencias/123"
}
```

## Testing

La colección de Postman incluye todos los endpoints y puede ser importada directamente.

## Docker

El proyecto incluye un `Dockerfile` para containerización:

```bash
docker build -t experiencias-soria-backend .
docker run -p 8080:8080 experiencias-soria-backend
```

## Licencia

Este proyecto es privado.

