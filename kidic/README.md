# Kidic - Childcare Management System

A comprehensive Spring Boot application for managing childcare facilities, children, classrooms, activities, and attendance.

## Database Setup with XAMPP

### Prerequisites
- XAMPP installed and running
- Java 21 or higher
- Maven or Gradle

### Database Configuration

1. **Start XAMPP Services**
   - Start Apache and MySQL services in XAMPP Control Panel

2. **Create Database**
   - Open phpMyAdmin (http://localhost/phpmyadmin)
   - Create a new database named `kidic_db`
   - Or run the SQL script: `src/main/resources/sql/init-database.sql`

3. **Database Connection**
   - The application is configured to connect to MySQL on localhost:3306
   - Default credentials: username=`root`, password=`` (empty)
   - Database name: `kidic_db`

### Application Configuration

The application is configured in `src/main/resources/application.properties`:

```properties
# Database Configuration for XAMPP MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/kidic_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
```

## Running the Application

1. **Build the project**
   ```bash
   ./gradlew build
   ```

2. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

3. **Access the application**
   - Application runs on: http://localhost:8080
   - Test database connection: http://localhost:8080/api/test/database

## Database Schema

### Entities

1. **User** - System users (Admin, Teacher, Parent, Staff)
2. **Child** - Children enrolled in the system
3. **Classroom** - Classrooms with age ranges and capacity
4. **Activity** - Activities and events
5. **Attendance** - Daily attendance records
6. **Schedule** - Weekly schedules for classrooms
7. **ActivityMaterial** - Materials needed for activities

### Key Relationships

- Users can be parents of multiple children
- Teachers manage classrooms
- Children belong to classrooms
- Activities are associated with classrooms and children
- Attendance tracks daily presence
- Schedules define weekly routines
- Materials are linked to activities

## API Endpoints

### Test Endpoints
- `GET /api/test/database` - Test database connection
- `GET /api/test/users` - Get all users
- `POST /api/test/users` - Create a new user

## Features

- **User Management**: Different user types with role-based access
- **Child Management**: Complete child profiles with medical information
- **Classroom Management**: Age-appropriate classroom organization
- **Activity Planning**: Educational and recreational activities
- **Attendance Tracking**: Daily check-in/check-out records
- **Schedule Management**: Weekly classroom schedules
- **Material Management**: Activity material requirements

## Database Tables

The system creates the following tables:
- `users` - User accounts and profiles
- `classrooms` - Classroom information
- `children` - Child profiles and information
- `activities` - Activity planning and management
- `attendances` - Daily attendance records
- `schedules` - Weekly schedule management
- `activity_materials` - Material requirements for activities

## Sample Data

The initialization script includes sample data:
- Admin user (admin@kidic.com)
- Teacher user (john.teacher@kidic.com)
- Parent user (jane.parent@kidic.com)
- Sample classrooms (Toddler Class, Preschool Class)
- Sample children (Emma Smith, Liam Johnson)

## Development Notes

- Uses Spring Boot 3.5.5 with Java 21
- JPA/Hibernate for ORM
- MySQL as the database
- RESTful API design
- Comprehensive validation
- Automatic timestamp management
- Soft delete functionality
