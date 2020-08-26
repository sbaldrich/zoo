## A simple JWT demo using Spring Boot

A basic implementation of an authorization flow using JWT.

### The Setup

The `SecuredResource` controller contains a secured `GET` endpoint that can only be accessed
by a user with the `ADMIN` authority. To get it, a user with the proper permissions has to 
authenticate herself by sending a `POST` request to the `/api/authenticate/` endpoint that 
is present in the `UserJWTController` to get a (jwt) token. 
By presenting this token in the `Authorization` header, a user with enough permissions should 
be able to access the secured resource.

### See it in action

1 - Run the project either via `bootRun` or launching the main class.

2 - Send a get request to `/api/resource/` to see it be denied.

```bash
HTTP/1.1 401 Unauthorized
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Type: application/json
Date: Wed, 26 Aug 2020 16:00:28 GMT
Expires: 0
Pragma: no-cache
Transfer-Encoding: chunked
WWW-Authenticate: Basic realm="Realm"
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block

{
    "error": "Unauthorized",
    "message": "",
    "path": "/api/resource",
    "status": 401,
    "timestamp": "2020-08-26T16:00:28.438+00:00"
}
```

3 - Authenticate as an `admin` by sending a `POST` request to the `/api/authenticate/`. Note the `id_token` that is returned both in the `Authorization` header and the response body.
```bash
> TOKEN=`http -h :8080/api/authenticate username=admin password=admin | grep Authorization | awk '{print $3}'`
```

4 - Use the given token to get the secured resource:

```bash
> http :8080/api/resource "Authorization: Bearer $TOKEN"                                                                                                       

HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Length: 21
Content-Type: text/plain;charset=UTF-8
Date: Wed, 26 Aug 2020 15:58:58 GMT
Expires: 0
Pragma: no-cache
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block

Aw shucks, you got me
```

5 - Optionally, repeat the same steps using the `username=user, password=user` credentials to see the request being forbidden for lack of privileges.