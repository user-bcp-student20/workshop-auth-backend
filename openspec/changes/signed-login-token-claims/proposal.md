# Proposal

## Why

GitHub Issue #13 ("HU-01 - Token firmado con expiracion y claims minimos") and the confirmed HU-01 require login tokens to expose an authenticated subject, client, issue time, and expiration time. The current login token is only Base64-encoded data, so these claims and the relationship between its expiration and `expiresIn` are not verifiable as required.

## What Changes

- Define the successful login token contract with the `sub`, `client`, `iat`, and `exp` claims and a signature.
- **BREAKING**: Require a nonblank `client` string in the login request and include its accepted value in the token. This changes the Day 1 contract, where `client` is optional and has no effect; HU-01 explicitly requires it to participate in token issuance.
- Require `expiresIn` to represent the remaining lifetime derived from `exp`, within a documented tolerance.
- Require `AUTH_TOKEN_SECRET` not to rely on an undocumented default outside the laboratory, and prohibit logging token or secret values.
- Preserve the existing login validation and response behavior except for the explicitly changed `client` requirement and token semantics.

### Out of Scope

- Choosing HMAC versus a JWT library, token encoding details, key rotation, or other implementation architecture.
- Changing credential lookup, user/role data, the existing `user` response fields, the `debug` response field, or the `/q/health` endpoint.
- Adding token-consuming endpoints or changing authorization behavior for other routes.

## Capabilities

### New Capabilities

- `auth-login`: Observable login request validation, successful response, and token claims/lifetime contract.

### Modified Capabilities

None. The repository has no existing OpenSpec capabilities.

## Impact

The behavior contract for `POST /auth/login`, its request and successful response, token configuration, security logging expectations, and related acceptance tests are affected. The Day 1 endpoint contract remains the baseline for all behavior not explicitly changed above.

## Source and Scope Note

Issue #13's title says HU-01 while its body says HU-02; the user confirmed that HU-01 is the intended scope. HU-01.md supplies the detailed claims, expiration, configuration, logging, and test expectations used by this proposal. For review, the spec treats any nonblank `client` string as valid (no allowlist is introduced) and proposes a one-second expiration tolerance to match the contract's integer-second values. These are observable contract assumptions, not implementation choices.