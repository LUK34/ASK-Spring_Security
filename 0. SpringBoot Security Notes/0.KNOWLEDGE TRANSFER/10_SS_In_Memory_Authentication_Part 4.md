# Spring Security -- In-Memory Authentication

## Overview

**In-memory authentication** is an authentication approach where user
information is stored directly in the application's memory instead of
being retrieved from an external persistent user store such as a
database or directory service.

Typical user information includes:

``` text
Username
Password
Roles
Authorities
```

In-memory authentication is especially useful for:

-   Development
-   Testing
-   Learning Spring Security
-   Prototypes
-   Proof-of-concept applications
-   Small applications with a fixed set of users

It is generally not the preferred approach for production applications
that require persistent and dynamically managed user accounts.

------------------------------------------------------------------------

# 1. What Is In-Memory Authentication?

Consider a normal database-backed authentication architecture:

``` text
User
 |
 v
Spring Security
 |
 v
AuthenticationProvider
 |
 v
UserDetailsService
 |
 v
Database
 |
 v
UserDetails
```

With in-memory authentication, the external database is replaced by an
in-memory user store:

``` text
User
 |
 v
Spring Security
 |
 v
AuthenticationProvider
 |
 v
UserDetailsService
 |
 v
Application Memory
 |
 v
UserDetails
```

The user definitions are available while the application is running.

------------------------------------------------------------------------

# 2. Common Spring Security Component

Modern Spring Security commonly provides in-memory users through:

``` java
InMemoryUserDetailsManager
```

This class implements `UserDetailsService`.

Conceptually:

``` text
UserDetailsService
       ^
       |
       |
InMemoryUserDetailsManager
```

Therefore, when a username/password authentication provider needs user
information, it can obtain that information from
`InMemoryUserDetailsManager`.

------------------------------------------------------------------------

# 3. Important Terminology

It is common for tutorials to refer to this concept as:

``` text
In-Memory Authentication Provider
```

However, it is useful to understand the actual Spring Security
architecture.

You commonly have:

``` text
DaoAuthenticationProvider
        |
        v
UserDetailsService
        |
        v
InMemoryUserDetailsManager
```

So `InMemoryUserDetailsManager` acts as the in-memory implementation of
the user-details service.

You should not necessarily expect a class named:

``` text
InMemoryAuthenticationProvider
```

------------------------------------------------------------------------

# 4. Authentication Flow

A simplified in-memory username/password authentication flow is:

``` text
HTTP Login Request
        |
        v
Authentication Filter
        |
        v
AuthenticationManager
        |
        v
ProviderManager
        |
        v
DaoAuthenticationProvider
        |
        v
UserDetailsService
        |
        v
InMemoryUserDetailsManager
        |
        v
UserDetails
        |
        v
PasswordEncoder
        |
        v
Password Verification
        |
   +----+----+
   |         |
 Failed   Successful
             |
             v
   Authenticated Authentication
             |
             v
        SecurityContext
```

------------------------------------------------------------------------

# 5. Example In-Memory User

A user can be created using Spring Security's `User` builder.

For example:

``` java
UserDetails user = User
        .withUsername("john")
        .password(passwordEncoder.encode("password"))
        .roles("USER")
        .build();
```

The user contains:

``` text
Username : john
Password : encoded password
Role     : ROLE_USER
```

The user can then be stored in:

``` java
InMemoryUserDetailsManager
```

------------------------------------------------------------------------

# 6. Complete Example

A simple configuration could look like:

``` java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails user = User
                .withUsername("john")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

This example creates one user:

``` text
Username : john
Password : password
Role     : USER
```

The stored password itself is encoded using the configured
`PasswordEncoder`.

------------------------------------------------------------------------

# 7. Multiple In-Memory Users

One major advantage during testing is the ability to create several
users with different roles.

Example:

``` java
@Bean
public UserDetailsService userDetailsService(
        PasswordEncoder passwordEncoder) {

    UserDetails user = User
            .withUsername("john")
            .password(passwordEncoder.encode("password"))
            .roles("USER")
            .build();

    UserDetails admin = User
            .withUsername("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN")
            .build();

    return new InMemoryUserDetailsManager(
            user,
            admin
    );
}
```

Now the application has:

  Username   Role
  ---------- --------------
  `john`     `ROLE_USER`
  `admin`    `ROLE_ADMIN`

This makes role-based security testing very convenient.

------------------------------------------------------------------------

# 8. Testing Different Authorization Rules

Suppose the application contains:

``` java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
    .anyRequest().authenticated()
)
```

You can test:

``` text
john
ROLE_USER
```

against:

``` text
/user/**
```

and:

``` text
admin
ROLE_ADMIN
```

against:

``` text
/admin/**
```

This allows authorization scenarios to be tested without creating
database tables or repositories.

------------------------------------------------------------------------

# 9. Use Case -- Development

During development, you may want to test Spring Security before
implementing the database layer.

Instead of building:

``` text
Database
Repository
Entity
UserDetailsService implementation
Database migrations
Test data
```

you can temporarily define:

``` java
InMemoryUserDetailsManager
```

This allows the security configuration to be tested quickly.

------------------------------------------------------------------------

# 10. Use Case -- Testing

Suppose you want to test:

``` text
USER access
ADMIN access
Unauthorized access
Forbidden access
```

You can create users such as:

``` text
user1  -> ROLE_USER
admin1 -> ROLE_ADMIN
```

without requiring persistent database records.

This is especially useful while learning:

``` text
permitAll()
authenticated()
hasRole()
hasAuthority()
denyAll()
```

------------------------------------------------------------------------

# 11. Use Case -- Prototypes and POCs

A proof-of-concept application may exist only to demonstrate
functionality.

For example:

``` text
Client Demo
     |
     v
Spring Boot Prototype
     |
     +---- UI
     +---- REST API
     +---- Spring Security
```

If persistent user management is not part of the demonstration, an
in-memory user store can reduce unnecessary setup.

This lets developers focus on demonstrating the application's core
functionality.

------------------------------------------------------------------------

# 12. Use Case -- Small Applications

In-memory users may also be acceptable in narrowly scoped applications
where:

-   The number of users is extremely small.
-   Users rarely change.
-   Persistent account management is not required.
-   The security architecture explicitly permits this approach.

However, production requirements should be evaluated carefully before
choosing this design.

------------------------------------------------------------------------

# 13. Benefits

## Simplicity

In-memory authentication is easy to configure.

You do not immediately need:

``` text
Database tables
JPA entities
Repositories
Database migrations
User administration functionality
```

------------------------------------------------------------------------

## Speed of Development

Developers can quickly create test users and begin testing security
rules.

For example:

``` text
USER
ADMIN
MANAGER
```

can be created without inserting database records.

------------------------------------------------------------------------

## No Database Lookup

User information is already available in application memory.

There is no database round trip required to retrieve those in-memory
user definitions.

For small test scenarios, this keeps the setup simple and fast.

------------------------------------------------------------------------

## Convenient Role Testing

Different users can quickly be created with different authorities.

Example:

``` java
UserDetails doctor = User
        .withUsername("doctor")
        .password(passwordEncoder.encode("doctor123"))
        .roles("DOCTOR")
        .build();

UserDetails admin = User
        .withUsername("admin")
        .password(passwordEncoder.encode("admin123"))
        .roles("ADMIN")
        .build();
```

This is useful for verifying authorization behavior.

------------------------------------------------------------------------

# 14. Limitations

In-memory authentication also has important limitations.

## Lack of Normal Persistence

If users are created dynamically only in memory, those runtime changes
do not survive an application restart unless the definitions are
recreated from configuration/code.

For example:

``` text
Application Running
      |
      +---- User A
      +---- User B
      +---- User C

Application Stops
      |
      v
Runtime memory is destroyed
```

If the users are hardcoded/configured, they will of course be recreated
the next time the application starts.

Therefore, the precise statement is:

> **Runtime in-memory state is not persistent across application
> restarts.**

------------------------------------------------------------------------

## Poor Scalability for User Management

Imagine manually maintaining:

``` text
10 users
100 users
10,000 users
```

inside application configuration.

This quickly becomes impractical.

A persistent identity store is normally more appropriate for systems
with significant user-management requirements.

------------------------------------------------------------------------

## Updating Users

Changing a hardcoded user's:

``` text
Password
Role
Authority
Account status
```

may require changing configuration/code and potentially restarting or
redeploying the application.

Database-backed systems can usually manage these changes dynamically.

------------------------------------------------------------------------

## Credential Management

Hardcoding real production passwords directly into source code is
unsafe.

Do not commit plaintext production credentials such as:

``` java
.password("secret123")
```

to a source repository.

Even for development, use appropriate password encoding and avoid
reusing real credentials.

------------------------------------------------------------------------

# 15. In-Memory vs Database Authentication

  Feature                           In-Memory                    Database
  --------------------------------- ---------------------------- -------------------
  Setup                             Simple                       More involved
  Persistent runtime user changes   No                           Yes
  Good for testing                  Excellent                    Possible
  Good for prototypes               Excellent                    Often unnecessary
  Dynamic user management           Limited                      Strong
  Large user base                   Poor fit                     Better fit
  Database required                 No                           Yes
  Production suitability            Limited/use-case dependent   Common

------------------------------------------------------------------------

# 16. Relationship with DaoAuthenticationProvider

This relationship is important.

For username/password authentication:

``` text
DaoAuthenticationProvider
        |
        v
UserDetailsService
```

The `UserDetailsService` can have different implementations.

### Database-backed

``` text
DaoAuthenticationProvider
        |
        v
Custom UserDetailsService
        |
        v
Repository
        |
        v
Database
```

### In-memory

``` text
DaoAuthenticationProvider
        |
        v
InMemoryUserDetailsManager
        |
        v
Application Memory
```

The authentication provider can therefore participate in the same
overall username/password authentication model while the source of user
information changes.

------------------------------------------------------------------------

# 17. Role of PasswordEncoder

Even when users are stored in memory, password handling still matters.

Spring Security commonly uses:

``` java
PasswordEncoder
```

Example:

``` java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Then:

``` java
.password(passwordEncoder.encode("password"))
```

The conceptual verification process is:

``` text
Submitted Password
        |
        v
PasswordEncoder.matches(...)
        |
        +---- false --> Authentication fails
        |
        +---- true ---> Authentication succeeds
```

------------------------------------------------------------------------

# 18. Important Correction: `findByUsername()`

A database-backed custom `UserDetailsService` may internally use a
repository method such as:

``` java
findByUsername(...)
```

However, this is **not a mandatory part of in-memory authentication**.

`InMemoryUserDetailsManager` does not need a JPA repository or database
`findByUsername()` query.

Conceptually:

``` text
Database authentication:

loadUserByUsername()
        |
        v
Repository.findByUsername()
        |
        v
Database
```

while:

``` text
In-memory authentication:

loadUserByUsername()
        |
        v
InMemoryUserDetailsManager
        |
        v
In-memory user collection
```

This distinction is important when understanding the Spring Security
architecture.

------------------------------------------------------------------------

# 19. Production Considerations

In-memory authentication is excellent for:

``` text
Learning
Development
Testing
Demonstrations
POCs
Small fixed-user scenarios
```

For larger production systems, users are commonly stored or managed
through:

``` text
Relational Database
LDAP
Active Directory
External Identity Provider
Enterprise Identity Platform
```

The appropriate choice depends on the application's security
requirements.

------------------------------------------------------------------------

# 20. Interview Questions

## What is in-memory authentication?

In-memory authentication stores user information directly in application
memory rather than retrieving it from a persistent external user store.

------------------------------------------------------------------------

## Which Spring Security class is commonly used?

``` java
InMemoryUserDetailsManager
```

------------------------------------------------------------------------

## Does in-memory authentication require a database?

No.

------------------------------------------------------------------------

## Is in-memory authentication persistent?

Runtime modifications to the in-memory user store do not survive an
application restart unless the users are recreated from
code/configuration.

------------------------------------------------------------------------

## What is it commonly used for?

-   Development
-   Testing
-   Learning
-   Prototypes
-   Proof-of-concept applications
-   Small fixed-user scenarios

------------------------------------------------------------------------

## Can multiple users and roles be configured?

Yes.

For example:

``` text
john  -> ROLE_USER
admin -> ROLE_ADMIN
```

------------------------------------------------------------------------

## Does it still use password encoding?

It should. A `PasswordEncoder` should be used rather than treating
plaintext production passwords as acceptable storage.

------------------------------------------------------------------------

# 21. Key Takeaways

Remember this architecture:

``` text
                Authentication Request
                         |
                         v
                 AuthenticationManager
                         |
                         v
                   ProviderManager
                         |
                         v
               DaoAuthenticationProvider
                         |
                         v
                  UserDetailsService
                         |
                         v
              InMemoryUserDetailsManager
                         |
                         v
                 In-Memory Users
                         |
                         v
                  PasswordEncoder
                         |
                         v
               Authentication Result
```

The important points are:

1.  In-memory authentication stores user definitions in application
    memory.
2.  `InMemoryUserDetailsManager` is commonly used to manage those users.
3.  It implements `UserDetailsService`.
4.  It can participate in the standard username/password authentication
    architecture.
5.  It does not require a database.
6.  It is excellent for development, testing, demonstrations, and POCs.
7.  Multiple users with different roles can be configured easily.
8.  Runtime changes are not persistent across application restarts.
9.  It is usually not the best user-management strategy for large
    production applications.
10. Passwords should still be handled securely using an appropriate
    `PasswordEncoder`.

> **In-memory authentication is primarily valuable because it provides a
> fast and simple way to test Spring Security without first building a
> persistent user-management system.**
