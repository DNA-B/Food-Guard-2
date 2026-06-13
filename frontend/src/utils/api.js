const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1";

export class ApiError extends Error {
  constructor(message, status, payload) {
    super(message);
    this.name = "ApiError";
    this.status = status;
    this.payload = payload;
  }
}

const readBody = async (response) => {
  const contentType = response.headers.get("content-type") ?? "";
  if (response.status === 204) return null;
  if (contentType.includes("application/json")) return response.json();
  const text = await response.text();
  return text || null;
};

export const apiRequest = async (path, options = {}) => {
  const headers = new Headers(options.headers);
  const hasBody = options.body !== undefined && options.body !== null;

  if (hasBody && !headers.has("Content-Type")) {
    // body있고, Content-Type 없으면 기본 json으로 설정
    headers.set("Content-Type", "application/json");
  }

  // JWT 토큰이 있으면 Authorization 헤더에 추가
  const token = localStorage.getItem("accessToken");
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });
  const payload = await readBody(response);

  if (!response.ok) {
    const fallback = `요청에 실패했습니다. (${response.status})`;
    const message = payload?.message ?? payload?.error ?? payload ?? fallback;
    throw new ApiError(message, response.status, payload);
  }
  return payload;
};

export const api = {
  // Auth & User
  login: (body) =>
    apiRequest("/auth/login", { method: "POST", body: JSON.stringify(body) }),
  signup: (body) =>
    apiRequest("/auth/signup", { method: "POST", body: JSON.stringify(body) }),
  checkNickname: (body) =>
    apiRequest("/auth/check/nickname", {
      method: "POST",
      body: JSON.stringify(body),
    }),
  me: () => apiRequest("/users/me"),
  deleteMe: () => apiRequest("/users/me", { method: "DELETE" }),

  // Food
  foods: () => apiRequest("/foods"),
  food: (id) => apiRequest(`/foods/${id}`),
  createFood: (body) =>
    apiRequest("/foods", { method: "POST", body: JSON.stringify(body) }),
  updateFood: (id, body) =>
    apiRequest(`/foods/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  deleteFood: (id) => apiRequest(`/foods/${id}`, { method: "DELETE" }),

  // Donation
  donations: () => apiRequest("/donations"),
  donation: (id) => apiRequest(`/donations/${id}`),
  createDonation: (body) =>
    apiRequest("/donations", { method: "POST", body: JSON.stringify(body) }),
  updateDonation: (id, body) =>
    apiRequest(`/donations/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    }),
  deleteDonation: (id) => apiRequest(`/donations/${id}`, { method: "DELETE" }),
  startChat: (donationId) =>
    apiRequest(`/donations/${donationId}/chatrooms`, { method: "POST" }),

  // Post
  posts: () => apiRequest("/posts"),
  post: (id) => apiRequest(`/posts/${id}`),
  createPost: (body) =>
    apiRequest("/posts", { method: "POST", body: JSON.stringify(body) }),
  updatePost: (id, body) =>
    apiRequest(`/posts/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  deletePost: (id) => apiRequest(`/posts/${id}`, { method: "DELETE" }),

  // Comment
  comments: (postId) => apiRequest(`/posts/${postId}/comments`),
  createComment: (postId, body) =>
    apiRequest(`/posts/${postId}/comments`, {
      method: "POST",
      body: JSON.stringify(body),
    }),
  updateComment: (id, body) =>
    apiRequest(`/comments/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    }),
  deleteComment: (id) => apiRequest(`/comments/${id}`, { method: "DELETE" }),

  // Group
  groups: () => apiRequest("/groups/me"),
  group: (id) => apiRequest(`/groups/${id}`),
  createGroup: (body) =>
    apiRequest("/groups", { method: "POST", body: JSON.stringify(body) }),
  updateGroup: (id, body) =>
    apiRequest(`/groups/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  deleteGroup: (id) => apiRequest(`/groups/${id}`, { method: "DELETE" }),
  exitGroup: (id) => apiRequest(`/groups/${id}/exit`, { method: "POST" }),

  // ChatRoom & ChatMessage
  chatRooms: () => apiRequest("/chatrooms"),
  closeChatRoom: (id) =>
    apiRequest(`/chatrooms/${id}/close`, { method: "PATCH" }),
  chatMessages: (chatRoomId) => apiRequest(`/chatrooms/${chatRoomId}/messages`),

  sendChatMessage: (chatRoomId, content) =>
    apiRequest(`/chatrooms/${chatRoomId}/messages`, {
      method: "POST",
      body: JSON.stringify({ content }),
    }),
};
