import React, { useState, useEffect } from 'react';
import { ApiClient } from '../api/client';
import { AssessmentInfo, CandidateStartResponse } from '../types';
import { FullscreenModal } from '../components/FullscreenModal';
import { Code2, Database, Mic, Clock, ShieldCheck, CheckCircle, ArrowRight, AlertCircle } from 'lucide-react';

interface CandidateStartViewProps {
  testCode: string;
  onExamStarted: (attempt: CandidateStartResponse) => void;
}

export const CandidateStartView: React.FC<CandidateStartViewProps> = ({
  testCode,
  onExamStarted,
}) => {
  const [assessment, setAssessment] = useState<AssessmentInfo | null>(null);
  const [candidateName, setCandidateName] = useState('');
  const [candidateEmail, setCandidateEmail] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [showFullscreenModal, setShowFullscreenModal] = useState(false);

  useEffect(() => {
    loadAssessmentInfo();
  }, [testCode]);

  const loadAssessmentInfo = async () => {
    try {
      setLoading(true);
      const info = await ApiClient.getAssessmentInfo(testCode);
      setAssessment(info);
    } catch (err: any) {
      setError(err.message || 'Assessment not found or inactive.');
    } finally {
      setLoading(false);
    }
  };

  const handleStartClick = (e: React.FormEvent) => {
    e.preventDefault();
    if (!candidateName.trim() || !candidateEmail.trim()) {
      alert('Please enter your full name and email address.');
      return;
    }
    setShowFullscreenModal(true);
  };

  const handleEnterFullscreenAndStart = async () => {
    setShowFullscreenModal(false);
    setSubmitting(true);
    try {
      // Request Fullscreen
      if (document.documentElement.requestFullscreen) {
        await document.documentElement.requestFullscreen().catch(() => {});
      }

      const response = await ApiClient.startExam(candidateName.trim(), candidateEmail.trim(), testCode);
      onExamStarted(response);
    } catch (err: any) {
      alert(err.message || 'Failed to start examination.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <div className="text-center space-y-3">
          <div className="w-10 h-10 border-2 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="text-xs text-slate-400 font-medium">Loading Assessment Details...</p>
        </div>
      </div>
    );
  }

  if (error || !assessment) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center p-4">
        <div className="max-w-md w-full bg-slate-900 border border-slate-800 rounded-2xl p-8 text-center space-y-4">
          <AlertCircle className="w-12 h-12 text-rose-500 mx-auto" />
          <h2 className="text-xl font-bold text-white">Assessment Unavailable</h2>
          <p className="text-sm text-slate-400">{error || 'Please verify the exam link.'}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col items-center justify-center p-4 sm:p-6">
      <div className="max-w-3xl w-full bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl overflow-hidden">
        {/* Banner */}
        <div className="bg-gradient-to-r from-blue-900/60 via-indigo-900/40 to-slate-900 p-8 border-b border-slate-800">
          <div className="flex items-center space-x-2 text-xs font-bold text-blue-400 uppercase tracking-widest mb-2">
            <ShieldCheck className="w-4 h-4" />
            <span>Official Technical Screening</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold text-white mb-2">
            {assessment.title}
          </h1>
          <p className="text-xs sm:text-sm text-slate-300 leading-relaxed max-w-2xl">
            {assessment.description}
          </p>
        </div>

        <div className="p-6 sm:p-8 space-y-8">
          {/* Assessment Specifications */}
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
            <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 flex items-center space-x-3">
              <div className="p-2 rounded-lg bg-blue-950/80 text-blue-400">
                <Clock className="w-5 h-5" />
              </div>
              <div>
                <span className="text-[11px] text-slate-500 font-bold block">DURATION</span>
                <span className="text-sm font-bold text-slate-200">90 Minutes</span>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 flex items-center space-x-3">
              <div className="p-2 rounded-lg bg-emerald-950/80 text-emerald-400">
                <Code2 className="w-5 h-5" />
              </div>
              <div>
                <span className="text-[11px] text-slate-500 font-bold block">JAVA SECTION</span>
                <span className="text-sm font-bold text-slate-200">3 Questions</span>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 flex items-center space-x-3">
              <div className="p-2 rounded-lg bg-amber-950/80 text-amber-400">
                <Database className="w-5 h-5" />
              </div>
              <div>
                <span className="text-[11px] text-slate-500 font-bold block">SQL SECTION</span>
                <span className="text-sm font-bold text-slate-200">3 Questions</span>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 flex items-center space-x-3">
              <div className="p-2 rounded-lg bg-purple-950/80 text-purple-400">
                <Mic className="w-5 h-5" />
              </div>
              <div>
                <span className="text-[11px] text-slate-500 font-bold block">ENGLISH</span>
                <span className="text-sm font-bold text-slate-200">1 Question</span>
              </div>
            </div>
          </div>

          {/* Form */}
          <form onSubmit={handleStartClick} className="space-y-6 pt-2">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-300">Full Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Jane Doe"
                  value={candidateName}
                  onChange={(e) => setCandidateName(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-sm text-slate-200 focus:outline-none focus:border-blue-500 transition-colors"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-semibold text-slate-300">Email Address</label>
                <input
                  type="email"
                  required
                  placeholder="e.g. jane.doe@example.com"
                  value={candidateEmail}
                  onChange={(e) => setCandidateEmail(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-3 text-sm text-slate-200 focus:outline-none focus:border-blue-500 transition-colors"
                />
              </div>
            </div>

            <div className="p-4 rounded-xl bg-slate-950 border border-slate-800 text-xs text-slate-400 space-y-2">
              <h4 className="font-bold text-slate-300 uppercase tracking-wider">Candidate Instructions:</h4>
              <p>• The exam is timed at exactly 90 minutes. When the timer expires, all answers will be automatically submitted.</p>
              <p>• You may freely navigate between questions during the assessment.</p>
              <p>• Fullscreen mode is strictly enforced. Leaving fullscreen or switching tabs will be logged as an assessment violation.</p>
            </div>

            <button
              type="submit"
              disabled={submitting}
              className="w-full py-4 bg-gradient-to-r from-blue-600 via-indigo-600 to-blue-500 hover:from-blue-500 hover:to-indigo-500 text-white font-bold text-sm rounded-xl shadow-xl shadow-blue-600/25 transition-all flex items-center justify-center space-x-2 active:scale-98 disabled:opacity-50"
            >
              <span>{submitting ? 'Preparing Exam...' : 'Proceed to Fullscreen & Start Exam'}</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </form>
        </div>
      </div>

      {showFullscreenModal && (
        <FullscreenModal onEnterFullscreen={handleEnterFullscreenAndStart} />
      )}
    </div>
  );
};
