import { useMemo, useEffect, useState } from "react";
import type { SessionData } from "../types/session";

interface Props {
  session: SessionData;
  uptime: string;
  onLogout: () => void;
}

function InfoLine({ label, value }: { label: string; value: string }) {
  return (
    <div className="text-sm text-slate-700">
      <span className="font-semibold">{label}: </span>
      <span>{value}</span>
    </div>
  );
}

export default function UserProfile({ session, uptime, onLogout }: Props) {
  // Hostname do navegador (DNS)
  const browserHostname = useMemo(() => window.location.hostname || "localhost", []);
  
  // Estado para guardar o nome REAL do servidor (vindo do Java)
  const [backendHostname, setBackendHostname] = useState<string>("Carregando...");

  // Efeito para validar a sessão no Redis e pegar o nome do servidor
  useEffect(() => {
    fetch("/auth/profile")
      .then(async (res) => {
        if (res.ok) {
          const data = await res.json();
          // Se o seu Java retornar o nome do servidor, use-o aqui.
          // Ex: data.serverName ou data.hostname
          if (data.serverName) {
            setBackendHostname(data.serverName);
          } else {
            setBackendHostname("Java Backend (Sem nome)");
          }
        } else {
          // Se der erro 401 ou 403, o Redis expirou a sessão
          console.warn("Sessão inválida ou expirada no Redis");
          onLogout();
        }
      })
      .catch((err) => {
        console.error("Erro ao validar perfil:", err);
        // Opcional: deslogar em erro de rede ou apenas avisar
      });
  }, [onLogout]);

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-6">
      <div className="w-full max-w-5xl">
        <header className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-lg font-semibold text-slate-900 flex items-center gap-2">
              <span className="text-2xl">⚡</span>
              <span>PSReletric.com</span>
            </h1>
            <p className="text-xs text-slate-500">
              Perfil Autenticado • Sessão Centralizada (Redis)
            </p>
          </div>
          <button
            onClick={onLogout}
            className="rounded-full bg-rose-500 hover:bg-rose-600 px-4 py-2 text-xs sm:text-sm font-medium text-white shadow-sm"
          >
            Sair
          </button>
        </header>

        <div className="rounded-2xl bg-white border border-slate-200 shadow-lg p-6 sm:p-8">
          <div className="grid gap-6 lg:grid-cols-[minmax(0,2fr)_minmax(0,1.4fr)] items-start">
            <section>
              <h2 className="text-xl font-semibold text-slate-900 mb-4">
                Dados do perfil
              </h2>

              <div className="space-y-3">
                <InfoLine label="Nome" value={session.user.name} />
                <InfoLine label="Data de nascimento" value={session.user.birthDate} />
                <InfoLine label="Login" value={session.user.username} />
                <div className="text-sm text-slate-700">
                  <div className="font-semibold mb-1">Descrição</div>
                  <p className="leading-relaxed">
                    {session.user.description || "Usuário do sistema distribuído."}
                  </p>
                </div>
              </div>
            </section>

            <section className="space-y-4">
              {/* CARD DE SESSÃO */}
              <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="text-xs font-semibold text-slate-600 mb-1">
                  Status da sessão (Redis)
                </div>
                <div className="text-xs text-slate-500 mb-2">
                  Sessão ativa há{" "}
                  <span className="text-slate-800 font-medium">{uptime}</span>
                </div>

                <div className="space-y-2 text-xs">
                  <div>
                    <div className="text-slate-500">ID da sessão</div>
                    <div className="font-mono text-slate-900 break-all">
                      {session.sessionId}
                    </div>
                  </div>
                  <div>
                    <div className="text-slate-500">Logado em</div>
                    <div className="font-mono text-slate-900">
                      {new Date(session.loginAt).toLocaleString()}
                    </div>
                  </div>
                </div>
              </div>

              {/* CARD DE SERVIDOR (O MAIS IMPORTANTE PRO TRABALHO) */}
              <div className="rounded-2xl border border-red-200 bg-red-50 p-4">
                <div className="text-xs font-semibold text-red-700 mb-1">
                  Infraestrutura
                </div>
                <p className="text-xs text-red-600 mb-2">
                  Estes dados mostram qual servidor atendeu sua requisição HTTP.
                </p>

                <div className="space-y-2">
                    <div className="rounded-xl border border-red-200 bg-white px-3 py-2">
                        <div className="text-[11px] text-red-500">DNS (Navegador)</div>
                        <div className="text-sm font-mono text-red-700">
                            {browserHostname}
                        </div>
                    </div>

                    <div className="rounded-xl border border-red-200 bg-white px-3 py-2">
                        <div className="text-[11px] text-red-500">Backend (Java)</div>
                        <div className="text-sm font-mono font-bold text-red-700">
                            {backendHostname}
                        </div>
                    </div>
                </div>
              </div>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
}
