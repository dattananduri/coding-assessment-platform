import React from 'react';
import { Clock, ShieldAlert, CheckCircle2, UserCheck, AlertTriangle } from 'lucide-react';

interface NavbarProps {
  title: string;
  candidateName: string;
  candidateEmail: string;
  remainingSeconds: number;
  violationsCount: number;
  onFinalSubmit: () => void;
  isFullscreen: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({
  title,
  candidateName,
  candidateEmail,
  remainingSeconds,
  violationsCount,
  onFinalSubmit,
  isFullscreen,
}) => {
  const formatTime = (totalSeconds: number) => {
    const mins = Math.floor(Math.max(0, totalSeconds) / 60);
    const secs = Math.max(0, totalSeconds) % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const isUrgent = remainingSeconds <= 300; // Under 5 mins

  return (
    <header className="h-16 bg-slate-900/95 backdrop-blur border-b border-slate-800 px-6 flex items-center justify-between z-30 select-none">
      {/* Brand & Assessment Title */}
      <div className="flex items-center space-x-4">
        <div className="flex items-center space-x-2">
          <div className="w-9 h-9 rounded-lg bg-gradient-to-tr from-blue-600 to-indigo-500 flex items-center justify-center font-bold text-white shadow-lg shadow-blue-500/20">
            AG
          </div>
          <div>
            <h1 className="text-sm font-semibold text-slate-100 tracking-wide uppercase">
              Coding Assessment
            </h1>
            <p className="text-xs text-slate-400 max-w-xs truncate">{title}</p>
          </div>
        </div>

        <div className="hidden md:flex items-center pl-4 border-l border-slate-800 text-xs text-slate-400 space-x-3">
          <div className="flex items-center space-x-1.5">
            <UserCheck className="w-3.5 h-3.5 text-blue-400" />
            <span className="text-slate-200 font-medium">{candidateName}</span>
          </div>
          <span>•</span>
          <span className="text-slate-400">{candidateEmail}</span>
        </div>
      </div>

      {/* Center: Global 90-Min Countdown Timer */}
      <div className="flex items-center space-x-4">
        <div
          className={`flex items-center space-x-2 px-4 py-1.5 rounded-full border transition-all duration-300 font-mono font-bold text-lg ${
            isUrgent
              ? 'bg-rose-950/60 border-rose-600 text-rose-400 animate-pulse shadow-lg shadow-rose-950/50'
              : 'bg-slate-800/80 border-slate-700 text-emerald-400'
          }`}
        >
          <Clock className={`w-4 h-4 ${isUrgent ? 'text-rose-400 animate-spin' : 'text-emerald-400'}`} />
          <span className="tracking-widest">{formatTime(remainingSeconds)}</span>
        </div>

        {/* Fullscreen Status */}
        <div className="hidden sm:flex items-center space-x-1.5 text-xs">
          {isFullscreen ? (
            <span className="inline-flex items-center px-2 py-1 rounded bg-emerald-950/40 text-emerald-400 border border-emerald-800/40 font-medium">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400 mr-1.5 animate-pulse"></span>
              Fullscreen Active
            </span>
          ) : (
            <span className="inline-flex items-center px-2 py-1 rounded bg-amber-950/40 text-amber-400 border border-amber-800/40 font-medium">
              <AlertTriangle className="w-3 h-3 mr-1" />
              Not Fullscreen
            </span>
          )}

          {violationsCount > 0 && (
            <span className="inline-flex items-center px-2 py-1 rounded bg-rose-950/40 text-rose-400 border border-rose-800/40 font-medium">
              <ShieldAlert className="w-3 h-3 mr-1" />
              {violationsCount} Violation{violationsCount > 1 ? 's' : ''}
            </span>
          )}
        </div>
      </div>

      {/* Right: Final Submit Action */}
      <div className="flex items-center space-x-3">
        <button
          onClick={onFinalSubmit}
          className="px-4 py-2 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-medium text-xs rounded-lg shadow-md shadow-emerald-900/30 transition-all active:scale-95 flex items-center space-x-1.5"
        >
          <CheckCircle2 className="w-4 h-4" />
          <span>Final Submit</span>
        </button>
      </div>
    </header>
  );
};
