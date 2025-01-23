# 공지사항 api



---

## build & test

docker engine 이 실행되어 있는 상태에서 아래 명령어로 빌드합니다. 

```shell

# 반드시 메인 폴더에서 실행 요망

# jar 파일 생성
gradlew bootJar

# image 생성 후, container 실행 
docker build -t announcement .
docker-compose build
docker-compose up -d 

```
✅ 컨테이너를 최초 실행 시킬 때 타이밍 이슈로 announcement app 이 실행되지 않을 수 있습니다.
이때는 컨테이너를 재 실행 시켜주시기 바랍니다.

1. 먼저 user 포인트 호출로 사용자 생성을 해줍니다. (이때 권한은 ADMIN 권한으로 생성합니다.)


```bash
curl --location 'localhost:8080/user' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "eddy",
    "email" : "ktest92@gmail.com",
    "password" : "eddy",
    "role" : "ADMIN"
}'
```
2. notice 포인트를 호출하여 공지사항을 생성합니다. 1번에서 생성한 user index 값을 동봉하여 호출합니다.


```bash
curl --location 'localhost:8080/notice' \
--header 'Content-Type: application/json' \
--data '{
    "title" : "test 1",
    "content" : "test content",
    "startDate" : "2025-01-23 00:00:00",
    "endDate" : "2025-01-25 00:00:00",
    "attachments" : {
        "fileName" : "test attach 1",
        "filePath" : "test attach path 1"
    },
    "createId" : 1
}'
```

3. 수정 시 아래와 같이 호출합니다.


```bash
curl --location --request PUT 'localhost:8080/notice/2' \
--header 'Content-Type: application/json' \
--data '{
    "title": "update title",
    "content": "update title content",
    "startDate" : "2025-01-24 00:00:00",
    "endDate" : "2025-01-26 00:00:00",
    "updateUserId" : 1,
    "viewed" : true
}'
```

4. 개별 조회


```bash 
curl --location 'localhost:8080/notice/2'
```

5. 다건 조회는 pageable 도입으로 페이징이 가능하도록 하였습니다.

```bash
curl --location --max-time 10 'localhost:8080/notice?page=0&size=5&sort=createdAt%2Casc'
```

6. 삭제

```bash
curl --location --request DELETE 'localhost:8080/notice/1' \
--header 'Content-Type: application/json' \
--data '{
    "userId" : 1
}'
```

---
## 구성

jdk 17

spring boot 3.4.1

maria database

redis

hibernate 6.6.4

## 부하 테스트

k6

influxdb 1.8

---
## 설계 및 주요사항

### ✅엔티티 관계 
공지사항과 첨부파일의 관계는 1:N 관계로 하나의 공지사항에 여러개의 첨부파일이

존재할 것을 염두하고 설계하였습니다.

### ✅대용량
redis 의 캐시를 활용, 데이터 조회 부하 분산 적용하였습니다. 

transaction 을 활용한 무결성을 보장하였고, synchronized 키워드로 동시성을 보장하였습니다.

### ✅첨부 파일

첨부 파일은 별도 클라우드에 저장하고 해당 url 을 api 호출 시 기입 하는

순서를 가장하고 개발을 진행 했습니다.

### ✅사용자

사용자는 공지사항을 등록할 수 있는 권한을 가진 유저가 api 를 이용한다고 가정했고,

ADMIN, MANAGER  권한을 가진 사람만이 공지사항을 등록할 수 있도록 간단한 권한

체계를 두었습니다. 

추가로 편의상 UserController 를 추가해 User 등록을 할 수 있는 포인트를 별도로 두었습니다.

### ✅공지사항 및 첨부파일 수정

공지사항 수정 시 관련된 첨부파일 관련 정보 수정은 성능 이슈를 고려하여 각각의 포인트를

따로 호출하는 방식으로 분리하였습니다. 


```
1. Notice(공지사항) 수정
NoticeController.update

2. Attachment(첨부파일 정보) 수정
AttachmentController.update
```

---
## 폴더 구조
announcement  
│  ├── components  
│  ├── controller  
│      └── dto  
│  ├── config  
│  ├── entities  
│  ├── enums  
│  ├── exception  
│  ├── repositories  
│  └── services  
├── AnnouncementApplication.java  

controller : controller 클래스  
dto : controller 접근 layer dto 디렉토리  
services : 비즈니스 로직 등이 모여 있는 클래스  
repositories : database 접근 객체  
entities : 엔티티 클래스 디렉토리

---
## 동시성 테스트

대량의 요청이 올 것을 대비하여 로컬에서 테스트 코드를 작성

NoticeServiceConcurrencyTest.class 파일 내 작업 수를 100000,

동시 작업 스레드 수를 1000개로 하여 동시성 테스트를 

진행하였습니다.

---
## 부하 테스트

캐시를 통한 호출과 그렇지 않은 호출을 비교하여 부하 테스트를 진행하였습니다.

1. 일반 디비를 통한 테스트 
![dbUseTrafficK6.jpg](src%2Fmain%2Fjava%2Fapi%2Fannouncement%2Fimages%2FdbUseTrafficK6.jpg)
![dbUseTrafficInfluxDb.jpg](src%2Fmain%2Fjava%2Fapi%2Fannouncement%2Fimages%2FdbUseTrafficInfluxDb.jpg)
2. redis 캐시 연동 테스트 
![cacheUseTrafficK6.jpg](src%2Fmain%2Fjava%2Fapi%2Fannouncement%2Fimages%2FcacheUseTrafficK6.jpg)
![cacheUseTrafficInfluxDb.jpg](src%2Fmain%2Fjava%2Fapi%2Fannouncement%2Fimages%2FcacheUseTrafficInfluxDb.jpg)

### ✅비교 결과

| **지표**                    | **redis 캐시 연동**    | **디비 직접 호출**       | **비교 결과**                     |
|-----------------------------|--------------------|--------------------|------------------------------------|
| **Checks 성공률**           | 100% (14262/14262) | 48.49% (6904/14236) | **redis**           |
| **HTTP 요청 실패율**        | 0.00% (0/7131)     | 100.00% (7118/7118) | **redis**           |
| **HTTP 응답 시간 (평균)**   | 3.76ms             | 2.95ms             | **디비 직접 호출**           |
| **HTTP 응답 시간 (최대)**   | 489.48ms           | 306.84ms           | **디비 직접 호출**           |
| **HTTP 응답 시간 (p90)**    | 5.7ms              | 4.25ms             | **디비 직접 호출**           |
| **HTTP 응답 시간 (p95)**    | 6.5ms              | 5.31ms             | **디비 직접 호출**           |
| **HTTP 요청 처리량**        | 7131 요청, 23.75 요청/초 | 7118 요청, 23.69 요청/초 | **유사**                          |
| **Iteration Duration**      | 1s (평균)            | 1s (평균)            | **유사**                          |

- 언뜻 보기엔 디비 직접 호출이 결과가 좋아 보이나 성공률에 따른 지표를 파악해야 하기 때문에 
14236 건 중 6904 만 성공한 디비 직접 호출 방식은 대용량 트래픽에 취약함을 알 수 있었습니다.