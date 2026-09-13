import React, { useState, useEffect } from 'react';
import { CandidateStartView } from './views/CandidateStartView';
import { ExamView } from './views/ExamView';
import { ResultView } from './views/ResultView';
import { AdminView } from './views/AdminView';
import { CandidateStartResponse, FinalResult } from './types';
import { ApiClient } from './api/client';

export function App() {
  const [currentPath, setCurrentPath] = useState<string>(window.location.pathname);
  const [activeAttempt, setActiveAttempt] = useState<CandidateStartResponse | null>(null);
  const [finalResult, setFinalResult] = useState<FinalResult | null>(null);

  useEffect(() => {
    const handlePopState = () => {
      setCurrentPath(window.location.pathname);
    };
    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, []);

  const navigate = (path: string) => {
    window.history.pushState({}, '', path);
    setCurrentPath(path);
  };

  // If candidate just submitted exam
  if (finalResult) {
    return (
      <ResultView
        result={finalResult}
        onHomeClick={() => {
          setFinalResult(null);
          setActiveAttempt(null);
          navigate('/test/DEMO90');
        }}
      />
    );
  }

  // Active Assessment Exam Screen
  if (activeAttempt) {
    return (
      <ExamView
        attemptId={activeAttempt.attemptId}
        onFinish={(result) => {
          setFinalResult(result);
          navigate(`/result/${result.attemptId}`);
        }}
      />
    );
  }

  // Route 1: Admin Portal (/admin)
  if (currentPath.startsWith('/admin')) {
    return <AdminView />;
  }

  // Route 2: Specific Result View (/result/:attemptId)
  if (currentPath.startsWith('/result/')) {
    const attemptId = parseInt(currentPath.replace('/result/', ''), 10);
    if (!isNaN(attemptId)) {
      return (
        <ResultLoader
          attemptId={attemptId}
          onHome={() => navigate('/test/DEMO90')}
        />
      );
    }
  }

  // Route 3: Test Start View (/test/:code or root /)
  let testCode = 'DEMO90';
  if (currentPath.startsWith('/test/')) {
    testCode = currentPath.replace('/test/', '').trim() || 'DEMO90';
  }

  return (
    <div>
      <CandidateStartView
        testCode={testCode}
        onExamStarted={(attempt) => {
          setActiveAttempt(attempt);
          navigate(`/assessment/${attempt.attemptId}`);
        }}
      />
    </div>
  );
}

// Subcomponent to load results directly from URL
function ResultLoader({ attemptId, onHome }: { attemptId: number; onHome: () => void }) {
  const [result, setResult] = useState<FinalResult | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    ApiClient.getFinalResult(attemptId)
      .then(setResult)
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, [attemptId]);

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  if (!result) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center p-4 text-center">
        <div className="space-y-4">
          <h2 className="text-xl font-bold text-white">Results Not Found</h2>
          <button
            onClick={onHome}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg text-xs"
          >
            Back to Exam Portal
          </button>
        </div>
      </div>
    );
  }

  return <ResultView result={result} onHomeClick={onHome} />;
}

export default App;
