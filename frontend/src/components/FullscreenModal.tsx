import React from 'react';
import { Maximize2, ShieldAlert, CheckCircle } from 'lucide-react';

interface FullscreenModalProps {
  onEnterFullscreen: () => void;
}

export const FullscreenModal: React.FC<FullscreenModalProps> = ({ onEnterFullscreen }) => {
  return (
    <div className="fixed inset-0 z-50 bg-slate-950/90 backdrop-blur-md flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-2xl space-y-5 text-center">
        <div className="w-14 h-14 mx-auto rounded-full bg-blue-950/80 border border-blue-800/60 flex items-center justify-center text-blue-400">
          <Maximize2 className="w-7 h-7" />
        </div>

        <div>
          <h3 className="text-lg font-bold text-white mb-2">
            Fullscreen Required for Technical Assessment
          </h3>
          <p className="text-xs text-slate-300 leading-relaxed bg-slate-950/80 p-3 rounded-lg border border-slate-800">
            "This examination must be completed in fullscreen mode. Leaving fullscreen or switching away from the examination may terminate your attempt."
          </p>
        </div>

        <div className="text-left text-xs text-slate-400 space-y-2 bg-slate-950/50 p-3.5 rounded-lg border border-slate-800">
          <div className="flex items-center space-x-2 text-slate-300 font-semibold">
            <ShieldAlert className="w-4 h-4 text-amber-400" />
            <span>Anti-Cheat Policies:</span>
          </div>
          <p>• Tab switches and window minimizations are tracked and logged.</p>
          <p>• Navigating away will record an exam security violation.</p>
          <p>• Total duration is strictly 90 minutes across all 7 questions.</p>
        </div>

        <button
          onClick={onEnterFullscreen}
          className="w-full py-3 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white font-semibold text-sm rounded-xl shadow-lg shadow-blue-600/30 transition-all flex items-center justify-center space-x-2"
        >
          <CheckCircle className="w-4 h-4" />
          <span>Agree & Enter Fullscreen</span>
        </button>
      </div>
    </div>
  );
};
