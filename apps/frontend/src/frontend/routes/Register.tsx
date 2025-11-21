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

  async function handleRegister() {
    try {
      const response = await fetch('http://localhost:3000/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, name, birthDate })
      });

      const data = await response.json();

      if(response.ok){
        alert("Cadastro Realizado! Faça Login.");
        onBackToLogin();
      }else{
        setMsg(data.erro || "Erro ao cadastrar");
      }
    }catch(error){
      console.error(error);
      setMsg("Erro de conexão com o servidor");
    }
    
  }


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
              <p className="text-xs text-slate-500">
              </p>
            </div>
            <button
              onClick={onBackToLogin}
              className="text-xs text-slate-500 hover:text-slate-800 underline"
            >
              Voltar para o login
            </button>
          </header>

          {/* AQUI o Nathan implementa a lógica / integração com backend depois */}
          <form className="grid gap-3 text-sm">
            {msg && <div className="text-red-500 text-center font-bold">{msg}</div>}
            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Login</label>
              <input
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                placeholder="E-mail"
                value={email} onChange={e => setEmail(e.target.value)}
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Senha</label>
              <input
                type="password"
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                placeholder="••••••••"
                value={password} onChange={e => setPassword(e.target.value)}
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Nome completo</label>
              <input
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                placeholder="Nome e sobrenome"
                value={name} onChange={e => setName(e.target.value)}              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Data de nascimento</label>
              <input
                type="date"
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                value={birthDate} onChange={e => setBirthDate(e.target.value)}
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
          Esta tela é mock – integração real com backend será feita depois.
        </p>
      </div>
    </div>
  );
}
