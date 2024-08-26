# Practice System Login-2

System Login-2 ทำขึ้นเพื่อศึกษา Spring boot, Restful, Database, Jwt, Kafka, Redis และการทำงานของระบบ

## Structure Modules

* login (server backend, producer)
* email (consumer)
* common ทำหน้าที่เป็นตัวกลางเพื่อแชร์ข้อมูล email ให้กับ kafka

**หลักการทำงานคร่าวๆ** เมื่อ user ทำการ register. server จะทำการบันทึกข้อมูลลง database ในขณะเดียวกัน server
จะส่งข้อมูลเกี่ยวกับ email ให้กับ kafka และ kafka จะทำหน้าที่ส่ง email ให้ user แทน และเมื่อ user
ทำการยืนยันอีเมลเรียบร้อยแล้ว จึงสามารถเข้าสู่ระบบได้

## Features

* Java 17
* Spring Boot 3
* Rest API
* PostgreSQL
* Kafka
* Redis
* Jwt
* Json
* Passay
* Mapstruct
* Lombok
* Unit Test

---

## Structure backend

    └── src/main/java/com

        /example_login_2
        ├── LoginApplication.java
        |
        ├── business
        |   ├── AdminBusiness.java
        |   ├── AuthBusiness.java
        |   └── EmailBusiness.java
        |
        ├── config
        |   ├── token
        |   │   └── TokenFilter.java
        │   │
        |   ├── AppConfig.java *ยังไม่ได้ทำ
        |   ├── KafkaConfig.java
        |   └── SecurityConfig.java
        |
        ├── controller
        |   ├── ApiResponse.java
        |   ├── ModelDTO.java
        |   |
        |   ├── api
        |   │   ├── AdminController.java
        |   │   └── AuthController.java
        │   │
        |   └── request
        |       ├── AuthActivateRequest.java
        |       ├── AuthLoginRequest.java
        |       ├── AuthRegisterRequest.java
        |       ├── AuthResendActivationEmailRequest.java
        |       ├── ProfilePictureRequest.java
        |       ├── RoleUpdateRequest.java
        |       └── UpdateRequest.java
        |
        ├── exception
        |   ├── GlobalExceptionHandler
        |   ├── BadRequestException.java
        |   ├── ConflictException.java
        |   ├── ForbiddenException.java
        |   ├── GoneException.java
        |   ├── NotFoundException.java
        |   ├── StorageException.java
        |   └── UnauthorizedException.java
        |
        ├── model
        |   ├── BaseModel.java
        |   ├── Address.java
        |   ├── EmailConfirm.java
        |   ├── JwtToken.java
        |   └── User.java
        |
        ├── repository
        |   ├── AddressRepository.java
        |   ├── AdminRepository.java
        |   ├── AuthRepository.java
        |   ├── EmailConfirmRepository.java
        |   └── JwtTokenRepository.java
        |
        ├── schedule
        |   └── UsersCleanup.java
        |
        ├── service
        |   ├── AddressService.java
        |   ├── AddressServiceImp.java
        |   ├── AdminService.java
        |   ├── AdminServiceImp.java
        |   ├── AuthService.java
        |   ├── AuthServiceImp.java
        |   ├── EmailConfirmService.java
        |   ├── EmailConfirmServiceImp.java
        |   ├── JwtTokenService.java
        |   ├── JwtTokenServiceImp.java
        |   ├── StorageService.java
        |   └── StorageServiceImp.java
        |
        ├── util
        |   └── SecurityUtil.java
        |


    └── src/main

        /resources
            ├── email
            |   └── email-activate-user.html
            |
            └── application.yml

---

## ตัวอย่าง Rest APIs

---

### Auth

| Method | Url                                  |       Decription        | Sample Valid <br/>Request Body |
|:------:|--------------------------------------|:-----------------------:|:------------------------------:|
|  POST  | /api/v1/auth/registe                 |         Sign up         |              JSON              |
|  POST  | /api/v1/auth/login                   |         Log in          |              JSON              | 
|  POST  | /api/v1/auth/activate                |      Confirm email      |              JSON              |
|  POST  | /api/v1/auth/resend-activation-email | Resend activation email |              JSON              |
|  GET   | /api/v1/auth                         |        Get by me        |                                |
|  PUT   | /api/v1/auth                         |      Update by me       |   multipart/form-data, JSON    |
| DELETE | /api/v1/auth                         |      Delete by me       |                                |
|  GET   | /api/v1/auth/refresh-token           |      Refresh token      |                                |

### Admin

| Method | Url                           |  Decription  | Sample Valid <br/>Request Body |
|:------:|-------------------------------|:------------:|:------------------------------:|
|  GET   | /api/v1/admin                 |   Get all    |                                |
|  GET   | /api/v1/admin/{id}            |  Get by ID   |                                | 
|  PUT   | /api/v1/admin/{id}            | Update by ID |   multipart/form-data, JSON    |
|  PUT   | /api/v1/admin/removeRole/{id} | Remove role  |              JSON              |
| DELETE | /api/v1/{id}                  | Delete by ID |                                |
|  GET   | /api/v1/admin/search          | Search role  |         Request Param          |
