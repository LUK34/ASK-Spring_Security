# Spring Security -- Types of Authentication Providers

## Overview

Spring Security supports several authentication mechanisms.
Understanding the major provider types helps you recognize which
architecture is appropriate for an application.

This guide covers `DaoAuthenticationProvider`, in-memory authentication,
LDAP, Active Directory, pre-authentication, and OAuth 2.0/OpenID
Connect.

## 1. AuthenticationProvider Recap

At the core of provider-based authentication is:

``` java
AuthenticationProvider
```

Two important operations are:

``` java
Authentication authenticate(Authentication authentication)
        throws AuthenticationException;

boolean supports(Class<?> authentication);
```

`supports()` indicates whether the provider understands a particular
`Authentication` type. `authenticate()` performs authentication and
returns an authenticated `Authentication` object when successful.

## 2. DaoAuthenticationProvider

`DaoAuthenticationProvider` is one of the most important providers for
traditional username/password authentication.

It commonly works with:

``` text
UserDetailsService
PasswordEncoder
```

Conceptually:

``` text
Username + Password
        |
        v
DaoAuthenticationProvider
        |
        +---- UserDetailsService --> Load UserDetails
        |
        +---- PasswordEncoder ----> Verify Password
        |
        v
Authentication Result
```

It is Spring Security's standard provider for `UserDetailsService`-based
username/password authentication.

A useful clarification is that its automatic configuration depends on
the Spring Boot/Spring Security version and the application's
configuration; it should not be treated as a universal provider for
every authentication setup.

## 3. In-Memory Authentication

In-memory authentication keeps user definitions in application memory
instead of an external database.

Modern Spring Security commonly uses:

``` java
InMemoryUserDetailsManager
```

Example:

``` java
@Bean
public UserDetailsService userDetailsService(PasswordEncoder encoder) {

    UserDetails user = User.withUsername("john")
            .password(encoder.encode("password"))
            .roles("USER")
            .build();

    return new InMemoryUserDetailsManager(user);
}
```

It is useful for learning, demonstrations, prototypes, and tests.

There is not necessarily a class literally named
`InMemoryAuthenticationProvider`. `InMemoryUserDetailsManager` can act
as the `UserDetailsService` used by the normal username/password
authentication infrastructure.

## 4. LDAP Authentication

LDAP stands for Lightweight Directory Access Protocol.

It is commonly used by organizations for centralized identity
information.

``` text
Employee
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

LDAP is frequently encountered in enterprise and internal employee
applications. Appropriate Spring Security LDAP dependencies are normally
required.

## 5. Active Directory Authentication

Microsoft Active Directory is widely used for centralized enterprise
identities.

Spring Security provides support such as:

``` java
ActiveDirectoryLdapAuthenticationProvider
```

when the appropriate LDAP support is included.

``` text
Corporate User
      |
      v
Spring Security
      |
      v
ActiveDirectoryLdapAuthenticationProvider
      |
      v
Microsoft Active Directory
      |
      v
Authentication Result
```

## 6. PreAuthenticatedAuthenticationProvider

Spring Security provides:

``` java
PreAuthenticatedAuthenticationProvider
```

for pre-authenticated scenarios.

Here, another trusted system has already established the user's
identity.

``` text
User
 |
 v
Trusted External Authentication System
 |
 v
Application
 |
 v
Pre-Authentication Filter
 |
 v
PreAuthenticatedAuthenticationProvider
 |
 v
SecurityContext
```

Examples can include SSO and trusted upstream authentication
architectures.

The provider normally does not verify the user's original password
because authentication has already happened upstream.

The trust boundary is critical: arbitrary client-supplied identity
headers must never automatically be treated as trusted authentication.

## 7. OAuth 2.0 and OpenID Connect

Modern applications may offer:

``` text
Sign in with Google
Sign in with GitHub
Sign in with another identity provider
```

Spring Security provides OAuth 2.0 and OpenID Connect support for these
scenarios.

``` text
User
 |
 v
Application
 |
 v
External Authorization / Identity Provider
 |
 v
Authentication Flow
 |
 v
Application
 |
 v
Authenticated User
```

A useful distinction is:

-   OAuth 2.0 is primarily an authorization framework.
-   OpenID Connect (OIDC) adds an identity/authentication layer on top
    of OAuth 2.0.

Spring Security has multiple OAuth/OIDC authentication components and
provider implementations. There is not one universal provider that
handles every OAuth scenario. The exact provider depends on whether the
application uses OAuth2 Login, OIDC, JWT resource-server authentication,
opaque tokens, or another flow.

Additional OAuth client/resource-server dependencies are normally
required.

## 8. Comparison

  ---------------------------------------------------------------------------------------------
  Mechanism                                  Typical Purpose           Identity Source
  ------------------------------------------ ------------------------- ------------------------
  `DaoAuthenticationProvider`                Username/password         `UserDetailsService`,
                                                                       often database-backed

  In-memory users                            Learning/testing/simple   Application memory
                                             apps                      

  LDAP                                       Enterprise authentication LDAP directory

  Active Directory                           Microsoft enterprise      Active Directory
                                             authentication            

  `PreAuthenticatedAuthenticationProvider`   Trusted pre-authenticated Trusted upstream system
                                             identity                  

  OAuth 2.0 / OIDC                           External identity/token   Authorization/identity
                                             flows                     provider
  ---------------------------------------------------------------------------------------------

## 9. Choosing the Mechanism

For users stored in an application database, a common architecture is:

``` text
DaoAuthenticationProvider
        +
UserDetailsService
        +
PasswordEncoder
```

For learning/testing:

``` text
InMemoryUserDetailsManager
```

For organizational directories, use LDAP support. For Microsoft
enterprise identities, Active Directory integration may be appropriate.
If a trusted external system authenticates the user before the request
reaches the application, pre-authentication may be appropriate. For
modern external identity login, use Spring Security's OAuth 2.0/OIDC
support.

## 10. Multiple Providers

A common `AuthenticationManager` implementation is:

``` text
ProviderManager
```

It can coordinate multiple providers:

``` text
Authentication Request
        |
        v
ProviderManager
        |
        +---- Provider A
        +---- Provider B
        +---- Provider C
```

Each provider declares which authentication request types it supports.

## 11. Interview Points

**What is DaoAuthenticationProvider?**

It is Spring Security's standard `AuthenticationProvider` for
username/password authentication based on a `UserDetailsService`. It
commonly uses a `PasswordEncoder` to verify passwords.

**Does in-memory authentication require an
InMemoryAuthenticationProvider class?**

No. Modern Spring Security commonly uses `InMemoryUserDetailsManager`
with the standard username/password authentication infrastructure.

**What is PreAuthenticatedAuthenticationProvider?**

It supports architectures where a trusted external system has already
authenticated the user and Spring Security receives pre-authenticated
identity information.

**Can Spring Security use multiple providers?**

Yes. `ProviderManager` can delegate authentication requests to multiple
configured `AuthenticationProvider` implementations.

## 12. Important Clarifications

### DaoAuthenticationProvider

Rather than saying it is always the default provider, a more accurate
statement is:

> `DaoAuthenticationProvider` is the standard provider for
> `UserDetailsService`-based username/password authentication and is
> commonly configured automatically when that authentication model is
> used.

### In-Memory Authentication

Rather than looking for an `InMemoryAuthenticationProvider` class:

> Look for `InMemoryUserDetailsManager`, which supplies in-memory user
> information to the username/password authentication infrastructure.

### OAuth Authentication

Rather than assuming one `OAuth2AuthenticationProvider` handles every
social-login scenario:

> Spring Security contains multiple OAuth 2.0/OIDC components and
> providers, and the exact implementation depends on the authentication
> flow.

## 13. Key Takeaways

1.  `AuthenticationProvider` is a core Spring Security authentication
    abstraction.
2.  `DaoAuthenticationProvider` is important for username/password
    authentication.
3.  `UserDetailsService` supplies user information for DAO-based
    authentication.
4.  `InMemoryUserDetailsManager` can supply users from memory.
5.  LDAP supports centralized directory authentication.
6.  Active Directory support is common in Microsoft enterprise
    environments.
7.  Pre-authentication is useful when a trusted external system has
    already authenticated the user.
8.  OAuth 2.0/OIDC supports modern external identity and token-based
    flows.
9.  Some authentication classes require additional Spring Security
    dependencies.
10. Multiple authentication mechanisms can coexist in one application.

``` text
                  Authentication
                        |
                        v
                 ProviderManager
                        |
       +----------------+----------------+
       |                |                |
       v                v                v
      DAO              LDAP        Pre-Authenticated
       |                |                |
       v                v                v
  UserDetails        Directory       Trusted System

             OAuth 2.0 / OIDC
                    |
                    v
        External Identity Provider
```

> **Choose an authentication mechanism according to where identity is
> stored, who is trusted to authenticate the user, and how
> authentication information reaches the application.**
