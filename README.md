# Workshop Auth Backend — Java Quarkus

> **Laboratorio intencionalmente vulnerable.** Ejecutar únicamente en local, repositorios privados y ambientes aislados. No desplegar en Internet, CERT o PROD.

Backend Quarkus que autentica usuarios de laboratorio almacenados en SQLite y entrega un token consumido por el frontend Angular y la API Python.

## Ejecución

Requisitos: JDK 21 y Maven 3.9+.

```bash
mvn quarkus:dev
```

Servicio: `http://localhost:8080`

En perfiles `dev` y `prod`, configura `AUTH_JWT_PRIVATE_KEY_PATH` con la ruta a una clave privada RSA PEM. La API de requisitos debe usar la clave pública correspondiente mediante `AUTH_JWT_PUBLIC_KEY_PATH`. `AUTH_JWT_ISSUER` y `AUTH_JWT_AUDIENCE` son opcionales y deben coincidir entre servicios; sus valores predeterminados son `workshop-auth` y `workshop-requirements-api`. Mantén ambas claves fuera del repositorio; Quarkus genera claves efímeras automáticamente en el perfil de test.

Credenciales locales:

- `student` / `Workshop123!`
- `trainer` / `Trainer123!`

## Endpoints

- `POST /auth/login`
- `GET /q/health`

## Propósito de la rama `main`

El backend conserva deuda técnica y de seguridad deliberada: SQL concatenado, contraseñas en texto plano, CORS permisivo, método largo, manejo genérico de excepciones y dependencias con CVEs. Los tokens de sesión se firman con RS256 y requieren una clave externa.
