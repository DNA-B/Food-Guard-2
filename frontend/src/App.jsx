import React, { useState, useEffect } from "react";
import Header from "./components/Header";
import Home from "./pages/Home";
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentPage, setCurrentPage] = useState("home");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) setIsLoggedIn(true);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    setCurrentPage("home");
    alert("로그아웃 되었습니다.");
  };

  // 현재 조건(state)에 맞는 페이지를 화면에 꽂아주는 내장 렌더러 함수
  const renderPage = () => {
    switch (currentPage) {
      case "login":
        return (
          <Login onNavigate={setCurrentPage} setIsLoggedIn={setIsLoggedIn} />
        );
      case "signup":
        return <SignUp onNavigate={setCurrentPage} />;
      case "home":
      default:
        return <Home />;
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 flex flex-col font-sans">
      {/* 1. 글로벌 헤더에 네비게이션 기능 주입 */}
      <Header
        isLoggedIn={isLoggedIn}
        onLogout={handleLogout}
        onOpenAuth={() => setCurrentPage("login")} // 로그인 버튼 누르면 로그인 페이지로
      />

      {/* 2. 동적 페이지 영역 */}
      {renderPage()}
    </div>
  );
}

export default App;
