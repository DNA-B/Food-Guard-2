// src/utils/api.js
const BASE_URL = "http://localhost:8080/api/v1";

export const customFetch = async (url, options = {}) => {
  // 공통 헤더 설정 (JSON)
  const headers = {
    "Content-Type": "application/json",
    ...options.headers,
  };

  // 로컬 스토리지에 토큰이 있다면 자동으로 Authorization 헤더에 Bearer 토큰 주입
  const token = localStorage.getItem("token");
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  // 공통 URL 접두사 붙여서 fetch 실행
  const response = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers,
  });

  return response;
};
