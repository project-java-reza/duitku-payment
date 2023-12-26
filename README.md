# Duitku Payment System

The Digital Wallet Application's REST API, developed by Duitku, offers users the convenience and flexibility of being able to transfer money to and from their digital wallet and linked bank accounts. The API also allows for transactions with other registered users on the Duitku platform and the ability to make payments directly to saved beneficiaries through their bank accounts. 

## Tech Stack
- Java
- Spring
- Spring Boot
- Hibernate
- Maven
- JDBC
- PostgreSQL
- Postman

## Dependencies
- Spring Data JPA
- PostgreSQL Driver
- Lombok
- Spring Boot Dev Tools
- Spring Web
- Spring Security
- JWT
- Spring Boot Validation


## Features
- Transfer money to and from digital wallet and linked bank accounts
- Perform transactions with other registered user on the duitku platform
- Make payments directly to saved beneficiaries through bank accounts

## User Functionalities
- **Authentication Management**
    - Endpoint for Sign Up
    - Endpoint for Sign In

- **Financial Management**

    - Endpoint for Updating Personal Information and Address
    - Endpoint for Adding Bank Account Information
    - Endpoint for Updating Bank Account Information
    - Endpoint for Viewing Bank Account Information
    - Endpoint for Topping Up Wallet from Bank Account Balance
    - Endpoint for Transferring Money from Wallet to Bank Account
  
    - Endpoint for Transferring User From User

##  Admin Functionalities

- **Authentication Management**
    - Endpoint for Sign Up
    - Endpoint for Sign In
  
  **Financial Management**

    - Endpoint for Get Users
    - Endpoint for Get All Transaction

<a href="https://ibb.co/dbXMcRS"><img src="https://i.ibb.co/8zGsBHJ/Duitku-drawio.png" alt="Duitku-drawio" border="0"></a>


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

### User

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
