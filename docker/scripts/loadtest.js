import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '1m', target: 10 },  // 1분간 10명의 유저로 부하 증가
    { duration: '3m', target: 50 }, // 3분간 50명의 유저 유지
    { duration: '1m', target: 0 },  // 1분간 유저 감소
  ],
};

const BASE_URL = 'http://172.18.0.4:8080'; // Docker Compose 내부 네트워크 주소
const NOTICE_ID = 24; // 테스트할 Notice ID

export default function () {
  const res = http.get(`${BASE_URL}/notice/${NOTICE_ID}`);

  // 응답 검증
  check(res, {
    'is status 200': (r) => r.status === 200,
    'has correct content type': (r) => r.headers['Content-Type'] === 'application/json',
  });

  sleep(1); // 1초 대기
}
