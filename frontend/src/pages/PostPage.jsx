// src/pages/PostPage.jsx
import React from "react";

export function PostPage({
  postForm,
  updatePostField,
  createPost,
  posts,
  deletePost,
  selectedPostId,
  openComments,
  comments,
  commentText,
  setCommentText,
  activeParentId,
  setActiveParentId,
  createComment,
  formatDate,
}) {
  return (
    <section className="two-column">
      <form className="panel form-stack" onSubmit={createPost}>
        <div className="section-heading compact">
          <p>API 요청</p>
          <h2>글쓰기</h2>
        </div>
        <label>
          제목
          <input
            value={postForm.title}
            onChange={updatePostField("title")}
            required
          />
        </label>
        <label>
          내용
          <textarea
            value={postForm.content}
            onChange={updatePostField("content")}
            rows={4}
            required
          />
        </label>
        <button type="submit" className="primary-button">
          작성
        </button>
      </form>

      <section className="panel list-panel">
        <div className="section-heading">
          <p>조회 결과</p>
          <h2>커뮤니티 글 목록</h2>
        </div>
        {posts.length === 0 ? (
          <p className="empty-state">등록된 글이 없습니다.</p>
        ) : (
          <div className="list-stack">
            {posts.map((post) => (
              <article className="item-card vertical" key={post.id}>
                <div className="card-header">
                  <div>
                    <h3>{post.title}</h3>
                    <small>
                      {post.author} • {formatDate(post.createdAt)}
                    </small>
                  </div>
                  <button
                    className="danger-button compact"
                    type="button"
                    onClick={() => deletePost(post.id)}
                  >
                    삭제
                  </button>
                </div>
                <p className="card-body">{post.content}</p>
                <div className="card-footer">
                  <button
                    type="button"
                    className="text-button"
                    onClick={() => openComments(post.id)}
                  >
                    댓글 보기
                  </button>
                </div>

                {selectedPostId === post.id && (
                  <div className="comment-section">
                    <h4>댓글</h4>
                    <div className="comment-list">
                      {comments.length === 0 ? (
                        <p className="empty-state">첫 댓글을 남겨보세요.</p>
                      ) : (
                        comments.map((comment) => (
                          <div
                            className="comment-group"
                            key={comment.id}
                            style={{ marginBottom: "12px" }}
                          >
                            {/* 최상위 부모 댓글 */}
                            <div className="comment-item">
                              <p>{comment.content}</p>
                              <div
                                style={{
                                  display: "flex",
                                  gap: "10px",
                                  alignItems: "center",
                                }}
                              >
                                <small>
                                  {comment.author} •{" "}
                                  {formatDate(comment.updatedAt)} •{" "}
                                  {comment.status}
                                </small>
                                <button
                                  type="button"
                                  className="text-button"
                                  style={{ fontSize: "11px", padding: 0 }}
                                  onClick={() =>
                                    setActiveParentId(
                                      activeParentId === comment.id
                                        ? null
                                        : comment.id,
                                    )
                                  }
                                >
                                  답글 달기
                                </button>
                              </div>
                            </div>

                            {/* 자식 대댓글 리스트 순회 (백엔드 children 데이터 구조 바인딩) */}
                            {comment.children &&
                              comment.children.map((reply) => (
                                <div
                                  className="comment-item reply"
                                  key={reply.id}
                                  style={{
                                    marginLeft: "24px",
                                    borderLeft: "2px solid #dfe5db",
                                    paddingLeft: "12px",
                                    marginTop: "6px",
                                  }}
                                >
                                  <p>
                                    <span
                                      style={{
                                        color: "#55645b",
                                        fontWeight: "bold",
                                      }}
                                    >
                                      ↳
                                    </span>{" "}
                                    {reply.content}
                                  </p>
                                  <small>
                                    {reply.author} •{" "}
                                    {formatDate(reply.updatedAt)} •{" "}
                                    {reply.status}
                                  </small>
                                </div>
                              ))}

                            {/* 대댓글 전용 입력 창 */}
                            {activeParentId === comment.id && (
                              <form
                                className="input-group"
                                style={{ marginLeft: "24px", marginTop: "6px" }}
                                onSubmit={(e) => createComment(e, comment.id)}
                              >
                                <input
                                  placeholder="답글을 입력하세요..."
                                  value={commentText}
                                  onChange={(e) =>
                                    setCommentText(e.target.value)
                                  }
                                  required
                                />
                                <button
                                  type="submit"
                                  className="primary-button"
                                >
                                  등록
                                </button>
                              </form>
                            )}
                          </div>
                        ))
                      )}
                    </div>

                    {/* 일반 최상위 댓글 입력 창 */}
                    {!activeParentId && (
                      <form
                        className="input-group"
                        onSubmit={(e) => createComment(e, null)}
                      >
                        <input
                          placeholder="댓글을 입력하세요..."
                          value={commentText}
                          onChange={(e) => setCommentText(e.target.value)}
                          required
                        />
                        <button type="submit" className="primary-button">
                          등록
                        </button>
                      </form>
                    )}
                  </div>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
    </section>
  );
}
