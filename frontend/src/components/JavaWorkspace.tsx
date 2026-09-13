import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { QuestionDetail, ExecutionResult } from '../types';
import { Play, Send, RotateCcw, CheckCircle2, XCircle, AlertTriangle, Clock, Terminal, ChevronRight } from 'lucide-react';

interface JavaWorkspaceProps {
  question: QuestionDetail;
  onRun: (code: string, customInput?: string) => Promise<ExecutionResult>;
  onSubmit: (code: string) => Promise<ExecutionResult>;
  onSave: (code: string) => void;
}

export const JavaWorkspace: React.FC<JavaWorkspaceProps> = ({
  question,
  onRun,
  onSubmit,
  onSave,
}) => {
  const [code, setCode] = useState<string>(question.currentCode || question.starterCode || '');
  const [customInput, setCustomInput] = useState<string>('');
  const [activeTab, setActiveTab] = useState<'problem' | 'customInput'>('problem');
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [result, setResult] = useState<ExecutionResult | null>(null);
  const [selectedTestCaseIndex, setSelectedTestCaseIndex] = useState<number>(0);

  useEffect(() => {
    setCode(question.currentCode || question.starterCode || '');
    setResult(null);
    setSelectedTestCaseIndex(0);
  }, [question.questionId]);

  const handleEditorChange = (value: string | undefined) => {
    const newCode = value || '';
    setCode(newCode);
    onSave(newCode);
  };

  const handleResetCode = () => {
    if (window.confirm('Reset code to default template?')) {
      const reset = question.starterCode || '';
      setCode(reset);
      onSave(reset);
    }
  };

  const handleRun = async () => {
    setIsRunning(true);
    try {
      const res = await onRun(code, activeTab === 'customInput' ? customInput : undefined);
      setResult(res);
      setSelectedTestCaseIndex(0);
    } catch (e: any) {
      alert(e.message || 'Error running code');
    } finally {
      setIsRunning(false);
    }
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    try {
      const res = await onSubmit(code);
      setResult(res);
      setSelectedTestCaseIndex(0);
    } catch (e: any) {
      alert(e.message || 'Error submitting code');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex-1 flex flex-col md:flex-row h-[calc(100vh-4rem)] overflow-hidden">
      {/* Left Panel: Problem Statement & Test Cases */}
      <div className="w-full md:w-1/2 border-r border-slate-800 flex flex-col h-full bg-slate-900/40">
        {/* Header Tabs */}
        <div className="h-11 bg-slate-900 border-b border-slate-800 px-4 flex items-center justify-between select-none">
          <div className="flex space-x-2">
            <button
              onClick={() => setActiveTab('problem')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-md transition-colors ${
                activeTab === 'problem'
                  ? 'bg-slate-800 text-blue-400'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Problem Description
            </button>
            <button
              onClick={() => setActiveTab('customInput')}
              className={`px-3 py-1.5 text-xs font-semibold rounded-md transition-colors ${
                activeTab === 'customInput'
                  ? 'bg-slate-800 text-blue-400'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Custom Test Input
            </button>
          </div>

          <div className="flex items-center space-x-2">
            <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-blue-950 text-blue-400 border border-blue-800/60">
              {question.difficulty}
            </span>
            <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-slate-800 text-slate-300">
              {question.maxScore} pts
            </span>
          </div>
        </div>

        {/* Tab Content */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          {activeTab === 'problem' ? (
            <>
              <div>
                <h2 className="text-xl font-bold text-white mb-2">
                  Q{question.order}. {question.title}
                </h2>
                <div className="flex items-center space-x-4 text-xs text-slate-400 mb-4 pb-4 border-b border-slate-800">
                  <span>Topic: <strong className="text-slate-200">{question.topic}</strong></span>
                  <span>Time Limit: <strong className="text-slate-200">{question.timeLimitMs} ms</strong></span>
                  <span>Memory Limit: <strong className="text-slate-200">{question.memoryLimitMb} MB</strong></span>
                </div>
              </div>

              {/* Statement */}
              <div className="prose prose-invert prose-sm max-w-none text-slate-300 leading-relaxed whitespace-pre-line font-sans">
                {question.description}
              </div>

              {/* Input / Output Formats */}
              {question.inputFormat && (
                <div className="space-y-2">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                    Input Format
                  </h4>
                  <pre className="p-3 bg-slate-950 rounded-lg border border-slate-800 text-xs font-mono text-slate-300 whitespace-pre-line">
                    {question.inputFormat}
                  </pre>
                </div>
              )}

              {question.outputFormat && (
                <div className="space-y-2">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                    Output Format
                  </h4>
                  <pre className="p-3 bg-slate-950 rounded-lg border border-slate-800 text-xs font-mono text-slate-300 whitespace-pre-line">
                    {question.outputFormat}
                  </pre>
                </div>
              )}

              {/* Constraints */}
              {question.constraints && (
                <div className="space-y-2">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                    Constraints
                  </h4>
                  <pre className="p-3 bg-slate-950 rounded-lg border border-slate-800 text-xs font-mono text-amber-300/80 whitespace-pre-line">
                    {question.constraints}
                  </pre>
                </div>
              )}

              {/* Examples */}
              {question.sampleTestCases && question.sampleTestCases.length > 0 && (
                <div className="space-y-3 pt-2">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                    Examples / Sample Test Cases
                  </h4>
                  {question.sampleTestCases.map((tc, idx) => (
                    <div
                      key={tc.id || idx}
                      className="p-3.5 bg-slate-950 rounded-lg border border-slate-800 space-y-2"
                    >
                      <div className="text-xs font-semibold text-blue-400">
                        Example {idx + 1}
                      </div>
                      <div>
                        <span className="text-[11px] text-slate-500 uppercase font-mono">Input:</span>
                        <pre className="p-2 bg-slate-900 rounded text-xs font-mono text-slate-200 mt-1">
                          {tc.inputData}
                        </pre>
                      </div>
                      <div>
                        <span className="text-[11px] text-slate-500 uppercase font-mono">Expected Output:</span>
                        <pre className="p-2 bg-slate-900 rounded text-xs font-mono text-emerald-400 mt-1">
                          {tc.expectedOutput}
                        </pre>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </>
          ) : (
            <div className="space-y-3">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                Custom Test Input
              </h4>
              <p className="text-xs text-slate-400">
                Provide custom input matching the question format to test your solution using the "Run Code" button.
              </p>
              <textarea
                value={customInput}
                onChange={(e) => setCustomInput(e.target.value)}
                placeholder="Enter input here..."
                rows={10}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg p-3 font-mono text-xs text-slate-200 focus:outline-none focus:border-blue-500"
              />
            </div>
          )}
        </div>
      </div>

      {/* Right Panel: Monaco Code Editor + Output Panel */}
      <div className="w-full md:w-1/2 flex flex-col h-full bg-slate-950">
        {/* Editor Controls Bar */}
        <div className="h-11 bg-slate-900 border-b border-slate-800 px-4 flex items-center justify-between select-none">
          <div className="flex items-center space-x-2">
            <span className="text-xs font-mono font-bold text-slate-300">Java 21</span>
            <span className="text-[10px] text-slate-500">Auto-saved</span>
          </div>

          <div className="flex items-center space-x-2">
            <button
              onClick={handleResetCode}
              title="Reset starter template"
              className="p-1.5 text-slate-400 hover:text-slate-200 rounded hover:bg-slate-800 transition-colors"
            >
              <RotateCcw className="w-3.5 h-3.5" />
            </button>
            <button
              onClick={handleRun}
              disabled={isRunning || isSubmitting}
              className="px-3.5 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 hover:text-white text-xs font-semibold rounded-md border border-slate-700 transition-all flex items-center space-x-1.5 disabled:opacity-50"
            >
              <Play className="w-3.5 h-3.5 text-emerald-400" />
              <span>{isRunning ? 'Running...' : 'Run Code'}</span>
            </button>
            <button
              onClick={handleSubmit}
              disabled={isRunning || isSubmitting}
              className="px-4 py-1.5 bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold rounded-md shadow-md shadow-blue-600/20 transition-all flex items-center space-x-1.5 disabled:opacity-50"
            >
              <Send className="w-3.5 h-3.5" />
              <span>{isSubmitting ? 'Evaluating...' : 'Submit Code'}</span>
            </button>
          </div>
        </div>

        {/* Monaco Editor Container */}
        <div className="flex-1 overflow-hidden relative">
          <Editor
            height="100%"
            defaultLanguage="java"
            theme="vs-dark"
            value={code}
            onChange={handleEditorChange}
            options={{
              minimap: { enabled: false },
              fontSize: 13,
              tabSize: 4,
              fontFamily: "'Fira Code', 'Cascadia Code', Consolas, monospace",
              lineNumbers: 'on',
              scrollBeyondLastLine: false,
              automaticLayout: true,
              wordWrap: 'on',
            }}
          />
        </div>

        {/* Test Execution Output Bottom Drawer */}
        {result && (
          <div className="h-64 border-t border-slate-800 bg-slate-900/95 flex flex-col overflow-hidden">
            {/* Output Bar Header */}
            <div className="h-10 bg-slate-900 border-b border-slate-800 px-4 flex items-center justify-between select-none">
              <div className="flex items-center space-x-3">
                <div className="flex items-center space-x-1.5 font-bold text-xs">
                  {result.passed ? (
                    <span className="flex items-center text-emerald-400">
                      <CheckCircle2 className="w-4 h-4 mr-1" />
                      ACCEPTED
                    </span>
                  ) : result.status === 'COMPILATION_ERROR' ? (
                    <span className="flex items-center text-rose-400">
                      <XCircle className="w-4 h-4 mr-1" />
                      Compilation Error
                    </span>
                  ) : result.status === 'TIME_LIMIT_EXCEEDED' ? (
                    <span className="flex items-center text-amber-400">
                      <Clock className="w-4 h-4 mr-1" />
                      Time Limit Exceeded
                    </span>
                  ) : (
                    <span className="flex items-center text-rose-400">
                      <XCircle className="w-4 h-4 mr-1" />
                      {result.status || 'Failed'}
                    </span>
                  )}
                </div>

                <span className="text-[11px] text-slate-400 font-mono">
                  {result.passedTestCases} / {result.totalTestCases} Tests Passed
                </span>

                <span className="text-[11px] text-slate-500 font-mono">
                  Runtime: {result.executionTimeMs} ms
                </span>
              </div>

              {result.scoreAwarded !== undefined && (
                <div className="text-xs font-mono font-bold text-emerald-400">
                  Score: +{result.scoreAwarded} pts
                </div>
              )}
            </div>

            {/* Output Body */}
            <div className="flex-1 overflow-y-auto p-4 font-mono text-xs">
              {result.error && (
                <div className="p-3 bg-rose-950/50 border border-rose-800 rounded text-rose-300 whitespace-pre-wrap mb-3">
                  <div className="flex items-center font-bold mb-1 text-rose-400">
                    <AlertTriangle className="w-4 h-4 mr-1.5" /> Error Log:
                  </div>
                  {result.error}
                </div>
              )}

              {/* Test Cases Tabs if available */}
              {result.testCaseResults && result.testCaseResults.length > 0 ? (
                <div className="space-y-3">
                  <div className="flex space-x-2 border-b border-slate-800 pb-2">
                    {result.testCaseResults.map((tc, idx) => (
                      <button
                        key={idx}
                        onClick={() => setSelectedTestCaseIndex(idx)}
                        className={`px-3 py-1 text-xs rounded font-medium flex items-center space-x-1.5 ${
                          selectedTestCaseIndex === idx
                            ? 'bg-slate-800 text-white border border-slate-700'
                            : 'text-slate-400 hover:text-slate-200'
                        }`}
                      >
                        {tc.passed ? (
                          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                        ) : (
                          <XCircle className="w-3.5 h-3.5 text-rose-400" />
                        )}
                        <span>Test {idx + 1} {tc.isHidden ? '(Hidden)' : ''}</span>
                      </button>
                    ))}
                  </div>

                  {result.testCaseResults[selectedTestCaseIndex] && (
                    <div className="space-y-2 bg-slate-950 p-3 rounded-lg border border-slate-800 text-xs">
                      <div>
                        <span className="text-slate-500 font-bold">Input:</span>
                        <pre className="p-2 bg-slate-900 rounded text-slate-200 mt-1">
                          {result.testCaseResults[selectedTestCaseIndex].input}
                        </pre>
                      </div>
                      <div>
                        <span className="text-slate-500 font-bold">Expected Output:</span>
                        <pre className="p-2 bg-slate-900 rounded text-emerald-400 mt-1">
                          {result.testCaseResults[selectedTestCaseIndex].expectedOutput}
                        </pre>
                      </div>
                      <div>
                        <span className="text-slate-500 font-bold">Your Output:</span>
                        <pre className={`p-2 bg-slate-900 rounded mt-1 ${
                          result.testCaseResults[selectedTestCaseIndex].passed
                            ? 'text-emerald-400'
                            : 'text-rose-400'
                        }`}>
                          {result.testCaseResults[selectedTestCaseIndex].actualOutput || '(No stdout)'}
                        </pre>
                      </div>
                    </div>
                  )}
                </div>
              ) : (
                result.output && (
                  <div>
                    <span className="text-slate-500 font-bold">Program Output:</span>
                    <pre className="p-2.5 bg-slate-950 rounded border border-slate-800 text-slate-200 mt-1">
                      {result.output}
                    </pre>
                  </div>
                )
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
