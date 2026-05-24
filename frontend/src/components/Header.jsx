import React from "react";

function Header({ isLoggedIn, onLogout, onOpenAuth }) {
  return (
    <nav className="bg-white border-b border-slate-200 sticky top-0 z-50 shadow-xs">
      <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
        {/* 로고 */}
        <div className="flex items-center space-x-2 cursor-pointer">
          <span className="text-2xl">🛡️</span>
          <span className="font-black text-xl bg-gradient-to-r from-emerald-600 to-teal-600 bg-clip-text text-transparent tracking-tight">
            Food-Guard
          </span>
        </div>

        {/* 메뉴 내비게이션 */}
        <div className="flex items-center space-x-8 text-sm font-semibold text-slate-600">
          <button className="hover:text-emerald-600 transition-colors cursor-pointer">
            음식
          </button>
          <button className="hover:text-emerald-600 transition-colors cursor-pointer">
            냉장고
          </button>
          <button className="hover:text-emerald-600 transition-colors cursor-pointer">
            커뮤니티
          </button>
          <button className="hover:text-emerald-600 transition-colors cursor-pointer">
            마이페이지
          </button>

          <div className="w-px h-4 bg-slate-200" />

          {isLoggedIn ? (
            <button
              onClick={onLogout}
              className="text-slate-400 hover:text-rose-500 transition-colors text-xs font-medium cursor-pointer"
            >
              로그아웃
            </button>
          ) : (
            <button
              onClick={onOpenAuth}
              className="text-emerald-600 hover:text-emerald-700 transition-colors text-sm font-bold cursor-pointer"
            >
              로그인
            </button>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Header;
