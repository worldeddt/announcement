import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 10, // 가상 사용자 수
  duration: '30s', // 테스트 실행 시간
};

export default function () {
  const res = http.get('http://announcement:8080'); // 테스트 대상 URL
  check(res, { 'status is 200': (r) => r.status === 200 }); // 응답 상태 확인
  sleep(1); // 1초 대기
}