const tabs = [
  { id: "dashboard", label: "대시보드" },
  { id: "foods", label: "식품" },
  { id: "donations", label: "나눔" },
  { id: "posts", label: "커뮤니티" },
  { id: "groups", label: "그룹" },
  { id: "chats", label: "채팅" },
];

function Header({ activeTab, isLoggedIn, onNavigate, onLogout }) {
  return (
    <header className="app-header">
      <button
        className="brand"
        type="button"
        onClick={() => onNavigate("dashboard")}
      >
        <span className="brand-mark">FG</span>
        <span>
          <strong>Food Guard</strong>
          <small>API Console</small>
        </span>
      </button>

      <nav className="nav-tabs" aria-label="주요 메뉴">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            type="button"
            className={activeTab === tab.id ? "active" : ""}
            onClick={() => onNavigate(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </nav>

      <div className="header-actions">
        {isLoggedIn ? (
          <>
            <button
              type="button"
              className="ghost-button"
              onClick={() => onNavigate("profile")}
            >
              내 정보
            </button>
            <button type="button" className="danger-button" onClick={onLogout}>
              로그아웃
            </button>
          </>
        ) : (
          <button
            type="button"
            className="primary-button"
            onClick={() => onNavigate("auth")}
          >
            로그인
          </button>
        )}
      </div>
    </header>
  );
}

export default Header;
