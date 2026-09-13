import {
  AssessmentInfo,
  CandidateStartResponse,
  AttemptState,
  QuestionDetail,
  ExecutionResult,
  SqlExecutionResult,
  EnglishSubmitResponse,
  FinalResult,
  AdminAttemptDetail,
} from '../types';

const API_BASE = '/api';

export class ApiClient {
  private static getHeaders(isJson: boolean = true): HeadersInit {
    const headers: Record<string, string> = {};
    if (isJson) {
      headers['Content-Type'] = 'application/json';
    }
    const adminToken = localStorage.getItem('admin_token');
    const candidateToken = localStorage.getItem('candidate_token');

    if (adminToken) {
      headers['Authorization'] = `Bearer ${adminToken}`;
    } else if (candidateToken) {
      headers['Authorization'] = `Bearer ${candidateToken}`;
    }
    return headers;
  }

  // --- Candidate APIs ---

  static async getAssessmentInfo(code: string): Promise<AssessmentInfo> {
    const res = await fetch(`${API_BASE}/assessment/info/${code}`, {
      headers: this.getHeaders(),
    });
    if (!res.ok) throw new Error('Failed to load assessment information.');
    return res.json();
  }

  static async startExam(candidateName: string, candidateEmail: string, testCode: string): Promise<CandidateStartResponse> {
    const res = await fetch(`${API_BASE}/assessment/start`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ candidateName, candidateEmail, testCode }),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to start exam.');
    }
    const data: CandidateStartResponse = await res.json();
    if (data.token) {
      localStorage.setItem('candidate_token', data.token);
    }
    return data;
  }

  static async getAttemptState(attemptId: number): Promise<AttemptState> {
    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/state`, {
      headers: this.getHeaders(),
    });
    if (!res.ok) throw new Error('Failed to fetch assessment state.');
    return res.json();
  }

  static async getQuestionDetail(attemptId: number, order: number): Promise<QuestionDetail> {
    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/questions/${order}`, {
      headers: this.getHeaders(),
    });
    if (!res.ok) throw new Error(`Failed to load question ${order}.`);
    return res.json();
  }

  static async saveCode(attemptId: number, order: number, code: string): Promise<void> {
    await fetch(`${API_BASE}/assessment/attempts/${attemptId}/questions/${order}/save`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ code }),
    });
  }

  static async runJava(attemptId: number, order: number, code: string, customInput?: string): Promise<ExecutionResult> {
    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/questions/${order}/run-java`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ code, customInput }),
    });
    if (!res.ok) throw new Error('Java run failed.');
    return res.json();
  }

  static async submitJava(attemptId: number, order: number, code: string): Promise<ExecutionResult> {
    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/questions/${order}/submit-java`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ code }),
    });
    if (!res.ok) throw new Error('Java submission failed.');
    return res.json();
  }

  static async runSql(attemptId: number, order: number, query: string): Promise<SqlExecutionResult> {
    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/questions/${order}/run-sql`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ query }),
    });
    if (!res.ok) throw new Error('SQL run failed.');
    return res.json();
  }

  static async submitSql(attemptId: number, order: number, query: string): Promise<SqlExecutionResult> {
    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/questions/${order}/submit-sql`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ query }),
    });
    if (!res.ok) throw new Error('SQL submission failed.');
    return res.json();
  }

  static async submitEnglish(attemptId: number, audioBlob: Blob | null, transcript: string, durationSeconds: number): Promise<EnglishSubmitResponse> {
    const formData = new FormData();
    if (audioBlob) {
      formData.append('audio', audioBlob, 'speaking_response.webm');
    }
    formData.append('transcript', transcript);
    formData.append('durationSeconds', durationSeconds.toString());

    const headers: Record<string, string> = {};
    const candidateToken = localStorage.getItem('candidate_token');
    if (candidateToken) {
      headers['Authorization'] = `Bearer ${candidateToken}`;
    }

    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/english`, {
      method: 'POST',
      headers,
      body: formData,
    });
    if (!res.ok) {
      let errMsg = 'Failed to submit English speaking response.';
      try {
        const data = await res.json();
        if (data.message) errMsg = data.message;
        else if (data.error) errMsg = data.error;
      } catch (e) {
        // Fallback to default
      }
      throw new Error(errMsg);
    }
    return res.json();
  }

  static async reportViolation(attemptId: number, violationType: string, details: string, terminate: boolean = false): Promise<any> {
    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/violations?terminate=${terminate}`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify({ violationType, details }),
    });
    return res.json();
  }

  static async finalSubmit(attemptId: number): Promise<FinalResult> {
    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/final-submit`, {
      method: 'POST',
      headers: this.getHeaders(),
    });
    if (!res.ok) throw new Error('Final submission failed.');
    return res.json();
  }

  static async getFinalResult(attemptId: number): Promise<FinalResult> {
    const res = await fetch(`${API_BASE}/assessment/attempts/${attemptId}/result`, {
      headers: this.getHeaders(),
    });
    if (!res.ok) throw new Error('Failed to fetch final results.');
    return res.json();
  }

  // --- Admin APIs ---

  static async adminLogin(username: string, password: string): Promise<string> {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    if (!res.ok) throw new Error('Invalid credentials.');
    const data = await res.json();
    localStorage.setItem('admin_token', data.token);
    return data.token;
  }

  static async adminGetAssessments(): Promise<any[]> {
    const res = await fetch(`${API_BASE}/admin/assessments`, {
      headers: this.getHeaders(),
    });
    if (!res.ok) throw new Error('Failed to fetch assessments.');
    return res.json();
  }

  static async adminCreateAssessment(data: { title: string; code: string; description: string; durationMinutes: number }): Promise<any> {
    const res = await fetch(`${API_BASE}/admin/assessments`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to create assessment.');
    return res.json();
  }

  static async adminGetQuestions(category?: string): Promise<any[]> {
    const url = category ? `${API_BASE}/admin/questions?category=${category}` : `${API_BASE}/admin/questions`;
    const res = await fetch(url, { headers: this.getHeaders() });
    if (!res.ok) throw new Error('Failed to fetch questions.');
    return res.json();
  }

  static async adminCreateQuestion(data: any): Promise<any> {
    const res = await fetch(`${API_BASE}/admin/questions`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to create question.');
    return res.json();
  }

  static async adminDeleteQuestion(id: number): Promise<void> {
    const res = await fetch(`${API_BASE}/admin/questions/${id}`, {
      method: 'DELETE',
      headers: this.getHeaders(),
    });
    if (!res.ok) throw new Error('Failed to delete question.');
  }

  static async adminGetAttempts(): Promise<any[]> {
    const res = await fetch(`${API_BASE}/admin/attempts`, {
      headers: this.getHeaders(),
    });
    if (!res.ok) throw new Error('Failed to fetch candidate attempts.');
    return res.json();
  }

  static async adminGetAttemptDetail(attemptId: number): Promise<AdminAttemptDetail> {
    const res = await fetch(`${API_BASE}/admin/attempts/${attemptId}`, {
      headers: this.getHeaders(),
    });
    if (!res.ok) throw new Error('Failed to fetch attempt detail.');
    return res.json();
  }
}
