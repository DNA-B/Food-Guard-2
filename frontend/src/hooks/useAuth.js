import { useState } from "react";
import { api } from "../utils/api";

const initialAuthForm = { username: "", password: "", nickname: "" };

export function useAuth(
  setIsLoggedIn,
  setActiveTab,
  refreshAll,
  handleLogout,
  showError,
  setNotice,
  setLoading,
) {
  const [authMode, setAuthMode] = useState("login");
  const [authForm, setAuthForm] = useState(initialAuthForm);

  const updateAuthField = (field) => (event) => {
    setAuthForm((prev) => ({ ...prev, [field]: event.target.value }));
  };

  // 로그인 핸들러
  const handleLogin = async (event) => {
    event.preventDefault();
    setLoading(true);
    try {
      const result = await api.login({
        username: authForm.username,
        password: authForm.password,
      });
      localStorage.setItem("accessToken", result.accessToken);
      setIsLoggedIn(true);
      setActiveTab("dashboard");
      setAuthForm(initialAuthForm);
      setNotice("");
      await refreshAll();
    } catch (error) {
      showError(error);
    } finally {
      setLoading(false);
    }
  };

  // 회원가입 핸들러
  const handleSignup = async (event) => {
    event.preventDefault();
    setLoading(true);
    try {
      await api.signup(authForm);
      setAuthMode("login");
      setNotice("가입이 완료되었습니다. 로그인해 주세요.");
    } catch (error) {
      showError(error);
    } finally {
      setLoading(false);
    }
  };

  // 닉네임 중복 체크
  const handleNicknameCheck = async () => {
    if (!authForm.nickname.trim()) {
      setNotice("닉네임을 입력해 주세요.");
      return;
    }
    try {
      const available = await api.checkNickname({
        nickname: authForm.nickname,
      });
      if (available) {
        setNotice("사용 가능한 닉네임입니다.");
      } else {
        setNotice("이미 사용 중인 닉네임입니다.");
      }
    } catch (error) {
      showError(error);
    }
  };

  // 회원 탈퇴 핸들러
  const deleteMe = async () => {
    if (!window.confirm("정말 탈퇴하시겠습니까?")) return;
    try {
      await api.deleteMe();
      handleLogout();
    } catch (error) {
      showError(error);
    }
  };

  return {
    authMode,
    setAuthMode,
    authForm,
    updateAuthField,
    handleLogin,
    handleSignup,
    handleNicknameCheck,
    deleteMe,
  };
}
