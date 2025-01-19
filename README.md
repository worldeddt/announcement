# 공지사항 api




## 데이터 구조

---

## build

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
---
## 구성

jdk 17

spring boot 3.4.1

maria database

redis

hibernate 6.6.4

---
## 설계

### 대용량
redis 의 캐시를 활용, 데이터 조회 부하 분산 적용하였습니다. 

transaction 을 활용한 무결성을 보장하였고, 기타 첨부 파일에 관해서는

비동기로 저장되도록 조치하였습니다.

### 첨부 파일

첨부 파일은 별도 클라우드에 저장하고 해당 url 을 api 호출 시 기입 하는

순서를 가장하고 개발을 진행했습니다.

### 사용자

사용자는 공지사항을 등록할 수 있는 권한을 가진 유저가 api 를 이용한다고 가정했고,

ADMIN, MANAGER  권한을 가진 사람많이 공지사항을 등록할 수 있도록 간단한 권한

체계를 두었습니다. 

추가로 편의상 UserController 를 추가해 User 등록을 할 수 있는 포인트를 별도로 두었습니다.

### 공지사항 및 첨부파일 수정

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
│├── controller  
│├── entities  
│├── repositories  
│└── services  
├── AnnouncementApplication.java  

controller : controller 클래스  
services : 비즈니스 로직 등이 모여 있는 클래스  
repositories : database 접근 객체  
entities : 엔티티 클래스 디렉토리