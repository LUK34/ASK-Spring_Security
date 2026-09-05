# Spring Security -- UserDetails and User

## Overview

`UserDetails` is one of the fundamental interfaces in Spring Security.
It provides a **standard representation of a security user** that Spring
Security can use during authentication and authorization.

Spring Security also provides a concrete implementation:

``` java
org.springframework.security.core.userdetails.User
```

A useful mental model is:

``` text
UserDetails
    ^
    |
 implements
    |
   User
```

`UserDetails` defines what security-related information must be
available. Spring Security's `User` class provides a ready-made
implementation.

------------------------------------------------------------------------

## 1. Where UserDetails Fits

Important user-management abstractions include:

``` text
UserDetailsService
        ^
        |
UserDetailsManager
        ^
        |
   +----+----------------------+
   |                           |
   v                           v
InMemoryUserDetailsManager   JdbcUserDetailsManager
```

An individual user is represented through `UserDetails`.

``` text
User Store
    |
    v
UserDetailsService
    |
    | loadUserByUsername(...)
    v
UserDetails
    |
    v
AuthenticationProvider
    |
    v
Authentication
```

------------------------------------------------------------------------

## 2. What Is UserDetails?

The interface is located in:

``` java
org.springframework.security.core.userdetails.UserDetails
```

It provides Spring Security with information such as:

``` text
Username
Password
Authorities
Account expiration state
Account lock state
Credential expiration state
Enabled/disabled state
```

This gives Spring Security a standardized contract regardless of how the
application's users are stored.

------------------------------------------------------------------------

## 3. Why Is UserDetails Important?

Applications can obtain identities from many sources:

``` text
Database
Application Memory
LDAP
Custom Identity Store
```

Spring Security needs a common security-oriented representation.

``` text
Application User Data
        |
        v
    UserDetails
        |
        v
 Spring Security
```

This abstraction helps keep Spring Security independent of the
application's exact persistence model.

------------------------------------------------------------------------

## 4. Important Methods

Common `UserDetails` methods include:

``` java
getAuthorities()
getPassword()
getUsername()

isAccountNonExpired()
isAccountNonLocked()
isCredentialsNonExpired()
isEnabled()
```

The exact declarations/default behavior can vary by Spring Security
version, but these represent the core concepts.

### getUsername()

Returns the username identifying the user.

``` java
String username = userDetails.getUsername();
```

### getPassword()

Returns the password representation associated with the user.

In production, this should normally be an **encoded password**, not
plaintext.

### getAuthorities()

Returns the user's granted authorities:

``` text
ROLE_USER
ROLE_ADMIN
READ_NOTES
WRITE_NOTES
```

These are later used for authorization decisions.

------------------------------------------------------------------------

## 5. Account Status Methods

### isAccountNonExpired()

``` text
true  -> account is NOT expired
false -> account IS expired
```

### isAccountNonLocked()

``` text
true  -> account is NOT locked
false -> account IS locked
```

### isCredentialsNonExpired()

``` text
true  -> credentials are NOT expired
false -> credentials ARE expired
```

### isEnabled()

``` text
true  -> user is enabled
false -> user is disabled
```

A useful summary:

  -----------------------------------------------------------------------------
  Method                        `true` means            `false` means
  ----------------------------- ----------------------- -----------------------
  `isAccountNonExpired()`       Account valid           Account expired

  `isAccountNonLocked()`        Account unlocked        Account locked

  `isCredentialsNonExpired()`   Credentials valid       Credentials expired

  `isEnabled()`                 Account enabled         Account disabled
  -----------------------------------------------------------------------------

Be careful with the word **Non**. For example,
`isAccountNonLocked() == true` means the account is **not locked**.

------------------------------------------------------------------------

## 6. UserDetails During Authentication

A simplified DAO username/password flow is:

``` text
Login Request
     |
     v
AuthenticationManager
     |
     v
DaoAuthenticationProvider
     |
     v
UserDetailsService
     |
     | loadUserByUsername(...)
     v
UserDetails
     |
     +---- Username
     +---- Encoded password
     +---- Authorities
     +---- Account status
     |
     v
Credential / Account Verification
     |
     v
Authenticated Authentication
```

The `UserDetails` object supplies security information needed during
authentication.

------------------------------------------------------------------------

## 7. UserDetailsService

Another important interface is:

``` java
UserDetailsService
```

Its central operation is:

``` java
UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException;
```

Notice the return type:

``` java
UserDetails
```

The relationship is:

``` text
UserDetailsService
       |
       | loadUserByUsername(...)
       v
   UserDetails
```

A simple memory aid:

``` text
UserDetails        = represents a user
UserDetailsService = loads a user
```

------------------------------------------------------------------------

## 8. Database Example

A database may contain:

``` text
APP_USER
-------------------------
ID
USERNAME
PASSWORD
ENABLED
```

A custom `UserDetailsService` can retrieve the application entity and
adapt its security information to `UserDetails`.

``` text
Database
   |
   v
Repository
   |
   v
Application User Entity
   |
   v
UserDetailsService
   |
   v
UserDetails
   |
   v
Spring Security
```

------------------------------------------------------------------------

## 9. In-Memory Example

With in-memory authentication:

``` text
InMemoryUserDetailsManager
        |
        v
     UserDetails
```

Example:

``` java
UserDetails user =
        User.withUsername("user1")
            .password("{noop}password1")
            .roles("USER")
            .build();
```

This creates one security user.

------------------------------------------------------------------------

## 10. Spring Security's User Class

Spring Security provides:

``` java
org.springframework.security.core.userdetails.User
```

This class implements `UserDetails`.

``` text
UserDetails
    ^
    |
 implements
    |
   User
```

It provides a convenient implementation for common username/password
users.

------------------------------------------------------------------------

## 11. Creating a User

A common builder-based approach is:

``` java
UserDetails user =
        User.withUsername("user1")
            .password("{noop}password1")
            .roles("USER")
            .build();
```

Breaking it down:

``` java
User.withUsername("user1")
```

sets the username.

``` java
.password("{noop}password1")
```

sets the password representation.

``` java
.roles("USER")
```

assigns the role.

``` java
.build()
```

creates the final user object.

------------------------------------------------------------------------

## 12. Builder Pattern

The API:

``` java
User.withUsername(...)
    .password(...)
    .roles(...)
    .build();
```

uses a builder-style design.

Conceptually:

``` text
User
 |
 +---- withUsername(...)
 |
 +---- password(...)
 |
 +---- roles(...)
 |
 +---- build()
```

This makes user creation readable and convenient.

------------------------------------------------------------------------

## 13. Roles and Authorities

For example:

``` java
.roles("ADMIN")
```

normally results in:

``` text
ROLE_ADMIN
```

A user can have multiple roles:

``` java
UserDetails admin =
        User.withUsername("admin")
            .password("{noop}adminPass")
            .roles("USER", "ADMIN")
            .build();
```

which conceptually provides:

``` text
ROLE_USER
ROLE_ADMIN
```

Authorization rules can then use:

``` java
.hasRole("ADMIN")
```

or explicit authorities:

``` java
.hasAuthority("ROLE_ADMIN")
```

------------------------------------------------------------------------

## 14. User and CredentialsContainer

Spring Security's `User` also implements `CredentialsContainer`.

Conceptually:

``` text
User
 |
 +---- UserDetails
 |
 +---- CredentialsContainer
```

`CredentialsContainer` supports erasing credential information when
appropriate, helping reduce how long sensitive authentication
information remains available in memory.

------------------------------------------------------------------------

## 15. Custom UserDetails

You are not required to use Spring Security's built-in `User`.

Applications can implement their own `UserDetails`:

``` java
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Implement/override the required account-state
    // behavior for the Spring Security version in use.
}
```

This is useful when adapting a custom application user/domain model to
Spring Security.

------------------------------------------------------------------------

## 16. Application Entity vs UserDetails

An application entity and Spring Security's user representation are not
necessarily the same thing.

Your entity might contain:

``` text
ID
Employee Number
Username
Password
Email
Department
Phone
Created Date
Updated Date
```

Spring Security primarily needs:

``` text
Username
Password
Authorities
Account status
```

Therefore, your domain model can be adapted to the `UserDetails`
contract without forcing all business information into Spring Security's
built-in `User`.

------------------------------------------------------------------------

## 17. UserDetailsManager

`UserDetailsManager` extends the user-loading abstraction with
user-management operations.

``` text
UserDetailsService
        ^
        |
UserDetailsManager
```

It supports concepts such as:

``` text
Create user
Update user
Delete user
Check whether user exists
Change password
```

Important implementations include:

``` text
InMemoryUserDetailsManager
JdbcUserDetailsManager
```

The individual user being managed is still represented as `UserDetails`.

------------------------------------------------------------------------

## 18. Do Not Confuse These Types

### UserDetails

Represents:

``` text
ONE SECURITY USER
```

### UserDetailsService

Responsible for:

``` text
LOADING A USER
```

### UserDetailsManager

Responsible for:

``` text
LOADING AND MANAGING USERS
```

A useful memory aid:

``` text
UserDetails
    =
"What does this security user look like?"

UserDetailsService
    =
"How do I load this user?"

UserDetailsManager
    =
"How do I manage these users?"
```

------------------------------------------------------------------------

## 19. Complete Relationship

``` text
                  Authentication Request
                           |
                           v
                 AuthenticationManager
                           |
                           v
               DaoAuthenticationProvider
                           |
                           v
                  UserDetailsService
                           |
             +-------------+-------------+
             |                           |
             v                           v
InMemoryUserDetailsManager      Custom/Database Service
             |                           |
             +-------------+-------------+
                           |
                           v
                       UserDetails
                           ^
                           |
                 Spring Security User
                 or Custom UserDetails
                           |
                           v
                 Username / Password
                 Authorities / Status
```

------------------------------------------------------------------------

## 20. Interview Questions

### What is UserDetails?

`UserDetails` is a Spring Security interface that provides a
standardized representation of security-related user information such as
username, password, authorities, and account state.

### What is Spring Security's User class?

`User` is a built-in implementation of `UserDetails`.

### What is UserDetailsService?

It loads user information, most notably through:

``` java
loadUserByUsername(String username)
```

### What is UserDetailsManager?

It extends user-loading functionality with user-management operations.

### What does getAuthorities() return?

It returns the `GrantedAuthority` objects assigned to the user.

### What does isAccountNonLocked() mean?

``` text
true  -> account is not locked
false -> account is locked
```

### Can we create a custom UserDetails?

Yes. This is common when integrating an application's custom user/domain
model with Spring Security.

------------------------------------------------------------------------

## 21. Key Takeaways

Remember these four concepts:

``` text
UserDetails
     |
     +---- Represents ONE security user

User
     |
     +---- Built-in UserDetails implementation

UserDetailsService
     |
     +---- Loads a user

UserDetailsManager
     |
     +---- Loads and manages users
```

And the hierarchy:

``` text
UserDetailsService
        ^
        |
UserDetailsManager
        ^
        |
   +----+----------------------+
   |                           |
   v                           v
InMemoryUserDetailsManager   JdbcUserDetailsManager


UserDetails
    ^
    |
   User
```

The essential points are:

1.  `UserDetails` represents security-related information about one
    user.
2.  It provides username, password, authorities, and account-state
    information.
3.  Spring Security's `User` is a built-in `UserDetails` implementation.
4.  `UserDetailsService` loads a `UserDetails`.
5.  `UserDetailsManager` adds user-management operations.
6.  `InMemoryUserDetailsManager` and `JdbcUserDetailsManager` are
    important manager implementations.
7.  Spring Security's `User` provides a convenient builder API.
8.  Applications can implement custom `UserDetails` classes.
9.  `UserDetails` gives Spring Security a common security contract
    regardless of the underlying user store.

> **UserDetails represents the user, UserDetailsService loads the user,
> and UserDetailsManager manages users.**
