# Spec Delta

## Purpose

Defines the observable login contract, including the token claims and lifetime issued to an authenticated user, while preserving the existing credential and response behavior unless HU-01 explicitly changes it.

## ADDED Requirements

### Requirement: Existing login validation and credential failures are preserved
The `POST /auth/login` endpoint SHALL retain the documented 400 responses for a null request or blank username/password and the documented 401 response for invalid credentials.

#### Scenario: Missing username or password
- **WHEN** a login request has a null body, a null/blank username, or a null/blank password
- **THEN** the endpoint responds with HTTP 400 and the corresponding documented validation message

#### Scenario: Invalid credentials
- **WHEN** the username or password does not match a user
- **THEN** the endpoint responds with HTTP 401 and the documented invalid-credentials message

### Requirement: Login client is validated and bound to the token
The `POST /auth/login` endpoint SHALL require `client` to be a nonblank string and SHALL place the accepted request value in the issued token. Any nonblank string is valid; this contract does not introduce a client allowlist.

#### Scenario: Client is missing or blank
- **WHEN** a login request omits `client` or supplies it as null, empty, or whitespace-only
- **THEN** the endpoint responds with HTTP 400

#### Scenario: Valid client is reflected in the token
- **WHEN** valid credentials and a nonblank client are submitted
- **THEN** the successful token's `client` claim equals the submitted client value

### Requirement: Successful login issues a verifiably signed token with minimum claims
For valid credentials, the issued token SHALL have a verifiable signature and contain `sub`, `client`, `iat`, and `exp`; `sub` SHALL identify the authenticated user's username, and `exp` SHALL be later than `iat`.

#### Scenario: Successful token has required claims
- **WHEN** valid credentials and a valid client are submitted
- **THEN** the issued token has a valid signature and contains `sub`, `client`, `iat`, and `exp` with `sub` matching the authenticated user's username

#### Scenario: Signed token is modified
- **WHEN** a token's signed contents are changed after issuance
- **THEN** its signature no longer verifies

### Requirement: Expiration and expiresIn agree
The issued `exp` claim SHALL define the token's expiration instant in seconds, and the response's integer `expiresIn` SHALL match the remaining lifetime to within one second. This one-second tolerance SHALL be documented with the token configuration.

#### Scenario: Successful response reports remaining token lifetime
- **WHEN** a successful login response is inspected at time `now`
- **THEN** the absolute difference between `expiresIn` and `exp - now` is no greater than one second

#### Scenario: Token reaches its expiration instant
- **WHEN** the current time is equal to or later than the issued token's `exp`
- **THEN** the token is considered expired and not valid under this contract

### Requirement: Successful login response preserves the documented shape
For valid credentials, `POST /auth/login` SHALL continue returning HTTP 200 with `token`, `expiresIn`, `user`, and `debug`; `user` SHALL retain `username`, HTML-escaped `displayName`, and `role`.

#### Scenario: Successful login response fields
- **WHEN** valid credentials and a valid client are submitted
- **THEN** the response contains the documented fields and user data, with `expiresIn` following the token lifetime requirement

### Requirement: Signing secret is not a production default
Outside the laboratory, token issuance SHALL use an explicitly configured `AUTH_TOKEN_SECRET` and SHALL NOT use a built-in default. Any default retained for laboratory use SHALL be documented as laboratory-only.

#### Scenario: Non-laboratory configuration
- **WHEN** token configuration for a non-laboratory environment is reviewed
- **THEN** it requires an explicitly supplied `AUTH_TOKEN_SECRET` and does not select a built-in default

### Requirement: Sensitive authentication values are not logged
Authentication logs SHALL NOT contain issued token values, `AUTH_TOKEN_SECRET`, passwords, or other secret material.

#### Scenario: Login activity is logged
- **WHEN** successful or failed login activity is recorded
- **THEN** the resulting logs contain no issued token, secret, password, or other secret material