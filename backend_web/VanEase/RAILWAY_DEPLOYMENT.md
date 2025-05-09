# Railway Deployment Guide for VanEase Backend

This guide outlines the steps to deploy the VanEase backend application to Railway.

## Prerequisites

- A Railway account (Sign up at [Railway.app](https://railway.app))
- Git installed on your local machine
- Railway CLI (optional but recommended)

## Configuration Files

The following configuration files have been added to support Railway deployment:

1. `railway.json` - Railway configuration file
2. `Procfile` - Defines the command to start the application
3. `system.properties` - Specifies the Java version
4. `application-prod.properties` - Production environment configuration

## Environment Variables

Railway automatically provides the following MySQL environment variables that are used in the application:

- `MYSQLHOST` - MySQL host (typically mysql.railway.internal)
- `MYSQLPORT` - MySQL port (typically 3306)
- `MYSQLDATABASE` - MySQL database name (typically railway)
- `MYSQLUSER` - MySQL username (typically root)
- `MYSQLPASSWORD` - MySQL password

You'll also need to set these additional environment variables:
- `JWT_SECRET` - Secret key for JWT token generation
- `JWT_EXPIRATION` - JWT token expiration time in milliseconds
- `JWT_REFRESH_EXPIRATION` - JWT refresh token expiration time in milliseconds
- `ADMIN_USERNAME` - Admin username (optional)
- `ADMIN_PASSWORD` - Admin password (optional)
- `PORT` - Application port (Railway will set this automatically)

## Deployment Steps

### Option 1: Deploy via Railway CLI

1. Install Railway CLI:
   ```
   npm i -g @railway/cli
   ```

2. Login to Railway:
   ```
   railway login
   ```

3. Link your project:
   ```
   railway link
   ```

4. Deploy the application:
   ```
   railway up
   ```

### Option 2: Deploy via Railway Dashboard

1. Create a new project in Railway dashboard
2. Connect your GitHub repository
3. Add a MySQL database service
4. Configure the environment variables
5. Railway will automatically detect and deploy your application

## Monitoring and Logs

- View logs in the Railway dashboard
- Monitor application performance in the Railway dashboard
- Set up alerts for application errors

## Troubleshooting

- If the application fails to start, check the logs for errors
- Verify that all environment variables are correctly set
- Ensure the database is properly configured
- Check that the Java version is correctly specified in `system.properties`
