import React from "react";

export function GroupPage({
  groupForm,
  updateGroupField,
  createGroup,
  groups,
  exitGroup,
  deleteGroup,
}) {
  return (
    <section className="two-column">
      <form className="panel form-stack" onSubmit={createGroup}>
        <div className="section-heading compact">
          <p>API 요청</p>
          <h2>그룹 생성</h2>
        </div>
        <label>
          그룹 이름
          <input
            value={groupForm.name}
            onChange={updateGroupField("name")}
            required
          />
        </label>
        <label>
          그룹 설명
          <input
            value={groupForm.description}
            onChange={updateGroupField("description")}
          />
        </label>
        <button type="submit" className="primary-button">
          생성
        </button>
      </form>

      <section className="panel list-panel">
        <div className="section-heading">
          <p>조회 결과</p>
          <h2>내 그룹 목록</h2>
        </div>
        {groups.length === 0 ? (
          <p className="empty-state">소속된 그룹이 없습니다.</p>
        ) : (
          <div className="list-stack">
            {groups.map((group) => (
              <article className="item-card" key={group.id}>
                <div>
                  <h3>{group.name}</h3>
                  <p>{group.description || "설명 없음"}</p>
                </div>
                <div className="item-actions">
                  <button
                    className="secondary-button"
                    type="button"
                    onClick={() => exitGroup(group.id)}
                  >
                    탈퇴
                  </button>
                  <button
                    className="danger-button"
                    type="button"
                    onClick={() => deleteGroup(group.id)}
                  >
                    삭제
                  </button>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </section>
  );
}
