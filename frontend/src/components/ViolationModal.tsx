import React from 'react';
import { AlertOctagon, ArrowRight } from 'lucide-react';

interface ViolationModalProps {
  violationType: string;
  onAcknowledge: () => void;
  isTerminated?: boolean;
}

export const ViolationModal: React.FC<ViolationModalProps> = ({
  violationType,
  onAcknowledge,
  isTerminated = false,
}) => {
  return (
    <div className="fixed inset-0 z-50 bg-slate-950/95 backdrop-blur-md flex items-center justify-center p-4 select-none">
      <div className="max-w-md w-full bg-slate-900 border border-rose-800/80 rounded-2xl p-6 shadow-2xl space-y-5 text-center">
        <div className="w-14 h-14 mx-auto rounded-full bg-rose-950/80 border border-rose-800 flex items-center justify-center text-rose-500 animate-pulse">
          <AlertOctagon className="w-8 h-8" />
        </div>

        <div>
          <h3 className="text-lg font-bold text-rose-400 mb-2">
            Exam Violation Detected
          </h3>
          <p className="text-xs text-rose-200 leading-relaxed bg-rose-950/40 p-3 rounded-lg border border-rose-900/60 font-medium">
            "Exam violation detected. Leaving the examination environment is not allowed. The exam will be terminated."
          </p>
        </div>

        <div className="p-3 bg-slate-950 rounded-lg border border-slate-800 text-xs text-left font-mono space-y-1">
          <div className="text-slate-400">
            Violation Type: <span className="text-rose-400 font-bold">{violationType}</span>
          </div>
          <div className="text-slate-500">
            Timestamp: <span>{new Date().toLocaleTimeString()}</span>
          </div>
        </div>

        <button
          onClick={onAcknowledge}
          className="w-full py-3 bg-rose-600 hover:bg-rose-500 text-white font-semibold text-xs rounded-xl shadow-lg shadow-rose-600/30 transition-all flex items-center justify-center space-x-2"
        >
          <span>{isTerminated ? 'Proceed to Final Submission' : 'Acknowledge & Re-enter Fullscreen'}</span>
          <ArrowRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
