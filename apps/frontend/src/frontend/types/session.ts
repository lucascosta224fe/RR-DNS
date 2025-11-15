export interface User {
  username: string;
  name: string;
  email: string;
  role: string;
}

export interface SessionData {
  user: User;
  sessionId: string;
  loginAt: string;
}
