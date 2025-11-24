import { useState } from "react";

interface RegisterProps {
  onBackToLogin: () => void;
}

export default function Register({ onBackToLogin }: RegisterProps) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [msg, setMsg] = useState("");
  const [status, setStatus] = useState<"idle" | "success" | "error">("idle");

  const formatarDataParaJava = (dataIso: string) => {
    if (!dataIso) return null;
    const [ano, mes, dia] = dataIso.split("-");
    return `${dia}-${mes}-${ano}`;
  };

  async function handleRegister() {
    setMsg("");
    setStatus("idle");

    try {
      const response = await fetch("/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          // mandamos todas as variações possíveis pro backend
          email,
          username: email,
          login: email,
          password,
          nome: name,
          dataNascimento: formatarDataParaJava(birthDate),
          descricao: "Novo usuário",
        }),
      });

      if (response.ok) {
        setStatus("success");
        setMsg("Cadastro realizado com sucesso! Redirecionando para o login...");

        // redireciona para o login depois de 2s
        setTimeout(() => {
          onBackToLogin();
        }, 2000);
      } else {
        try {
          const data = await response.json();
          setStatus("error");
          setMsg(data.message || "Erro ao cadastrar. Verifique os dados.");
        } catch {
          setStatus("error");
          setMsg("Erro ao cadastrar (Verifique os dados).");
        }
      }
    } catch (error) {
      console.error(error);
      setStatus("error");
      setMsg("Erro de conexão com o servidor.");
    }
  }

  const msgColor =
    status === "success"
      ? "bg-emerald-50 text-emerald-700 border-emerald-300"
      : "bg-red-50 text-red-700 border-red-300";

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
      <div className="w-full max-w-lg">
        <div className="rounded-2xl bg-white shadow-lg border border-slate-200 p-7">
          <header className="mb-6 flex items-center justify-between">
            <div>
              <h1 className="text-lg font-semibold text-slate-900 flex items-center gap-2">
                <span className="text-2xl">⚡</span>
                <span>Cadastro de usuário</span>
              </h1>
            </div>
            <button
              onClick={onBackToLogin}
              className="text-xs text-slate-500 hover:text-slate-800 underline"
            >
              Voltar para o login
            </button>
          </header>

          <form className="grid gap-3 text-sm">
            {msg && (
              <div
                className={`mb-2 px-3 py-2 rounded-md border text-xs font-medium ${msgColor}`}
              >
                {msg}
              </div>
            )}

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">
                Login (E-mail)
              </label>
              <input
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                placeholder="seu@email.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Senha</label>
              <input
                type="password"
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">
                Nome completo
              </label>
              <input
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                placeholder="Nome e sobrenome"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">
                Data de nascimento
              </label>
              <input
                type="date"
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                value={birthDate}
                onChange={(e) => setBirthDate(e.target.value)}
              />
            </div>

            <button
              type="button"
              onClick={handleRegister}
              className="mt-3 h-10 rounded-md bg-slate-900 text-white font-medium hover:bg-slate-800"
            >
              Salvar cadastro
            </button>
          </form>
        </div>
        <p className="text-center text-xs text-slate-500 mt-4">
          PSReletric.com • Cadastro Integrado
        </p>
      </div>
    </div>
  );
}
