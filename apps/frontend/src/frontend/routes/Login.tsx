import { useMemo, useState } from "react";
import TextField from "../components/TextField";
import { createSession, saveSession } from "../hooks/useSession";
import type { SessionData, SessionUser } from "../types/session";

const ADMIN_USER: SessionUser = {
  username: "adm",
  name: "Administrador do Sistema",
  birthDate: "01/01/2001",
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

  async function handleSubmit(e: React.FormEvent) {
  e.preventDefault();
  setError(null);

  // mock ADM local
  if (username === ADMIN_USER.username && password === "1234") {
    const sess = createSession(ADMIN_USER);
    saveSession(sess);
    onLoginSuccess(sess);
    return;
  }

  try {
    const response = await fetch("/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: username,
        username,
        login: username,
        password,
      }),
    });

    if (response.ok) {
      const data = await response.json();
      console.log("Resposta do Backend:", data);

      const userFromBackend = (data as any).user || data;
      const sess = createSession(userFromBackend);
      saveSession(sess);
      onLoginSuccess(sess);
    } else {
      let backendMsg = "Login ou senha inválidos.";
      try {
        const data = await response.json();
        if (data?.message) backendMsg = data.message;
      } catch {
        /* ignora se não vier JSON */
      }
      setError(backendMsg);
    }
  } catch (err) {
    console.error(err);
    setError("Erro de conexão com o servidor.");
  }
}

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
      <div className="w-full max-w-md">
        <div className="relative rounded-2xl bg-white shadow-lg border border-slate-200">
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

            {error && (
              <div className="mb-4 px-3 py-2 rounded-md border border-red-200 bg-red-50 text-xs text-red-700 font-medium text-center">
                {error}
              </div>
            )}

            <form onSubmit={handleSubmit} className="grid gap-4">
              <TextField
                label="Usuário"
                placeholder="Digite seu e-mail"
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
              />

              <div className="gap-3 mt-1">
                <button
                  type="submit"
                  className="w-full h-10 rounded-md bg-red-500 hover:bg-red-600 text-white font-medium transition"
                >
                  Entrar
                </button>

                <p className="text-center text-sm mt-2 text-slate-700">
                  Caso não tenha uma conta,{" "}
                  <button
                    type="button"
                    onClick={onGoToRegister}
                    className="text-red-600 font-medium hover:text-red-700 underline transition"
                  >
                    registre-se
                  </button>
                </p>
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
