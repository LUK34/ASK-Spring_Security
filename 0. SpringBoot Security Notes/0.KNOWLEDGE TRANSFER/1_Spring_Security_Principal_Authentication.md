# Spring Security: Principal and Authentication Object

## Introduction

When working with **Spring Security**, two terms appear frequently:

-   **Principal**
-   **Authentication object**

Understanding these concepts is important because they describe **who
the currently logged-in user is** and **what that user is allowed to
do**.

A simple way to remember the difference is:

> **Principal = Who you are**\
> **Authentication object = Who you are + your authentication/security
> information**

------------------------------------------------------------------------

## 1. What Is a Principal?

A **principal** represents the **currently logged-in user**.

In a web application, users normally authenticate by logging in. After
login, the application needs a way to represent the identity of the
authenticated user. The principal represents that identity.

The principal can contain user-related information such as:

-   Username
-   Email
-   Other user details

### Simple Example

Suppose a user logs into an application with the username:

``` text
John Doe
```

Conceptually:

``` text
Principal = John Doe
```

The easiest way to remember this is:

``` text
Principal = Who are you?
```

So, the **principal represents the user's identity within the
application**.

------------------------------------------------------------------------

## 2. What Is the Authentication Object?

The **authentication object** is a more comprehensive representation of
the user's authentication information.

According to the lecture, important information associated with it
includes:

-   **Principal**
-   **Credentials**
-   **Authorities**

Conceptually:

``` text
Authentication
│
├── Principal
│   └── Who the user is
│
├── Credentials
│   └── Authentication-related credentials
│
└── Authorities
    └── Roles or permissions
```

Therefore, the authentication object provides more information than the
principal alone.

------------------------------------------------------------------------

## 3. Principal Inside the Authentication Object

The **principal is part of the authentication object**.

For example:

``` text
Authentication Object
└── Principal: John Doe
```

Here, `John Doe` identifies the logged-in user. That identity is
represented by the principal and forms part of the complete
authentication information.

------------------------------------------------------------------------

## 4. What Are Authorities?

**Authorities** describe the roles or permissions that a user has in the
system.

For example, in an e-commerce application, users could have roles such
as:

``` text
USER
SELLER
ADMIN
SUPER_ADMIN
```

If the logged-in user is an administrator, the authentication
information could conceptually contain:

``` text
Principal: John Doe
Authority: ROLE_ADMIN
```

The authority helps the application determine **what the user can do**.

The exact roles and permissions depend on the application.

------------------------------------------------------------------------

## 5. Principal vs Authentication Object

  ------------------------------------------------------------------------
  Concept                 Purpose                  Simple Meaning
  ----------------------- ------------------------ -----------------------
  **Principal**           Represents the logged-in **Who you are**
                          user's identity          

  **Authentication        Represents broader       **Who you are and what
  Object**                authentication           you can do**
                          information              

  **Authorities**         Represents               **What access you
                          roles/permissions        have**

  **Credentials**         Authentication-related   **Information
                          credential information   associated with
                                                   authentication**
  ------------------------------------------------------------------------

### Example

Assume:

``` text
Username: John Doe
Role: ROLE_ADMIN
```

The conceptual structure is:

``` text
Authentication Object
│
├── Principal
│   └── John Doe
│
└── Authorities
    └── ROLE_ADMIN
```

The **principal** answers:

``` text
Who is logged in?
John Doe
```

The **authority** answers:

``` text
What role/permission does the user have?
ROLE_ADMIN
```

Together, this information forms part of the **authentication object**.

------------------------------------------------------------------------

## 6. Why Does Spring Security Need This Information?

After a user logs in, an application generally needs to answer two
important questions:

### Who is the user?

This is represented by the:

``` text
Principal
```

### What is the user allowed to do?

This is represented through:

``` text
Authorities
```

A useful mental model is:

``` text
Authentication Object
        |
        +---- Who are you?
        |        |
        |        +---- Principal
        |
        +---- What can you do?
                 |
                 +---- Authorities
```

------------------------------------------------------------------------

## 7. Real-World Example

Imagine an e-commerce application.

A user named John logs in and has administrator access.

``` text
User
├── Username: John Doe
└── Role: ROLE_ADMIN
```

Spring Security can conceptually represent the authentication
information as:

``` text
Authentication
├── Principal: John Doe
└── Authorities: ROLE_ADMIN
```

Now the application knows:

``` text
Who is the user?
John Doe

What can the user do?
Perform operations allowed to ROLE_ADMIN
```

This is why the authentication object is more comprehensive than the
principal.

------------------------------------------------------------------------

## 8. Easy Way to Remember

Think of the **principal** like the identity portion of an employee ID
card:

``` text
Name: John Doe
```

It answers:

> **Who are you?**

Now imagine the system also knows:

``` text
Name: John Doe
Role: Administrator
Permissions: Administrative access
```

That broader information is similar to the **authentication object**.

------------------------------------------------------------------------

## 9. Key Takeaways

### Principal

``` text
Principal = Identity of the logged-in user
```

Think: **Who are you?**

### Authentication Object

``` text
Authentication =
    Principal
    + Credentials
    + Authorities
```

Think: **Who are you, and what can you do?**

### Authorities

``` text
Authorities = Roles / Permissions
```

Examples:

``` text
ROLE_USER
ROLE_SELLER
ROLE_ADMIN
ROLE_SUPER_ADMIN
```

------------------------------------------------------------------------

## 10. Quick Revision

``` text
1. Principal
   -> Represents the logged-in user.

2. Authentication Object
   -> Contains broader authentication information.

3. Authorities
   -> Represent the user's roles or permissions.
```

### One-Line Summary

> **The principal identifies the user, while the authentication object
> contains the principal along with authentication and
> authorization-related information such as authorities.**

------------------------------------------------------------------------

## Conclusion

The concepts of **Principal** and **Authentication** are fundamental
when working with Spring Security.

Keep this mental model in mind:

``` text
Principal
    = Who you are

Authorities
    = What you can do

Authentication Object
    = The broader authentication representation containing this information
```

This foundation will make later Spring Security authentication code
easier to understand.
