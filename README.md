# Practice System Login-2

System Login-2 ทำขึ้นเพื่อศึกษา Spring boot, Restful, Database, Jwt, Kafka, Redis และการทำงานของระบบ

## Structure Modules

* login (server backend, producer)
* email (consumer)
* common ทำหน้าที่เป็นตัวกลางเพื่อแชร์ข้อมูล email ให้กับ kafka

**หลักการทำงาน** เมื่อ user ทำการ register. server จะทำการบันทึกข้อมูลลง database ในขณะเดียวกัน server
จะส่งข้อมูลเกี่ยวกับ email ให้กับ kafka และ kafka จะทำหน้าที่ส่ง email ให้ user. เมื่อ user
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
        |   ├── CustomUserDetails.java 
        |   ├── KafkaConfig.java
        |   ├── RedisConfig.java
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
        |   ├── AuthRequest
        |   |     ├── ActivateRequest.java
        |   |     ├── ForgotPasswordRequest.java
        |   |     ├── LoginRequest.java
        |   |     ├── PasswordResetRequest.java
        |   |     ├── RegisterRequest.java
        |   |     └── ResendActivationEmailRequest.java
        |   |     
        |   ├── AuthResponse
        |   |     └── MUserResponse.java
        |   |     
        |   ├── request
        |   |    ├── ProfilePictureRequest.java
        |   |    ├── RoleUpdateRequest.java
        |   |    └── UpdateRequest.java
        |   | 
        |   └── response
        |        └── ErrorResponse.java
        |
        ├── exception
        |   ├── GlobalExceptionHandler
        |   ├── BadRequestException.java
        |   ├── ConflictException.java
        |   ├── ForbiddenException.java
        |   ├── GoneException.java
        |   ├── NotFoundException.java
        |   ├── StorageException.java
        |   ├── UnauthorizedException.java
        |   |
        |   └── customImp
        |       └── CustomAccessDeniedHandler.java
        |
        ├── mapper
        |    └── UserMapper.java
        |
        ├── model
        |   ├── BaseModel.java
        |   ├── EmailConfirm.java
        |   ├── JwtBlacklist.java
        |   ├── JwtToken.java
        |   ├── PasswordResetToken.java
        |   └── User.java
        |
        ├── repository
        |   ├── AdminRepository.java
        |   ├── AuthRepository.java
        |   ├── EmailConfirmRepository.java
        |   ├── JwtBlacklistRepository.java
        |   └── JwtTokenRepository.java
        |
        ├── schedule
        |   ├── JwtSchedule.java
        |   └── UsersSchedule.java
        |
        ├── service
        |   ├── AdminService.java
        |   ├── AdminServiceImp.java
        |   ├── AuthService.java
        |   ├── AuthServiceImp.java
        |   ├── EmailConfirmService.java
        |   ├── EmailConfirmServiceImp.java
        |   ├── JwtBlacklistService.java
        |   ├── JwtBlacklistServiceImp.java
        |   ├── JwtTokenService.java
        |   ├── JwtTokenServiceImp.java
        |   ├── StorageService.java
        |   └── StorageServiceImp.java
        |
        ├── util
        |   └── SecurityUtil.java
        |
        |


    └── src/test
            ├── business_unit_test
            |       ├── AdminBusinessTest.java
            |       └── AuthBusinessTest.java
            |        
            ├── controller_unit_test
            |       ├── AdminControllerTest.java
            |       └── AuthControllerTest.java
            |
            ├── business_unit_test
            |       ├── AdminRepoTest.java
            |       ├── AuthRepoTest.java
            |       ├── EmailConfirmRepoTest.java
            |       ├── JwtBlacklistRepoTest.java
            |       └── JwtTokenRepoTest.java
            |        
            └── business_unit_test
                    ├── AdminServiceTest.java
                    ├── AuthServiceTest.java
                    ├── EmailConfirmServiceTest.java
                    ├── JwtBlacklistServiceTest.java
                    └── JwtTokenServiceTest.java
                    

    └── src/main
    
        /resources
            ├── email
            |   ├── activate-user.html
            |   └── reset-password.html
            |
            └── application.yml

---

## Sequence diagram auth

### registers
![registers](https://github.com/user-attachments/assets/d9b241f9-75ac-4622-a714-048937304814)

### login
![login](https://github.com/user-attachments/assets/ab755e06-8965-4bb8-a4b6-7b3d79e06180)

### activate
![activate](https://github.com/user-attachments/assets/13a8468e-9c0f-438b-9f92-5496b1184793)

### resend-activation-email
![resend-activation-email](https://github.com/user-attachments/assets/27ad0c6d-9d1c-40bb-a8fe-f9ed7cc17d39)

### forgot-password
![forgot-password](https://github.com/user-attachments/assets/db76aa64-a70b-45d8-8ac8-ba94e46ac4fa)

### reset-password
![reset-password](https://github.com/user-attachments/assets/7630501a-29e6-499a-a3db-cdd7ae5ed569)

### refresh-token
![refresh-token](https://github.com/user-attachments/assets/908505ec-5042-4591-a0de-3375cf55d141)

### profile
![get-me](https://github.com/user-attachments/assets/5770053a-b29d-4ff5-b12b-3e2af46742ee)

### update-me
![put-me](https://github.com/user-attachments/assets/2bfd9d9a-119d-4077-aeee-9fcf90db2549)

### delete-me
![delete-me](https://github.com/user-attachments/assets/10f2a101-5e4d-4d9a-9d75-9bd5b4d7c107)



## Sequence diagram admin

### get-all
![get-all](https://github.com/user-attachments/assets/a39cec53-edad-4c21-b8d2-58d082f1d016)

### get-id
![get-id](https://github.com/user-attachments/assets/b242639c-e3a4-43ce-938b-264b538b087c)

### update-id
![put-id](https://github.com/user-attachments/assets/187c9b14-6282-40fe-8c6c-637acf8eba9b)

### delete-id
![delete-id](https://github.com/user-attachments/assets/2d5e7abd-bab3-4902-bb19-3e83fd50847d)

### remove-role
![remove-role](https://github.com/user-attachments/assets/f70ee7c2-0d30-4c28-a5e1-5e91711d6363)

### search-role
![search-role](https://github.com/user-attachments/assets/d7da0fe7-05cf-4648-996a-933f74455ce3)

