# Authentication System

This project is an authentication system built with Kotlin, Ktor, and PostgreSQL. It allows users to register, log in, update their account information, and delete their account. The system uses JWT tokens for secure authentication.

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
3. Create a role and a database auth_system.

### Set up environment variables
1. DB_USER
2. DB_PASSWORD
3. JWT_SECRET (you can use openssl)

You can use IntelliJ for that: Run -> Edit Configurations -> Environment variables.

### Build and run the application
The application will be available at http://localhost:8080.

## Testing
For testing, we use a separate database.
1. Create a role and a database using data from test/resources/application-test.yaml
2. Run tests.

## API Endpoints
/auth/register - POST, register a new user

/auth/login - POST, login and get a JWT token

/user - GET,	get user information

/user - PUT, update user information

/user - DELETE, delete user account

/auth/logout - POST, log out the user
