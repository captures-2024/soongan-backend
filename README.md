# GCP 서버 사용시
[Dockerfile-api-dev](Dockerfile-api-dev) + .github + workflow 사용

# HomeServer 사용시
[Dockerfile-api-home](Dockerfile-api-home) + [docker-compose.yml](docker-compose.yml) 사용
.env 추가 필요
실행명령어
# 기존 컨테이너 완전히 삭제
docker-compose down

# 이미지도 삭제
docker rmi $(docker images | grep soongan-api | awk '{print $3}') 2>/dev/null

# 캐시 없이 빌드
docker-compose build --no-cache

# 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f soongan-api