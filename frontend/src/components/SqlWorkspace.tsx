import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { QuestionDetail, SqlExecutionResult } from '../types';
import { Play, Send, RotateCcw, CheckCircle2, XCircle, Database, Table, AlertTriangle } from 'lucide-react';

interface SqlWorkspaceProps {
  question: QuestionDetail;
  onRun: (query: string) => Promise<SqlExecutionResult>;
  onSubmit: (query: string) => Promise<SqlExecutionResult>;
  onSave: (query: string) => void;
}

export const SqlWorkspace: React.FC<SqlWorkspaceProps> = ({
  question,
  onRun,
  onSubmit,
  onSave,
}) => {
  const [query, setQuery] = useState<string>(question.currentCode || question.starterCode || '');
  const [isRunning, setIsRunning] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [result, setResult] = useState<SqlExecutionResult | null>(null);

  useEffect(() => {
    setQuery(question.currentCode || question.starterCode || '');
    setResult(null);
  }, [question.questionId]);

  const handleEditorChange = (value: string | undefined) => {
    const newQuery = value || '';
    setQuery(newQuery);
    onSave(newQuery);
  };

  const handleResetCode = () => {
    if (window.confirm('Reset SQL query to starter template?')) {
      const reset = question.starterCode || '';
      setQuery(reset);
      onSave(reset);
    }
  };

  const handleRun = async () => {
    setIsRunning(true);
    try {
      const res = await onRun(query);
      setResult(res);
    } catch (e: any) {
      alert(e.message || 'Error running SQL query');
    } finally {
      setIsRunning(false);
    }
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    try {
      const res = await onSubmit(query);
      setResult(res);
    } catch (e: any) {
      alert(e.message || 'Error submitting SQL query');
    } finally {
      setIsSubmitting(false);
    }
  };

  let sampleOutputRows: any[] = [];
  if (question.sqlSampleOutput) {
    try {
      sampleOutputRows = JSON.parse(question.sqlSampleOutput);
    } catch (ignored) {}
  }

  return (
    <div className="flex-1 flex flex-col md:flex-row h-[calc(100vh-4rem)] overflow-hidden">
      {/* Left Panel: Problem Statement & Schema Definition */}
      <div className="w-full md:w-1/2 border-r border-slate-800 flex flex-col h-full bg-slate-900/40">
        <div className="h-11 bg-slate-900 border-b border-slate-800 px-4 flex items-center justify-between select-none">
          <div className="flex items-center space-x-2 text-xs font-semibold text-emerald-400">
            <Database className="w-4 h-4" />
            <span>Database Schema & Problem</span>
          </div>

          <div className="flex items-center space-x-2">
            <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-emerald-950 text-emerald-400 border border-emerald-800/60">
              {question.topic}
            </span>
            <span className="text-[11px] font-mono px-2 py-0.5 rounded bg-slate-800 text-slate-300">
              {question.maxScore} pts
            </span>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          <div>
            <h2 className="text-xl font-bold text-white mb-2">
              Q{question.order}. {question.title}
            </h2>
            <div className="flex items-center space-x-4 text-xs text-slate-400 mb-4 pb-4 border-b border-slate-800">
              <span>Category: <strong className="text-slate-200">SQL</strong></span>
              <span>Topic: <strong className="text-slate-200">{question.topic}</strong></span>
              <span>Difficulty: <strong className="text-slate-200">{question.difficulty}</strong></span>
            </div>
          </div>

          <div className="prose prose-invert prose-sm max-w-none text-slate-300 leading-relaxed whitespace-pre-line font-sans">
            {question.description}
          </div>

          {/* Schema Information */}
          {question.sqlSchemaDescription && (
            <div className="space-y-2">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400 flex items-center space-x-1.5">
                <Table className="w-3.5 h-3.5 text-blue-400" />
                <span>Tables & Columns</span>
              </h4>
              <div className="p-4 bg-slate-950 rounded-lg border border-slate-800 text-xs text-slate-300 prose prose-invert max-w-none">
                <div className="whitespace-pre-line font-mono">{question.sqlSchemaDescription}</div>
              </div>
            </div>
          )}

          {/* Sample Expected Output Preview */}
          {sampleOutputRows.length > 0 && (
            <div className="space-y-2">
              <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                Sample Expected Output
              </h4>
              <div className="overflow-x-auto rounded-lg border border-slate-800 bg-slate-950">
                <table className="min-w-full text-xs text-left">
                  <thead className="bg-slate-900 border-b border-slate-800 text-slate-400 font-mono">
                    <tr>
                      {Object.keys(sampleOutputRows[0]).map((col) => (
                        <th key={col} className="px-3 py-2 font-semibold">
                          {col}
                        </th>
                      ))}
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800/60 font-mono text-slate-300">
                    {sampleOutputRows.map((row, rIdx) => (
                      <tr key={rIdx} className="hover:bg-slate-900/50">
                        {Object.values(row).map((val: any, cIdx) => (
                          <td key={cIdx} className="px-3 py-1.5">
                            {val !== null ? String(val) : 'NULL'}
                          </td>
                        ))}
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Right Panel: Monaco SQL Editor + Results Table */}
      <div className="w-full md:w-1/2 flex flex-col h-full bg-slate-950">
        <div className="h-11 bg-slate-900 border-b border-slate-800 px-4 flex items-center justify-between select-none">
          <div className="flex items-center space-x-2">
            <span className="text-xs font-mono font-bold text-slate-300">PostgreSQL / ANSI SQL</span>
            <span className="text-[10px] text-slate-500">Auto-saved</span>
          </div>

          <div className="flex items-center space-x-2">
            <button
              onClick={handleResetCode}
              title="Reset query template"
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
              <span>{isRunning ? 'Executing...' : 'Run Query'}</span>
            </button>
            <button
              onClick={handleSubmit}
              disabled={isRunning || isSubmitting}
              className="px-4 py-1.5 bg-emerald-600 hover:bg-emerald-500 text-white text-xs font-semibold rounded-md shadow-md shadow-emerald-600/20 transition-all flex items-center space-x-1.5 disabled:opacity-50"
            >
              <Send className="w-3.5 h-3.5" />
              <span>{isSubmitting ? 'Evaluating...' : 'Submit Query'}</span>
            </button>
          </div>
        </div>

        {/* Monaco Editor Container */}
        <div className="flex-1 overflow-hidden relative">
          <Editor
            height="100%"
            defaultLanguage="sql"
            theme="vs-dark"
            value={query}
            onChange={handleEditorChange}
            options={{
              minimap: { enabled: false },
              fontSize: 13,
              tabSize: 2,
              fontFamily: "'Fira Code', 'Cascadia Code', Consolas, monospace",
              lineNumbers: 'on',
              scrollBeyondLastLine: false,
              automaticLayout: true,
              wordWrap: 'on',
            }}
          />
        </div>

        {/* Query Result Panel Bottom Drawer */}
        {result && (
          <div className="h-64 border-t border-slate-800 bg-slate-900/95 flex flex-col overflow-hidden">
            <div className="h-10 bg-slate-900 border-b border-slate-800 px-4 flex items-center justify-between select-none">
              <div className="flex items-center space-x-3 font-bold text-xs">
                {result.passed ? (
                  <span className="flex items-center text-emerald-400">
                    <CheckCircle2 className="w-4 h-4 mr-1" />
                    ACCEPTED - Correct Result Set
                  </span>
                ) : (
                  <span className="flex items-center text-rose-400">
                    <XCircle className="w-4 h-4 mr-1" />
                    {result.status === 'SYNTAX_ERROR' ? 'Syntax Error' : 'Wrong Answer'}
                  </span>
                )}

                <span className="text-[11px] text-slate-500 font-mono">
                  Execution Time: {result.executionTimeMs} ms
                </span>
              </div>

              {result.scoreAwarded !== undefined && (
                <div className="text-xs font-mono font-bold text-emerald-400">
                  Score: +{result.scoreAwarded} pts
                </div>
              )}
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-4">
              {result.error && (
                <div className="p-3 bg-rose-950/50 border border-rose-800 rounded text-rose-300 text-xs font-mono whitespace-pre-wrap">
                  <div className="flex items-center font-bold mb-1 text-rose-400">
                    <AlertTriangle className="w-4 h-4 mr-1.5" /> Database Error:
                  </div>
                  {result.error}
                </div>
              )}

              {result.message && !result.error && (
                <div
                  className={`p-2.5 rounded text-xs border ${
                    result.passed
                      ? 'bg-emerald-950/40 border-emerald-800/60 text-emerald-300'
                      : 'bg-amber-950/40 border-amber-800/60 text-amber-300'
                  }`}
                >
                  {result.message}
                </div>
              )}

              {/* Tabular Output: Candidate Query Rows */}
              {result.candidateColumns && result.candidateColumns.length > 0 && (
                <div className="space-y-1.5">
                  <span className="text-xs font-semibold text-slate-400">
                    Your Query Result ({result.candidateRows.length} rows):
                  </span>
                  <div className="overflow-x-auto rounded border border-slate-800 bg-slate-950">
                    <table className="min-w-full text-xs text-left">
                      <thead className="bg-slate-900 border-b border-slate-800 text-slate-400 font-mono">
                        <tr>
                          {result.candidateColumns.map((col, idx) => (
                            <th key={idx} className="px-3 py-1.5 font-semibold">
                              {col}
                            </th>
                          ))}
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-slate-800/60 font-mono text-slate-300">
                        {result.candidateRows.map((row, rIdx) => (
                          <tr key={rIdx} className="hover:bg-slate-900/50">
                            {row.map((val: any, cIdx: number) => (
                              <td key={cIdx} className="px-3 py-1">
                                {val !== null ? String(val) : 'NULL'}
                              </td>
                            ))}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
