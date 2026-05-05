export interface CommentResponse {
  id: string;
  content: string;
  internal: boolean;
  author: { id: string; fullName: string; avatarUrl?: string };
  createdAt: string;
}

export interface CreateCommentRequest {
  content: string;
  internal: boolean;
}
