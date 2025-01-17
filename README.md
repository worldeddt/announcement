# announcement

공지사항 api

---

# build

docker engine 이 실행되어 있는 상태에서 아래 명령어로 빌드합니다. 


```shell

# 반드시 메인 폴더에서 실행 요망
docker build -t announcement .
docker-compose build
docker-compose up -d 

```
---
# 구성

java

jdk 17

spring boot 3.4.1

maria database

redis

hibernate 6.6.4

---
# 설계

redis 의 캐시를 활용, 데이터 조회 부하 분산 적용하였습니다. 

transaction 을 활용한 무결성을 보장하였고, 기타 첨부 파일에 관해서는

비동기로 저장되도록 조치하였습니다. 


