// src/pages/SignUp.jsx
import React, { useState } from "react";
import { customFetch } from "../utils/api";

function SignUp({ onNavigate }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [isNicknameChecked, setIsNicknameChecked] = useState(false);

  const [nicknameMessage, setNicknameMessage] = useState("");
  const [messageType, setMessageType] = useState("");

  // 💡 중복되는 안내 메시지 설정을 하나로 묶는 헬퍼 함수
  const updateMessage = (message, type = "warning", checkedStatus = false) => {
    setNicknameMessage(message);
    setMessageType(type);
    setIsNicknameChecked(checkedStatus);
  };

  // 1. 닉네임 중복 체크
  const checkNicknameDuplicate = async () => {
    if (!nickname.trim())
      return updateMessage("닉네임을 입력해주세요.", "warning");

    try {
      const response = await customFetch("/auth/check/nickname", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ nickname }),
      });

      if (!response.ok)
        return updateMessage(
          "이미 사용 중이거나 사용할 수 없는 닉네임입니다.",
          "warning",
        );

      const isAvailable = await response.json();
      if (isAvailable) {
        updateMessage("사용 가능한 닉네임입니다! 👍", "success", true);
      } else {
        updateMessage("이미 사용 중인 닉네임입니다. ❌", "warning", false);
      }
    } catch (error) {
      console.error(error);
      updateMessage("중복 체크 통신에 실패했습니다.", "error");
    }
  };

  // 2. 회원가입 연동
  const handleSignUp = async (e) => {
    e.preventDefault();

    if (!isNicknameChecked)
      return updateMessage("닉네임 중복 체크를 먼저 진행해주세요.", "warning");

    try {
      const response = await customFetch("/auth/signup", {
        method: "POST",
        body: JSON.stringify({ username, password, nickname }),
      });

      if (response.ok) {
        onNavigate("login");
      } else {
        alert("회원가입 실패. 입력 정보를 다시 확인해주세요.");
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
          🌱 수호대 합류
        </h2>
        <p className="text-slate-400 text-xs text-center mb-8 font-medium">
          Food-Guard의 새로운 이웃이 되어주세요
        </p>

        <form onSubmit={handleSignUp} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-500 mb-1">
              아이디
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-full bg-slate-50 border border-slate-200 rounded-xl py-2.5 px-4 text-sm focus:outline-none focus:border-emerald-500 font-medium"
              placeholder="사용할 아이디 입력"
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

          <div>
            <label className="block text-xs font-bold text-slate-500 mb-1">
              닉네임
            </label>
            <div className="flex space-x-2">
              <input
                type="text"
                value={nickname}
                onChange={(e) => {
                  setNickname(e.target.value);
                  updateMessage("", "", false); // 입력 변경 시 메시지 싹 비우기
                }}
                className="flex-1 bg-slate-50 border border-slate-200 rounded-xl py-2.5 px-4 text-sm focus:outline-none focus:border-emerald-500 font-medium"
                placeholder="동네에서 사용할 닉네임"
                required
              />
              <button
                type="button"
                onClick={checkNicknameDuplicate}
                className="bg-slate-800 hover:bg-slate-900 text-white text-xs px-4 rounded-xl font-bold shrink-0 transition-colors cursor-pointer"
              >
                중복 체크
              </button>
            </div>

            {nicknameMessage && (
              <p
                className={`text-[11px] mt-1.5 font-semibold ${
                  messageType === "success"
                    ? "text-emerald-600"
                    : messageType === "warning"
                      ? "text-amber-500"
                      : "text-red-500"
                }`}
              >
                {nicknameMessage}
              </p>
            )}
          </div>

          <button
            type="submit"
            className={`w-full font-bold py-3 rounded-xl shadow-md transition-all text-sm mt-4 cursor-pointer ${
              isNicknameChecked
                ? "bg-emerald-500 hover:bg-emerald-600 text-white shadow-emerald-100"
                : "bg-slate-200 text-slate-400 shadow-none cursor-not-allowed"
            }`}
          >
            가입 완료하기
          </button>
        </form>

        <div className="mt-8 pt-4 border-t border-slate-100 text-center text-xs text-slate-500 font-medium">
          이미 계정이 있으신가요?{" "}
          <span
            onClick={() => onNavigate("login")}
            className="text-emerald-600 font-bold hover:underline cursor-pointer"
          >
            로그인 하기
          </span>
        </div>
      </div>
    </div>
  );
}

export default SignUp;
