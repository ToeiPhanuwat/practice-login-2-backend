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