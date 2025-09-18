# Kidic Backend - API Documentation

Spring Boot backend for Kidic. This document lists all APIs, request/response bodies, authentication, and error formats based on the current codebase.

## Run

- Java 21, Gradle Wrapper
- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- Base URL: `http://localhost:8080`

## Authentication & Security

- JWT-based auth. Obtain a token via auth endpoints and send `Authorization: Bearer <token>`.
- Public routes: `/api/auth/**`, `/api/test/**`, `/actuator/**`
- All other routes require a valid JWT.

## Error Response Format

On validation/business/unexpected errors the API returns the following shape:

```json
{
  "timestamp": "2025-09-17T10:15:30",
  "status": 400,
  "error": "Validation Error | Bad Request | Internal Server Error",
  "message": "Human readable details",
  "path": "uri=/api/..."
}
```

## Auth APIs (`/api/auth`)

### POST `/api/auth/signup/new-family`
- Body:
```json
{
  "name": "string",
  "phone": "string",
  "email": "user@example.com",
  "gender": true,
  "password": "string"
}
```
- Response 200:
```json
{ "token": "<jwt>" }
```

### POST `/api/auth/signup/existing-family`
- Body:
```json
{
  "name": "string",
  "phone": "string",
  "email": "user@example.com",
  "gender": true,
  "password": "string",
  "familyId": "uuid"
}
```
- Response 200: `{ "token": "<jwt>" }`

### POST `/api/auth/login`
- Body:
```json
{ "email": "user@example.com", "password": "string" }
```
- Response 200: `{ "token": "<jwt>" }`

## Child APIs (`/api/child`)

Note: Controller uses `@RequestMapping(name = "api/child")`; this sets a name, not a path. If unreachable, change to `@RequestMapping("/api/child")`. Assuming path `/api/child` as intended.

### POST `/api/child`
- Headers: `Authorization: Bearer <token>`
- Body:
```json
{
  "name": "string (<=100)",
  "gender": true,
  "dateOfBirth": "YYYY-MM-DD",
  "medicalNotes": "string (<=1000)"
}
```
- Response 200:
```json
{
  "name": "string",
  "gender": true,
  "dateOfBirth": "YYYY-MM-DD",
  "medicalNotes": "string"
}
```

## Product APIs (`/api/products`)

### GET `/api/products`
- Response 200: `Product[]`

### POST `/api/products`
- Body (JSON):
```json
{
  "name": "string",
  "link": "string",
  "description": "string",
  "imageType": "IMAGE_1|IMAGE_2|IMAGE_3|IMAGE_4|IMAGE_5|CUSTOM",
  "category": "TOYS|BOOKS|CLOTHING|FOOD|MEDICAL|EDUCATIONAL|ENTERTAINMENT|OTHER"
}
```
- Response 201: `Product`

### PUT `/api/products/{productId}`
- Body: same as POST
- Response 200: `Product`

### DELETE `/api/products/{productId}`
- Response 204

### POST `/api/products/{productId}/image`
- Consumes: `multipart/form-data`
- Fields: `file` (binary)
- Response 200: `Product` (with updated image metadata)
  - 400 on invalid upload, 404 if product not found

### GET `/api/products/{productId}`
- Response 200: `Product`; 404 if not found

### GET `/api/products/{productId}/image`
- Response 200: binary content with headers `Content-Type`, `Content-Disposition`, `Content-Length`
- 404 if image missing or product not found

### DELETE `/api/products/{productId}/image`
- Response 200: `Product`; 404 if not found

#### Product schema
```json
{
  "id": 1,
  "name": "string",
  "link": "string",
  "description": "string",
  "imageType": "IMAGE_1|IMAGE_2|IMAGE_3|IMAGE_4|IMAGE_5|CUSTOM",
  "imageName": "string",
  "imageContent": "<bytes omitted>",
  "imageSize": 123,
  "imageContentType": "image/png",
  "category": "TOYS|BOOKS|CLOTHING|FOOD|MEDICAL|EDUCATIONAL|ENTERTAINMENT|OTHER"
}
```

## Growth Record APIs (`/api/growth-records`)

Enums:
- `GrowthType`: `EMOTIONAL|PHYSICAL|COGNITION`
- `StatusType`: `ACHIEVED|NOT_ACHIEVED`

### GET `/api/growth-records/children/{childId}`
- Response 200: `GrowthRecord[]`

### POST `/api/growth-records/children/{childId}`
- Consumes: `application/x-www-form-urlencoded` or `multipart/form-data`
- Fields:
  - `additionalInfo` (optional string)
  - `dateOfRecord` (required `YYYY-MM-DD`)
  - `height` (optional number)
  - `weight` (optional number)
  - `type` (required enum)
  - `status` (required enum)
- Response 201: `GrowthRecord`

### PUT `/api/growth-records/children/{childId}/{recordId}`
- Consumes: form-data or urlencoded
- Fields: same as POST but all optional
- Response 200: `GrowthRecord`

### DELETE `/api/growth-records/children/{childId}/{recordId}`
- Response 204

#### GrowthRecord schema
```json
{
  "id": 1,
  "additionalInfo": "string",
  "dateOfRecord": "YYYY-MM-DD",
  "height": 100.0,
  "weight": 15.0,
  "type": "EMOTIONAL|PHYSICAL|COGNITION",
  "status": "ACHIEVED|NOT_ACHIEVED",
  "child": { "id": 1, "name": "..." }
}
```

## Medical Record APIs (`/api/medical-records`)

Enums:
- `MedicalRecordType`: `VACCINATION|CHECKUP|ILLNESS|INJURY|ALLERGY|MEDICATION|OTHER`
- `FileType`: `PDF|IMAGE|DOCUMENT|VIDEO|AUDIO|OTHER`
- `StatusType`: `ACTIVE|ARCHIVED|PENDING|COMPLETED|CANCELLED`

### POST `/api/medical-records/children/{childId}`
- Headers: `Authorization: Bearer <token>`
- Body (JSON):
```json
{
  "type": "MedicalRecordType",
  "dateOfRecord": "YYYY-MM-DD",
  "description": "string",
  "file": "FileType",
  "status": "StatusType"
}
```
- Response 201: `MedicalRecordResponseDTO`

### GET `/api/medical-records/children/{childId}`
- Headers: `Authorization: Bearer <token>`
- Response 200: `MedicalRecordResponseDTO[]`

### GET `/api/medical-records/children/{childId}/{recordId}`
- Headers: `Authorization: Bearer <token>`
- Response 200: `MedicalRecordResponseDTO`

### PUT `/api/medical-records/children/{childId}/{recordId}`
- Headers: `Authorization: Bearer <token>`
- Body: same as POST
- Response 200: `MedicalRecordResponseDTO`

### DELETE `/api/medical-records/children/{childId}/{recordId}`
- Headers: `Authorization: Bearer <token>`
- Response 204

### POST `/api/medical-records/children/{childId}/with-file`
- Headers: `Authorization: Bearer <token>`
- Consumes: `multipart/form-data`
- Fields:
  - `type` (required, enum name)
  - `dateOfRecord` (required, `YYYY-MM-DD`)
  - `description` (optional)
  - `status` (optional, enum name)
  - `file` (optional, binary)
- Response 201: `MedicalRecordResponseDTO`

### PUT `/api/medical-records/children/{childId}/{recordId}/with-file`
- Headers: `Authorization: Bearer <token>`
- Consumes: `multipart/form-data`
- Fields: same as POST with-file
- Response 200: `MedicalRecordResponseDTO`

### GET `/api/medical-records/children/{childId}/{recordId}/file`
- Headers: `Authorization: Bearer <token>`
- Response 200: binary download with content headers; 404 if missing

#### MedicalRecordResponseDTO schema
```json
{
  "id": 1,
  "type": "MedicalRecordType",
  "dateOfRecord": "YYYY-MM-DD",
  "description": "string",
  "fileType": "FileType",
  "fileName": "string",
  "fileContent": "<bytes omitted>",
  "fileSize": 123,
  "fileContentType": "application/pdf",
  "status": "StatusType",
  "childId": 1,
  "childName": "string"
}
```

## Test APIs (`/api/test`)

All public.

- GET `/api/test/database` → "Database connection successful!..." or 500
- GET `/api/test/parents` → `Parent[]`
- GET `/api/test/families` → `Family[]`
- GET `/api/test/children` → `Child[]`
- POST `/api/test/parents` → create `Parent`
- POST `/api/test/init-data` → trigger `DataInitializer`
- POST `/api/test/clear-data` → clear all
- POST `/api/test/test-parent` → create a test parent

## Notes & Gotchas

- Child controller mapping likely should be `@RequestMapping("/api/child")` to expose the endpoint. If left as-is, the POST may not be reachable.
- Some endpoints return entity models directly (e.g., `Product`, `GrowthRecord`) which include nested relations. Consider DTOs if you need to control exposure.

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
