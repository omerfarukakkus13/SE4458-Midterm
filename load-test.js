import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 20 },  // Normal yük
    { duration: '30s', target: 50 },  // Peak (Tepe) yük
    { duration: '30s', target: 100 }, // Stress yükü
  ],
  thresholds: {
   
    http_req_duration: ['p(95)<500'], 
  },
};

export default function () {
  const url = 'http://13.53.131.21:8080/api/listings';

  const params = {
    headers: {
      'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0hPU1QiXSwic3ViIjoidGVzdHVzZXIzIiwiaWF0IjoxNzc0ODk0MjEyLCJleHAiOjE3NzQ5ODA2MTJ9.pJV1W4ChnKlbqos5600Iy6ilOS9UpUz51fE5AkDwHCA', 
      'Content-Type': 'application/json',
    },
  };

  const res = http.get(url, params);

  
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
  sleep(1);
}