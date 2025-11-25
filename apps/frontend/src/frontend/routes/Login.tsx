import { useMemo, useState } from "react";
import TextField from "../components/TextField";
import { createSession, saveSession } from "../hooks/useSession";
import type { SessionData, SessionUser } from "../types/session";

// Usuário Admin local para testes (opcional, pode manter ou remover)
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

    // 1) Mock local (pode manter para testes rápidos)
    if (username === ADMIN_USER.username && password === "1234") {
      const sess = createSession(ADMIN_USER);
      saveSession(sess);
      onLoginSuccess(sess);
      return;
    }

    // 2) Chamada REAL para o Backend Java
    try {
      // ATENÇÃO: Mudamos para caminho relativo e rota /auth/login
      const response = await fetch("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email:username, password }), // Backend espera receber isso
      });

      // Se o Java retornar status 200 (OK)
      if (response.ok) {
        // Assumindo que o Java retorna o objeto do usuário ou sessão direto
        // Se o Java retornar vazio, precisamos tratar, mas geralmente retorna JSON
        const data = await response.json();
        
        console.log("Resposta do Backend:", data);

        // Cria a sessão com os dados reais vindos do banco
        // Verifique se 'data' é o usuário ou se o usuário está em 'data.user'
        // Vou assumir aqui que 'data' já é o objeto do usuário (UserDTO)
        const userFromBackend = data.token; 
        localStorage.setItem("idUser", data.idUser);
        const sess = createSession(userFromBackend);
        saveSession(sess);
        onLoginSuccess(sess);
      } else {
        // Se o Java retornar 401 ou 403
        setError("Login ou senha inválidos.");
      }
    } catch (error) {
      console.error(error);
      setError("Erro de conexão com o servidor.");
    }
    // REMOVI A LINHA QUE ESTAVA AQUI EMBAIXO FORÇANDO O ERRO
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
                error={error ?? undefined}
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
