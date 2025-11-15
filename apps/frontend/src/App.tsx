import "./index.css";
import { useSession, clearSession } from "./frontend/hooks/useSession";
import Login from "./frontend/routes/Login";
import UserProfile from "./frontend/routes/UserProfile";

export default function App() {
  const { session, setSession, uptime } = useSession();

  function handleLogout() {
    clearSession();
    setSession(null);
  }

  if (!session) {
    return <Login onLogged={setSession} />;
  }

  return (
    <UserProfile
      session={session}
      uptime={uptime}
      onLogout={handleLogout}
    />
  );
}
