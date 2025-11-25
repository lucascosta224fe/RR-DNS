import { useMemo } from "react";
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
  const hostname = useMemo(() => window.location.hostname || "localhost", []);

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
              Demo de perfil • dados mockados • sessão simulada
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
                <div className="text-sm text-slate-700">
                  <div className="font-semibold mb-1">Descrição</div>
                  <p className="leading-relaxed">
                    {session.user.description || "Sem descrição cadastrada."}
                  </p>
                </div>
              </div>
            </section>

            <section className="space-y-4">
              <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="text-xs font-semibold text-slate-600 mb-1">
                  Status da sessão
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

              <div className="rounded-2xl border border-red-200 bg-red-50 p-4">
                <div className="text-xs font-semibold text-red-700 mb-1">
                  Servidor conectado
                </div>
                <p className="text-xs text-red-600 mb-2">
                  Aqui vamos mostrar de qual servidor de aplicação essa sessão veio
                  (ex.: <code>app1.psreletric.com</code>). No momento está mockado.
                </p>

                <div className="rounded-xl border border-red-200 bg-white px-3 py-2">
                  <div className="text-[11px] text-red-500">Hostname atual</div>
                  <div className="text-sm font-mono text-red-700">
                    {hostname}
                  </div>
                </div>
              </div>
            </section>
          </div>
        </div>

        <p className="text-center text-[11px] text-slate-500 mt-4">
          PSReletric.com • tela de perfil mockada • integração com backend e RR DNS vem depois
        </p>
      </div>
    </div>
  );
}
