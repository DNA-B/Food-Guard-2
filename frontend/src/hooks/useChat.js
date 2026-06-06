import { useState } from "react";
import { api } from "../utils/api";

export function useChat(setData, loadPrivateData, showError) {
  const [selectedChatRoomId, setSelectedChatRoomId] = useState(null);
  const [chatInput, setChatInput] = useState(""); // 메시지 입력 상태 추가

  const openChatRoom = async (chatRoomId) => {
    setSelectedChatRoomId(chatRoomId);
    try {
      const messages = await api.chatMessages(chatRoomId);
      setData((prev) => ({ ...prev, messages }));
    } catch (error) {
      showError(error);
    }
  };

  const closeChatRoom = async (id) => {
    try {
      await api.closeChatRoom(id);
      if (selectedChatRoomId === id) {
        setSelectedChatRoomId(null);
      }
      await loadPrivateData();
    } catch (error) {
      showError(error);
    }
  };

  // 💡 [교정] 백엔드 명세 기반 전송 기능 구현
  const sendChatMessage = async (event) => {
    event.preventDefault();
    if (!selectedChatRoomId || !chatInput.trim()) return;

    try {
      await api.sendChatMessage(selectedChatRoomId, chatInput);
      setChatInput("");
      // 전송 후 최신 메시지 내역 다시 불러오기
      const messages = await api.chatMessages(selectedChatRoomId);
      setData((prev) => ({ ...prev, messages }));
    } catch (error) {
      showError(error);
    }
  };

  return {
    selectedChatRoomId,
    setSelectedChatRoomId,
    chatInput,
    setChatInput,
    openChatRoom,
    closeChatRoom,
    sendChatMessage,
  };
}
