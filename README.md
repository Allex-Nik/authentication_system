# Authentication System

This project is an authentication system built with Kotlin, Ktor, and PostgreSQL. It allows users to register, log in, update their account information, log out, and delete their account. The system uses JWT tokens for secure authentication.

## Features
- User Registration
- User Login (with JWT token generation)
- View User Information
- Update User Information
- Delete User Account
- JWT-based Authentication

## Setup Instructions

### Install PostgreSQL, create a user and a database
1. Download and install PostgreSQL.
2. Open `pgAdmin` or use the command line to set up your database.
3. Create a role and a database `auth_system`.

### Set up environment variables and JDK
1. DB_USER (set username of the owner of your database in PostgreSQL)
2. DB_PASSWORD (set the password of your database)
3. JWT_SECRET (you can use openssl for generation of the secret: ```bash openssl rand -base64 32 ```)

You can use IntelliJ for that: `Run -> Edit Configurations -> Environment variables`. Add the variables mentioned above and their values.

Use Oracle JDK 17.

### Build and run the application
Run PostgreSQL and connect to your database `auth_system`.

Run IntelliJ, build project and run main function in Application.kt.

The application will be available at http://localhost:8080.

You can check (and change) the state of `users` table in `pgAdmin` (your server -> `auth_system` database -> Schemas -> Tables -> Right click on `users` table -> View/Edit data -> All Rows), or via terminal.

## Testing
For testing, we use a separate database.
1. Create a role and a database in PostgreSQL using data (username, password, and JWT secret) from `test/resources/application-test.yaml`.
2. Run tests (`Right click on "test" package -> Run tests`).

## API Endpoints
`/auth/register` - POST, register a new user

`/auth/login` - POST, login and get a JWT token

`/user` - GET,	get user information

`/user` - PUT, update user information

`/user` - DELETE, delete user account

`/auth/logout` - POST, log out the user
