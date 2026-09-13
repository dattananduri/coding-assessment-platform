import React from 'react';
import { QuestionSummary } from '../types';
import { Code2, Database, Mic, Check, AlertCircle, Circle, Clock } from 'lucide-react';

interface QuestionSidebarProps {
  questions: QuestionSummary[];
  currentOrder: number;
  onSelectQuestion: (order: number) => void;
}

export const QuestionSidebar: React.FC<QuestionSidebarProps> = ({
  questions,
  currentOrder,
  onSelectQuestion,
}) => {
  const javaQuestions = questions.filter((q) => q.category === 'JAVA');
  const sqlQuestions = questions.filter((q) => q.category === 'SQL');
  const englishQuestions = questions.filter((q) => q.category === 'ENGLISH');

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'PASSED':
        return (
          <span className="flex items-center text-[10px] font-semibold text-emerald-400 bg-emerald-950/60 border border-emerald-800/60 px-2 py-0.5 rounded-full">
            <Check className="w-2.5 h-2.5 mr-1" /> Passed
          </span>
        );
      case 'FAILED':
        return (
          <span className="flex items-center text-[10px] font-semibold text-rose-400 bg-rose-950/60 border border-rose-800/60 px-2 py-0.5 rounded-full">
            <AlertCircle className="w-2.5 h-2.5 mr-1" /> Failed
          </span>
        );
      case 'SUBMITTED':
        return (
          <span className="flex items-center text-[10px] font-semibold text-blue-400 bg-blue-950/60 border border-blue-800/60 px-2 py-0.5 rounded-full">
            <Check className="w-2.5 h-2.5 mr-1" /> Submitted
          </span>
        );
      case 'IN_PROGRESS':
        return (
          <span className="flex items-center text-[10px] font-semibold text-amber-400 bg-amber-950/60 border border-amber-800/60 px-2 py-0.5 rounded-full">
            <Clock className="w-2.5 h-2.5 mr-1 animate-spin" /> In Progress
          </span>
        );
      default:
        return (
          <span className="flex items-center text-[10px] font-semibold text-slate-400 bg-slate-800/60 border border-slate-700/60 px-2 py-0.5 rounded-full">
            <Circle className="w-2.5 h-2.5 mr-1" /> Not Attempted
          </span>
        );
    }
  };

  const renderQuestionList = (items: QuestionSummary[]) => {
    return items.map((q) => {
      const isSelected = q.order === currentOrder;
      return (
        <button
          key={q.order}
          onClick={() => onSelectQuestion(q.order)}
          className={`w-full text-left p-3 rounded-lg transition-all duration-200 border mb-2 flex flex-col justify-between ${
            isSelected
              ? 'bg-blue-600/15 border-blue-500 shadow-md shadow-blue-500/10'
              : 'bg-slate-900/60 hover:bg-slate-800/80 border-slate-800/80'
          }`}
        >
          <div className="flex items-center justify-between w-full mb-1.5">
            <div className="flex items-center space-x-2">
              <span
                className={`font-mono text-xs font-bold px-1.5 py-0.5 rounded ${
                  isSelected ? 'bg-blue-500 text-white' : 'bg-slate-800 text-slate-300'
                }`}
              >
                Q{q.order}
              </span>
              <span className="text-xs text-slate-300 font-medium truncate max-w-[120px]">
                {q.title}
              </span>
            </div>
            <span className="text-[11px] font-mono font-medium text-slate-400">
              {q.scoreAwarded > 0 ? (
                <span className="text-emerald-400 font-bold">{q.scoreAwarded}</span>
              ) : (
                '0'
              )}
              /{q.maxScore} pts
            </span>
          </div>

          <div className="flex items-center justify-between w-full mt-1">
            <span className="text-[10px] text-slate-500 uppercase tracking-wider font-semibold">
              {q.topic}
            </span>
            {getStatusBadge(q.status)}
          </div>
        </button>
      );
    });
  };

  return (
    <aside className="w-72 bg-slate-950 border-r border-slate-800 flex flex-col h-[calc(100vh-4rem)] select-none">
      <div className="p-4 border-b border-slate-800/80">
        <h2 className="text-xs font-bold uppercase tracking-wider text-slate-400">
          Exam Navigation
        </h2>
        <p className="text-[11px] text-slate-500 mt-0.5">
          7 Questions • 90 Minutes Total
        </p>
      </div>

      <div className="flex-1 overflow-y-auto p-3 space-y-5">
        {/* Java Section */}
        <div>
          <div className="flex items-center space-x-2 mb-2 px-1 text-blue-400">
            <Code2 className="w-4 h-4" />
            <h3 className="text-xs font-bold tracking-wider uppercase">
              Java Coding (3)
            </h3>
          </div>
          {renderQuestionList(javaQuestions)}
        </div>

        {/* SQL Section */}
        <div>
          <div className="flex items-center space-x-2 mb-2 px-1 text-emerald-400">
            <Database className="w-4 h-4" />
            <h3 className="text-xs font-bold tracking-wider uppercase">
              SQL Database (3)
            </h3>
          </div>
          {renderQuestionList(sqlQuestions)}
        </div>

        {/* English Section */}
        <div>
          <div className="flex items-center space-x-2 mb-2 px-1 text-purple-400">
            <Mic className="w-4 h-4" />
            <h3 className="text-xs font-bold tracking-wider uppercase">
              English Speaking (1)
            </h3>
          </div>
          {renderQuestionList(englishQuestions)}
        </div>
      </div>

      <div className="p-3 bg-slate-900/60 border-t border-slate-800 text-center">
        <div className="text-xs text-slate-400">
          Total Score:{' '}
          <span className="font-bold text-white font-mono">
            {questions.reduce((acc, q) => acc + q.scoreAwarded, 0)} / 100
          </span>
        </div>
      </div>
    </aside>
  );
};
