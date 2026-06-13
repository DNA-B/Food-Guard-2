import { useState } from "react";
import { api } from "../utils/api";

const initialGroupForm = { name: "", description: "" };

export function useGroup(loadPrivateData, showError) {
  const [groupForm, setGroupForm] = useState(initialGroupForm);

  // 그룹 생성
  const createGroup = async (event) => {
    event.preventDefault();
    try {
      await api.createGroup(groupForm);
      setGroupForm(initialGroupForm);
      await loadPrivateData(); // 생성 후 내 데이터 새로고침
    } catch (error) {
      showError(error);
    }
  };

  // 그룹 삭제
  const deleteGroup = async (id) => {
    try {
      await api.deleteGroup(id);
      await loadPrivateData(); // 삭제 후 내 데이터 새로고침
    } catch (error) {
      showError(error);
    }
  };

  // 그룹 탈퇴
  const exitGroup = async (id) => {
    try {
      await api.exitGroup(id);
      await loadPrivateData(); // 탈퇴 후 내 데이터 새로고침
    } catch (error) {
      showError(error);
    }
  };

  return {
    groupForm,
    setGroupForm,
    createGroup,
    deleteGroup,
    exitGroup,
  };
}
