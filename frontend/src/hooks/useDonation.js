import { useState } from "react";
import { api } from "../utils/api";

const initialDonationForm = { title: "", content: "", foodId: "" };

export function useDonation(loadPublicData, loadPrivateData, showError) {
  const [donationForm, setDonationForm] = useState(initialDonationForm);

  // 나눔 게시글 등록
  const createDonation = async (event) => {
    event.preventDefault();
    try {
      await api.createDonation({
        ...donationForm,
        foodId: Number(donationForm.foodId),
      });
      setDonationForm(initialDonationForm);
      await loadPublicData(); // 등록 후 전체 나눔 목록 새로고침
    } catch (error) {
      showError(error);
    }
  };

  // 나눔 게시글 삭제
  const deleteDonation = async (id) => {
    try {
      await api.deleteDonation(id);
      await loadPublicData(); // 삭제 후 전체 나눔 목록 새로고침
    } catch (error) {
      showError(error);
    }
  };

  // 나눔 신청 (채팅방 시작)
  const startChat = async (donationId) => {
    try {
      await api.startChat(donationId);
      await loadPrivateData(); // 채팅방 생성 후 내 데이터 새로고침
    } catch (error) {
      showError(error);
    }
  };

  return {
    donationForm,
    setDonationForm,
    createDonation,
    deleteDonation,
    startChat,
  };
}
