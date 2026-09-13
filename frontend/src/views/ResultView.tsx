import React, { useEffect } from 'react';
import confetti from 'canvas-confetti';
import { FinalResult } from '../types';
import { Award, CheckCircle2, Code2, Database, Mic, ShieldAlert, Sparkles, Home } from 'lucide-react';

interface ResultViewProps {
  result: FinalResult;
  onHomeClick: () => void;
}

export const ResultView: React.FC<ResultViewProps> = ({ result, onHomeClick }) => {
  useEffect(() => {
    // Fire festive celebratory confetti
    confetti({
      particleCount: 100,
      spread: 70,
      origin: { y: 0.6 },
    });
  }, []);

  const percentage = Math.round((result.totalScore / result.maxScore) * 100);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col items-center justify-center p-4 sm:p-6">
      <div className="max-w-3xl w-full bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl overflow-hidden">
        {/* Banner */}
        <div className="bg-gradient-to-r from-emerald-950/60 via-teal-950/40 to-slate-900 p-8 border-b border-slate-800 text-center space-y-3">
          <div className="w-16 h-16 rounded-full bg-emerald-950/80 border border-emerald-800/80 mx-auto flex items-center justify-center text-emerald-400 shadow-xl shadow-emerald-950/50">
            <Award className="w-9 h-9" />
          </div>
          <h1 className="text-2xl sm:text-3xl font-black text-white tracking-wide uppercase">
            Assessment Completed
          </h1>
          <p className="text-xs sm:text-sm text-slate-300">
            Thank you, <strong className="text-white">{result.candidateName}</strong>. Your technical evaluation has been submitted and recorded.
          </p>
        </div>

        <div className="p-6 sm:p-8 space-y-8">
          {/* Main Scorecard */}
          <div className="p-6 bg-slate-950 rounded-2xl border border-slate-800 flex flex-col sm:flex-row items-center justify-between gap-6">
            <div className="space-y-1 text-center sm:text-left">
              <span className="text-xs font-bold uppercase tracking-wider text-slate-400">
                Total Assessment Score
              </span>
              <div className="flex items-baseline space-x-2">
                <span className="text-5xl font-black font-mono text-emerald-400">
                  {result.totalScore}
                </span>
                <span className="text-slate-500 font-mono text-xl font-bold">
                  / {result.maxScore}
                </span>
              </div>
              <span className="text-xs text-slate-400 block font-medium">
                Overall Score Percentage: <strong className="text-white">{percentage}%</strong>
              </span>
            </div>

            <div className="w-full sm:w-64 bg-slate-900 p-4 rounded-xl border border-slate-800 text-xs space-y-2">
              <div className="flex justify-between text-slate-400">
                <span>Assessment:</span>
                <span className="text-white font-medium truncate max-w-[120px]">{result.assessmentTitle}</span>
              </div>
              <div className="flex justify-between text-slate-400">
                <span>Attempt Status:</span>
                <span className="text-emerald-400 font-bold uppercase">{result.status}</span>
              </div>
              {result.violationsCount > 0 && (
                <div className="flex justify-between text-rose-400 font-semibold">
                  <span>Violations Logged:</span>
                  <span>{result.violationsCount}</span>
                </div>
              )}
            </div>
          </div>

          {/* Sectional Breakdown (Section 17 Format) */}
          <div className="space-y-3">
            <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">
              Sectional Performance Breakdown
            </h3>

            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              {/* Java Score */}
              <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
                <div className="flex items-center space-x-2 text-blue-400">
                  <Code2 className="w-4 h-4" />
                  <span className="text-xs font-bold uppercase tracking-wider">Java Coding</span>
                </div>
                <div className="flex items-baseline space-x-1.5 font-mono">
                  <span className="text-3xl font-black text-white">{result.javaScore}</span>
                  <span className="text-slate-500 font-bold text-sm">/ 30 pts</span>
                </div>
                <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden">
                  <div
                    className="bg-blue-500 h-full rounded-full transition-all duration-500"
                    style={{ width: `${(result.javaScore / 30) * 100}%` }}
                  />
                </div>
              </div>

              {/* SQL Score */}
              <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
                <div className="flex items-center space-x-2 text-emerald-400">
                  <Database className="w-4 h-4" />
                  <span className="text-xs font-bold uppercase tracking-wider">SQL Database</span>
                </div>
                <div className="flex items-baseline space-x-1.5 font-mono">
                  <span className="text-3xl font-black text-white">{result.sqlScore}</span>
                  <span className="text-slate-500 font-bold text-sm">/ 30 pts</span>
                </div>
                <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden">
                  <div
                    className="bg-emerald-500 h-full rounded-full transition-all duration-500"
                    style={{ width: `${(result.sqlScore / 30) * 100}%` }}
                  />
                </div>
              </div>

              {/* English Score */}
              <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 space-y-2">
                <div className="flex items-center space-x-2 text-purple-400">
                  <Mic className="w-4 h-4" />
                  <span className="text-xs font-bold uppercase tracking-wider">English Speaking</span>
                </div>
                <div className="flex items-baseline space-x-1.5 font-mono">
                  <span className="text-3xl font-black text-white">{result.englishScore}</span>
                  <span className="text-slate-500 font-bold text-sm">/ 40 pts</span>
                </div>
                <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden">
                  <div
                    className="bg-purple-500 h-full rounded-full transition-all duration-500"
                    style={{ width: `${(result.englishScore / 40) * 100}%` }}
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Spoken English AI Evaluation Summary */}
          {result.englishEvaluation && (
            <div className="p-5 bg-gradient-to-br from-slate-950 to-slate-900 rounded-2xl border border-purple-800/40 space-y-3">
              <div className="flex items-center space-x-2 text-purple-400 font-bold text-xs uppercase tracking-wider">
                <Sparkles className="w-4 h-4" />
                <span>AI Spoken Communication Feedback</span>
              </div>
              <p className="text-xs text-slate-300 leading-relaxed bg-slate-950/80 p-3.5 rounded-xl border border-slate-800">
                "{result.englishEvaluation.feedback}"
              </p>
            </div>
          )}

          {/* Action */}
          <div className="pt-2 text-center">
            <button
              onClick={onHomeClick}
              className="px-6 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-bold rounded-xl border border-slate-700 transition-colors inline-flex items-center space-x-2"
            >
              <Home className="w-4 h-4" />
              <span>Return to Assessment Portal</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
