// src/frontend/routes/Login.tsx
import { useMemo, useState } from "react";
import TextField from "../components/TextField";
import { createSession, saveSession } from "../hooks/useSession";
import type { SessionData, SessionUser } from "../types/session";

const ADMIN_USER: SessionUser = {
  username: "adm",                // pode mudar pra "admin" se quiser
  name: "Administrador do Sistema",
  birthDate: "2000-01-01",
  description: "Usuário administrador mockado para testes.",
  role: "Admin",
};

interface LoginProps {
  onLoginSuccess: (session: SessionData) => void;
  onGoToRegister: () => void;
}

export default function Login({ onLoginSuccess, onGoToRegister }: LoginProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);

  const hostname = useMemo(() => window.location.hostname || "localhost", []);

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);

    // 1) login do ADM mocado
    if (username === ADMIN_USER.username && password === "1234") {
      const sess = createSession(ADMIN_USER);
      saveSession(sess);
      onLoginSuccess(sess);
      return;
    }

    // 2) AQUI DEPOIS entra a chamada pro backend / cadastro
    // ex: fetch("/api/login", { body: JSON.stringify({ username, password }) })

    setError("Usuário ou senha inválidos.");
  }

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
      <div className="w-full max-w-md">
        <div className="relative rounded-2xl bg-white shadow-lg border border-slate-200">
          <div className="p-7">
            {/* topo / logo e título */}
            <div className="text-center mb-6">
              <div className="inline-flex items-center gap-2 mb-1">
                <span className="text-2xl">⚡</span>
                <span className="text-xl font-semibold tracking-tight">
                  PSReletric.com
                </span>
              </div>
              <div className="text-xs text-slate-500">
                Login • Servidor: <span className="font-mono">{hostname}</span>
              </div>
            </div>

            {/* formulário */}
            <form onSubmit={handleSubmit} className="grid gap-4">
              <TextField
                label="Usuário"
                placeholder="Digite seu login"
                value={username}
                onChange={(e) => setUsername(e.currentTarget.value)}
                autoComplete="username"
              />
              <TextField
                label="Senha"
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.currentTarget.value)}
                autoComplete="current-password"
                error={error ?? undefined}
              />

              <div className="flex gap-3 mt-1">
                <button
                  type="submit"
                  className="flex-1 h-10 rounded-md bg-red-500 hover:bg-red-600 text-white font-medium transition"
                >
                  Entrar
                </button>

                <button
                  type="button"
                  onClick={onGoToRegister}
                  className="flex-1 h-10 rounded-md border border-slate-300 text-slate-700 hover:bg-slate-50 text-sm font-medium transition"
                >
                  Cadastrar
                </button>
              </div>
            </form>
          </div>
        </div>

        <p className="text-center text-xs text-slate-500 mt-4">
          PSReletric.com • sessão simulada • login ADM: <code>adm / 1234</code>
        </p>
      </div>
    </div>
  );
}
