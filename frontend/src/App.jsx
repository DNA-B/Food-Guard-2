import React, { useState, useEffect } from "react";
import Header from "./components/Header";
import Home from "./pages/Home";
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";
import Food from "./pages/Food"; // 💡 1. Food 컴포넌트 임포트 추가!

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

      // 💡 2. 'food' 주소가 들어왔을 때 Food 컴포넌트를 그리도록 분기 추가!
      case "food":
        return <Food />;

      case "home":
      default:
        return <Home />;
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800 flex flex-col font-sans">
      {/* 💡 3. 헤더가 currentPage를 알고, 이를 변경(setCurrentPage)할 수 있도록 프롭스 주입! */}
      <Header
        isLoggedIn={isLoggedIn}
        onLogout={handleLogout}
        onOpenAuth={() => setCurrentPage("login")}
        currentTab={currentPage} // 👈 추가
        onNavigate={setCurrentPage} // 👈 추가
      />

      {/* 2. 동적 페이지 영역 */}
      {renderPage()}
    </div>
  );
}

export default App;
