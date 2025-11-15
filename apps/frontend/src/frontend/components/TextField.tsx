import type { InputHTMLAttributes } from "react";

type Props = {
  label: string;
  error?: string;
  className?: string;
} & InputHTMLAttributes<HTMLInputElement>;

export default function TextField({ label, error, className = "", ...props }: Props) {
  return (
    <label className="grid gap-1 text-sm">
      <span className="text-slate-700">{label}</span>
      <input
        {...props}
        className={`h-10 rounded-md border border-slate-300 bg-white px-3 outline-none
        focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 transition ${className}`}
      />
      {error && <span className="text-xs text-rose-600">{error}</span>}
    </label>
  );
}
