// src/pages/SignUp.jsx
import React, { useState } from "react";
import { customFetch } from "../utils/api";

function SignUp({ onNavigate }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [isNicknameChecked, setIsNicknameChecked] = useState(false);

  // 1. 닉네임 중복 체크 (백엔드 @RequestBody 설계에 맞춰 POST JSON으로 연동)
  const checkNicknameDuplicate = async () => {
    if (!nickname.trim()) {
      alert("닉네임을 입력해주세요.");
      return;
    }
    try {
      const response = await customFetch("/auth/check/nickname", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ nickname: nickname }),
      });

      if (response.ok) {
        const isAvailable = await response.json(); // 백엔드가 주는 true/false 파싱

        if (isAvailable) {
          alert("사용 가능한 닉네임입니다! 👍");
          setIsNicknameChecked(true);
        } else {
          alert("이미 사용 중인 닉네임입니다. ❌");
          setIsNicknameChecked(false);
        }
      } else {
        alert("이미 사용 중이거나 사용할 수 없는 닉네임입니다.");
        setIsNicknameChecked(false);
      }
    } catch (error) {
      console.error(error);
      alert("중복 체크 통신 실패");
    }
  };

  // 2. 회원가입 연동
  const handleSignUp = async (e) => {
    e.preventDefault();

    if (!isNicknameChecked) {
      alert("닉네임 중복 체크를 먼저 진행해주세요.");
      return;
    }

    try {
      const response = await customFetch("/auth/signup", {
        method: "POST",
        body: JSON.stringify({ username, password, nickname }),
      });

      if (response.ok) {
        alert("회원가입 완료! 로그인 페이지로 이동합니다. 🎉");
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
                  setIsNicknameChecked(false);
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
            {isNicknameChecked && (
              <p className="text-[11px] text-emerald-600 mt-1.5 font-semibold">
                ✓ 닉네임 검증 완료
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
