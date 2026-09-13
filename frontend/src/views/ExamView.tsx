import React, { useState, useEffect, useRef, useCallback } from 'react';
import { ApiClient } from '../api/client';
import { AttemptState, QuestionDetail, FinalResult } from '../types';
import { Navbar } from '../components/Navbar';
import { QuestionSidebar } from '../components/QuestionSidebar';
import { JavaWorkspace } from '../components/JavaWorkspace';
import { SqlWorkspace } from '../components/SqlWorkspace';
import { EnglishWorkspace } from '../components/EnglishWorkspace';
import { ViolationModal } from '../components/ViolationModal';

interface ExamViewProps {
  attemptId: number;
  onFinish: (result: FinalResult) => void;
}

export const ExamView: React.FC<ExamViewProps> = ({ attemptId, onFinish }) => {
  const [state, setState] = useState<AttemptState | null>(null);
  const [currentOrder, setCurrentOrder] = useState<number>(1);
  const [currentQuestion, setCurrentQuestion] = useState<QuestionDetail | null>(null);
  const [remainingSeconds, setRemainingSeconds] = useState<number>(5400); // 90 mins default
  const [loadingQuestion, setLoadingQuestion] = useState<boolean>(true);
  const [isFullscreen, setIsFullscreen] = useState<boolean>(true);
  const [violationAlert, setViolationAlert] = useState<{ type: string; terminated: boolean } | null>(null);
  const [showSubmitConfirm, setShowSubmitConfirm] = useState<boolean>(false);
  const [violationsCount, setViolationsCount] = useState<number>(0);

  const timerRef = useRef<any>(null);
  const syncTimerRef = useRef<any>(null);

  // 1. Initial State Fetch
  const loadState = useCallback(async () => {
    try {
      const data = await ApiClient.getAttemptState(attemptId);
      setState(data);
      setRemainingSeconds(data.remainingSeconds);

      if (data.status === 'SUBMITTED' || data.status === 'TERMINATED_VIOLATION') {
        const finalRes = await ApiClient.getFinalResult(attemptId);
        onFinish(finalRes);
      }
    } catch (err) {
      console.error('Failed to load state', err);
    }
  }, [attemptId, onFinish]);

  useEffect(() => {
    loadState();
  }, [loadState]);

  // 2. Load Question Details on Order Change
  useEffect(() => {
    let isMounted = true;
    const loadQuestion = async () => {
      setLoadingQuestion(true);
      try {
        const q = await ApiClient.getQuestionDetail(attemptId, currentOrder);
        if (isMounted) {
          setCurrentQuestion(q);
        }
      } catch (err) {
        console.error('Error loading question detail', err);
      } finally {
        if (isMounted) setLoadingQuestion(false);
      }
    };
    loadQuestion();
    return () => { isMounted = false; };
  }, [attemptId, currentOrder]);

  // 3. Countdown Timer (Persistent 90 Mins)
  useEffect(() => {
    timerRef.current = setInterval(() => {
      setRemainingSeconds((prev) => {
        if (prev <= 1) {
          clearInterval(timerRef.current);
          handleAutoSubmit();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    // Sync state with server every 30 seconds
    syncTimerRef.current = setInterval(() => {
      loadState();
    }, 30000);

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);
      if (syncTimerRef.current) clearInterval(syncTimerRef.current);
    };
  }, [loadState]);

  const handleAutoSubmit = async () => {
    try {
      const res = await ApiClient.finalSubmit(attemptId);
      onFinish(res);
    } catch (err) {
      console.error('Auto submit failed', err);
    }
  };

  // 4. Fullscreen & Anti-Cheat Violation Listeners
  const reportViolation = useCallback(async (type: string, details: string) => {
    try {
      setViolationsCount((v) => v + 1);
      const isCritical = violationsCount >= 2; // terminate on repeated exit
      await ApiClient.reportViolation(attemptId, type, details, isCritical);
      setViolationAlert({ type, terminated: isCritical });
    } catch (e) {
      console.error('Failed to log violation', e);
    }
  }, [attemptId, violationsCount]);

  useEffect(() => {
    const handleFullscreenChange = () => {
      const active = !!document.fullscreenElement;
      setIsFullscreen(active);
      if (!active) {
        reportViolation('FULLSCREEN_EXIT', 'Candidate exited fullscreen mode.');
      }
    };

    const handleVisibilityChange = () => {
      if (document.hidden) {
        reportViolation('TAB_HIDDEN', 'Candidate switched browser tab or minimized window.');
      }
    };

    const handleBlur = () => {
      reportViolation('WINDOW_BLUR', 'Candidate window lost focus.');
    };

    const handleBeforeUnload = (e: BeforeUnloadEvent) => {
      e.preventDefault();
      e.returnValue = 'You are currently in an active examination attempt. Leaving will submit your answers.';
    };

    document.addEventListener('fullscreenchange', handleFullscreenChange);
    document.addEventListener('visibilitychange', handleVisibilityChange);
    window.addEventListener('blur', handleBlur);
    window.addEventListener('beforeunload', handleBeforeUnload);

    return () => {
      document.removeEventListener('fullscreenchange', handleFullscreenChange);
      document.removeEventListener('visibilitychange', handleVisibilityChange);
      window.removeEventListener('blur', handleBlur);
      window.removeEventListener('beforeunload', handleBeforeUnload);
    };
  }, [reportViolation]);

  const handleAcknowledgeViolation = async () => {
    if (violationAlert?.terminated) {
      handleAutoSubmit();
    } else {
      setViolationAlert(null);
      if (document.documentElement.requestFullscreen) {
        await document.documentElement.requestFullscreen().catch(() => {});
      }
    }
  };

  // Handlers for Java
  const handleJavaRun = async (code: string, customInput?: string) => {
    const res = await ApiClient.runJava(attemptId, currentOrder, code, customInput);
    loadState();
    return res;
  };

  const handleJavaSubmit = async (code: string) => {
    const res = await ApiClient.submitJava(attemptId, currentOrder, code);
    loadState();
    return res;
  };

  // Handlers for SQL
  const handleSqlRun = async (query: string) => {
    const res = await ApiClient.runSql(attemptId, currentOrder, query);
    loadState();
    return res;
  };

  const handleSqlSubmit = async (query: string) => {
    const res = await ApiClient.submitSql(attemptId, currentOrder, query);
    loadState();
    return res;
  };

  // Handler for English
  const handleEnglishSubmit = async (audioBlob: Blob | null, transcript: string, durationSeconds: number) => {
    const res = await ApiClient.submitEnglish(attemptId, audioBlob, transcript, durationSeconds);
    loadState();
    return res;
  };

  const handleSaveCode = (code: string) => {
    ApiClient.saveCode(attemptId, currentOrder, code);
  };

  const handleConfirmFinalSubmit = async () => {
    setShowSubmitConfirm(false);
    try {
      const res = await ApiClient.finalSubmit(attemptId);
      onFinish(res);
    } catch (err: any) {
      alert(err.message || 'Submission failed');
    }
  };

  if (!state) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <div className="text-center space-y-3">
          <div className="w-10 h-10 border-2 border-blue-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="text-xs text-slate-400 font-medium">Initializing Examination Session...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 flex flex-col select-none overflow-hidden">
      {/* Header */}
      <Navbar
        title={state.assessmentTitle}
        candidateName={state.candidateName}
        candidateEmail={state.candidateEmail}
        remainingSeconds={remainingSeconds}
        violationsCount={violationsCount}
        isFullscreen={isFullscreen}
        onFinalSubmit={() => setShowSubmitConfirm(true)}
      />

      {/* Main Examination View */}
      <div className="flex-1 flex overflow-hidden">
        {/* Left Navigation Sidebar */}
        <QuestionSidebar
          questions={state.questions}
          currentOrder={currentOrder}
          onSelectQuestion={(order) => setCurrentOrder(order)}
        />

        {/* Center Workspace */}
        {loadingQuestion || !currentQuestion ? (
          <div className="flex-1 flex items-center justify-center bg-slate-900/20">
            <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
          </div>
        ) : currentQuestion.category === 'JAVA' ? (
          <JavaWorkspace
            key={currentQuestion.questionId}
            question={currentQuestion}
            onRun={handleJavaRun}
            onSubmit={handleJavaSubmit}
            onSave={handleSaveCode}
          />
        ) : currentQuestion.category === 'SQL' ? (
          <SqlWorkspace
            key={currentQuestion.questionId}
            question={currentQuestion}
            onRun={handleSqlRun}
            onSubmit={handleSqlSubmit}
            onSave={handleSaveCode}
          />
        ) : (
          <EnglishWorkspace
            key={currentQuestion.questionId}
            question={currentQuestion}
            onSubmit={handleEnglishSubmit}
          />
        )}
      </div>

      {/* Violation Popup */}
      {violationAlert && (
        <ViolationModal
          violationType={violationAlert.type}
          onAcknowledge={handleAcknowledgeViolation}
          isTerminated={violationAlert.terminated}
        />
      )}

      {/* Final Submit Confirmation Modal */}
      {showSubmitConfirm && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="max-w-md w-full bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-2xl space-y-4">
            <h3 className="text-lg font-bold text-white">Final Exam Submission</h3>
            <p className="text-xs text-slate-300 leading-relaxed">
              Are you sure you want to conclude and submit your entire assessment? Once submitted, your code and recordings cannot be modified.
            </p>

            <div className="p-3 bg-slate-950 rounded-lg border border-slate-800 text-xs space-y-1">
              <div className="flex justify-between text-slate-400">
                <span>Attempted Questions:</span>
                <span className="font-bold text-white">
                  {state.questions.filter((q) => q.status !== 'NOT_ATTEMPTED').length} / 7
                </span>
              </div>
              <div className="flex justify-between text-slate-400">
                <span>Time Remaining:</span>
                <span className="font-bold text-emerald-400 font-mono">
                  {Math.floor(remainingSeconds / 60)}m {remainingSeconds % 60}s
                </span>
              </div>
            </div>

            <div className="flex space-x-3 pt-2">
              <button
                onClick={() => setShowSubmitConfirm(false)}
                className="flex-1 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold rounded-xl transition-colors"
              >
                Continue Assessment
              </button>
              <button
                onClick={handleConfirmFinalSubmit}
                className="flex-1 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-bold rounded-xl shadow-lg shadow-emerald-600/30 transition-all"
              >
                Submit Now
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
