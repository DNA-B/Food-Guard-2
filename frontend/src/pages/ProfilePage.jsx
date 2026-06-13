import React from "react";

export function ProfilePage({ me, onDeleteMe }) {
  return (
    <section className="panel profile-panel">
      <div className="section-heading">
        <p>사용자</p>
        <h1>내 정보</h1>
      </div>
      {me ? (
        <dl className="profile-list">
          <dt>ID</dt>
          <dd>{me.id}</dd>
          <dt>아이디</dt>
          <dd>{me.username}</dd>
          <dt>닉네임</dt>
          <dd>{me.nickname}</dd>
        </dl>
      ) : (
        <p className="empty-state">로그인이 필요합니다.</p>
      )}
      <button type="button" className="danger-button" onClick={onDeleteMe}>
        회원 탈퇴
      </button>
    </section>
  );
}
