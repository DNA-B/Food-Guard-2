import { useEffect, useMemo, useState } from "react";
import Header from "./components/Header";
import { api, ApiError } from "./utils/api";

// hooks
import { useFood } from "./hooks/useFood";
import { useDonation } from "./hooks/useDonation";
import { usePost } from "./hooks/usePost";
import { useGroup } from "./hooks/useGroup";
import { useChat } from "./hooks/useChat";
import { useAuth } from "./hooks/useAuth";

// pages
import { FoodPage } from "./pages/FoodPage";
import { DonationPage } from "./pages/DonationPage";
import { PostPage } from "./pages/PostPage";
import { GroupPage } from "./pages/GroupPage";
import { ChatPage } from "./pages/ChatPage";
import { AuthPage } from "./pages/AuthPage";
import { ProfilePage } from "./pages/ProfilePage";

import "./App.css";

// 헬퍼 함수들
const formatDate = (value) => {
  if (!value) return "-";
  return new Intl.DateTimeFormat("ko-KR", { dateStyle: "medium" }).format(
    new Date(value),
  );
};

const getExpiryState = (expiryAt) => {
  if (!expiryAt) return { label: "-", tone: "muted" };
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const target = new Date(expiryAt);
  target.setHours(0, 0, 0, 0);
  const diff = Math.ceil((target - today) / 86400000);

  if (diff < 0) return { label: `${Math.abs(diff)}일 지남`, tone: "danger" };
  if (diff === 0) return { label: "오늘 만료", tone: "danger" };
  if (diff <= 3) return { label: `D-${diff}`, tone: "warning" };
  return { label: `D-${diff}`, tone: "success" };
};

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(
    Boolean(localStorage.getItem("accessToken")),
  );
  const [activeTab, setActiveTab] = useState(isLoggedIn ? "dashboard" : "auth");

  const [notice, setNotice] = useState("");
  const [loading, setLoading] = useState(false);

  // 통합 데이터 상태 관리
  const [data, setData] = useState({
    me: null,
    foods: [],
    donations: [],
    posts: [],
    groups: [],
    chatRooms: [],
    messages: [],
  });

  // 대시보드 통계 정보
  const summary = useMemo(() => {
    return [
      { label: "보관 식품", value: data.foods.length },
      { label: "나눔 게시글", value: data.donations.length },
      { label: "커뮤니티 글", value: data.posts.length },
      { label: "내 그룹", value: data.groups.length },
      { label: "채팅방", value: data.chatRooms.length },
    ];
  }, [data]);

  // 공통 에러 핸들러
  const showError = (error) => {
    if (error instanceof ApiError) {
      setNotice(error.message);
      if (error.status === 401 || error.status === 403) {
        setIsLoggedIn(false);
      }
      return;
    }
    setNotice(
      "서버와 통신하지 못했습니다. 백엔드가 실행 중인지 확인해 주세요.",
    );
  };

  // 데이터 새로고침 함수들
  const loadPublicData = async () => {
    setLoading(true);
    try {
      const [donations, posts] = await Promise.all([
        api.donations(),
        api.posts(),
      ]);
      setData((prev) => ({ ...prev, donations, posts }));
      setNotice("");
    } catch (error) {
      showError(error);
    } finally {
      setLoading(false);
    }
  };

  const loadPrivateData = async () => {
    if (!localStorage.getItem("accessToken")) return;
    setLoading(true);
    try {
      const [me, foods, groups, chatRooms] = await Promise.all([
        api.me(),
        api.foods(),
        api.groups(),
        api.chatRooms(),
      ]);
      setData((prev) => ({ ...prev, me, foods, groups, chatRooms }));
      setNotice("");
    } catch (error) {
      showError(error);
    } finally {
      setLoading(false);
    }
  };

  const refreshAll = async (forceRefresh = false) => {
    await loadPublicData();
    if (isLoggedIn) {
      await loadPrivateData();
    }
  };

  useEffect(() => {
    void refreshAll();
  }, []);

  useEffect(() => {
    if (!isLoggedIn && activeTab === "dashboard") {
      setActiveTab("auth");
    }
  }, [activeTab, isLoggedIn]);

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    setIsLoggedIn(false);
    setData((prev) => ({
      ...prev,
      me: null,
      foods: [],
      groups: [],
      chatRooms: [],
      messages: [],
    }));
    setActiveTab("auth");
  };

  // -------------------------------------------------------------
  // 💡 커스텀 훅들의 주입 & 연결
  // -------------------------------------------------------------
  const {
    authMode,
    setAuthMode,
    authForm,
    updateAuthField,
    handleLogin,
    handleSignup,
    handleNicknameCheck,
    deleteMe,
  } = useAuth(
    setIsLoggedIn,
    setActiveTab,
    () => refreshAll(true),
    handleLogout,
    showError,
    setNotice,
    setLoading,
  );

  const {
    foodForm,
    setFoodForm,
    updateFoodField,
    editingFoodId,
    handleSubmit: handleFoodSubmit,
    startEdit,
    cancelEdit,
  } = useFood(loadPrivateData, showError);

  const {
    donationForm,
    setDonationForm,
    createDonation,
    deleteDonation,
    startChat,
  } = useDonation(loadPublicData, loadPrivateData, showError);

  const {
    postForm,
    setPostForm,
    selectedPostId,
    comments,
    commentText,
    setCommentText,
    activeParentId,
    setActiveParentId,
    createPost,
    deletePost,
    openComments,
    createComment,
  } = usePost(loadPublicData, showError);

  const { groupForm, setGroupForm, createGroup, deleteGroup, exitGroup } =
    useGroup(loadPrivateData, showError);

  const {
    selectedChatRoomId,
    setSelectedChatRoomId,
    chatInput,
    setChatInput,
    openChatRoom,
    closeChatRoom,
    sendChatMessage,
  } = useChat(setData, loadPrivateData, showError);

  // 폼 입력 필드 변경 핸들러 매핑
  const updateDonationField = (field) => (event) =>
    setDonationForm((prev) => ({ ...prev, [field]: event.target.value }));
  const updatePostField = (field) => (event) =>
    setPostForm((prev) => ({ ...prev, [field]: event.target.value }));
  const updateGroupField = (field) => (event) =>
    setGroupForm((prev) => ({ ...prev, [field]: event.target.value }));

  const handleNavigate = (tabId) => {
    if (!isLoggedIn && tabId === "dashboard") {
      setActiveTab("auth");
      return;
    }
    setActiveTab(tabId);
  };

  return (
    <div className="app-shell">
      <Header
        activeTab={activeTab}
        isLoggedIn={isLoggedIn}
        onNavigate={handleNavigate}
        onLogout={handleLogout}
      />

      <main className="app-main">
        {notice && <div className="notice">{notice}</div>}
        {loading && <div className="loading-bar">불러오는 중...</div>}
        {/* 💡 깔끔하게 컴포넌트 호출로 교체된 페이지 라우팅 파트 */}
        {activeTab === "auth" && (
          <AuthPage
            authMode={authMode}
            setAuthMode={setAuthMode}
            authForm={authForm}
            updateAuthField={updateAuthField}
            handleLogin={handleLogin}
            handleSignup={handleSignup}
            handleNicknameCheck={handleNicknameCheck}
          />
        )}
        {activeTab === "dashboard" && (
          <Dashboard summary={summary} isLoggedIn={isLoggedIn} />
        )}
        {activeTab === "foods" && (
          <FoodPage
            foodForm={foodForm}
            updateFoodField={updateFoodField}
            handleSubmit={handleFoodSubmit}
            editingFoodId={editingFoodId}
            startEdit={startEdit}
            cancelEdit={cancelEdit}
            groups={data.groups}
            foods={data.foods}
            getExpiryState={getExpiryState}
            onDeleteFood={(id) =>
              api.deleteFood(id).then(loadPrivateData).catch(showError)
            }
          />
        )}
        {activeTab === "donations" && (
          <DonationPage
            donationForm={donationForm}
            updateDonationField={updateDonationField}
            createDonation={createDonation}
            foods={data.foods}
            donations={data.donations}
            startChat={startChat}
            deleteDonation={deleteDonation}
            formatDate={formatDate}
          />
        )}
        {activeTab === "posts" && (
          <PostPage
            postForm={postForm}
            updatePostField={updatePostField}
            createPost={createPost}
            posts={data.posts}
            deletePost={deletePost}
            selectedPostId={selectedPostId}
            openComments={openComments}
            comments={comments}
            commentText={commentText}
            setCommentText={setCommentText}
            activeParentId={activeParentId}
            setActiveParentId={setActiveParentId}
            createComment={createComment}
            formatDate={formatDate}
            me={data.me}
          />
        )}
        {activeTab === "chats" && (
          <ChatPage
            chatRooms={data.chatRooms}
            selectedChatRoomId={selectedChatRoomId}
            chatInput={chatInput}
            setChatInput={setChatInput}
            openChatRoom={openChatRoom}
            closeChatRoom={closeChatRoom}
            sendChatMessage={sendChatMessage}
            messages={data.messages}
            me={data.me}
            formatDate={formatDate}
          />
        )}
        {activeTab === "groups" && (
          <GroupPage
            groupForm={groupForm}
            updateGroupField={updateGroupField}
            createGroup={createGroup}
            groups={data.groups}
            exitGroup={exitGroup}
            deleteGroup={deleteGroup}
          />
        )}
        {activeTab === "profile" && (
          <ProfilePage me={data.me} onDeleteMe={deleteMe} />
        )}
      </main>
    </div>
  );
}

function Dashboard({ summary, isLoggedIn }) {
  return (
    <section className="dashboard-grid">
      <div className="panel welcome-panel">
        <div className="section-heading">
          <p>Food Guard</p>
          <h1>식재료를 안전하게 보호하세요!</h1>
        </div>
        <p>
          보관 중인 식품의 소비기한을 관리하고, 남는 식재료를 이웃과 나눠보세요.
        </p>
        {!isLoggedIn && (
          <p className="hint">로그인 후 모든 기능을 이용해 보세요.</p>
        )}
      </div>
      <div className="metrics-layout">
        {summary.map((metric, index) => (
          <article className="panel metric-card" key={index}>
            <p className="metric-label">{metric.label}</p>
            <p className="metric-value">{isLoggedIn ? metric.value : "-"}</p>
          </article>
        ))}
      </div>
    </section>
  );
}

export default App;
