import React from "react";

export function ChatPage({
  chatRooms,
  selectedChatRoomId,
  chatInput,
  setChatInput,
  openChatRoom,
  closeChatRoom,
  sendChatMessage,
  messages,
  me,
  formatDate,
}) {
  return (
    <section className="two-column">
      <section className="panel list-panel">
        <div className="section-heading">
          <p>조회 결과</p>
          <h2>채팅방 목록</h2>
        </div>
        {chatRooms.length === 0 ? (
          <p className="empty-state">진행 중인 채팅이 없습니다.</p>
        ) : (
          <div className="list-stack">
            {chatRooms.map((room) => (
              <article
                className={`item-card clickable ${selectedChatRoomId === room.id ? "active" : ""}`}
                key={room.id}
                onClick={() => openChatRoom(room.id)}
              >
                <div className="room-info">
                  <div className="room-title">
                    <h3>{room.opponentNickname}님과의 대화</h3>
                    <span
                      className={`status-dot ${room.status.toLowerCase()}`}
                    />
                  </div>
                  <p className="last-message">
                    {room.lastMessage || "대화 기록 없음"}
                  </p>
                  <small>{formatDate(room.lastMessageAt)}</small>
                </div>
                <button
                  className="danger-button compact"
                  type="button"
                  onClick={(e) => {
                    e.stopPropagation();
                    void closeChatRoom(room.id);
                  }}
                >
                  종료
                </button>
              </article>
            ))}
          </div>
        )}
      </section>

      <section className="panel chat-window">
        <div className="section-heading">
          <p>메시지</p>
          <h2>채팅창</h2>
        </div>
        {selectedChatRoomId ? (
          <div
            className="chat-box"
            style={{ display: "flex", flexDirection: "column", height: "100%" }}
          >
            <div
              className="message-history"
              style={{ flex: 1, overflowY: "auto" }}
            >
              {messages.length === 0 ? (
                <p className="empty-state">메시지가 없습니다.</p>
              ) : (
                messages.map((msg) => (
                  <div
                    key={msg.id}
                    className={`message-balloon ${msg.type.toLowerCase()} ${msg.senderNickname === me?.nickname ? "mine" : "yours"}`}
                  >
                    <small className="sender">{msg.senderNickname}</small>
                    <p className="content">{msg.content}</p>
                    <small className="time">{formatDate(msg.createdAt)}</small>
                  </div>
                ))
              )}
            </div>

            {/* 💡 [교정] 실제 타이핑 및 메시지 송신 폼 배치 */}
            <form
              className="input-group"
              onSubmit={sendChatMessage}
              style={{ marginTop: "12px" }}
            >
              <input
                placeholder="메시지를 입력하세요..."
                value={chatInput}
                onChange={(e) => setChatInput(e.target.value)}
                required
              />
              <button type="submit" className="primary-button">
                전송
              </button>
            </form>
          </div>
        ) : (
          <p className="empty-state">왼쪽 목록에서 채팅방을 선택해 주세요.</p>
        )}
      </section>
    </section>
  );
}
