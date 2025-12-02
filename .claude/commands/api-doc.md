---
description: 컨트롤러 엔드포인트의 상세한 API 문서를 노션 형식으로 생성합니다
---

특정 엔드포인트 또는 컨트롤러에 대한 상세한 API 문서를 노션(Notion) 형식으로 생성하는 작업을 수행합니다.

## 작업 순서

1. **엔드포인트 확인**: 어떤 컨트롤러 또는 엔드포인트를 문서화할지 사용자에게 물어봅니다 (명시되지 않은 경우)

2. **코드 분석**: 컨트롤러, 서비스, DTO, 관련 검증 로직을 모두 읽어서 분석합니다

3. **문서 생성**: 아래 섹션들을 포함하여 노션 형식으로 문서를 생성합니다

   ### 📌 기본 정보
   - HTTP 메서드 & 경로
   - 기능 설명
   - 인증 필요 여부 (필요/불필요)
   - Rate Limiting (있는 경우)

   ### 📥 Request

   **Headers**
   - 필수/선택 여부 명시
   - 각 헤더의 설명

   **Path Parameters**
   - 타입, 설명, 검증 규칙 포함
   - 예시 값 제공

   **Query Parameters**
   - 타입, 설명, 검증 규칙 포함
   - 기본값 명시 (있는 경우)

   **Request Body**
   - 예시 JSON 포함
   - 모든 필드의 타입 명시
   - 필수/선택 여부 표시
   - 검증 제약사항 (최소/최대 길이, 패턴 등)

   ### 📤 Response

   #### ✅ 성공 응답 (200/201)
   ```json
   {
     "statusCode": 0,
     "message": "성공",
     "data": {
       // 실제 응답 구조와 예시 값
     }
   }
   ```
   - data 객체의 모든 필드 설명
   - 데이터 타입 포함
   - 예시 값 제공

   #### ❌ 에러 응답
   각 에러 케이스마다:
   - HTTP Status Code
   - StatusCode enum 값
   - 에러 메시지
   - 예시 JSON 응답
   - 발생 조건

   확인할 일반적인 에러들:
   - 400 Bad Request (검증 실패)
   - 401 Unauthorized (인증 필요)
   - 403 Forbidden (권한 부족)
   - 404 Not Found (리소스 없음)
   - 500 Internal Server Error (비즈니스 로직 예외)


4. **형식**: 노션에 적합한 깔끔한 마크다운 사용 (토글, 콜아웃, 테이블 등)

5. **꼼꼼하게 확인**: 실제 코드에서 다음을 체크
   - Validator 클래스의 비즈니스 규칙
   - Exception이 발생하는 위치
   - 사용되는 StatusCode enum 값
   - DTO 필드 어노테이션 (@NotNull, @Size 등)

## 📋 출력 예시 (노션 형식)

아래와 같은 형식으로 문서를 생성합니다:

---

# POST /api/v1/weekly/contests/{contestId}/posts

> 📌 **주간 콘테스트에 작품 제출**

---

## 📌 기본 정보

- **HTTP Method**: `POST`
- **경로**: `/api/v1/weekly/contests/{contestId}/posts`
- **인증**: ✅ 필요 (JWT)
- **Content-Type**: `multipart/form-data`

---

## 📥 Request

### Headers

| 헤더명 | 필수 여부 | 설명 |
|--------|----------|------|
| Authorization | ✅ 필수 | Bearer {accessToken} |
| Content-Type | ✅ 필수 | multipart/form-data |

### Path Parameters

| 파라미터 | 타입 | 설명 | 검증 규칙 |
|---------|------|------|----------|
| contestId | Long | 주간 콘테스트 ID | 양수여야 함 |

### Request Body (multipart/form-data)

| 필드 | 타입 | 필수 여부 | 설명 | 검증 규칙 |
|-----|------|----------|------|----------|
| title | String | ✅ 필수 | 작품 제목 | 1-100자 |
| image | File | ✅ 필수 | 콘테스트 이미지 | JPG/PNG, 최대 50MB |

---

## 📤 Response

### ✅ 성공 응답 (201 Created)

```json
{
  "statusCode": 0,
  "message": "성공",
  "data": {
    "postId": 123,
    "title": "아름다운 석양",
    "imageUrl": "https://storage.googleapis.com/soongan-bucket/...",
    "createdAt": "2025-12-02T10:30:00"
  }
}
```

**응답 필드 설명**

| 필드 | 타입 | 설명 |
|-----|------|------|
| postId | Long | 생성된 게시글 ID |
| title | String | 작품 제목 |
| imageUrl | String | 업로드된 이미지 URL (GCS) |
| createdAt | String | 생성 일시 (ISO 8601) |

---

### ❌ 에러 응답

<details>
<summary><strong>400 Bad Request - 제목 길이 오류</strong></summary>

```json
{
  "statusCode": 4001,
  "message": "제목은 1-100자 사이여야 합니다."
}
```

**발생 조건**: 제목이 비어있거나 100자를 초과한 경우

</details>

<details>
<summary><strong>401 Unauthorized - 인증 토큰 없음</strong></summary>

```json
{
  "statusCode": 1001,
  "message": "인증이 필요합니다."
}
```

**발생 조건**: Authorization 헤더가 없거나 유효하지 않은 JWT 토큰

</details>

<details>
<summary><strong>404 Not Found - 콘테스트 없음</strong></summary>

```json
{
  "statusCode": 2001,
  "message": "콘테스트를 찾을 수 없습니다."
}
```

**발생 조건**: 존재하지 않는 contestId로 요청

</details>

<details>
<summary><strong>500 Internal Server Error - 콘테스트 종료됨</strong></summary>

```json
{
  "statusCode": 3001,
  "message": "종료된 콘테스트에는 참여할 수 없습니다."
}
```

**발생 조건**: 콘테스트 상태가 ONGOING이 아닌 경우

</details>

위와 같은 형식으로 문서화할 엔드포인트를 알려주세요!