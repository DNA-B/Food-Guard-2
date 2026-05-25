import React, { useState } from "react";
import { customFetch } from "../utils/api";

function Login({ onNavigate, setIsLoggedIn }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  // 💡 로그인 실패 메시지와 스타일 타입을 관리하는 상태 추가
  const [loginMessage, setLoginMessage] = useState("");
  const [messageType, setMessageType] = useState(""); // "warning" 또는 "error"

  // 💡 메시지를 한 번에 업데이트하고 정리하기 위한 핸러
  const updateMessage = (message = "", type = "") => {
    setLoginMessage(message);
    setMessageType(type);
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await customFetch("/auth/login", {
        method: "POST",
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const data = await response.json();
        localStorage.setItem("token", data.accessToken);
        setIsLoggedIn(true);
        onNavigate("home");
      } else {
        // 💡 alert 대신 버튼 위에 경고 메시지 표시
        updateMessage("아이디 또는 비밀번호를 확인해주세요.", "warning");
      }
    } catch (error) {
      console.error(error);
      updateMessage("서버 통신 에러가 발생했습니다.", "error");
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
              onChange={(e) => {
                setUsername(e.target.value);
                updateMessage(); // 💡 다시 타이핑하기 시작하면 에러 문구 초기화
              }}
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
              onChange={(e) => {
                setPassword(e.target.value);
                updateMessage(); // 💡 다시 타이핑하기 시작하면 에러 문구 초기화
              }}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl py-2.5 px-4 text-sm focus:outline-none focus:border-emerald-500 font-medium"
              placeholder="비밀번호 입력"
              required
            />
          </div>

          {/* 💡 버튼 바로 위에 노출되는 조건부 안내 메시지 레이어 */}
          {loginMessage && (
            <p
              className={`text-[11px] text-center font-semibold pt-1 ${
                messageType === "warning" ? "text-amber-500" : "text-red-500"
              }`}
            >
              {loginMessage}
            </p>
          )}

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
