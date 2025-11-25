export interface SessionUser {
  username: string;
  name: string;
  birthDate: string;      
  description: string;
  role: "Admin" | "User";
}

export interface SessionData {
  user: SessionUser;
  sessionId: string;
  loginAt: string;      
}
