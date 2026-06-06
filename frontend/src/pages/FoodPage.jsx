import React from "react";

export function FoodPage({
  foodForm,
  updateFoodField,
  handleSubmit, // 변경: createFood 대신 handleSubmit 사용
  editingFoodId, // 추가
  cancelEdit, // 추가
  startEdit, // 추가
  groups,
  foods,
  getExpiryState,
  onDeleteFood,
}) {
  return (
    <section className="two-column">
      {/* 폼 섹션 */}
      <form className="panel form-stack" onSubmit={handleSubmit}>
        <div className="section-heading compact">
          <p>API 요청</p>
          {/* 💡 모드에 따라 타이틀 변경 */}
          <h2>{editingFoodId ? "식품 수정" : "식품 등록"}</h2>
        </div>

        <label>
          식품명
          <input
            value={foodForm.name}
            onChange={updateFoodField("name")}
            required
          />
        </label>
        <label>
          종류
          <input
            value={foodForm.type}
            onChange={updateFoodField("type")}
            placeholder="DAIRY, VEGETABLE 등"
            required
          />
        </label>
        <label>
          설명
          <input
            value={foodForm.description}
            onChange={updateFoodField("description")}
          />
        </label>
        <label>
          소비기한
          <input
            type="date"
            value={foodForm.expiryAt}
            onChange={updateFoodField("expiryAt")}
            required
          />
        </label>
        <label>
          그룹 ID
          <select
            value={foodForm.groupId}
            onChange={updateFoodField("groupId")}
          >
            <option value="">그룹 선택 (개인 보관은 선택 안 함)</option>
            {/* 만약 그룹 해제 기능(-1)을 명시적으로 쓰고 싶다면 아래 항목 활성화 */}
            {editingFoodId && (
              <option value="-1">그룹 해제 (개인 보관으로 변경)</option>
            )}
            {groups.map((group) => (
              <option key={group.id} value={group.id}>
                {group.name}
              </option>
            ))}
          </select>
        </label>

        <div className="item-actions" style={{ marginTop: "10px" }}>
          {/* 💡 모드에 따라 버튼 텍스트 변경 */}
          <button type="submit" className="primary-button">
            {editingFoodId ? "수정 완료" : "등록"}
          </button>
          {/* 💡 수정 모드일 때는 취소 버튼도 노출 */}
          {editingFoodId && (
            <button
              type="button"
              className="secondary-button"
              onClick={cancelEdit}
            >
              취소
            </button>
          )}
        </div>
      </form>

      {/* 목록 섹션 */}
      <section className="panel list-panel">
        <div className="section-heading">
          <p>조회 결과</p>
          <h2>내 식품</h2>
        </div>
        {foods.length === 0 ? (
          <p className="empty-state">등록된 식품이 없습니다.</p>
        ) : (
          <div className="list-stack">
            {foods.map((food) => {
              const expiry = getExpiryState(food.expiryAt);
              return (
                <article className="item-card" key={food.id}>
                  <div>
                    <span className="badge">{food.type}</span>
                    <h3>{food.name}</h3>
                    <p>{food.description || "설명 없음"}</p>
                    <small>소비기한 {food.expiryAt}</small>
                  </div>
                  <div className="item-actions">
                    <span className={`status ${expiry.tone}`}>
                      {expiry.label}
                    </span>
                    {/* 💡 [수정] 버튼 추가 */}
                    <button
                      className="secondary-button"
                      type="button"
                      onClick={() => startEdit(food)}
                    >
                      수정
                    </button>
                    <button
                      className="danger-button"
                      type="button"
                      onClick={() => onDeleteFood(food.id)}
                    >
                      삭제
                    </button>
                  </div>
                </article>
              );
            })}
          </div>
        )}
      </section>
    </section>
  );
}
