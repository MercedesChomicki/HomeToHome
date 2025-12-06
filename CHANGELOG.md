## \[Unreleased\]

------------------------------------------------------------------------

## \[1.0.0\] -- 2025-12-05

### 🔐 Security & Authentication Overhaul Between Microservices

Added 
- Added `USER_SERVICE_SECRET`, `PET_SERVICE_SECRET` and
`CHAT_SERVICE_SECRET` to the `chat-service` environment in
`docker-compose`. 
- Added support for fine-grained service token scopes
(e.g., `users.read`, `pets.write`) in `FeignConfig` and
`ServiceTokenController`.

Changed 
- Reworked FeignConfig: 
    - Replaced static UUID tokens with
tokens generated based on service name and scopes. 
    - Unified token
generation logic across microservices. 
- Refactored SecurityConfig: 
    - Introduced two separate JWT filters: 
        - `ServiceJwtAuthFilter` for
inter-service authentication. 
        - `UserJwtAuthFilter` for end-user
authentication. 
    - Ensured correct order of filters in the security
chain. 
- Redesigned ServiceTokenController: 
    - Replaced
`/auth/service-token` with `/auth/token`. 
    - New authentication method
using headers: 
        - `X-Service-Name` 
        - `X-Service-Secret` 
    - Tokens are now
created according to the scopes defined per service. 
- Updated AuthService:  
    - Replaced `generateToken()` with `generateUserToken()` to
differentiate user vs service tokens. 
- Improved UserPrincipal: 
    - Enhanced `getUsername()` to safely resolve username from email, id, or
null fields. 
    - Removed duplicated logic and improved null handling.

#### Purpose 
These changes introduce a more robust and flexible security model for microservices, enabling: 
- Independent secrets per service. 
- Fine-grained permission scopes. 
- Stronger service-to-service
authentication. 
- Cleaner separation between user and service authentication flows.