import { useState } from "react";
import { api } from "../utils/api";

const initialFoodForm = {
  name: "",
  type: "",
  description: "",
  expiryAt: "",
  groupId: "",
};

export function useFood(loadPrivateData, showError) {
  const [foodForm, setFoodForm] = useState(initialFoodForm);
  // 💡 현재 수정 중인 식품의 ID 관리 (null이면 등록 모드, ID가 있으면 수정 모드)
  const [editingFoodId, setEditingFoodId] = useState(null);

  // 폼 필드 변경 헬퍼 함수
  const updateFoodField = (field) => (event) => {
    setFoodForm((prev) => ({ ...prev, [field]: event.target.value }));
  };

  // 💡 등록과 수정을 모두 처리하는 통합 제출 핸들러
  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const payload = {
        ...foodForm,
        groupId: foodForm.groupId ? Number(foodForm.groupId) : null,
      };

      if (editingFoodId) {
        // 1) 수정 모드일 때: 수정 API 호출
        await api.editFood(editingFoodId, payload);
        setEditingFoodId(null); // 수정 완료 후 모드 리셋
      } else {
        // 2) 등록 모드일 때: 기존 등록 API 호출
        await api.createFood(payload);
      }

      setFoodForm(initialFoodForm); // 폼 초기화
      await loadPrivateData(); // 목록 새로고침
    } catch (error) {
      showError(error);
    }
  };

  // 💡 [수정] 버튼 클릭 시 호출되어 기존 데이터를 입력창에 채워주는 함수
  const startEdit = (food) => {
    setEditingFoodId(food.id);
    setFoodForm({
      name: food.name,
      type: food.type,
      description: food.description || "",
      expiryAt: food.expiryAt,
      groupId: food.groupId || "",
    });
  };

  // 수정 취소 기능
  const cancelEdit = () => {
    setEditingFoodId(null);
    setFoodForm(initialFoodForm);
  };

  return {
    foodForm,
    setFoodForm,
    updateFoodField, // 추가됨
    editingFoodId, // 추가됨
    handleSubmit, // 추가됨 (기존 createFood 대체)
    startEdit, // 추가됨
    cancelEdit, // 추가됨
  };
}
