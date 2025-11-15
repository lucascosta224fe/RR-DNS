import { useMemo, useState } from "react";
import TextField from "../components/TextField";
import { createSession, saveSession } from "../hooks/useSession";
import type { SessionData, User } from "../types/session";

type UserWithPassword = User & { password: string };

const USERS: UserWithPassword[] = [
  {
    username: "paulo",
    password: "1234",
    name: "Paulo Souza",
    email: "paulo@psreletric.com",
    role: "Engenheiro Eletricista",
  },
  {
    username: "nathan",
    password: "abcd",
    name: "Nathan Santos",
    email: "nathan@psreletric.com",
    role: "Dev Fullstack",
  },
];

interface LoginProps {
  onLogged: (session: SessionData) => void;
}

export default function Login({ onLogged }: LoginProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  const hostname = useMemo(() => window.location.hostname || "localhost", []);

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);

    const found = USERS.find(
      (u) => u.username === username && u.password === password,
    );

    if (!found) {
      setError("Usuário ou senha inválidos.");
      return;
    }

    const { password: _omit, ...user } = found;
    const session = createSession(user);
    saveSession(session);
    onLogged(session);
  }

  function fillMock(i: number) {
    const u = USERS[i];
    setUsername(u.username);
    setPassword(u.password);
    setError(null);
  }

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
      <div className="w-full max-w-md">
        <div className="rounded-xl bg-white shadow-lg border border-slate-200">
          <div className="p-7">

            <div className="text-center mb-6">
              <div className="inline-flex items-center gap-2 mb-1">
                <span className="text-2xl">⚡</span>
                <span className="text-xl font-semibold tracking-tight">
                  PSReletric.com
                </span>
              </div>
              <div className="text-xs text-slate-500">
                Login • Servidor:{" "}
                <span className="font-mono">{hostname}</span>
              </div>
            </div>


            <form onSubmit={handleSubmit} className="grid gap-4">
              <TextField
                label="Usuário"
                placeholder="paulo ou nathan"
                value={username}
                onChange={(e) => setUsername(e.currentTarget.value)}
                autoComplete="username"
              />
              <TextField
                label="Senha"
                type="password"
                placeholder="••••"
                value={password}
                onChange={(e) => setPassword(e.currentTarget.value)}
                autoComplete="current-password"
                error={error ?? undefined}
              />

              <button
                type="submit"
                className="h-10 rounded-md bg-red-500 hover:bg-red-600 text-white font-medium transition"
              >
                Entrar
              </button>

              <div className="flex items-center justify-between text-xs text-slate-500">
                <button
                  type="button"
                  onClick={() => fillMock(0)}
                  className="underline underline-offset-4 hover:text-slate-700"
                >
                  Senha: paulo / 1234
                </button>
                <button
                  type="button"
                  onClick={() => fillMock(1)}
                  className="underline underline-offset-4 hover:text-slate-700"
                >
                  Senha: nathan / abcd
                </button>
              </div>
            </form>
          </div>
        </div>

        <p className="text-center text-xs text-slate-500 mt-4">
          PSReletric.com • sessão simulada
        </p>
      </div>
    </div>
  );
}
