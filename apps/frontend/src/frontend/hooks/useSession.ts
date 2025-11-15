import { useEffect, useMemo, useState } from "react";
import type { SessionData, User } from "../types/session";

const STORAGE_KEY = "psr_session";

export function createSession(user: User): SessionData {
  const sessionId =
    (crypto as any)?.randomUUID?.() ?? `${Date.now()}-${Math.random()}`;
  const loginAt = new Date().toISOString();
  return { user, sessionId, loginAt };
}

export function saveSession(sess: SessionData) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(sess));
}

export function loadSession(): SessionData | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as SessionData) : null;
  } catch {
    return null;
  }
}

export function clearSession() {
  localStorage.removeItem(STORAGE_KEY);
}

export function useSession() {
  const [session, setSession] = useState<SessionData | null>(() => loadSession());
  const [tick, setTick] = useState(0);

  useEffect(() => {
    const id = setInterval(() => setTick((t) => t + 1), 1000);
    return () => clearInterval(id);
  }, []);

  const uptime = useMemo(() => {
    if (!session) return "0s";
    const start = new Date(session.loginAt).getTime();
    const diff = Math.max(0, Math.floor((Date.now() - start) / 1000));
    const h = Math.floor(diff / 3600);
    const m = Math.floor((diff % 3600) / 60);
    const s = diff % 60;
    return [h ? `${h}h` : null, m ? `${m}m` : null, `${s}s`]
      .filter(Boolean)
      .join(" ");
  }, [session, tick]);

  return { session, setSession, uptime };
}
