# Spring Security – Custom User Model

## Overview

Spring Security provides the `UserDetails` interface and a built-in `User` implementation for representing authenticated users.

For simple applications, the built-in model may be enough. However, many real-world applications require additional user information and domain-specific behavior.

That is where a **custom user model** becomes useful.

A custom user model allows you to represent application users with fields and logic that are specific to your business requirements while still integrating with Spring Security.

Conceptually:

```text
Spring Security
      |
      v
 UserDetails
      ^
      |
 implements
      |
Custom User Model
```

## Why Use a Custom User Model?

Spring Security's built-in user representation focuses mainly on:

```text
Username
Password
Authorities
Account status
```

Real applications often need more, such as:

```text
First name
Last name
Email
Phone number
Profile picture
Department
Employee ID
Military ID
Patient ID
Preferences
Account verification status
MFA settings
```

A custom user model allows you to store and expose this additional information.

## Extending User Information

Suppose your application requires:

```text
firstName
lastName
email
profileImageUrl
```

A custom model can combine Spring Security data and business-specific data:

```text
CustomUser
   |
   +---- username
   +---- password
   +---- authorities
   +---- firstName
   +---- lastName
   +---- email
   +---- profileImageUrl
```

## Domain-Specific Requirements

Different domains need different user attributes.

A medical application may need:

```text
Doctor ID
Department
Specialization
Employee number
Facility
Clinical role
```

A gaming application may need:

```text
Display name
Level
Rank
Achievements
Avatar
```

An enterprise application may need:

```text
Employee ID
Department
Manager
Office location
Organization
Cost center
```

This is one of the main reasons to create a custom user model.

## Custom Authentication and Authorization Logic

Sometimes authentication or authorization depends on extra information, such as:

```text
Account verified?
MFA enabled?
User approved?
Department active?
Employee status active?
Account suspended?
```

A custom model can expose these attributes to the security layer.

```text
Authentication Request
        |
        v
Custom UserDetailsService
        |
        v
Custom User Model
        |
        +---- username
        +---- password
        +---- enabled
        +---- verified
        +---- mfaEnabled
        +---- department
        |
        v
Authentication / Authorization Decision
```

## Integration with Other Systems

Applications may integrate with:

```text
CRM
ERP
HR system
Hospital system
Directory service
External identity service
```

A custom user model can store identifiers such as:

```text
crmCustomerId
erpEmployeeId
hrPersonId
externalDirectoryId
```

## Enhanced Security Requirements

Custom models can support security state such as:

```text
MFA enabled
Account verified
Password changed date
Password expiry date
Failed login count
Account locked until
Last login date
Recovery email
```

Example:

```text
CustomUser
   |
   +---- username
   +---- password
   +---- enabled
   +---- accountVerified
   +---- mfaEnabled
   +---- failedLoginAttempts
   +---- passwordExpiryDate
```

## Basic Architecture

A simple approach is:

```text
            UserDetails
                ^
                |
          implements
                |
        CustomUserDetails
```

A cleaner real-world design is often:

```text
Database Entity
      |
      v
ApplicationUser
      |
      v
CustomUserDetails
      |
      v
Spring Security
```

## Example Custom UserDetails

```java
public class CustomUserDetails implements UserDetails {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;

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

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // Additional getters
}
```

The exact required methods can vary slightly by Spring Security version.

## Example Domain Entity

```java
@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
    private boolean accountVerified;
    private boolean mfaEnabled;
}
```

## Custom UserDetails Wrapper

Instead of making the entity implement `UserDetails`, you can wrap it:

```java
public class CustomUserDetails implements UserDetails {

    private final AppUser user;

    public CustomUserDetails(AppUser user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getFirstName() {
        return user.getFirstName();
    }
}
```

This keeps domain/persistence concerns separate from Spring Security concerns.

## Custom UserDetailsService

A custom user model is commonly loaded through a custom `UserDetailsService`:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        AppUser user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return new CustomUserDetails(user);
    }
}
```

The flow becomes:

```text
Authentication Request
        |
        v
DaoAuthenticationProvider
        |
        v
CustomUserDetailsService
        |
        v
UserRepository
        |
        v
Database
        |
        v
AppUser
        |
        v
CustomUserDetails
        |
        v
Spring Security
```

## Benefits

A custom user model provides:

```text
Flexibility
Better domain modeling
Cleaner organization
Support for custom security logic
Improved testability
External-system integration
Personalization
Future extensibility
```

## Built-In User vs Custom User

| Feature | Built-In `User` | Custom User Model |
|---|---|---|
| Username | Yes | Yes |
| Password | Yes | Yes |
| Authorities | Yes | Yes |
| Account status | Yes | Yes |
| Email | No custom field | Yes |
| First/last name | No custom field | Yes |
| Domain-specific fields | Limited | Yes |
| MFA information | Limited | Yes |
| External IDs | Limited | Yes |
| Personalization | Limited | Yes |
| Flexibility | Basic | High |

## When Should You Use One?

A custom user model is useful when your application needs:

```text
Additional user fields
Domain-specific information
Custom account states
MFA support
Account verification
External system identifiers
Advanced authorization
User personalization
Scalable user management
```

You may not need one when:

```text
The application is very small
Only username/password/roles are needed
You are creating a prototype
You are learning Spring Security
You are using simple in-memory users
```

## Important Design Recommendation

Do not add every database field to the security model simply because it exists.

For larger applications, a cleaner architecture is often:

```text
Domain Entity
     |
     v
Custom UserDetails
     |
     v
Spring Security
```

This keeps business data and security concerns properly separated.

## Interview Questions

### What is a custom user model?

A custom user model is an application-specific representation of a user that exposes additional fields and behavior beyond Spring Security's basic built-in `User`.

### Why use one?

To support additional attributes, domain-specific requirements, MFA, custom authorization, external integrations, and application-specific account state.

### Must the entity implement `UserDetails` directly?

No. A wrapper such as `CustomUserDetails` is often a cleaner design.

### How is it usually loaded?

Through a custom `UserDetailsService`, commonly backed by a repository.

### Can it contain non-security fields?

Yes, although only fields useful to authentication, authorization, identity, or relevant application logic should be exposed to the security model.

## Key Takeaways

Remember:

```text
Spring Security User
    =
basic built-in security representation

Custom User Model
    =
application-specific security representation
```

A common real-world design is:

```text
Database Entity
     |
     v
Custom UserDetails
     |
     v
UserDetails
     |
     v
Spring Security
```

The main reasons for using a custom user model are:

1. Additional user information.
2. Domain-specific fields.
3. Custom authentication requirements.
4. Custom authorization requirements.
5. External-system integration.
6. MFA and stronger account security.
7. Better code organization.
8. Easier testing.
9. User personalization.
10. Future extensibility.

> **A custom user model bridges your application's domain-specific user data with Spring Security's `UserDetails` contract.**
