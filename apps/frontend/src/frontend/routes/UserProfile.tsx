import { useMemo } from "react";
import type { SessionData } from "../types/session";

interface ProfileInfo {
  age: number;
  city: string;
  jobTitle: string;
  about: string;
  avatarUrl?: string;
}

const PROFILE_DATA: Record<string, ProfileInfo> = {
  paulo: {
    age: 24,
    city: "Brasília / DF",
    jobTitle: "Eletricista que gosta de fio terra",
    about:
      "Cuida da eletrica da casa dos outros e famoso por resolver tudo na base do GEMINI.",
    avatarUrl: "/avatars/paulo.jpeg",
  },
  nathan: {
    age: 24,
    city: "Brasília / DF",
    jobTitle: "Ele é do job",
    about:
      "Acabou de arrumar um estagio e ate hoje o lucas não arrumou nenhum.",
    avatarUrl: "/avatars/nathan.jpeg",
  },
};

interface Props {
  session: SessionData;
  uptime: string;
  onLogout: () => void;
}

function InfoRow({
  label,
  value,
  mono = false,
}: {
  label: string;
  value: string;
  mono?: boolean;
}) {
  return (
    <div className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
      <div className="text-xs text-slate-500">{label}</div>
      <div
        className={`text-sm sm:text-base text-slate-800 ${
          mono ? "font-mono" : "font-medium"
        }`}
      >
        {value}
      </div>
    </div>
  );
}

export default function UserProfile({ session, uptime, onLogout }: Props) {
  const hostname = useMemo(() => window.location.hostname || "localhost", []);

  const profile = PROFILE_DATA[session.user.username] ?? {
    age: 0,
    city: "—",
    jobTitle: session.user.role,
    about: "Perfil mockado para demonstração.",
  };

  const initials = session.user.name
    .split(" ")
    .map((p) => p[0]?.toUpperCase())
    .slice(0, 2)
    .join("");

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
            className="rounded-xl bg-rose-500 hover:bg-rose-600 px-3 py-2 text-xs sm:text-sm font-medium text-white transition shadow-sm"
          >
            Sair
          </button>
        </header>


        <div className="rounded-2xl bg-white border border-slate-200 shadow-lg p-6 sm:p-8">
          <div className="grid gap-6 lg:grid-cols-[minmax(0,2fr)_minmax(0,1.4fr)] items-start">

            <div className="flex gap-4 sm:gap-6">

              <div className="flex-shrink-0">
                {profile.avatarUrl ? (
                  <img
                    src={profile.avatarUrl}
                    alt={session.user.name}
                    className="h-24 w-24 sm:h-32 sm:w-32 rounded-2xl object-cover border border-slate-200 bg-slate-100"
                  />
                ) : (
                  <div className="h-24 w-24 sm:h-32 sm:w-32 rounded-2xl border border-slate-200 bg-slate-100 flex items-center justify-center text-2xl sm:text-3xl font-semibold text-slate-700">
                    {initials}
                  </div>
                )}
              </div>


              <div className="min-w-0 flex-1">
                <h2 className="text-xl sm:text-2xl font-semibold text-slate-900 truncate">
                  {session.user.name}
                </h2>
                <p className="text-xs sm:text-sm text-slate-500 mb-2">
                  @{session.user.username} • {profile.jobTitle}
                </p>

                <div className="flex flex-wrap gap-2 text-[11px] sm:text-xs text-slate-700">
                  <span className="rounded-full border border-slate-200 bg-slate-50 px-3 py-1">
                    Idade: {profile.age || "—"} anos
                  </span>
                  <span className="rounded-full border border-slate-200 bg-slate-50 px-3 py-1">
                    Cidade: {profile.city}
                  </span>
                  <span className="rounded-full border border-slate-200 bg-slate-50 px-3 py-1">
                    Papel: {session.user.role}
                  </span>
                </div>

                <p className="mt-3 text-xs sm:text-sm text-slate-700 leading-relaxed">
                  {profile.about}
                </p>
              </div>
            </div>

            <div className="space-y-3">
              <div className="rounded-2xl border border-slate-200 bg-slate-50 p-4">
                <div className="text-xs font-semibold text-slate-700 mb-1">
                  Status da sessão
                </div>
                <div className="text-xs text-slate-500 mb-2">
                  Sessão ativa há{" "}
                  <span className="text-slate-800 font-medium">{uptime}</span>
                </div>

                <div className="grid gap-2">
                  <InfoRow label="ID da sessão" value={session.sessionId} mono />
                  <InfoRow
                    label="Logado em"
                    value={new Date(session.loginAt).toLocaleString()}
                    mono
                  />
                </div>
              </div>

              <div className="rounded-2xl border border-red-200 bg-red-50 p-4">
                <div className="text-xs font-semibold text-red-700 mb-1">
                  Servidor conectado
                </div>
                <p className="text-xs text-red-700/80 mb-2">
                  Aqui vamos mostrar de qual servidor essa sessão veio 
                  (ex.: app1.psreletric.com). No momento tá mockado.
                </p>

                <div className="rounded-xl border border-red-200 bg-white px-3 py-2">
                  <div className="text-[11px] text-slate-500">Hostname atual</div>
                  <div className="text-sm font-mono text-slate-800">
                    {hostname}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <p className="text-center text-[11px] text-slate-500 mt-4">
          PSReletric.com • tela de perfil mockada 
        </p>
      </div>
    </div>
  );
}
