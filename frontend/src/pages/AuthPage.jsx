import React from "react";

export function AuthPage({
  authMode,
  setAuthMode,
  authForm,
  updateAuthField,
  handleLogin,
  handleSignup,
  handleNicknameCheck,
}) {
  return (
    <section className="two-column">
      <div className="panel">
        <div className="tabs">
          <button
            className={authMode === "login" ? "active" : ""}
            onClick={() => setAuthMode("login")}
          >
            로그인
          </button>
          <button
            className={authMode === "signup" ? "active" : ""}
            onClick={() => setAuthMode("signup")}
          >
            회원가입
          </button>
        </div>

        {authMode === "login" ? (
          <form className="form-stack" onSubmit={handleLogin}>
            <label>
              아이디
              <input
                value={authForm.username}
                onChange={updateAuthField("username")}
                required
              />
            </label>
            <label>
              비밀번호
              <input
                type="password"
                value={authForm.password}
                onChange={updateAuthField("password")}
                required
              />
            </label>
            <button type="submit" className="primary-button">
              로그인
            </button>
          </form>
        ) : (
          <form className="form-stack" onSubmit={handleSignup}>
            <label>
              아이디
              <input
                value={authForm.username}
                onChange={updateAuthField("username")}
                required
              />
            </label>
            <label>
              비밀번호
              <input
                type="password"
                value={authForm.password}
                onChange={updateAuthField("password")}
                required
              />
            </label>
            <label>
              닉네임
              <div className="input-group">
                <input
                  value={authForm.nickname}
                  onChange={updateAuthField("nickname")}
                  required
                />
                <button
                  type="button"
                  className="secondary-button"
                  onClick={handleNicknameCheck}
                >
                  중복 확인
                </button>
              </div>
            </label>
            <button type="submit" className="primary-button">
              회원가입
            </button>
          </form>
        )}
      </div>
    </section>
  );
}
