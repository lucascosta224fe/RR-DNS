interface RegisterProps {
  onBackToLogin: () => void;
}

export default function Register({ onBackToLogin }: RegisterProps) {
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
                Login, senha, nome, data de nascimento e descrição.
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
            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Login</label>
              <input
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                placeholder="usuário"
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Senha</label>
              <input
                type="password"
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                placeholder="••••••••"
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Nome completo</label>
              <input
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
                placeholder="Nome e sobrenome"
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Data de nascimento</label>
              <input
                type="date"
                className="h-9 rounded-md border border-slate-300 px-3 text-slate-800"
              />
            </div>

            <div className="flex flex-col gap-1">
              <label className="font-semibold text-slate-700">Descrição</label>
              <textarea
                rows={3}
                className="rounded-md border border-slate-300 px-3 py-2 text-slate-800 resize-none"
                placeholder="Fale um pouco sobre o usuário..."
              />
            </div>

            <button
              type="button"
              className="mt-3 h-10 rounded-md bg-slate-900 text-white font-medium hover:bg-slate-800"
            >
              (mock) Salvar cadastro
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
