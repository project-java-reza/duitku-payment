# Duitku Wallet
## Duitku Wallet is an application that has `top up`, `transfer`, `beneficiary`, `online loans`, 'add address', 'bill payment' features as the main feature.

## Download the Repository
- Download the repository to your computer using the `git clone` command. Url of the repository can be seen in the desired repository.
```
git clone <repository url> <destination folder>
```

#### Example :
```
git clone https://github.com/project-java-reza/duitku-payment.git
```

## How to run Test:
1. Make sure you have installed the Java Development Kit (JDK) at least `Version 11` and Maven on your computer.
2. Open a terminal or command prompt and navigate to the project directory Example: `final-project-duitku`.
3. Run the following command to run all tests:
```
mvn clean test
```

## How to Run Application:
```
mvn spring-boot:run
```

## Noted:
- Make sure you have done the proper configuration in the `application.properties` file.

# API Spec
## Authentication

### Register User
#### Request Body :
- Method: POST
- Endpoint: `api/auth/register/user`
- Header:
  - Content-Type: application/json
  - Accept : application/json
- Body:
```json
{
  "mobileNumber": "08xxxxxx",
  "email": "abcde@gmail.com",
  "name": "rizqi",
  "password": "rahasia"
}
```

### Response Body : 

```json
{
 "statusCode":{
    "created" : 201,
    "bad request": 400,
    "conflict": 409
 },
 "message": {
  "created": "Successfully registered user",
  "bad request": "Failed registered user + e.getMessage()",
  "conflict": "User already exists"
 },
 "paging" : null
}
```

### Register Admin 
#### Request Body :
- Method: POST
- Endpoint: `api/auth/register/admin`
- Header:
  - Content-Type: application/json
  - Accept : application/json
- Body:
```json
{
  "mobileNumber": "08xxxxxx",
  "email": "abcde@gmail.com",
  "name": "rizqi",
  "password": "rahasia"
}
```

### Response Body : 

```json
{
 "statusCode":{
    "created" : 201,
    "bad request": 400,
    "conflict": 409
 },
 "message": {
  "created": "Successfully registered admin",
  "bad request": "Failed registered admin + e.getMessage()",
  "conflict": "Admin already exists"
 },
 "paging" : null
}
```

### Login
#### Request Body :
- Method: POST
- Endpoint: `api/auth/login`
- Header : 
  - Content-Type : application/json
  - Accept : applicatin/json
- Body :
```json
{
    "mobileNumber": "08xxxxxxx",
    "password" : "supersecret"
}
```

### Response Body
```json
{
 "statusCode":{
    "Ok": 200,
    "bad request": 400
 },
 "message": {
  "Ok": "Successfully Login",
  "bad request": "Incorrect Mobile number or password",
 },
 "paging" : null
}
```

## User

### Get All User `[Admin]`
#### Request Body :
- Method : GET
- Endpoint : `api/auth`
- Header :
 - Accept: application/json,
#### Response Body:

```json

{
    "statusCode":{
    "Ok": 200,
    "Forbidden": 403
 },
    "message": {
        "Ok": "Successfully get all users",
        "forbidden" : "prohibited access"
    },
    "data": [
        
    ]
}

```