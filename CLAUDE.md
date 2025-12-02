# Soongan Backend - Architecture Guide

## Project Overview

**Soongan** is a Kotlin/Spring Boot monorepo application with a main REST API for managing weekly contests and a consumer app for push notifications. The system demonstrates clean architecture patterns with clear separation between application layers.

**Tech Stack:**
- Language: Kotlin 1.9.23
- Framework: Spring Boot 3.2.4
- Database: MySQL with JPA/Hibernate
- Query Tool: QueryDSL for type-safe queries
- Cache: Redis (JWT tokens, message streams)
- Cloud: Google Cloud Storage (GCS) for images, Firebase Cloud Messaging (FCM)
- Testing: JUnit 5, MockK
- API Documentation: SpringDoc OpenAPI (Swagger)

---

## Project Structure

```
soongan-backend/
├── apps/
│   ├── soongan-api/        # Main REST API application
│   └── soongan-consumer/   # Event consumer app (FCM notifications)
├── libs/
│   ├── soongan-persistence/  # JPA entities, repositories, adapters
│   ├── soongan-support/      # Utilities, exceptions, enums, handlers
│   ├── soongan-redis/        # Redis integration (JWT, message streams)
│   └── soongan-web/          # Web layer config (security, filters, resolvers)
└── gradle/                   # Gradle wrapper files
```

---

## Module Responsibilities

### 1. **soongan-api** (Main Application)
**Entry Point:** `SoonganApiApplication.kt`

This is the main REST API application handling all business logic and HTTP endpoints.

**Key Features:**
- Spring Boot @SpringBootApplication with @EnableScheduling
- ComponentScan across all `com.soongan.soonganbackend` packages
- Multipart file upload support (50MB max)
- Environment-based configuration (`application.yml` imports 4 lib configs)

**Structure:**
```
soongan-api/src/main/kotlin/com/soongan/soonganbackend/soonganapi/
├── interfaces/          # REST Controllers (input layer)
│   ├── auth/           # Authentication endpoints
│   ├── weeklyContest/  # Contest management
│   ├── comment/        # Comments on posts
│   ├── postLike/       # Post likes
│   ├── notification/   # Notifications
│   ├── member/         # Member profiles
│   ├── home/           # Home screen
│   ├── awards/         # Awards/winners
│   ├── report/         # User reports
│   ├── fcm/            # FCM token management
│   ├── commentLike/    # Comment likes
│   └── callback/       # Webhooks
├── service/            # Business logic layer
│   ├── auth/          # OAuth2 validators, login/logout
│   ├── weeklyContest/ # Contest service with validators
│   ├── comment/       # Comment service with validators
│   ├── member/        # Member service
│   ├── postLike/      # Like functionality
│   ├── notification/  # Notification service
│   ├── home/          # Home screen data service
│   ├── awards/        # Award/winner logic
│   ├── report/        # Report processing
│   ├── fcm/           # FCM integration
│   └── commentLike/   # Comment like service
├── admin/             # Admin endpoints
│   ├── weeklyContest/ # Manage contests
│   ├── member/        # User management
│   └── report/        # View reports
├── consumer/          # Redis event consumers
└── helper/            # Helper utilities
```

**Key Controllers Pattern:**
- All use `@RestController` with `@RequestMapping` using Uri constants
- Use `@LoginMember` custom annotation for authentication
- Support both authenticated (JWT required) and public endpoints
- Return wrapped responses via `ApiResponseBodyWrapper`
- Validate input with `@Valid` Jakarta validation

**Services Pattern:**
- Transaction management with `@Transactional`
- Read-only transactions where applicable
- Dependency injection of Adapters (not direct repositories)
- Business logic validation via dedicated Validator classes
- Error handling with custom exceptions

### 2. **soongan-persistence**
**Core Responsibility:** Database layer with JPA entities and repositories.

**Configuration:**
- `JpaConfig.kt`: Enables JPA repositories, JPA auditing, entity scanning
- `QuerydslConfig.kt`: Sets up JPAQueryFactory with custom Hibernate5 templates

**Entity Structure:**
```
storage/
├── member/           # User accounts
├── weeklyContest/    # Contest definitions
├── weeklyContestPost/# Contest submissions
├── weeklyContestFinal/ # Top 7 winners
├── postLike/         # Post likes
├── comment/          # Comments on posts
├── commentLike/      # Comment likes
├── notification/     # Notification records
├── notiSetting/      # User notification preferences
├── notiMember/       # Notification recipients
├── fcm/              # FCM device tokens
├── report/           # User reports
├── explain/          # Explanation posts
├── userBanHistory/   # Ban records
```

**Entity-Repository-Adapter Pattern:**
Each entity has three components:

1. **Entity** (`*Entity.kt`)
   - JPA `@Entity` with `@EntityListeners(AuditingEntityListener::class)`
   - Auto-managed `createdAt` and `updatedAt` timestamps
   - Lazy-loaded foreign keys with `@ManyToOne(fetch = FetchType.LAZY)`
   - Strategic indexes for query optimization

2. **Repository** (`*Repository.kt`)
   - Extends `JpaRepository<T, Long>` for CRUD operations
   - Custom query methods using Spring Data naming conventions
   - `@Query` with JPQL for complex queries
   - Cursor pagination support for large datasets

3. **Adapter** (`*Adapter.kt`)
   - Business logic wrapper around repositories
   - Transaction boundaries `@Transactional`
   - Read-only queries with `@Transactional(readOnly = true)`
   - Exposes business methods to services

**Example Flow:**
```kotlin
// Service calls adapter (not repository)
weeklyContestPostAdapter.getLatestPostWithSlicing(contest, page, size)

// Adapter wraps repository with transaction
@Transactional(readOnly = true)
fun getLatestPostWithSlicing(...): Slice<WeeklyContestPostEntity> {
    return weeklyContestPostRepository.findAllByWeeklyContestOrderByCreatedAtDesc(...)
}
```

**Key Entities:**

**MemberEntity:**
```kotlin
- id: Long (PK)
- email: String (unique)
- nickname: String?
- birthYear: Int?
- selfIntroduction: String?
- profileImageUrl: String?
- provider: ProviderEnum (GOOGLE, KAKAO, APPLE)
- providerId: String
- withdrawalAt: LocalDateTime? (soft delete)
- banUntil: LocalDateTime?
- createdAt/updatedAt: LocalDateTime (auto)
```

**WeeklyContestPostEntity:**
```kotlin
- id: Long (PK)
- weeklyContest: WeeklyContestEntity (FK, lazy)
- member: MemberEntity (FK, lazy)
- title: String
- imageUrl: String (GCS path)
- likeCount: Int
- commentCount: Int
- blindedAt: LocalDateTime? (content hidden but exists)
- deletedAt: LocalDateTime? (soft delete)
- deletedReason: DeletedReasonEnum?
- createdAt/updatedAt: LocalDateTime (auto)
- Indexes: (weeklyContestId, ranking), (memberId)
```

**QueryDSL Customization:**
- Custom `CursorSpec` for cursor-based pagination
- `DateTimeCursorSpec` for timestamp-based sorting
- `IntegerCursorSpec` for numeric field sorting
- Custom `CustomFunctionContributor` for database functions

### 3. **soongan-support**
**Core Responsibility:** Cross-cutting concerns and utilities.

**Key Components:**

**Exception Handling:**
- `SoonganException`: Base exception with StatusCode
- `SoonganUnauthorizedException`: For auth failures
- `GlobalExceptionHandler`: RestControllerAdvice with 14+ exception mappings
- Custom error response formatting with color-coded logging (MDC tracking)

**Domain Enums** (10+ shared enums):
```kotlin
- ProviderEnum: GOOGLE, KAKAO, APPLE
- UserAgentEnum: ANDROID, IOS, WEB
- ContestStatusEnum: ONGOING, VOTING, FINISHED
- WeeklyContestPostOrderCriteriaEnum: LATEST, OLDEST, MOST_LIKED
- NotificationTypeEnum: COMMENT, LIKE, AWARD, etc.
- ReportTypeEnum: INAPPROPRIATE, SPAM, etc.
- ReportTargetTypeEnum: POST, COMMENT, MEMBER
- DeletedReasonEnum: REPORTED, REQUESTED, etc.
```

**Response DTOs:**
- `ExportResponseDto`: Standard success response wrapper
- `CommonErrorResponseDto`: Standard error response
- `PageDto`: Pagination metadata
- `FcmMessageDto`: Firebase Cloud Messaging format

**Utilities:**
- `LocalDateTimeUtil`: Date/time helpers
- `GcpStorageService`: Google Cloud Storage integration
- `ObjectJsonConverter`: JSON serialization
- `ResourceLoader`: Configuration loading
- `SortDirection`: ASC/DESC enum

**Configuration:**
- `JasyptEncryptor`: Property encryption for secrets
- `ObjectMapperConfig`: Jackson configuration
- `GcpStorageConfig`: GCS client setup

### 4. **soongan-redis**
**Core Responsibility:** Redis integration for stateful operations.

**JWT Management:**
- `JwtData`: Redis-backed JWT storage
- `JwtRepository`: CrudRepository for JWT CRUD
- `JwtAdapter`: Business wrapper with transactional boundaries
- TTL-based expiration (30 min access, 14 day refresh)

**Message Streaming:**
- `RedisMessageProducer`: Publishes events to Redis Streams
- Stream-based event pipeline for async processing
- ObjectMapper integration for serialization

**Configuration:**
- `RedisConfig`: Redis template and connection setup
- `RedisProperties`: Environment-based configuration
- `RedisStreamKey`: Constants for stream names
- `RedisMessageConsumer`: Consumer configuration

### 5. **soongan-web**
**Core Responsibility:** Web layer configuration, security, and custom resolvers.

**Security Configuration:**
```kotlin
SecurityConfig:
- Stateless session management (JWT-based)
- CORS enabled for https://api-dev.soongan.site
- CSRF disabled (API endpoints)
- Filter chain: ApiKeyFilter -> JwtFilter
```

**Authentication Filters:**
- `JwtFilter`: Extracts and validates JWT, sets SecurityContext
  - Skips public endpoints (GET/POST allowlists)
  - Adds `UsernamePasswordAuthenticationToken` with email
  
- `ApiKeyFilter`: API key validation for server-to-server calls

- `MdcContextFilter`: Request UUID for logging correlation

- `HttpLoggingFilter`: Request/response logging

**Custom Argument Resolvers:**
```kotlin
@LoginMember(throwIfUnauthorized = true/false)
MemberEntity? loginMember
// Resolves from SecurityContext, optionally requires auth
```

**Response Processing:**
- `ApiResponseBodyWrapper`: ResponseBodyAdvice intercepting all responses
- Wraps successful responses in `ExportResponseDto`
- Bypasses wrapping for specific URIs (error responses, exports)

**Configuration:**
- `WebMvcConfig`: WebMvcConfigurer for component registration
- `SwaggerConfig`: SpringDoc OpenAPI UI setup
- `RestTemplateConfig`: HTTP client beans

---

## Authentication & Security Flow

### OAuth2 Login Process

```
Client (App)
    |
    v
Auth Provider (Google/Kakao/Apple) -- obtains idToken
    |
    v
POST /api/v1/auth/login
    |
    ├─> Validate idToken (Provider-specific validator)
    ├─> Check if member exists (provider + providerId)
    ├─> Create new member if needed
    ├─> Update FCM token association
    ├─> Issue JWT pair (access + refresh)
    └─> Return tokens

JWT Structure:
- Access Token: 30 minutes (subject = user email)
- Refresh Token: 14 days
- Stored in Redis with TTL
```

### Request Authentication

```
GET /api/v1/weekly/contests
    |
    v
JwtFilter:
  1. Extract "Authorization: Bearer <token>"
  2. Validate with JwtHandler (checks expiry, signature, Redis)
  3. Set SecurityContext with email as principal
  4. Allow controller to access via @LoginMember
```

---

## Database & Persistence Architecture

### JPA Configuration
- Hibernate 6.0 with Jakarta Persistence
- Auto-managed timestamps via `@EntityListeners(AuditingEntityListener::class)`
- Lazy loading for performance optimization
- Strategic indexing for hot queries

### QueryDSL Integration
- Type-safe query building
- Custom cursor pagination for large result sets
- Expression-based filtering and sorting
- Integration with Spring Data Slice/Page

### Cursor-Based Pagination
```kotlin
// Replaces offset-based pagination for better performance
// Supports sorting by created date or like count
CursorResponseWrapper<List<Post>>
  - data: List[Post]
  - nextCursor: String? (encodes position + PK)
```

---

## API Structure & Patterns

### Response Format

**Success Response:**
```json
{
  "statusCode": 0,
  "message": "성공",
  "data": { /* controller return value */ }
}
```

**Error Response:**
```json
{
  "statusCode": 400,
  "message": "에러 메시지",
  "details": "상세 정보"
}
```

### URI Constants
Centralized in `Uri.kt` for DRY principle:
```kotlin
Uri.AUTH          = "/api/v1/auth"
Uri.LOGIN         = "/login"
Uri.LOGOUT        = "/logout"
Uri.WEEKLY        = "/weekly"
Uri.CONTESTS      = "/contests"
Uri.POSTS         = "/posts"
Uri.CURSOR        = "/cursor"
Uri.ADMIN         = "/admin"
```

### Controller Patterns

1. **Endpoint Definition:**
   - `@Tag` for Swagger grouping
   - `@Operation` with summary/description
   - `@SecurityRequirement(name = "JWT")` for secured endpoints
   - Request validation with `@Valid`

2. **Method Signatures:**
   ```kotlin
   @GetMapping("/path/{id:[0-9]+}")
   fun getResource(
       @LoginMember(throwIfUnauthorized = false) loginMember: MemberEntity?,
       @PathVariable id: Long,
       @RequestParam orderBy: Enum,
       @RequestBody @Valid request: RequestDto
   ): ResponseDto
   ```

3. **Service Delegation:**
   - Controllers don't contain business logic
   - Delegate all logic to services
   - Services use adapters (not direct repositories)

### Service Layer Patterns

1. **Transaction Boundaries:**
   ```kotlin
   @Transactional           // read-write, propagation required
   fun modifyData(...) { ... }
   
   @Transactional(readOnly = true)  // optimized for reads
   fun fetchData(...) { ... }
   ```

2. **Validation Pattern:**
   ```kotlin
   // Services use dedicated validators
   val validatedObject = validator.validate(inputs)
   
   // Validators throw SoonganException with StatusCode
   if (!isValid) throw SoonganException(StatusCode.ERROR_CODE)
   ```

3. **Service Composition:**
   ```kotlin
   @Service
   class ContestService(
       private val contestAdapter: ContestAdapter,
       private val postAdapter: PostAdapter,
       private val validator: ContestValidator,
       private val gcpStorageService: GcpStorageService
   )
   ```

---

## Key Domain Models & Relationships

### Contest Domain
```
WeeklyContest (1) ---> (*) WeeklyContestPost
    |
    ├─ Round: Int
    ├─ StartAt/EndAt: LocalDateTime
    ├─ Status: ContestStatusEnum
    └─ Winners: WeeklyContestFinal

WeeklyContestPost ---> Member
    ├─ Title: String
    ├─ ImageUrl: String (GCS)
    ├─ LikeCount/CommentCount: Int
    └─ Status: (Active, Blinded, Deleted)
```

### Interaction Domain
```
PostLike: Post <---> Member
    ├─ PostId, ContestType (WEEKLY/AWARDS)
    └─ Unique constraint on (postId, memberId, contestType)

Comment: Post <---> Member
    ├─ Content: String
    ├─ ParentComment?: Comment (threading)
    └─ Status: (Active, Deleted)

CommentLike: Comment <---> Member
```

### Member Domain
```
Member:
    ├─ OAuth2: (provider, providerId) unique
    ├─ Profile: (nickname, bio, profileImage)
    ├─ Status: (Active, Withdrawn, Banned)
    └─ Relations:
        ├─ Posts: (*) WeeklyContestPost
        ├─ Comments: (*) Comment
        ├─ Likes: (*) PostLike/CommentLike
        ├─ Notifications: (*) Notification
        └─ FcmTokens: (*) FcmToken
```

---

## Testing Architecture

### Test Structure
```
src/test/kotlin/com/soongan/soonganbackend/soonganapi/
├── unit/
│   └── service/       # Service unit tests with MockK
│       ├── auth/
│       ├── weeklyContest/
│       ├── comment/
│       └── ...
```

### Testing Patterns

**Unit Test Example:**
```kotlin
@ExtendWith(MockKExtension::class)
class AuthServiceTest {
    @MockK
    private lateinit var memberAdapter: MemberAdapter
    
    @InjectMockKs
    private lateinit var authService: AuthService
    
    @Test
    fun `로그인 성공 - 신규 회원`() {
        // given
        every { memberAdapter.getByEmail(any()) } returns null
        
        // when
        val result = authService.login(userAgent, loginDto)
        
        // then
        verify { memberAdapter.save(any()) }
        assertThat(result).isNotNull()
    }
}
```

**Testing Tools:**
- JUnit 5 with MockK for mocking
- AssertJ for fluent assertions
- Kotlin test coroutine support

---

## Unique Architectural Patterns

### 1. **Adapter Pattern (Repository Wrapper)**
Every persistence layer has three tiers:
- Repository: Spring Data interface
- Adapter: Business wrapper with transactions
- Service: Consumer of adapter

Benefits: Single responsibility, easy mocking, centralized transaction management.

### 2. **Cursor-Based Pagination**
Replaces traditional offset-based pagination for:
- Efficient large dataset browsing
- Consistent ordering with deletions
- State-encoded position (timestamp + PK)

### 3. **Validator Classes**
Business logic separated from services:
```kotlin
WeeklyContestValidator -> Dedicated validation
WeeklyContestPostValidator -> Post-specific rules
CommentValidator -> Comment business rules
```

### 4. **Soft Deletes**
Entities use `deletedAt` timestamps instead of hard deletes:
- Data preservation for auditing
- Referential integrity maintained
- Queries filter out deleted records

### 5. **Lazy Loading with Strategic Indexing**
```kotlin
@ManyToOne(fetch = FetchType.LAZY)  // Don't load related entities
@Index(columnList = "weekly_contest_id,ranking")  // Add indexes for queries
```

### 6. **MDC-Based Request Tracing**
Each request gets a UUID for logging:
```
[UUID] [Error] 400 Bad Request
[UUID] [Info] Processing completed
```

### 7. **Environment-Based Configuration**
```yaml
# Application imports lib configs
spring.config.import:
  - optional:soongan-redis.yml
  - optional:soongan-persistence.yml
  - optional:soongan-support.yml
  - optional:soongan-web.yml
```

---

## soongan-consumer Application

**Entry Point:** `SoonganConsumerApplication.kt`

Lightweight event consumer for FCM push notifications:
- Subscribes to Redis message streams
- Processes events asynchronously
- Sends push notifications via Firebase Cloud Messaging
- Minimal direct database operations

**Service:**
```kotlin
@Service
class FcmService(
    private val restTemplate: RestTemplate
) {
    fun pushFcmMessage(message: Message): ResponseEntity<Map<String, Any>>
    fun getFcmAccessToken(): String
}
```

---

## Configuration & Dependency Management

### Gradle Structure
**Root build.gradle.kts:**
- All subprojects use Java 17
- Shared dependency versions
- Kotlin compiler options (-Xjsr305=strict)
- JUnit 5 platform

**Library build.gradle.kts:**
- `bootJar.enabled = false` (library, not executable)
- `jar.enabled = true` (publish as JAR)
- No Spring Boot embedded server

**App build.gradle.kts:**
- `bootJar.enabled = true` (executable)
- Spring Boot starter dependencies
- Application-specific libraries only

### Application Properties
```yaml
# soongan-api/application.yml
spring:
  application.name: soongan-api
  config.import:
    - optional:soongan-redis.yml
    - optional:soongan-persistence.yml
    - optional:soongan-support.yml
    - optional:soongan-web.yml
  profiles.active: ${EXECUTION_ENV}
  servlet.multipart:
    max-file-size: 50MB
    max-request-size: 50MB

server.port: ${SERVER_PORT}

oauth2:
  android.google.client-id: ${OAUTH2_ANDROID_GOOGLE_CLIENT_ID}
  ios.google.client-id: ${OAUTH2_IOS_GOOGLE_CLIENT_ID}

firebase:
  project-id: ${FCM_FIREBASE_PROJECT_ID}
  key-json-string: ${FCM_FIREBASE_KEY_JSON}
```

---

## Development Patterns & Conventions

### Naming Conventions
- **Entities:** `*Entity` (MemberEntity, PostEntity)
- **Repositories:** `*Repository` (MemberRepository)
- **Adapters:** `*Adapter` (MemberAdapter)
- **Services:** `*Service` (MemberService)
- **Controllers:** `*Controller` (MemberController)
- **DTOs:** `*Request/ResponseDto` (LoginRequestDto)
- **Validators:** `*Validator` (MemberValidator)

### Package Structure
```
com.soongan.soonganbackend.{module}
├── config/          # Configuration beans
├── storage/         # Persistence layer
│   └── {entity}/
│       ├── {Entity}Entity.kt
│       ├── {Entity}Repository.kt
│       ├── {Entity}Adapter.kt
│       └── {Entity}RepositoryCustom.kt
├── service/         # Business logic
│   └── {domain}/
│       ├── {Domain}Service.kt
│       └── validator/
│           └── {Domain}Validator.kt
├── interfaces/      # API layer
│   └── {domain}/
│       ├── {Domain}Controller.kt
│       └── dto/
│           ├── request/
│           └── response/
└── util/            # Shared utilities
```

### Data Validation
1. **Framework Level:** Jakarta `@Valid` on method parameters
2. **Business Level:** Custom validators throwing `SoonganException`
3. **Database Level:** Constraints on entities (unique, nullable, etc.)

### Error Handling Strategy
```
1. Validation Exceptions -> 400 Bad Request
2. SoonganException -> 500 with StatusCode-specific message
3. SoonganUnauthorizedException -> 401 Unauthorized
4. Generic Exception -> 500 Service Not Available
```

---

## Integration Points

### Google Cloud Storage
- Image upload for contest posts
- Path structure: `contests/{contestType}/{round}/{memberId}/{timestamp}`
- Configured in `GcpStorageConfig`

### Firebase Cloud Messaging
- Push notifications to app users
- Token management in FcmTokenEntity
- Async processing via consumer app
- Message format: Title, Body, Data payload

### OAuth2 Providers
- Google: Android/iOS apps with provider-specific client IDs
- Kakao: Korean OAuth2 provider
- Apple: Sign in with Apple
- Validators: `GoogleOAuth2Validator`, `KakaoOAuth2Validator`, `AppleOAuth2Validator`

---

## Performance Considerations

### Query Optimization
1. **Lazy Loading:** Related entities loaded on demand
2. **Strategic Indexes:** Composite indexes for common queries
3. **Cursor Pagination:** Efficient browsing of large datasets
4. **Read-Only Transactions:** QueryDSL optimizations

### Caching Strategy
1. **JWT Tokens:** Redis TTL-based expiration
2. **Message Streams:** Event queuing via Redis Streams
3. **Database Queries:** Can be extended with Spring Cache

### N+1 Query Prevention
- Repository methods designed for batch operations
- Lazy loading with explicit join requirements
- QueryDSL for complex multi-table queries

---

## Security Considerations

1. **JWT Security:** 
   - HMAC-SHA signing with environment secret
   - Token validation on every protected request
   - Redis-backed revocation on logout

2. **CORS:**
   - Limited to `https://api-dev.soongan.site`
   - Credentials allowed with appropriate headers
   - 3600s max age for preflight

3. **Encryption:**
   - Jasypt for property-level encryption
   - GCS service account key as environment variable
   - Firebase keys loaded from environment

4. **Input Validation:**
   - Jakarta Validation annotations
   - Regex patterns for path variables
   - File upload size limits

---

## Known Architectural Decisions

1. **No Direct Repository Usage in Services:**
   - All data access through Adapters
   - Enables easier mocking and testing
   - Centralizes transaction management

2. **Monolithic Database:**
   - Single MySQL database for all modules
   - Normalized schema with foreign keys
   - Could be split into service-specific databases later

3. **Synchronous API:**
   - HTTP request-response for all user-facing APIs
   - Async event processing via Redis Streams (consumer app)
   - No queue system for non-critical notifications yet

4. **No GraphQL:**
   - REST endpoints following RESTful conventions
   - Could be extended with GraphQL layer later

5. **Soft Deletes:**
   - Maintains historical data
   - Could impact query performance with large datasets
   - Consider archival strategy for very old data

---

## Extension Points

1. **Adding New Entity:**
   - Create `*Entity.kt` in `persistence/storage/{domain}/`
   - Create `*Repository.kt` interface
   - Create `*Adapter.kt` wrapper
   - Create `*Service.kt` in `soongan-api/service/`
   - Create `*Controller.kt` in `soongan-api/interfaces/`

2. **Adding New OAuth Provider:**
   - Implement `*OAuth2Validator.kt`
   - Add provider enum case
   - Update `AuthService.kt` login method

3. **Adding New Notification Type:**
   - Add `NotificationTypeEnum` case
   - Update `CreateNotiMessages.kt`
   - Extend consumer app logic

4. **Custom Caching:**
   - Use Spring `@Cacheable`, `@CacheEvict`
   - Integrate with Redis via `spring-data-redis`
   - Cache invalidation on entity updates

---

## Build & Deployment

### Gradle Build
```bash
./gradlew clean build          # Full build
./gradlew :soongan-api:build   # Single app
./gradlew test                 # Run all tests
```

### Docker
- `Dockerfile-api-dev` for local development
- Multi-stage build for production readiness
- Exposed port 8080 by default

### Environment Variables
```
EXECUTION_ENV              # dev, prod, etc.
SERVER_PORT                # 8080, etc.
OAUTH2_ANDROID_GOOGLE_CLIENT_ID
OAUTH2_IOS_GOOGLE_CLIENT_ID
FCM_FIREBASE_PROJECT_ID
FCM_FIREBASE_KEY_JSON      # JSON string
JWT_SECRET                 # Base64 encoded 256-bit key
DATABASE_URL
DATABASE_USER
DATABASE_PASSWORD
REDIS_HOST
REDIS_PORT
GCS_BUCKET_NAME
```

---

## Summary

This is a **well-structured monorepo** demonstrating:
- Clear separation of concerns (persistence, support, web, redis)
- Consistent patterns (Entity-Repository-Adapter-Service)
- Security-first design (JWT, CORS, validation)
- Scalability through lazy loading and cursor pagination
- Testability via dependency injection and mocking
- Cloud-native integration (GCS, FCM, Redis)

The architecture supports rapid feature development while maintaining code quality and performance standards.
