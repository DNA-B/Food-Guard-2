import React, { useState } from "react";
import { customFetch } from "../utils/api";

function Login({ onNavigate, setIsLoggedIn }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await customFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const data = await response.json();
        // 💡 포스트맨 결과 화면의 "accessToken" key 매핑 완료
        localStorage.setItem("token", data.accessToken);
        setIsLoggedIn(true);
        onNavigate("home");
        alert("로그인 성공! 🛡️");
      } else {
        alert("아이디 또는 비밀번호를 확인해주세요.");
      }
    } catch (error) {
      console.error(error);
      alert("서버 통신 에러 발생");
    }
  };

  return (
    <div className="flex-1 flex items-center justify-center p-6 bg-slate-50">
      <div className="bg-white rounded-2xl shadow-xl border border-slate-200 w-full max-w-md p-8">
        <h2 className="text-3xl font-black text-slate-900 mb-2 text-center tracking-tight">
          🛡️ Food-Guard
        </h2>
        <p className="text-slate-400 text-xs text-center mb-8 font-medium">
          우리 동네 식재료 수호대 로그인
        </p>

        <form onSubmit={handleLogin} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-500 mb-1">
              아이디
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl py-2.5 px-4 text-sm focus:outline-none focus:border-emerald-500 font-medium"
              placeholder="아이디 입력"
              required
            />
          </div>
          <div>
            <label className="block text-xs font-bold text-slate-500 mb-1">
              비밀번호
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl py-2.5 px-4 text-sm focus:outline-none focus:border-emerald-500 font-medium"
              placeholder="비밀번호 입력"
              required
            />
          </div>

          <button
            type="submit"
            className="w-full bg-emerald-500 hover:bg-emerald-600 text-white font-bold py-3 rounded-xl shadow-md shadow-emerald-100 transition-all text-sm mt-2 cursor-pointer"
          >
            로그인하기
          </button>
        </form>

        <div className="mt-8 pt-4 border-t border-slate-100 text-center text-xs text-slate-500 font-medium">
          아직 회원이 아니신가요?{" "}
          <span
            onClick={() => onNavigate("signup")}
            className="text-emerald-600 font-bold hover:underline cursor-pointer"
          >
            회원가입 하기
          </span>
        </div>
      </div>
    </div>
  );
}

export default Login;
