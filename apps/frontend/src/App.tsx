import { useState } from "react";
import "./index.css";

import { useSession } from "./frontend/hooks/useSession";
import Login from "./frontend/routes/Login";
import UserProfile from "./frontend/routes/UserProfile";
import Register from "./frontend/routes/Register";
import type { SessionData } from "./frontend/types/session";

type Screen = "login" | "profile" | "register";

export default function App() {
  const { session, setSession, uptime, clearSession } = useSession();
  const [screen, setScreen] = useState<Screen>(
    session ? "profile" : "login",
  );

  function handleLogged(newSession: SessionData) {
    setSession(newSession);
    setScreen("profile");
  }

  function handleLogout() {
    clearSession();
    setScreen("login");
  }

  if (screen === "register") {
    return <Register onBackToLogin={() => setScreen("login")} />;
  }

  if (!session || screen === "login") {
    return (
      <Login
        onLoginSuccess={handleLogged}
        onGoToRegister={() => setScreen("register")}
      />
    );
  }

  return (
    <UserProfile
      session={session}
      uptime={uptime}
      onLogout={handleLogout}
    />
  );
}
