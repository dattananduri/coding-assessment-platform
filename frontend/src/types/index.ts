export interface AssessmentInfo {
  title: string;
  code: string;
  description: string;
  durationMinutes: number;
  active: boolean;
  totalQuestions: number;
  javaQuestions: number;
  sqlQuestions: number;
  englishQuestions: number;
}

export interface CandidateStartResponse {
  attemptId: number;
  candidateName: string;
  candidateEmail: string;
  testCode: string;
  assessmentTitle: string;
  startedAt: string;
  expiresAt: string;
  remainingSeconds: number;
  token: string;
}

export interface QuestionSummary {
  order: number;
  questionId: number;
  category: 'JAVA' | 'SQL' | 'ENGLISH';
  topic: string;
  title: string;
  status: 'NOT_ATTEMPTED' | 'IN_PROGRESS' | 'SUBMITTED' | 'PASSED' | 'FAILED';
  maxScore: number;
  scoreAwarded: number;
  passedTestCases: number;
  totalTestCases: number;
}

export interface AttemptState {
  attemptId: number;
  candidateName: string;
  candidateEmail: string;
  testCode: string;
  assessmentTitle: string;
  status: 'IN_PROGRESS' | 'SUBMITTED' | 'TERMINATED_VIOLATION';
  startedAt: string;
  expiresAt: string;
  remainingSeconds: number;
  currentTotalScore: number;
  questions: QuestionSummary[];
}

export interface TestCaseDto {
  id: number;
  inputData: string;
  expectedOutput: string;
  isHidden: boolean;
}

export interface QuestionDetail {
  order: number;
  questionId: number;
  category: 'JAVA' | 'SQL' | 'ENGLISH';
  topic: string;
  difficulty: string;
  title: string;
  description: string;
  inputFormat?: string;
  outputFormat?: string;
  constraints?: string;
  starterCode?: string;
  currentCode?: string;
  status: string;
  maxScore: number;
  scoreAwarded: number;
  timeLimitMs: number;
  memoryLimitMb: number;
  sampleTestCases?: TestCaseDto[];
  sqlSchemaDescription?: string;
  sqlSampleOutput?: string;
  audioFilePath?: string;
  transcript?: string;
  englishEvaluation?: EnglishSubmitResponse;
}

export interface TestCaseResult {
  testCaseIndex: number;
  isHidden: boolean;
  input: string;
  expectedOutput: string;
  actualOutput: string;
  passed: boolean;
  executionTimeMs: number;
  error?: string;
}

export interface ExecutionResult {
  status: string;
  passed: boolean;
  executionTimeMs: number;
  passedTestCases: number;
  totalTestCases: number;
  scoreAwarded: number;
  output?: string;
  error?: string;
  testCaseResults: TestCaseResult[];
}

export interface SqlExecutionResult {
  passed: boolean;
  status: string;
  executionTimeMs: number;
  scoreAwarded: number;
  candidateColumns: string[];
  candidateRows: any[][];
  expectedColumns: string[];
  expectedRows: any[][];
  message?: string;
  error?: string;
}

export interface EnglishSubmitResponse {
  grammar: number;
  vocabulary: number;
  fluency: number;
  pronunciation: number;
  relevance: number;
  structure: number;
  total: number;
  feedback: string;
  transcript: string;
}

export interface FinalResult {
  attemptId: number;
  candidateName: string;
  candidateEmail: string;
  testCode: string;
  assessmentTitle: string;
  status: string;
  startedAt: string;
  submittedAt: string;
  javaScore: number;
  sqlScore: number;
  englishScore: number;
  totalScore: number;
  maxScore: number;
  violationsCount: number;
  questionSummaries: QuestionSummary[];
  englishEvaluation?: EnglishSubmitResponse;
}

export interface ViolationLog {
  id: number;
  violationType: string;
  timestamp: string;
  details: string;
}

export interface AttemptQuestionDetail {
  order: number;
  questionId: number;
  category: string;
  topic: string;
  title: string;
  status: string;
  maxScore: number;
  scoreAwarded: number;
  submittedCode: string;
  passedTestCases: number;
  totalTestCases: number;
  lastRunOutput: string;
}

export interface AdminAttemptDetail {
  attemptId: number;
  candidateName: string;
  candidateEmail: string;
  testCode: string;
  assessmentTitle: string;
  status: string;
  startedAt: string;
  expiresAt: string;
  submittedAt?: string;
  javaScore: number;
  sqlScore: number;
  englishScore: number;
  totalScore: number;
  maxScore: number;
  questions: AttemptQuestionDetail[];
  englishEvaluation?: EnglishSubmitResponse;
  englishAudioUrl?: string;
  violations: ViolationLog[];
}
