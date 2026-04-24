# Auth Response Format

## Capability Overview

- **Name**: auth-response-format
- **Type**: spec
- **Status**: draft

## Description

Standardizes the JSON response format for all AuthController endpoints to follow a consistent `{code, message, data}` structure with appropriate HTTP status codes.

## API Specification

### Response Format

All API responses follow this JSON structure:

```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": null
}
```

| Field | Type | Description |
|-------|------|-------------|
| code | int | HTTP status code (200, 400, 401, 500) |
| message | string | Human-readable message |
| data | object/null | Response payload |

### Endpoints

#### POST /user/register

**Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Success Response (200):**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

**Error Response (400):**
```json
{
  "code": 400,
  "message": "用户名不能为空",
  "data": null
}
```

**Duplicate Username (400):**
```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

#### POST /user/login

**Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Success Response (200):**
```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Authentication Failed (401):**
```json
{
  "code": 401,
  "message": "认证失败",
  "data": null
}
```

#### POST /user/logout

**Success Response (200):**
```json
{
  "code": 200,
  "message": "注销成功",
  "data": null
}
```

#### GET /user/info/{username}

**Success Response (200):**
```json
{
  "code": 200,
  "message": "SUCCESS",
  "data": "用户详情: admin"
}
```

**Access Denied (403):**
```json
{
  "code": 403,
  "message": "禁止访问",
  "data": null
}
```

**Unauthorized (401):**
```json
{
  "code": 401,
  "message": "认证失败",
  "data": null
}
```

## Implementation

### CommonResult<T> Class

```java
@Data
public class CommonResult<T> {
    private int code;
    private String message;
    private T data;

    public static <T> CommonResult<T> success(T data) { ... }
    public static <T> CommonResult<T> success(String message) { ... }
    public static <T> CommonResult<T> error(int code, String message) { ... }
}
```

### Location
- `CommonResult.java` → `src/main/java/icu/ruiyu/framework/common/CommonResult.java`
- `AuthController.java` → Modify return types to `CommonResult<?>`
- `GlobalExceptionHandler.java` → Handle authentication exceptions

## Error Codes

| HTTP Status | Code Value | Use Case |
|-------------|------------|----------|
| 200 | 200 | Success |
| 400 | 400 | Bad request (validation errors) |
| 401 | 401 | Authentication failed |
| 403 | 403 | Access denied (insufficient permissions) |
| 500 | 500 | Internal server error |