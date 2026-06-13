import React from "react";

export function DonationPage({
  donationForm,
  updateDonationField,
  createDonation,
  foods,
  donations,
  startChat,
  deleteDonation,
  formatDate,
}) {
  return (
    <section className="two-column">
      <form className="panel form-stack" onSubmit={createDonation}>
        <div className="section-heading compact">
          <p>API 요청</p>
          <h2>나눔 등록</h2>
        </div>
        <label>
          제목
          <input
            value={donationForm.title}
            onChange={updateDonationField("title")}
            required
          />
        </label>
        <label>
          내용
          <input
            value={donationForm.content}
            onChange={updateDonationField("content")}
            required
          />
        </label>
        <label>
          나눔할 식품
          <select
            value={donationForm.foodId}
            onChange={updateDonationField("foodId")}
            required
          >
            <option value="">식품 선택</option>
            {foods.map((food) => (
              <option key={food.id} value={food.id}>
                {food.name} ({food.expiryAt}까지)
              </option>
            ))}
          </select>
        </label>
        <button type="submit" className="primary-button">
          등록
        </button>
      </form>

      <section className="panel list-panel">
        <div className="section-heading">
          <p>조회 결과</p>
          <h2>나눔 목록</h2>
        </div>
        {donations.length === 0 ? (
          <p className="empty-state">등록된 나눔이 없습니다.</p>
        ) : (
          <div className="list-stack">
            {donations.map((donation) => (
              <article className="item-card" key={donation.donationId}>
                <div>
                  <span
                    className={`badge ${donation.status === "ONGOING" ? "success" : "muted"}`}
                  >
                    {donation.status === "ONGOING" ? "나눔중" : "나눔완료"}
                  </span>
                  <h3>{donation.title}</h3>
                  <p>{donation.content}</p>
                  <div className="meta-row">
                    <span>식품: {donation.foodName}</span>
                    <span>작성자: {donation.author}</span>
                    <span>{formatDate(donation.createdAt)}</span>
                  </div>
                </div>
                <div className="item-actions">
                  {donation.status === "ONGOING" && (
                    <button
                      className="secondary-button"
                      type="button"
                      onClick={() => startChat(donation.donationId)}
                    >
                      나눔 신청 (채팅)
                    </button>
                  )}
                  <button
                    className="danger-button"
                    type="button"
                    onClick={() => deleteDonation(donation.donationId)}
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
