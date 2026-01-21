
# 1. Key Security Principles (Easy Notes)

### 1.1.Least Privilege
- Give users and systems only the minimum access they need to do their job, nothing more.
- In Spring Security, this means roles like USER, ADMIN should access only their allowed endpoints.

### 1.2.Secure by Design
- Security should be built into the application from the start, not added later as a fix.
- Spring Security follows this by blocking everything by default unless you explicitly allow it.

### 1.3.Fail-Safe Defaults
- If something goes wrong or is unclear, the system should deny access by default, not allow it.
- Example: If a URL is not configured in Spring Security, access is denied automatically.

### 1.4️.Secure Communication
- All data exchanged between client and server should be encrypted and protected.
- This is why Spring applications use HTTPS, SSL/TLS, and secure headers.

### 1.5️.Input Validation
- Never trust user input—always validate and sanitize it before processing.
- Prevents attacks like SQL Injection, XSS, even before Spring Security checks authorization.

### 1.6️.Auditing and Logging
- Track who did what and when in the system for security and troubleshooting.
- In Spring Security, this helps log login attempts, access failures, and sensitive actions.

### 1.7️.Regular Updates and Patch Management
- Keep frameworks, libraries, and dependencies up to date to avoid known vulnerabilities.
- Using the latest Spring Security version protects you from already-fixed security flaws.

*** 
# 2. Heart of Spring Security

### 2.1 Client Sends Request
- A user sends a request (login request or API call) from browser/Postman.
- Example: /login or /api/orders

### 2️.2 Request Enters the Security Filter Chain
- The request first goes into the Filter Chain (pink box).
- This is a series of security checks that run before your controller.

### 2.3️ Authentication Filter Intercepts the Request
- The Authentication Filter checks:
- Is this a login request?
- Does it contain username & password?
- If yes, it creates an Authentication object and forwards it.

### 2.4️.AuthenticationManager Takes Control
- The AuthenticationManager is the main decision maker.
- It decides which AuthenticationProvider should handle the request.

### 2.5️. AuthenticationProvider Validates User
- DaoAuthenticationProvider does the real work:
- Calls UserDetailsService.loadUserByUsername()
- Fetches user details from the Database
- Username, password hash, roles are loaded here.

### 2.6 PasswordEncoder Verifies Password
- The PasswordEncoder:
- Encrypts the incoming password
- Compares it with the stored encrypted password using matches()
- If passwords match → authentication success
- If not → authentication fails.

### 2.7 SecurityContext Is Updated
- If authentication is successful:
- User details are stored in SecurityContext
- Spring Security now knows who the user is
- This is how Spring knows the logged-in user across requests.

### 2.8 ️Request Reaches Controller
- Now the request is forwarded to Your Application Controllers.
- Controller executes normally because the user is authenticated.

### 2.9 Response Goes Back Through Filters
- The response travels back through the filter chain and reaches the client.
***

# 3. Form Base Authentication
### Project : SS_1_FB_4.x.x
- The defualt authentication in spring security is FORM BASED AUTHENTICATION.
- 1. Without Spring security
```
URL: localhost:8080/hello
```
- 2. With Spring security when you execute the above url. The url will redirected to default spring security/
```
URL: localhost:8080/login
```
- 3. to access the application you need to take the password from the console of your sts ide.
```
Username: user
Passwrod: 6cce3dba-98d8-4f3c-b4cf-2ae5bdd8dfd0
```
- 4. After step 2. If you want to logout. there is a logout page as well that can be accessed.
```
URL: localhost:8080/logout
```
- 5. **---- IMPORTANT ----**
- If you want to setup a default password. Then in the properties file setup the user password.
- e.g:
```
spring.security.user.password=hello_world
```
- 6. After updating the property and try accessing the above url.
```
URL: localhost:8080/hello
```
- 7. You will be redirected to login page.
```
URL: localhost:8080/login
Username: user
Passwrod: hello_world
```
- 8. ** ---- IMPORTANT ----**
- Same as step 5. But we add username also.
```
spring.security.user.name= hello
spring.security.user.password=hello_world
```
- 9. After updating the property and try accessing the above url.
```
URL: localhost:8080/hello
```
- 10. You will be redirected to login page.
```
URL: localhost:8080/login
Username: hello
Passwrod: hello_world
```
***









