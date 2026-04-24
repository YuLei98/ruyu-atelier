## 1. Create CommonResult Response Wrapper (Global)

- [x] 1.1 Create `CommonResult<T>` class in global package (e.g., `icu.ruiyu.framework.common`)
- [x] 1.2 Implement `success(T data)` and `success(String message)` static methods
- [x] 1.3 Implement `error(int code, String message)` static method

## 2. Modify AuthController to Return CommonResult

- [x] 2.1 Update `register()` method to return `CommonResult<Void>`
- [x] 2.2 Update `login()` method to return `CommonResult<String>` (token in data)
- [x] 2.3 Update `logout()` method to return `CommonResult<Void>`
- [x] 2.4 Update `getUserDetail()` method to return `CommonResult<String>`

## 3. Add Exception Handling

- [x] 3.1 Create `AuthExceptionHandler` with `@ControllerAdvice`
- [x] 3.2 Handle `BadCredentialsException` → return 401 JSON
- [x] 3.3 Handle `UsernameNotFoundException` → return 401 JSON
- [x] 3.4 Handle validation errors → return 400 JSON

## 4. Update Tests

- [x] 4.1 Update `AuthIntegrationTest` to expect JSON response format
- [x] 4.2 Run all tests and verify they pass