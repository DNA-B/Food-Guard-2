import React, { useState } from "react";

function App() {
  // 임시 채팅 데이터 (나중에 백엔드 API / 웹소켓으로 대체될 영역)
  const [messages, setMessages] = useState([
    {
      id: 1,
      sender: "당근이웃",
      content: "혹시 남은 식재료 나눔 아직 가능한가요?",
      time: "오후 4:55",
    },
    {
      id: 2,
      sender: "나(백엔드마스터)",
      content: "네! 양파랑 감자 아직 많이 남아있어요. 셰어 냉장고로 오실래요?",
      time: "오후 4:56",
    },
  ]);
  const [input, setInput] = useState("");

  // 메시지 전송 버튼 클릭 시 동작 (우선 화면에만 추가되게)
  const handleSend = () => {
    if (!input.trim()) return;
    setMessages([
      ...messages,
      {
        id: Date.now(),
        sender: "나(백엔드마스터)",
        content: input,
        time: "방금 전",
      },
    ]);
    setInput("");
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
      {/* 채팅방 메인 컨테이너 */}
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl border border-slate-200 flex flex-col h-[600px] overflow-hidden">
        {/* 1. 상단 헤더 */}
        <div className="bg-orange-600 text-white p-4 flex items-center justify-between shadow-sm">
          <div>
            <h2 className="font-bold text-lg">🥕 공유 냉장고 1호점 채팅방</h2>
            <p className="text-md text-orange-100">참여자: 당근이웃, 나</p>
          </div>
          <span className="bg-orange-700 text-md px-2.5 py-1 rounded-full font-medium">
            LIVE
          </span>
        </div>

        {/* 2. 채팅 메시지 피드 영역 */}
        <div className="flex-1 p-4 overflow-y-auto space-y-4 bg-slate-50">
          {messages.map((msg) => {
            const isMe = msg.sender.includes("나");
            return (
              <div
                key={msg.id}
                className={`flex flex-col ${isMe ? "items-end" : "items-start"}`}
              >
                {/* 닉네임 */}
                <span className="text-md text-slate-500 mb-1 px-1">
                  {msg.sender}
                </span>
                {/* 말풍선 세트 */}
                <div
                  className={`flex items-end space-x-1 max-w-[85%] ${isMe ? "flex-row-reverse space-x-reverse" : "flex-row"}`}
                >
                  <div
                    className={`p-3 rounded-2xl text-sm shadow-sm ${
                      isMe
                        ? "bg-orange-400 text-white rounded-tr-none"
                        : "bg-white text-slate-800 border border-slate-200 rounded-tl-none"
                    }`}
                  >
                    {msg.content}
                  </div>
                  {/* 시간 표기 */}
                  <span className="text-[10px] text-slate-400 whitespace-nowrap min-w-fit">
                    {msg.time}r
                  </span>
                </div>
              </div>
            );
          })}
        </div>

        {/* 3. 하단 입력 바 */}
        <div className="p-3 bg-white border-t border-slate-200 flex items-center space-x-2">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSend()}
            placeholder="메시지를 입력하세요..."
            className="flex-1 bg-slate-100 border-none rounded-xl py-2.5 px-4 text-sm focus:outline-none focus:ring-2 focus:ring-orange-400 transition-all text-slate-800"
          />
          <button
            onClick={handleSend}
            className="bg-orange-500 hover:bg-orange-600 text-white font-medium py-2.5 px-4 rounded-xl text-sm transition-all shadow-md shadow-orange-100 shrink-0"
          >
            전송
          </button>
        </div>
      </div>
    </div>
  );
}

export default App;
