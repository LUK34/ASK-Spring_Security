# Spring Security -- `UserDetailsService`, `UserDetailsManager`, `JdbcUserDetailsManager` and `InMemoryUserDetailsManager`

## Overview

This topic expands the Spring Security user-management hierarchy and
explains how the framework **loads, represents, and manages users**.

The main components are:

``` text
UserDetails
UserDetailsService
UserDetailsManager
JdbcUserDetailsManager
InMemoryUserDetailsManager
```

A useful high-level picture is:

``` text
                     UserDetailsService
                            ^
                            |
                     UserDetailsManager
                            ^
                            |
                 +----------+----------+
                 |                     |
                 v                     v
      JdbcUserDetailsManager   InMemoryUserDetailsManager
                 |                     |
                 v                     v
             Database            Application Memory
```

Throughout this architecture, an individual Spring Security user is
represented using:

``` java
UserDetails
```

------------------------------------------------------------------------

# 1. First Remember: What Is `UserDetails`?

`UserDetails` represents **one security user**.

Conceptually:

``` text
UserDetails
    |
    +---- Username
    +---- Password
    +---- Authorities
    +---- Account enabled?
    +---- Account locked?
    +---- Account expired?
    +---- Credentials expired?
```

It provides Spring Security with a standardized representation of
security-related user information.

This means the underlying source can change:

``` text
Database       ----\
Memory         -----\
Custom Store  ------> UserDetails ---> Spring Security
LDAP          -----/
```

The rest of the authentication infrastructure can work with the common
`UserDetails` abstraction.

------------------------------------------------------------------------

# 2. `UserDetailsService`

`UserDetailsService` is an interface responsible for **loading user
information**.

It is located in:

``` java
org.springframework.security.core.userdetails.UserDetailsService
```

Its central method is:

``` java
UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException;
```

The important relationship is:

``` text
Username
   |
   v
UserDetailsService
   |
   | loadUserByUsername(username)
   v
UserDetails
```

So:

> **`UserDetailsService` loads a user and returns that user as
> `UserDetails`.**

------------------------------------------------------------------------

# 3. `loadUserByUsername()`

The method:

``` java
loadUserByUsername(String username)
```

accepts a username and attempts to locate the corresponding user.

Example:

``` java
UserDetails userDetails =
        userDetailsService.loadUserByUsername("admin");
```

Conceptually:

``` text
"admin"
   |
   v
loadUserByUsername("admin")
   |
   +---- User exists
   |        |
   |        v
   |    UserDetails
   |
   +---- User does not exist
            |
            v
UsernameNotFoundException
```

The returned `UserDetails` can then be used by Spring Security's
authentication infrastructure.

------------------------------------------------------------------------

# 4. `UserDetailsService` and `DaoAuthenticationProvider`

`UserDetailsService` is especially important in username/password
authentication using `DaoAuthenticationProvider`.

A simplified flow is:

``` text
Login Request
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
     | loadUserByUsername(...)
     v
UserDetails
     |
     v
PasswordEncoder
     |
     v
Authentication Result
```

`DaoAuthenticationProvider` can use a `UserDetailsService` to retrieve
the user's security information and then verify the supplied
credentials.

------------------------------------------------------------------------

# 5. `UsernameNotFoundException`

If the requested user cannot be found, a `UsernameNotFoundException` can
be thrown.

For example:

``` java
@Override
public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {

    // Find user...

    if (userNotFound) {
        throw new UsernameNotFoundException(
                "User not found: " + username);
    }

    // Return UserDetails...
}
```

In real production applications, be careful not to expose unnecessarily
detailed authentication failure information to clients, because it can
help attackers determine whether particular usernames exist.

------------------------------------------------------------------------

# 6. `UserDetailsManager`

`UserDetailsManager` is another Spring Security interface.

The important relationship is:

``` text
UserDetailsService
        ^
        |
      extends
        |
UserDetailsManager
```

More precisely:

``` java
UserDetailsManager extends UserDetailsService
```

Therefore, a `UserDetailsManager` can load users **and** provides
additional user-management operations.

A simple way to remember the distinction is:

``` text
UserDetailsService
        =
LOAD USERS

UserDetailsManager
        =
LOAD + MANAGE USERS
```

------------------------------------------------------------------------

# 7. User Management Operations

`UserDetailsManager` adds operations conceptually such as:

``` text
Create user
Update user
Delete user
Check whether user exists
Change password
```

Typical methods include:

``` java
createUser(UserDetails user)

updateUser(UserDetails user)

deleteUser(String username)

userExists(String username)

changePassword(String oldPassword, String newPassword)
```

Because `UserDetailsManager` extends `UserDetailsService`, it also
supports:

``` java
loadUserByUsername(String username)
```

------------------------------------------------------------------------

# 8. Hierarchy So Far

``` text
UserDetailsService
        |
        | loadUserByUsername(...)
        |
        v
Loads UserDetails


UserDetailsManager
        |
        | extends UserDetailsService
        |
        +---- load user
        +---- create user
        +---- update user
        +---- delete user
        +---- check user exists
        +---- change password
```

------------------------------------------------------------------------

# 9. Implementations of `UserDetailsManager`

Two important Spring Security implementations are:

``` java
JdbcUserDetailsManager
```

and:

``` java
InMemoryUserDetailsManager
```

Their main difference is **where the user information is stored and
managed**.

``` text
                    UserDetailsManager
                           ^
                           |
               +-----------+-----------+
               |                       |
               v                       v
     JdbcUserDetailsManager   InMemoryUserDetailsManager
               |                       |
               v                       v
           Database              Application Memory
```

------------------------------------------------------------------------

# 10. `JdbcUserDetailsManager`

`JdbcUserDetailsManager` is a Spring Security implementation of
`UserDetailsManager` designed to manage users through a JDBC-accessible
relational database.

Conceptually:

``` text
JdbcUserDetailsManager
        |
        v
       JDBC
        |
        v
    DataSource
        |
        v
Relational Database
```

It can perform user-management operations against database tables.

------------------------------------------------------------------------

# 11. JDBC-Based User Loading

A simplified flow is:

``` text
Authentication Request
        |
        v
DaoAuthenticationProvider
        |
        v
UserDetailsService
        |
        v
JdbcUserDetailsManager
        |
        v
      JDBC
        |
        v
    Database
        |
        v
User / Authority Data
        |
        v
    UserDetails
```

Unlike in-memory storage, the users can persist independently of the
application's runtime memory.

------------------------------------------------------------------------

# 12. Database Operations

`JdbcUserDetailsManager` contains JDBC-based logic for operations such
as:

``` text
Load user
Create user
Update user
Delete user
Load authorities
Create authorities
Check whether user exists
Change password
```

Spring Security provides default SQL/schema expectations for its JDBC
user-management support, and many query behaviors can be customized when
integrating with a different schema.

------------------------------------------------------------------------

# 13. Conceptual SQL

At a simplified level, JDBC user management requires operations similar
to:

``` sql
SELECT username, password, enabled
FROM users
WHERE username = ?
```

and authority retrieval similar to:

``` sql
SELECT username, authority
FROM authorities
WHERE username = ?
```

There can also be SQL operations for:

``` text
INSERT
UPDATE
DELETE
```

The exact queries depend on configuration and schema.

------------------------------------------------------------------------

# 14. Important JDBC Clarification

A database-backed Spring Security application does **not always have to
use `JdbcUserDetailsManager`**.

Many real-world applications instead create:

``` text
JPA Entity
Repository
Custom UserDetailsService
```

For example:

``` text
DaoAuthenticationProvider
        |
        v
Custom UserDetailsService
        |
        v
Spring Data JPA Repository
        |
        v
Database
```

Therefore:

> `JdbcUserDetailsManager` is one convenient Spring Security solution
> for JDBC-backed user management, but it is not the only way to
> authenticate users stored in a database.

This distinction is important in real projects.

------------------------------------------------------------------------

# 15. `InMemoryUserDetailsManager`

`InMemoryUserDetailsManager` is another implementation of
`UserDetailsManager`.

Instead of managing users through JDBC, it stores them in application
memory.

Conceptually:

``` text
InMemoryUserDetailsManager
        |
        v
In-Memory User Collection
        |
        +---- user1
        +---- admin
        +---- testuser
```

No external user database is required for these user definitions.

------------------------------------------------------------------------

# 16. In-Memory User Example

For example:

``` java
@Bean
public UserDetailsService userDetailsService() {

    InMemoryUserDetailsManager manager =
            new InMemoryUserDetailsManager();

    manager.createUser(
            User.withUsername("user1")
                    .password("{noop}password1")
                    .roles("USER")
                    .build()
    );

    manager.createUser(
            User.withUsername("admin")
                    .password("{noop}adminPass")
                    .roles("ADMIN")
                    .build()
    );

    return manager;
}
```

Conceptually:

``` text
Application Memory
│
├── user1
│   ├── password1
│   └── ROLE_USER
│
└── admin
    ├── adminPass
    └── ROLE_ADMIN
```

For learning purposes, `{noop}` indicates no password encoding.
Production systems should use an appropriate secure `PasswordEncoder`.

------------------------------------------------------------------------

# 17. Runtime Persistence

An important characteristic of in-memory user management is that runtime
state is not persistent.

``` text
Application starts
      |
      v
Users created in memory
      |
      v
Application runs
      |
      v
Application stops
      |
      v
Runtime memory disappears
```

If users are declared in application configuration/code, they are
recreated when the application starts again.

Therefore, a more precise statement than simply saying "all users are
lost" is:

> Runtime changes to the in-memory user store do not survive application
> restart unless those users are recreated from persistent configuration
> or code.

------------------------------------------------------------------------

# 18. Why Use In-Memory User Management?

It is particularly useful for:

``` text
Learning Spring Security
Development
Testing
Proof of Concept
Demonstrations
Small fixed-user scenarios
```

For example, when learning role-based authorization, you can quickly
create:

``` text
user1  -> ROLE_USER
admin  -> ROLE_ADMIN
```

without creating database tables or repositories.

------------------------------------------------------------------------

# 19. JDBC vs In-Memory

  Feature                         `JdbcUserDetailsManager`   `InMemoryUserDetailsManager`
  ------------------------------- -------------------------- ---------------------------------
  Storage                         Relational database        Application memory
  JDBC/DataSource required        Yes                        No
  Persistent user data            Yes                        Runtime state is not persistent
  Good for learning               Possible                   Excellent
  Good for quick testing          More setup                 Excellent
  User-management operations      Yes                        Yes
  SQL interaction                 Yes                        No
  Production suitability          Depends on architecture    Usually limited
  Large/dynamic user population   Better suited              Usually poor fit

------------------------------------------------------------------------

# 20. Same Interface, Different Implementations

This demonstrates one of the benefits of interface-based design.

Both classes implement the same user-management contract:

``` text
                UserDetailsManager
                       ^
                       |
             +---------+---------+
             |                   |
             v                   v
JdbcUserDetailsManager   InMemoryUserDetailsManager
```

Your code can therefore work with the abstraction:

``` java
UserDetailsManager
```

while the implementation determines where the users are stored.

------------------------------------------------------------------------

# 21. Role of `UserDetails`

Notice that `UserDetails` appears throughout the hierarchy.

For example:

``` java
UserDetails loadUserByUsername(String username);
```

and:

``` java
void createUser(UserDetails user);
```

and:

``` java
void updateUser(UserDetails user);
```

This demonstrates why `UserDetails` is such an important Spring Security
abstraction.

It gives the framework a standard representation of a security user.

------------------------------------------------------------------------

# 22. Putting Everything Together

The complete conceptual relationship is:

``` text
                         UserDetails
                             ^
                             |
                    Represents ONE USER
                             |
                             |
                 +-----------+-----------+
                 |                       |
                 |                       |
         UserDetailsService              |
                 ^                       |
                 |                       |
                 | extends               |
                 |                       |
         UserDetailsManager              |
                 ^                       |
                 |                       |
        +--------+---------+             |
        |                  |             |
        v                  v             |
JdbcUserDetailsManager  InMemoryUserDetailsManager
        |                  |
        v                  v
    Database         Application Memory
```

A cleaner inheritance-focused view is:

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
JdbcUserDetailsManager   InMemoryUserDetailsManager
```

while:

``` text
UserDetails
```

is the common representation of an individual user passed through these
APIs.

------------------------------------------------------------------------

# 23. Authentication Flow with JDBC

``` text
HTTP Request
     |
     v
Authentication Filter
     |
     v
AuthenticationManager
     |
     v
DaoAuthenticationProvider
     |
     v
JdbcUserDetailsManager
     |
     v
DataSource / JDBC
     |
     v
Database
     |
     v
UserDetails
     |
     v
Password Verification
     |
     v
Authenticated User
```

------------------------------------------------------------------------

# 24. Authentication Flow with In-Memory Users

``` text
HTTP Request
     |
     v
Authentication Filter
     |
     v
AuthenticationManager
     |
     v
DaoAuthenticationProvider
     |
     v
InMemoryUserDetailsManager
     |
     v
Application Memory
     |
     v
UserDetails
     |
     v
Password Verification
     |
     v
Authenticated User
```

The major difference is the source from which the user information is
obtained.

------------------------------------------------------------------------

# 25. Easy Memory Trick

Remember:

``` text
UserDetails
    =
THE USER

UserDetailsService
    =
LOAD THE USER

UserDetailsManager
    =
LOAD + MANAGE THE USER

JdbcUserDetailsManager
    =
LOAD + MANAGE USERS IN A DATABASE USING JDBC

InMemoryUserDetailsManager
    =
LOAD + MANAGE USERS IN APPLICATION MEMORY
```

------------------------------------------------------------------------

# 26. Interview Questions

## What is `UserDetailsService`?

It is a Spring Security interface used to load user information. Its key
method is:

``` java
loadUserByUsername(String username)
```

which returns `UserDetails`.

## What happens when the username cannot be found?

A `UsernameNotFoundException` can be thrown.

## What is `UserDetailsManager`?

It extends `UserDetailsService` and adds user-management operations such
as creating, updating, deleting, and checking users.

## What is `JdbcUserDetailsManager`?

It is a Spring Security `UserDetailsManager` implementation that manages
users through a JDBC `DataSource`.

## What is `InMemoryUserDetailsManager`?

It is a non-persistent, in-memory `UserDetailsManager` implementation
commonly useful for development, testing, learning, and POCs.

## Does every database-backed application need `JdbcUserDetailsManager`?

No. Applications can implement a custom `UserDetailsService`, for
example using Spring Data JPA repositories.

## What represents an individual user?

``` java
UserDetails
```

## What is the relationship between `UserDetailsService` and `UserDetailsManager`?

``` java
UserDetailsManager extends UserDetailsService
```

Therefore, a manager can perform the loading operation as well as
additional user-management operations.

------------------------------------------------------------------------

# 27. Key Takeaways

The hierarchy to remember is:

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
JdbcUserDetailsManager   InMemoryUserDetailsManager
```

And separately:

``` text
UserDetails
     =
standard representation of one Spring Security user
```

The essential points are:

1.  `UserDetailsService` is responsible for loading user information.
2.  Its central method is `loadUserByUsername()`.
3.  The method returns a `UserDetails`.
4.  `UserDetailsManager` extends `UserDetailsService`.
5.  It adds user-management operations.
6.  `JdbcUserDetailsManager` manages users using JDBC and a relational
    database.
7.  `InMemoryUserDetailsManager` manages users in application memory.
8.  Runtime in-memory changes are not persistent across application
    restarts.
9.  `JdbcUserDetailsManager` is not mandatory for every database-backed
    Spring Security application; custom `UserDetailsService`
    implementations are also common.
10. `UserDetails` remains the standardized representation of an
    individual security user throughout the architecture.

> **UserDetails represents the user; UserDetailsService loads the user;
> UserDetailsManager manages users; and the manager implementation
> determines where those users are stored.**
