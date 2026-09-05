# Spring Security -- Authentication Providers

## Overview

An **Authentication Provider** in Spring Security is a component
responsible for processing an authentication request and verifying a
user's credentials.

In simple terms:

> **The Authentication Provider performs the actual authentication
> check.**

Depending on the application, authentication information may be verified
against a database, LDAP directory, external identity system, or custom
authentication service.

Spring Security represents this concept through the
`AuthenticationProvider` interface.

## 1. What Is an Authentication Provider?

Spring Security provides:

``` java
org.springframework.security.authentication.AuthenticationProvider
```

An implementation handles a supported type of authentication.

``` text
User submits credentials
        |
        v
Spring Security
        |
        v
AuthenticationProvider
        |
        +---- Verify credentials
        +---- Determine authorities
        |
        v
Authentication result
```

For traditional username/password authentication, the credentials
normally consist of a username and password. Other mechanisms can use
different authentication information.

## 2. Main Responsibilities

### 2.1 Authenticate the User

The provider verifies the supplied authentication information against
the appropriate source.

Examples include:

-   Database
-   LDAP
-   External identity system
-   Custom authentication service

Conceptually:

``` text
Incoming credentials
        |
        v
AuthenticationProvider
        |
        v
Verify credentials
        |
        +---- Invalid --> Authentication fails
        |
        +---- Valid ----> Authentication succeeds
```

### 2.2 Return an Authenticated Authentication Object

After successful verification, the provider returns an authenticated
`Authentication` object containing information such as the principal and
authorities.

``` text
Successful authentication
        |
        v
Authenticated Authentication
        |
        +---- Principal
        +---- Authorities / Roles
        +---- Authentication details
```

Spring Security can then use this information during authorization.

## 3. AuthenticationProvider Interface

Two important methods are:

``` java
Authentication authenticate(Authentication authentication)
        throws AuthenticationException;

boolean supports(Class<?> authentication);
```

### `authenticate()`

This performs the authentication logic.

``` java
@Override
public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {

    // Obtain authentication information
    // Verify credentials
    // Determine authorities
    // Return authenticated Authentication object
}
```

### `supports()`

This tells Spring Security which `Authentication` types the provider can
process.

``` java
@Override
public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class
            .isAssignableFrom(authentication);
}
```

## 4. Simplified Authentication Flow

``` text
Client
  |
  | credentials
  v
Authentication Filter
  |
  v
AuthenticationManager
  |
  v
AuthenticationProvider
  |
  +---- Verify credentials
  +---- Determine authorities
  |
  v
Authenticated Authentication
  |
  v
SecurityContext
```

The exact flow depends on the authentication mechanism and Spring
Security configuration.

## 5. AuthenticationManager vs AuthenticationProvider

`AuthenticationManager` coordinates authentication.

A commonly used implementation is:

``` text
ProviderManager
```

`ProviderManager` can delegate to one or more `AuthenticationProvider`
implementations.

``` text
AuthenticationManager
        |
        v
ProviderManager
        |
        +---- AuthenticationProvider A
        +---- AuthenticationProvider B
        +---- AuthenticationProvider C
```

Each provider uses `supports()` to indicate which authentication request
types it understands.

## 6. Multiple Authentication Providers

Spring Security can support multiple authentication mechanisms in one
application.

Examples include:

``` text
Username/Password
LDAP
Custom Authentication
External Identity Provider Integration
```

This gives applications flexibility when authentication requirements
differ between users or systems.

> OAuth2/OIDC social login has its own Spring Security infrastructure
> and flow. The broader lesson is that Spring Security supports multiple
> authentication mechanisms; not every mechanism requires writing the
> same kind of custom `AuthenticationProvider`.

## 7. Database Authentication

A common application stores user information in a database:

``` text
USERS
-------------------------
ID
USERNAME
PASSWORD_HASH
ENABLED
```

Conceptually:

``` text
Login Request
     |
     v
Authentication Provider
     |
     v
Load User
     |
     v
Verify submitted password
against stored password hash
     |
     +---- Incorrect --> Reject
     |
     +---- Correct ----> Authenticate
```

Passwords should never be stored as plaintext. Spring Security commonly
uses a `PasswordEncoder` to verify encoded passwords securely.

## 8. LDAP Authentication

Enterprise applications may authenticate against an LDAP directory:

``` text
User
 |
 v
Spring Security
 |
 v
LDAP Authentication
 |
 v
LDAP Directory
 |
 v
Authentication Result
```

This is common where employee identities are centrally managed.

## 9. Custom Authentication Provider

Applications with specialized authentication requirements can implement
their own provider.

``` java
@Component
public class CustomAuthenticationProvider
        implements AuthenticationProvider {

    @Override
    public Authentication authenticate(
            Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String password =
                authentication.getCredentials().toString();

        // Perform secure custom verification.

        // Return an authenticated Authentication object
        // when verification succeeds.

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
```

This is only a structural example. Production code should use Spring
Security's established components where possible and must handle
credentials securely.

## 10. Why Authentication Providers Are Important

### Flexibility

Different authentication mechanisms can coexist within the application's
security architecture.

### Separation of Concerns

Authentication logic remains separate from application business logic.

Instead of:

``` text
Controller
  +---- Business logic
  +---- Password verification
  +---- User authentication
```

prefer:

``` text
Controller --> Business Service

Spring Security --> AuthenticationProvider
```

### Extensibility

Custom providers can support legacy systems, enterprise identity
services, specialized tokens, or custom authentication rules.

### Security

Centralizing authentication helps provide consistent credential
verification and reduces duplicated security code.

## 11. Authentication vs Authorization

### Authentication

Answers:

> **Who are you?**

``` text
Credentials
    |
    v
Verification
    |
    v
Authenticated Identity
```

### Authorization

Answers:

> **What are you allowed to do?**

``` text
Authenticated Identity
        |
        v
Authorities / Roles
        |
        v
Access Decision
```

Authentication Providers primarily participate in authentication.
Authorization happens after the user's identity has been established.

## 12. Authorities

A successful `Authentication` object can contain authorities such as:

``` text
ROLE_USER
READ_NOTES
WRITE_NOTES
```

These can later be used by authorization rules:

``` java
.requestMatchers("/admin/**").hasRole("ADMIN")
```

or:

``` java
.requestMatchers("/notes/**").hasAuthority("READ_NOTES")
```

Authentication establishes identity; authorization determines permitted
actions.

## 13. Important Spring Security Components

  -----------------------------------------------------------------------
  Component                           Responsibility
  ----------------------------------- -----------------------------------
  `Authentication`                    Represents authentication
                                      information

  `AuthenticationManager`             Coordinates authentication

  `ProviderManager`                   Delegates to configured
                                      authentication providers

  `AuthenticationProvider`            Performs authentication for
                                      supported authentication types

  `UserDetailsService`                Commonly loads username-based user
                                      information

  `PasswordEncoder`                   Securely verifies encoded passwords

  `SecurityContext`                   Holds current authenticated
                                      security information
  -----------------------------------------------------------------------

A useful mental model is:

``` text
Authentication Request
        |
        v
Authentication Filter
        |
        v
AuthenticationManager
        |
        v
AuthenticationProvider
        |
        v
Credential / Identity Source
        |
        v
Authenticated Authentication
        |
        v
SecurityContext
        |
        v
Authorization
```

## 14. Interview Perspective

**Question: What is an AuthenticationProvider in Spring Security?**

An `AuthenticationProvider` is a Spring Security component responsible
for authenticating a supported type of authentication request. It
verifies authentication information and, when successful, returns an
authenticated `Authentication` object containing the principal and
authorities.

**Question: Can Spring Security use multiple AuthenticationProviders?**

Yes. `ProviderManager`, a common `AuthenticationManager` implementation,
can delegate authentication requests to multiple providers. Each
provider declares the authentication types it supports through
`supports()`.

## 15. Key Takeaways

1.  `AuthenticationProvider` performs authentication for supported
    authentication types.
2.  It verifies authentication information against an appropriate source
    or service.
3.  Successful authentication returns an authenticated `Authentication`
    object.
4.  The authenticated result can contain authorities.
5.  Multiple providers can be configured.
6.  `AuthenticationManager` coordinates authentication while providers
    perform the actual supported authentication logic.
7.  Providers separate authentication concerns from normal business
    logic.
8.  Custom providers can be created when standard mechanisms do not meet
    application requirements.

``` text
Authentication Request
        |
        v
AuthenticationProvider
        |
        v
Verification
        |
   +----+----+
   |         |
 Failed   Successful
             |
             v
   Authenticated Authentication
             |
             v
        Authorities
```

> **Authentication Providers are core Spring Security components that
> make authentication modular, extensible, and easier to maintain.**
