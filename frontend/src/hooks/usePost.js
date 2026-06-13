import { useState } from "react";
import { api } from "../utils/api";

const initialPostForm = { title: "", content: "" };

export function usePost(loadPublicData, showError) {
  const [postForm, setPostForm] = useState(initialPostForm);
  const [selectedPostId, setSelectedPostId] = useState(null);
  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState("");
  // 대댓글을 작성할 부모 댓글 ID 상태 추가
  const [activeParentId, setActiveParentId] = useState(null);

  const createPost = async (event) => {
    event.preventDefault();
    try {
      await api.createPost(postForm);
      setPostForm(initialPostForm);
      await loadPublicData();
    } catch (error) {
      showError(error);
    }
  };

  const deletePost = async (id) => {
    try {
      await api.deletePost(id);
      await loadPublicData();
    } catch (error) {
      showError(error);
    }
  };

  const openComments = async (postId) => {
    setSelectedPostId(postId);
    setActiveParentId(null);
    try {
      const result = await api.comments(postId);
      setComments(result); // 백엔드 스펙에 맞춰 상위 계층 댓글 배열이 주입됨
    } catch (error) {
      showError(error);
    }
  };

  // 댓글/대댓글 통합 생성 함수
  const createComment = async (event, parentId = null) => {
    if (event) event.preventDefault();
    if (!selectedPostId || !commentText.trim()) return;
    try {
      await api.createComment(selectedPostId, {
        content: commentText,
        parentId: parentId, // 💡 단방향 Soft 관계 매핑을 위한 부모 ID전송
      });
      setCommentText("");
      setActiveParentId(null);
      // 댓글 목록 리프레시
      const result = await api.comments(selectedPostId);
      setComments(result);
    } catch (error) {
      showError(error);
    }
  };

  return {
    postForm,
    setPostForm,
    selectedPostId,
    comments,
    commentText,
    setCommentText,
    activeParentId,
    setActiveParentId,
    createPost,
    deletePost,
    openComments,
    createComment,
  };
}
