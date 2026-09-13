import React, { useState, useEffect } from 'react';
import { ApiClient } from '../api/client';
import { AdminAttemptDetail } from '../types';
import {
  Users,
  FileQuestion,
  ShieldAlert,
  Download,
  Plus,
  Trash2,
  ExternalLink,
  CheckCircle2,
  XCircle,
  Copy,
  Lock,
  LogOut,
  Play,
  Volume2,
  Sparkles,
  Search,
} from 'lucide-react';

export const AdminView: React.FC = () => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(
    !!localStorage.getItem('admin_token')
  );
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('admin123');
  const [loginError, setLoginError] = useState('');

  const [activeTab, setActiveTab] = useState<'attempts' | 'questions' | 'assessments'>('attempts');
  const [attempts, setAttempts] = useState<any[]>([]);
  const [questions, setQuestions] = useState<any[]>([]);
  const [assessments, setAssessments] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);

  // Attempt Detail Modal
  const [selectedAttemptDetail, setSelectedAttemptDetail] = useState<AdminAttemptDetail | null>(null);

  // New Assessment Form
  const [newAssessTitle, setNewAssessTitle] = useState('');
  const [newAssessCode, setNewAssessCode] = useState('');
  const [newAssessDesc, setNewAssessDesc] = useState('');
  const [newAssessDuration, setNewAssessDuration] = useState(90);

  // New Question Form
  const [showNewQuestionModal, setShowNewQuestionModal] = useState(false);
  const [qCategory, setQCategory] = useState<'JAVA' | 'SQL' | 'ENGLISH'>('JAVA');
  const [qTopic, setQTopic] = useState('ARRAYS');
  const [qTitle, setQTitle] = useState('');
  const [qDesc, setQDesc] = useState('');
  const [qStarter, setQStarter] = useState('');
  const [qMaxScore, setQMaxScore] = useState(10);
  // Java Test Case
  const [tcInput, setTcInput] = useState('');
  const [tcExpected, setTcExpected] = useState('');
  const [tcHidden, setTcHidden] = useState(false);
  // SQL Dataset
  const [sqlSchemaDdl, setSqlSchemaDdl] = useState('');
  const [sqlSeedData, setSqlSeedData] = useState('');
  const [sqlRefQuery, setSqlRefQuery] = useState('');

  useEffect(() => {
    if (isAuthenticated) {
      loadData();
    }
  }, [isAuthenticated, activeTab]);

  const loadData = async () => {
    setLoading(true);
    try {
      if (activeTab === 'attempts') {
        const data = await ApiClient.adminGetAttempts();
        setAttempts(data);
      } else if (activeTab === 'questions') {
        const data = await ApiClient.adminGetQuestions();
        setQuestions(data);
      } else if (activeTab === 'assessments') {
        const data = await ApiClient.adminGetAssessments();
        setAssessments(data);
      }
    } catch (err: any) {
      console.error('Error loading data', err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoginError('');
    try {
      await ApiClient.adminLogin(username, password);
      setIsAuthenticated(true);
    } catch (err: any) {
      setLoginError(err.message || 'Login failed');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('admin_token');
    setIsAuthenticated(false);
  };

  const handleCreateAssessment = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await ApiClient.adminCreateAssessment({
        title: newAssessTitle,
        code: newAssessCode.toUpperCase(),
        description: newAssessDesc,
        durationMinutes: newAssessDuration,
      });
      alert('Assessment created successfully!');
      setNewAssessTitle('');
      setNewAssessCode('');
      setNewAssessDesc('');
      loadData();
    } catch (err: any) {
      alert(err.message || 'Failed to create assessment.');
    }
  };

  const handleCreateQuestion = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload: any = {
        category: qCategory,
        topic: qTopic,
        difficulty: 'MEDIUM',
        title: qTitle,
        description: qDesc,
        starterCode: qStarter,
        maxScore: qCategory === 'ENGLISH' ? 40 : qMaxScore,
      };

      if (qCategory === 'JAVA' && tcExpected) {
        payload.testCases = [{ inputData: tcInput, expectedOutput: tcExpected, isHidden: tcHidden }];
      } else if (qCategory === 'SQL') {
        payload.schemaDdl = sqlSchemaDdl;
        payload.seedDataSql = sqlSeedData;
        payload.referenceQuery = sqlRefQuery;
      }

      await ApiClient.adminCreateQuestion(payload);
      alert('Question added to Question Bank!');
      setShowNewQuestionModal(false);
      setQTitle('');
      setQDesc('');
      setQStarter('');
      loadData();
    } catch (err: any) {
      alert(err.message || 'Failed to create question');
    }
  };

  const handleDeleteQuestion = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this question?')) {
      await ApiClient.adminDeleteQuestion(id);
      loadData();
    }
  };

  const handleViewAttemptDetail = async (attemptId: number) => {
    try {
      const detail = await ApiClient.adminGetAttemptDetail(attemptId);
      setSelectedAttemptDetail(detail);
    } catch (err: any) {
      alert('Failed to load attempt details');
    }
  };

  const copyLink = (code: string) => {
    const url = `${window.location.origin}/test/${code}`;
    navigator.clipboard.writeText(url);
    alert(`Candidate link copied to clipboard:\n${url}`);
  };

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center p-4">
        <div className="max-w-md w-full bg-slate-900 border border-slate-800 rounded-3xl p-8 shadow-2xl space-y-6">
          <div className="text-center space-y-2">
            <div className="w-12 h-12 rounded-2xl bg-blue-600/20 border border-blue-500/40 mx-auto flex items-center justify-center text-blue-400">
              <Lock className="w-6 h-6" />
            </div>
            <h2 className="text-xl font-bold text-white">Administrator Portal</h2>
            <p className="text-xs text-slate-400">Authenticate to access questions and assessment attempts</p>
          </div>

          {loginError && (
            <div className="p-3 bg-rose-950/60 border border-rose-800 rounded-xl text-xs text-rose-300 text-center">
              {loginError}
            </div>
          )}

          <form onSubmit={handleLogin} className="space-y-4">
            <div className="space-y-1">
              <label className="text-xs font-medium text-slate-300">Username</label>
              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-200 focus:outline-none focus:border-blue-500"
              />
            </div>

            <div className="space-y-1">
              <label className="text-xs font-medium text-slate-300">Password</label>
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2.5 text-xs text-slate-200 focus:outline-none focus:border-blue-500"
              />
            </div>

            <button
              type="submit"
              className="w-full py-3 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs rounded-xl shadow-lg shadow-blue-600/20 transition-colors"
            >
              Log In to Admin
            </button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col">
      {/* Admin Top Navbar */}
      <header className="h-16 bg-slate-900 border-b border-slate-800 px-6 flex items-center justify-between select-none">
        <div className="flex items-center space-x-3">
          <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center font-bold text-white text-xs">
            ADM
          </div>
          <span className="font-bold text-sm text-white tracking-wide">
            Assessment Management Portal
          </span>
        </div>

        {/* Tab Controls */}
        <div className="flex space-x-2">
          <button
            onClick={() => setActiveTab('attempts')}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-medium transition-colors flex items-center space-x-1.5 ${
              activeTab === 'attempts' ? 'bg-blue-600 text-white' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Users className="w-4 h-4" />
            <span>Candidate Attempts</span>
          </button>
          <button
            onClick={() => setActiveTab('questions')}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-medium transition-colors flex items-center space-x-1.5 ${
              activeTab === 'questions' ? 'bg-blue-600 text-white' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <FileQuestion className="w-4 h-4" />
            <span>Question Bank</span>
          </button>
          <button
            onClick={() => setActiveTab('assessments')}
            className={`px-3.5 py-1.5 rounded-lg text-xs font-medium transition-colors flex items-center space-x-1.5 ${
              activeTab === 'assessments' ? 'bg-blue-600 text-white' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <ExternalLink className="w-4 h-4" />
            <span>Assessments & Links</span>
          </button>
        </div>

        <button
          onClick={handleLogout}
          className="p-2 text-slate-400 hover:text-rose-400 rounded-lg hover:bg-slate-800 transition-colors"
          title="Log out"
        >
          <LogOut className="w-4 h-4" />
        </button>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 p-6 max-w-7xl w-full mx-auto space-y-6">
        {/* Tab 1: Attempts */}
        {activeTab === 'attempts' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-lg font-bold text-white">Candidate Attempts & Submissions</h2>
                <p className="text-xs text-slate-400">Monitor live and completed candidate evaluation attempts</p>
              </div>
              <a
                href="/api/admin/export/csv"
                target="_blank"
                rel="noreferrer"
                className="px-3.5 py-2 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold rounded-lg border border-slate-700 flex items-center space-x-1.5 transition-colors"
              >
                <Download className="w-4 h-4 text-blue-400" />
                <span>Export CSV</span>
              </a>
            </div>

            <div className="overflow-x-auto rounded-xl border border-slate-800 bg-slate-900 shadow-xl">
              <table className="min-w-full text-xs text-left">
                <thead className="bg-slate-950 border-b border-slate-800 text-slate-400 font-semibold">
                  <tr>
                    <th className="px-4 py-3">Attempt ID</th>
                    <th className="px-4 py-3">Candidate</th>
                    <th className="px-4 py-3">Test Code</th>
                    <th className="px-4 py-3">Status</th>
                    <th className="px-4 py-3">Java (30)</th>
                    <th className="px-4 py-3">SQL (30)</th>
                    <th className="px-4 py-3">English (40)</th>
                    <th className="px-4 py-3">Total (100)</th>
                    <th className="px-4 py-3">Violations</th>
                    <th className="px-4 py-3 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800 text-slate-300">
                  {attempts.length === 0 ? (
                    <tr>
                      <td colSpan={10} className="px-4 py-8 text-center text-slate-500">
                        No candidate attempts recorded yet.
                      </td>
                    </tr>
                  ) : (
                    attempts.map((a) => (
                      <tr key={a.attemptId} className="hover:bg-slate-800/40">
                        <td className="px-4 py-3 font-mono text-slate-400">#{a.attemptId}</td>
                        <td className="px-4 py-3">
                          <div className="font-bold text-white">{a.candidateName}</div>
                          <div className="text-[11px] text-slate-400">{a.candidateEmail}</div>
                        </td>
                        <td className="px-4 py-3 font-mono text-blue-400">{a.testCode}</td>
                        <td className="px-4 py-3">
                          <span
                            className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                              a.status === 'SUBMITTED'
                                ? 'bg-emerald-950 text-emerald-400 border border-emerald-800'
                                : a.status === 'TERMINATED_VIOLATION'
                                ? 'bg-rose-950 text-rose-400 border border-rose-800'
                                : 'bg-amber-950 text-amber-400 border border-amber-800'
                            }`}
                          >
                            {a.status}
                          </span>
                        </td>
                        <td className="px-4 py-3 font-mono">{a.javaScore}</td>
                        <td className="px-4 py-3 font-mono">{a.sqlScore}</td>
                        <td className="px-4 py-3 font-mono">{a.englishScore}</td>
                        <td className="px-4 py-3 font-mono font-bold text-emerald-400 text-sm">
                          {a.totalScore}
                        </td>
                        <td className="px-4 py-3">
                          {a.violationsCount > 0 ? (
                            <span className="text-rose-400 font-bold flex items-center space-x-1">
                              <ShieldAlert className="w-3.5 h-3.5" />
                              <span>{a.violationsCount}</span>
                            </span>
                          ) : (
                            <span className="text-slate-500">0</span>
                          )}
                        </td>
                        <td className="px-4 py-3 text-right">
                          <button
                            onClick={() => handleViewAttemptDetail(a.attemptId)}
                            className="px-2.5 py-1 bg-blue-600/20 hover:bg-blue-600/40 text-blue-400 text-xs font-semibold rounded border border-blue-500/30 transition-colors"
                          >
                            Review Attempt
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Tab 2: Question Bank */}
        {activeTab === 'questions' && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-lg font-bold text-white">Question Bank</h2>
                <p className="text-xs text-slate-400">Manage questions for Java, SQL, and English sections</p>
              </div>
              <button
                onClick={() => setShowNewQuestionModal(true)}
                className="px-3.5 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs font-semibold rounded-lg shadow-md flex items-center space-x-1.5 transition-colors"
              >
                <Plus className="w-4 h-4" />
                <span>Add Question</span>
              </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {questions.map((q) => (
                <div key={q.id} className="p-4 rounded-xl bg-slate-900 border border-slate-800 space-y-3 flex flex-col justify-between">
                  <div className="space-y-2">
                    <div className="flex items-center justify-between">
                      <span
                        className={`text-[10px] font-bold px-2 py-0.5 rounded uppercase font-mono ${
                          q.category === 'JAVA'
                            ? 'bg-blue-950 text-blue-400 border border-blue-800'
                            : q.category === 'SQL'
                            ? 'bg-emerald-950 text-emerald-400 border border-emerald-800'
                            : 'bg-purple-950 text-purple-400 border border-purple-800'
                        }`}
                      >
                        {q.category} • {q.topic}
                      </span>
                      <span className="text-xs font-mono text-slate-400 font-bold">{q.maxScore} pts</span>
                    </div>

                    <h3 className="text-sm font-bold text-white">{q.title}</h3>
                    <p className="text-xs text-slate-400 line-clamp-3 leading-relaxed">{q.description}</p>
                  </div>

                  <div className="pt-2 border-t border-slate-800 flex items-center justify-between text-xs text-slate-500">
                    <span>Difficulty: <strong className="text-slate-300">{q.difficulty}</strong></span>
                    <button
                      onClick={() => handleDeleteQuestion(q.id)}
                      className="text-rose-400 hover:text-rose-300 p-1 rounded hover:bg-rose-950/40 transition-colors"
                      title="Delete question"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Tab 3: Assessments */}
        {activeTab === 'assessments' && (
          <div className="space-y-6">
            <div className="p-6 rounded-2xl bg-slate-900 border border-slate-800 space-y-4">
              <h3 className="text-base font-bold text-white">Create New Assessment Link</h3>
              <form onSubmit={handleCreateAssessment} className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-300">Assessment Title</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Senior Software Engineer Assessment"
                    value={newAssessTitle}
                    onChange={(e) => setNewAssessTitle(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2 text-xs text-slate-200 focus:outline-none focus:border-blue-500"
                  />
                </div>

                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-300">Unique Code (e.g. 8F42KD)</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. 8F42KD"
                    value={newAssessCode}
                    onChange={(e) => setNewAssessCode(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2 text-xs text-slate-200 uppercase font-mono focus:outline-none focus:border-blue-500"
                  />
                </div>

                <div className="sm:col-span-2 space-y-1">
                  <label className="text-xs font-semibold text-slate-300">Description</label>
                  <input
                    type="text"
                    placeholder="Brief description for candidates..."
                    value={newAssessDesc}
                    onChange={(e) => setNewAssessDesc(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-4 py-2 text-xs text-slate-200 focus:outline-none focus:border-blue-500"
                  />
                </div>

                <button
                  type="submit"
                  className="sm:col-span-2 py-2.5 bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs rounded-xl shadow transition-colors"
                >
                  Generate Assessment Link
                </button>
              </form>
            </div>

            <div className="space-y-4">
              <h3 className="text-base font-bold text-white">Active Assessment Codes & Links</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                {assessments.map((a) => (
                  <div key={a.id} className="p-4 rounded-xl bg-slate-900 border border-slate-800 space-y-3">
                    <div className="flex items-center justify-between">
                      <span className="font-mono text-sm font-bold text-blue-400">{a.code}</span>
                      <span className="text-[11px] font-mono bg-emerald-950 text-emerald-400 border border-emerald-800/60 px-2 py-0.5 rounded-full">
                        {a.durationMinutes} Minutes
                      </span>
                    </div>

                    <h4 className="text-sm font-bold text-white">{a.title}</h4>
                    <p className="text-xs text-slate-400">{a.description}</p>

                    <div className="pt-2 border-t border-slate-800 flex items-center justify-between">
                      <span className="text-xs text-slate-500 font-mono">/test/{a.code}</span>
                      <button
                        onClick={() => copyLink(a.code)}
                        className="px-3 py-1 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold rounded flex items-center space-x-1.5 transition-colors"
                      >
                        <Copy className="w-3.5 h-3.5" />
                        <span>Copy Link</span>
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </main>

      {/* New Question Modal */}
      {showNewQuestionModal && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="max-w-2xl w-full max-h-[90vh] bg-slate-900 border border-slate-800 rounded-3xl p-6 shadow-2xl overflow-y-auto space-y-4">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-white">Create New Assessment Question</h3>
              <button
                onClick={() => setShowNewQuestionModal(false)}
                className="p-1 text-slate-400 hover:text-white"
              >
                <XCircle className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateQuestion} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Category</label>
                  <select
                    value={qCategory}
                    onChange={(e: any) => setQCategory(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-200 mt-1"
                  >
                    <option value="JAVA">Java Coding</option>
                    <option value="SQL">SQL Database</option>
                    <option value="ENGLISH">English Speaking</option>
                  </select>
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-300">Topic</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. ARRAYS, JOINS, etc."
                    value={qTopic}
                    onChange={(e) => setQTopic(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-200 mt-1 uppercase"
                  />
                </div>
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300">Title</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Invert Binary Tree"
                  value={qTitle}
                  onChange={(e) => setQTitle(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-200 mt-1"
                />
              </div>

              <div>
                <label className="text-xs font-semibold text-slate-300">Problem Description</label>
                <textarea
                  rows={4}
                  required
                  placeholder="Enter problem statement..."
                  value={qDesc}
                  onChange={(e) => setQDesc(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-200 mt-1"
                />
              </div>

              {qCategory !== 'ENGLISH' && (
                <div>
                  <label className="text-xs font-semibold text-slate-300">Starter Code Template</label>
                  <textarea
                    rows={4}
                    placeholder="public class Solution { ... }"
                    value={qStarter}
                    onChange={(e) => setQStarter(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs font-mono text-slate-200 mt-1"
                  />
                </div>
              )}

              {qCategory === 'JAVA' && (
                <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-2">
                  <span className="text-xs font-bold text-slate-300">Initial Test Case</span>
                  <div className="grid grid-cols-2 gap-2">
                    <input
                      type="text"
                      placeholder="Input data..."
                      value={tcInput}
                      onChange={(e) => setTcInput(e.target.value)}
                      className="bg-slate-900 border border-slate-800 rounded px-2 py-1 text-xs text-slate-200 font-mono"
                    />
                    <input
                      type="text"
                      placeholder="Expected output..."
                      value={tcExpected}
                      onChange={(e) => setTcExpected(e.target.value)}
                      className="bg-slate-900 border border-slate-800 rounded px-2 py-1 text-xs text-slate-200 font-mono"
                    />
                  </div>
                  <label className="flex items-center space-x-2 text-xs text-slate-400">
                    <input
                      type="checkbox"
                      checked={tcHidden}
                      onChange={(e) => setTcHidden(e.target.checked)}
                    />
                    <span>Mark as hidden test case</span>
                  </label>
                </div>
              )}

              {qCategory === 'SQL' && (
                <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 space-y-2">
                  <span className="text-xs font-bold text-slate-300">SQL Schema & Golden Query</span>
                  <textarea
                    rows={2}
                    placeholder="CREATE TABLE ...;"
                    value={sqlSchemaDdl}
                    onChange={(e) => setSqlSchemaDdl(e.target.value)}
                    className="w-full bg-slate-900 border border-slate-800 rounded px-2 py-1 text-xs text-slate-200 font-mono"
                  />
                  <textarea
                    rows={2}
                    placeholder="INSERT INTO ...;"
                    value={sqlSeedData}
                    onChange={(e) => setSqlSeedData(e.target.value)}
                    className="w-full bg-slate-900 border border-slate-800 rounded px-2 py-1 text-xs text-slate-200 font-mono"
                  />
                  <textarea
                    rows={2}
                    placeholder="Reference Solution: SELECT ...;"
                    value={sqlRefQuery}
                    onChange={(e) => setSqlRefQuery(e.target.value)}
                    className="w-full bg-slate-900 border border-slate-800 rounded px-2 py-1 text-xs text-slate-200 font-mono"
                  />
                </div>
              )}

              <div className="flex justify-end space-x-2 pt-2">
                <button
                  type="button"
                  onClick={() => setShowNewQuestionModal(false)}
                  className="px-4 py-2 bg-slate-800 text-slate-300 text-xs font-semibold rounded-xl"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold rounded-xl"
                >
                  Save Question
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Attempt Detail Modal */}
      {selectedAttemptDetail && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="max-w-4xl w-full max-h-[90vh] bg-slate-900 border border-slate-800 rounded-3xl flex flex-col shadow-2xl overflow-hidden">
            {/* Modal Header */}
            <div className="p-6 border-b border-slate-800 flex items-center justify-between bg-slate-950/50">
              <div>
                <h3 className="text-lg font-bold text-white">
                  Candidate Evaluation Review: {selectedAttemptDetail.candidateName}
                </h3>
                <p className="text-xs text-slate-400">
                  {selectedAttemptDetail.candidateEmail} • Test Code: {selectedAttemptDetail.testCode}
                </p>
              </div>

              <div className="flex items-center space-x-3">
                <div className="font-mono text-sm font-bold text-emerald-400 bg-emerald-950 border border-emerald-800 px-3 py-1 rounded-full">
                  Total: {selectedAttemptDetail.totalScore} / 100
                </div>
                <button
                  onClick={() => setSelectedAttemptDetail(null)}
                  className="p-1.5 text-slate-400 hover:text-white rounded-lg hover:bg-slate-800"
                >
                  <XCircle className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* Modal Body */}
            <div className="flex-1 overflow-y-auto p-6 space-y-6">
              {/* Scorecard */}
              <div className="grid grid-cols-3 gap-3">
                <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-center">
                  <span className="text-[11px] text-slate-400 uppercase font-bold">Java Score</span>
                  <div className="text-xl font-bold font-mono text-blue-400 mt-0.5">{selectedAttemptDetail.javaScore}/30</div>
                </div>
                <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-center">
                  <span className="text-[11px] text-slate-400 uppercase font-bold">SQL Score</span>
                  <div className="text-xl font-bold font-mono text-emerald-400 mt-0.5">{selectedAttemptDetail.sqlScore}/30</div>
                </div>
                <div className="p-3 bg-slate-950 rounded-xl border border-slate-800 text-center">
                  <span className="text-[11px] text-slate-400 uppercase font-bold">English Score</span>
                  <div className="text-xl font-bold font-mono text-purple-400 mt-0.5">{selectedAttemptDetail.englishScore}/40</div>
                </div>
              </div>

              {/* Violations Audit Trail */}
              {selectedAttemptDetail.violations && selectedAttemptDetail.violations.length > 0 && (
                <div className="p-4 bg-rose-950/30 border border-rose-800/60 rounded-xl space-y-2">
                  <h4 className="text-xs font-bold text-rose-400 uppercase tracking-wider flex items-center space-x-1.5">
                    <ShieldAlert className="w-4 h-4" />
                    <span>Anti-Cheat Security Violations ({selectedAttemptDetail.violations.length})</span>
                  </h4>
                  <div className="space-y-1">
                    {selectedAttemptDetail.violations.map((v) => (
                      <div key={v.id} className="text-xs text-rose-300 font-mono flex justify-between bg-rose-950/40 p-2 rounded">
                        <span>• {v.violationType}: {v.details}</span>
                        <span className="text-rose-400">{new Date(v.timestamp).toLocaleTimeString()}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Spoken English Recording & AI Rubrics */}
              {selectedAttemptDetail.englishEvaluation && (
                <div className="p-5 bg-gradient-to-br from-purple-950/30 to-slate-950 rounded-xl border border-purple-800/40 space-y-4">
                  <div className="flex items-center justify-between">
                    <h4 className="text-xs font-bold text-purple-300 uppercase tracking-wider flex items-center space-x-1.5">
                      <Volume2 className="w-4 h-4 text-purple-400" />
                      <span>English Speaking Evaluation & Audio</span>
                    </h4>
                    <span className="text-xs font-mono font-bold text-purple-400">
                      Score: {selectedAttemptDetail.englishEvaluation.total} / 40
                    </span>
                  </div>

                  {selectedAttemptDetail.englishAudioUrl && (
                    <audio src={selectedAttemptDetail.englishAudioUrl} controls className="w-full h-8" />
                  )}

                  <div className="grid grid-cols-6 gap-2 text-center text-xs">
                    <div className="p-2 bg-slate-950 rounded border border-slate-800 font-mono">
                      <div className="text-[10px] text-slate-500">Grammar</div>
                      <div className="text-white font-bold">{selectedAttemptDetail.englishEvaluation.grammar}/10</div>
                    </div>
                    <div className="p-2 bg-slate-950 rounded border border-slate-800 font-mono">
                      <div className="text-[10px] text-slate-500">Vocab</div>
                      <div className="text-white font-bold">{selectedAttemptDetail.englishEvaluation.vocabulary}/10</div>
                    </div>
                    <div className="p-2 bg-slate-950 rounded border border-slate-800 font-mono">
                      <div className="text-[10px] text-slate-500">Fluency</div>
                      <div className="text-white font-bold">{selectedAttemptDetail.englishEvaluation.fluency}/10</div>
                    </div>
                    <div className="p-2 bg-slate-950 rounded border border-slate-800 font-mono">
                      <div className="text-[10px] text-slate-500">Pronun.</div>
                      <div className="text-white font-bold">{selectedAttemptDetail.englishEvaluation.pronunciation}/10</div>
                    </div>
                    <div className="p-2 bg-slate-950 rounded border border-slate-800 font-mono">
                      <div className="text-[10px] text-slate-500">Relevance</div>
                      <div className="text-white font-bold">{selectedAttemptDetail.englishEvaluation.relevance}/10</div>
                    </div>
                    <div className="p-2 bg-slate-950 rounded border border-slate-800 font-mono">
                      <div className="text-[10px] text-slate-500">Structure</div>
                      <div className="text-white font-bold">{selectedAttemptDetail.englishEvaluation.structure}/10</div>
                    </div>
                  </div>

                  <div>
                    <span className="text-xs font-semibold text-slate-400">Captured Speech Transcript:</span>
                    <p className="text-xs text-slate-300 p-3 bg-slate-950 rounded-lg border border-slate-800 mt-1 font-mono">
                      "{selectedAttemptDetail.englishEvaluation.transcript}"
                    </p>
                  </div>

                  <div>
                    <span className="text-xs font-semibold text-slate-400">AI Narrative Feedback:</span>
                    <p className="text-xs text-slate-400 p-3 bg-slate-950 rounded-lg border border-slate-800 mt-1">
                      {selectedAttemptDetail.englishEvaluation.feedback}
                    </p>
                  </div>
                </div>
              )}

              {/* Questions & Submitted Code */}
              <div className="space-y-4">
                <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
                  Submitted Answers & Code
                </h4>

                {selectedAttemptDetail.questions.map((q) => (
                  <div key={q.order} className="p-4 bg-slate-950 rounded-xl border border-slate-800 space-y-3">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center space-x-2">
                        <span className="font-mono text-xs font-bold text-white px-2 py-0.5 rounded bg-slate-800">
                          Q{q.order}
                        </span>
                        <span className="text-xs font-bold text-slate-200">{q.title}</span>
                        <span className="text-[10px] text-slate-500 uppercase font-semibold">({q.category})</span>
                      </div>

                      <div className="flex items-center space-x-3 text-xs font-mono">
                        <span className="font-bold text-emerald-400">{q.scoreAwarded} / {q.maxScore} pts</span>
                        <span className="text-slate-400 uppercase font-semibold">[{q.status}]</span>
                      </div>
                    </div>

                    {q.submittedCode && (
                      <pre className="p-3 bg-slate-900 rounded-lg border border-slate-800 text-xs font-mono text-slate-300 overflow-x-auto max-h-48">
                        {q.submittedCode}
                      </pre>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
