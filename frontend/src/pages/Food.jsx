import React, { useState, useEffect } from "react";
import { customFetch } from "../utils/api";

function Food() {
  const [foods, setFoods] = useState([]);

  // 백엔드 FoodCreateRequest / FoodEditRequest 규격에 맞춘 폼 상태
  const [name, setName] = useState("");
  const [type, setType] = useState(""); // 예: 야채, 생선, 가공식품, 유제품 등
  const [description, setDescription] = useState(""); // 상세 설명 / 메모
  const [expiryAt, setExpiryAt] = useState(""); // 유통기한 (LocalDate 매핑)

  // 💡 [GET] 로그인한 유저의 전체 식재료 목록 로드
  const fetchFoods = async () => {
    try {
      // ⚠️ 현재 단건 조회(/api/v1/foods/{id})만 확인되므로, 전체 리스트 API 엔드포인트가 열리면 주소를 맞춰주세요.
      const response = await customFetch("/foods", { method: "GET" });
      if (response.ok) {
        const data = await response.json();
        setFoods(data); // 백엔드에서 온 List<FoodResponse> 그대로 박기
      }
    } catch (error) {
      console.error("식재료 목록 로드 실패:", error);
    }
  };

  useEffect(() => {
    fetchFoods();
  }, []);

  // 1. [POST] 식재료 등록 핸들러
  const handleAddFood = async (e) => {
    e.preventDefault();
    if (!name.trim() || !expiryAt) return;

    try {
      // FoodCreateRequest DTO 스펙 바인딩
      const response = await customFetch("/foods", {
        method: "POST",
        body: JSON.stringify({ name, type, description, expiryAt }),
      });

      if (response.ok) {
        // 등록 성공 시 입력 폼 리셋 후 목록 새로고침
        setName("");
        setType("");
        setDescription("");
        setExpiryAt("");
        fetchFoods();
      } else {
        alert("식재료 등록 실패");
      }
    } catch (error) {
      console.error(error);
      alert("서버 통신 에러");
    }
  };

  // 2. [DELETE] 식재료 삭제 핸들러
  const handleDeleteFood = async (id) => {
    if (!window.confirm("이 식재료를 수호대 명단에서 제외할까요?")) return;

    try {
      const response = await customFetch(`/foods/${id}`, {
        method: "DELETE",
      });

      if (response.ok) {
        fetchFoods(); // 삭제 완료 후 갱신
      } else {
        alert("삭제 실패");
      }
    } catch (error) {
      console.error(error);
    }
  };

  // 유통기한 디데이(D-Day) 계산 함수
  const getDDay = (dateStr) => {
    if (!dateStr) return "";
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const targetDate = new Date(dateStr);
    targetDate.setHours(0, 0, 0, 0);

    const diffTime = targetDate - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 0) return "D-Day";
    return diffDays > 0 ? `D-${diffDays}` : `만료 ${Math.abs(diffDays)}일 지남`;
  };

  return (
    <div className="flex-1 bg-slate-50 p-6 overflow-y-auto">
      <div className="max-w-4xl mx-auto space-y-6">
        {/* 헤더 섹션 */}
        <div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">
            🍎 냉장고 수호대
          </h1>
          <p className="text-slate-500 text-xs mt-1">
            우리 집 식재료의 유통기한을 안전하게 지키세요.
          </p>
        </div>

        {/* 1. 상단 요약 대시보드 */}
        <div className="bg-white p-4 rounded-2xl border border-slate-200 shadow-sm w-fit px-8 text-center">
          <span className="text-xs font-bold text-slate-400 block mb-1">
            보호 중인 총 식재료
          </span>
          <span className="text-2xl font-black text-slate-800">
            {foods.length}개
          </span>
        </div>

        {/* 2. 식재료 등록 폼 (백엔드 DTO 규격) */}
        <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
          <h3 className="text-sm font-bold text-slate-800 mb-4">
            새 식재료 호위하기
          </h3>
          <form
            onSubmit={handleAddFood}
            className="grid grid-cols-1 md:grid-cols-5 gap-3 items-end"
          >
            <div>
              <label className="block text-[11px] font-bold text-slate-400 mb-1">
                식재료명
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="예: 서울우유 1L"
                className="w-full bg-slate-50 border border-slate-200 rounded-xl py-2 px-3 text-xs focus:outline-none focus:border-emerald-500 font-medium"
                required
              />
            </div>
            <div>
              <label className="block text-[11px] font-bold text-slate-400 mb-1">
                분류(종류)
              </label>
              <input
                type="text"
                value={type}
                onChange={(e) => setType(e.target.value)}
                placeholder="예: 유제품, 야채"
                className="w-full bg-slate-50 border border-slate-200 rounded-xl py-2 px-3 text-xs focus:outline-none focus:border-emerald-500 font-medium"
              />
            </div>
            <div className="md:col-span-2 grid grid-cols-2 gap-2">
              <div>
                <label className="block text-[11px] font-bold text-slate-400 mb-1">
                  설명/메모
                </label>
                <input
                  type="text"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="예: 개봉 후 빨리 먹기"
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl py-2 px-3 text-xs focus:outline-none focus:border-emerald-500 font-medium"
                />
              </div>
              <div>
                <label className="block text-[11px] font-bold text-slate-400 mb-1">
                  유통기한
                </label>
                <input
                  type="date"
                  value={expiryAt}
                  onChange={(e) => setExpiryAt(e.target.value)}
                  className="w-full bg-slate-50 border border-slate-200 rounded-xl py-2 px-3 text-xs focus:outline-none focus:border-emerald-500 font-medium text-slate-700"
                  required
                />
              </div>
            </div>
            <button
              type="submit"
              className="bg-emerald-500 hover:bg-emerald-600 text-white font-bold text-xs py-2.5 rounded-xl transition-colors cursor-pointer h-[38px]"
            >
              식재료 추가
            </button>
          </form>
        </div>

        {/* 3. 식재료 카드 그리드 */}
        <div className="space-y-3">
          {foods.length === 0 ? (
            <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center text-xs font-medium text-slate-400">
              보관 중인 식재료가 없습니다. 안전하게 등록해 보세요! 🌱
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {foods.map((food) => {
                const dDayStr = getDDay(food.expiryAt);
                const isUrgent =
                  (dDayStr.startsWith("D-") &&
                    Number(dDayStr.replace("D-", "")) <= 3) ||
                  dDayStr === "D-Day";

                return (
                  <div
                    key={food.id}
                    className="bg-white rounded-2xl border border-slate-200 shadow-sm p-4 flex items-center justify-between"
                  >
                    <div className="space-y-1">
                      <div className="flex items-center space-x-2">
                        {food.type && (
                          <span className="text-[10px] font-black px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-600">
                            {food.type}
                          </span>
                        )}
                      </div>
                      <h4 className="text-sm font-bold text-slate-800">
                        {food.name}
                      </h4>
                      {food.description && (
                        <p className="text-xs text-slate-400 font-medium">
                          {food.description}
                        </p>
                      )}
                      <p className="text-[11px] text-slate-400 font-semibold pt-1">
                        ⏳ {food.expiryAt}
                      </p>
                    </div>

                    <div className="flex items-center space-x-3">
                      <span
                        className={`text-xs font-black px-2.5 py-1 rounded-xl ${
                          isUrgent
                            ? "bg-red-50 text-red-500"
                            : "bg-slate-100 text-slate-600"
                        }`}
                      >
                        {dDayStr}
                      </span>
                      <button
                        type="button"
                        onClick={() => handleDeleteFood(food.id)}
                        className="text-slate-300 hover:text-red-500 transition-colors text-xs font-bold cursor-pointer"
                      >
                        삭제
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default Food;
