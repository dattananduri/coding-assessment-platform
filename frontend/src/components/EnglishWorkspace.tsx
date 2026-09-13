import React, { useState, useRef, useEffect } from 'react';
import { QuestionDetail, EnglishSubmitResponse } from '../types';
import { Mic, Square, Play, RotateCcw, Send, CheckCircle2, Volume2, Sparkles, Award } from 'lucide-react';

interface EnglishWorkspaceProps {
  question: QuestionDetail;
  onSubmit: (audioBlob: Blob | null, transcript: string, durationSeconds: number) => Promise<EnglishSubmitResponse>;
}

export const EnglishWorkspace: React.FC<EnglishWorkspaceProps> = ({
  question,
  onSubmit,
}) => {
  const [isRecording, setIsRecording] = useState<boolean>(false);
  const [recordingSeconds, setRecordingSeconds] = useState<number>(0);
  const [audioBlob, setAudioBlob] = useState<Blob | null>(null);
  const [audioUrl, setAudioUrl] = useState<string | null>(null);
  const [liveTranscript, setLiveTranscript] = useState<string>('');
  const [isEvaluating, setIsEvaluating] = useState<boolean>(false);
  const [evaluation, setEvaluation] = useState<EnglishSubmitResponse | null>(
    question.englishEvaluation || null
  );

  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const audioChunksRef = useRef<Blob[]>([]);
  const recognitionRef = useRef<any>(null);
  const timerIntervalRef = useRef<any>(null);

  useEffect(() => {
    if (question.englishEvaluation) {
      setEvaluation(question.englishEvaluation);
      setLiveTranscript(question.englishEvaluation.transcript || '');
    }
  }, [question]);

  useEffect(() => {
    return () => {
      if (timerIntervalRef.current) clearInterval(timerIntervalRef.current);
      if (audioUrl) URL.revokeObjectURL(audioUrl);
      if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
        mediaRecorderRef.current.stop();
      }
      if (recognitionRef.current) {
        try { recognitionRef.current.stop(); } catch (ignored) {}
      }
    };
  }, [audioUrl]);

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      audioChunksRef.current = [];
      setLiveTranscript('');
      setRecordingSeconds(0);
      setAudioBlob(null);
      if (audioUrl) URL.revokeObjectURL(audioUrl);
      setAudioUrl(null);

      const recorder = new MediaRecorder(stream);
      mediaRecorderRef.current = recorder;

      recorder.ondataavailable = (e) => {
        if (e.data.size > 0) {
          audioChunksRef.current.push(e.data);
        }
      };

      recorder.onstop = () => {
        const blob = new Blob(audioChunksRef.current, { type: 'audio/webm' });
        setAudioBlob(blob);
        setAudioUrl(URL.createObjectURL(blob));
        stream.getTracks().forEach((track) => track.stop());
      };

      recorder.start(500);
      setIsRecording(true);

      // Start timer
      timerIntervalRef.current = setInterval(() => {
        setRecordingSeconds((prev) => prev + 1);
      }, 1000);

      // Web Speech API for real-time speech transcription
      const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
      if (SpeechRecognition) {
        const recognition = new SpeechRecognition();
        recognition.continuous = true;
        recognition.interimResults = true;
        recognition.lang = 'en-US';

        recognition.onresult = (event: any) => {
          let accumulated = '';
          for (let i = 0; i < event.results.length; i++) {
            accumulated += event.results[i][0].transcript + ' ';
          }
          setLiveTranscript(accumulated.trim());
        };

        recognition.onerror = (event: any) => {
          console.warn('Speech recognition status:', event.error);
        };

        recognition.start();
        recognitionRef.current = recognition;
      }
    } catch (err: any) {
      alert('Could not access microphone: ' + err.message + '. Please ensure microphone permissions are granted.');
    }
  };

  const stopRecording = () => {
    if (mediaRecorderRef.current && isRecording) {
      mediaRecorderRef.current.stop();
      setIsRecording(false);
      if (timerIntervalRef.current) clearInterval(timerIntervalRef.current);
    }
    if (recognitionRef.current) {
      try {
        recognitionRef.current.stop();
      } catch (ignored) {}
    }
  };

  const handleReset = () => {
    if (window.confirm('Clear recorded audio and record again?')) {
      setAudioBlob(null);
      if (audioUrl) URL.revokeObjectURL(audioUrl);
      setAudioUrl(null);
      setLiveTranscript('');
      setRecordingSeconds(0);
    }
  };

  const handleSubmit = async () => {
    if (!audioBlob && !liveTranscript) {
      alert('Please record your answer before submitting.');
      return;
    }

    setIsEvaluating(true);
    try {
      const res = await onSubmit(audioBlob, liveTranscript, recordingSeconds);
      setEvaluation(res);
    } catch (err: any) {
      alert(err.message || 'Error submitting spoken response.');
    } finally {
      setIsEvaluating(false);
    }
  };

  const formatTimer = (secs: number) => {
    const m = Math.floor(secs / 60);
    const s = secs % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className="flex-1 flex flex-col md:flex-row h-[calc(100vh-4rem)] overflow-hidden">
      {/* Left Panel: Speaking Question & Instructions */}
      <div className="w-full md:w-1/2 border-r border-slate-800 flex flex-col h-full bg-slate-900/40 p-6 overflow-y-auto space-y-6">
        <div>
          <div className="flex items-center space-x-2 text-xs font-semibold text-purple-400 mb-2">
            <Mic className="w-4 h-4" />
            <span>Spoken Technical Communication Evaluation</span>
          </div>
          <h2 className="text-xl font-bold text-white mb-2">
            Q{question.order}. {question.title}
          </h2>
          <div className="flex items-center space-x-4 text-xs text-slate-400 mb-4 pb-4 border-b border-slate-800">
            <span>Category: <strong className="text-slate-200">English Speaking</strong></span>
            <span>Evaluation: <strong className="text-purple-400">AI Rubric Scored</strong></span>
            <span>Total Points: <strong className="text-emerald-400">40 Points</strong></span>
          </div>
        </div>

        {/* Prompt Card */}
        <div className="p-5 bg-gradient-to-br from-purple-950/30 to-indigo-950/30 border border-purple-800/40 rounded-xl space-y-3">
          <h3 className="text-sm font-bold text-purple-200 flex items-center space-x-2">
            <Volume2 className="w-4 h-4 text-purple-400" />
            <span>Speaking Prompt</span>
          </h3>
          <p className="text-slate-200 font-medium text-base leading-relaxed">
            "{question.description}"
          </p>
        </div>

        {/* Instructions */}
        <div className="space-y-3">
          <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400">
            Assessment Guidelines & Rubrics
          </h4>
          <ul className="text-xs text-slate-300 space-y-2 list-disc list-inside">
            <li>Ensure you are in a quiet environment and your browser microphone permission is granted.</li>
            <li>Recommended speech duration: <strong>1 to 3 minutes</strong>.</li>
            <li>You will be evaluated across 6 core criteria:</li>
          </ul>

          <div className="grid grid-cols-2 sm:grid-cols-3 gap-2.5 pt-2">
            {[
              { label: 'Grammar', desc: 'Accuracy & sentence cadence', max: '10' },
              { label: 'Vocabulary', desc: 'Technical & domain depth', max: '10' },
              { label: 'Fluency', desc: 'Flow & minimal fillers', max: '10' },
              { label: 'Pronunciation', desc: 'Clarity & enunciation', max: '10' },
              { label: 'Relevance', desc: 'Direct prompt response', max: '10' },
              { label: 'Structure', desc: 'Intro, body & conclusions', max: '10' },
            ].map((r, i) => (
              <div key={i} className="p-2.5 rounded-lg bg-slate-950 border border-slate-800/80 text-center">
                <div className="text-xs font-bold text-slate-200">{r.label}</div>
                <div className="text-[10px] text-slate-500 mt-0.5">{r.desc}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Right Panel: Audio Recorder, Speech-to-Text & AI Evaluation */}
      <div className="w-full md:w-1/2 flex flex-col h-full bg-slate-950 p-6 overflow-y-auto space-y-6">
        {/* Audio Recording Console */}
        <div className="p-6 bg-slate-900 rounded-xl border border-slate-800 space-y-5">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-slate-200 flex items-center space-x-2">
              <Mic className="w-4 h-4 text-purple-400" />
              <span>Voice Recording Console</span>
            </h3>

            {/* Recording Timer */}
            <div className={`font-mono text-base font-bold px-3 py-1 rounded-full border flex items-center space-x-2 ${
              isRecording
                ? 'bg-rose-950/60 border-rose-600 text-rose-400 animate-pulse'
                : 'bg-slate-950 border-slate-800 text-slate-400'
            }`}>
              <span className={`w-2 h-2 rounded-full ${isRecording ? 'bg-rose-500 animate-ping' : 'bg-slate-600'}`} />
              <span>{formatTimer(recordingSeconds)}</span>
            </div>
          </div>

          {/* Animated Waveform Visualizer */}
          <div className="h-20 bg-slate-950 rounded-lg border border-slate-800/80 flex items-center justify-center space-x-1.5 px-4 overflow-hidden">
            {isRecording ? (
              Array.from({ length: 28 }).map((_, i) => (
                <div
                  key={i}
                  className="w-1.5 bg-gradient-to-t from-purple-600 to-indigo-400 rounded-full animate-pulse"
                  style={{
                    height: `${Math.max(12, Math.sin(i * 0.7 + recordingSeconds) * 45 + 30)}px`,
                    animationDuration: `${0.4 + (i % 5) * 0.1}s`,
                  }}
                />
              ))
            ) : (
              <span className="text-xs text-slate-500">
                {audioUrl ? 'Audio recording ready for playback and submission' : 'Click "Start Recording" when you are ready to speak'}
              </span>
            )}
          </div>

          {/* Audio Player if recorded */}
          {audioUrl && (
            <div className="p-3 bg-slate-950 rounded-lg border border-slate-800">
              <audio src={audioUrl} controls className="w-full h-8" />
            </div>
          )}

          {/* Action Buttons */}
          <div className="flex items-center justify-between pt-2">
            <div className="flex items-center space-x-2">
              {!isRecording ? (
                <button
                  onClick={startRecording}
                  disabled={isEvaluating}
                  className="px-4 py-2 bg-purple-600 hover:bg-purple-500 text-white text-xs font-semibold rounded-lg shadow-md shadow-purple-600/20 transition-all flex items-center space-x-2 disabled:opacity-50"
                >
                  <Mic className="w-4 h-4" />
                  <span>{audioUrl ? 'Record Again' : 'Start Recording'}</span>
                </button>
              ) : (
                <button
                  onClick={stopRecording}
                  className="px-4 py-2 bg-rose-600 hover:bg-rose-500 text-white text-xs font-semibold rounded-lg shadow-md shadow-rose-600/20 transition-all flex items-center space-x-2 animate-pulse"
                >
                  <Square className="w-4 h-4" />
                  <span>Stop Recording</span>
                </button>
              )}

              {audioUrl && !isRecording && (
                <button
                  onClick={handleReset}
                  className="p-2 text-slate-400 hover:text-slate-200 rounded-lg border border-slate-800 hover:bg-slate-800 transition-colors"
                  title="Clear recording"
                >
                  <RotateCcw className="w-4 h-4" />
                </button>
              )}
            </div>

            <button
              onClick={handleSubmit}
              disabled={isRecording || (!audioBlob && !liveTranscript) || isEvaluating}
              className="px-5 py-2 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white text-xs font-semibold rounded-lg shadow-md shadow-emerald-600/20 transition-all flex items-center space-x-2 disabled:opacity-50"
            >
              <Send className="w-4 h-4" />
              <span>{isEvaluating ? 'Evaluating with AI...' : 'Submit Recording'}</span>
            </button>
          </div>
        </div>

        {/* Live Speech-to-Text Transcript Preview */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <h4 className="text-xs font-bold uppercase tracking-wider text-slate-400 flex items-center space-x-1.5">
              <Sparkles className="w-3.5 h-3.5 text-blue-400" />
              <span>Speech-to-Text Transcript (Automatic)</span>
            </h4>
            <span className="text-[10px] text-slate-500">Read-only capture</span>
          </div>
          <div className="p-4 bg-slate-900/70 rounded-lg border border-slate-800 text-xs text-slate-300 min-h-[100px] max-h-[150px] overflow-y-auto whitespace-pre-wrap leading-relaxed">
            {liveTranscript || (
              <span className="text-slate-600 italic">
                Your spoken words will appear here automatically while speaking...
              </span>
            )}
          </div>
        </div>

        {/* AI Evaluation Rubric Card */}
        {evaluation && (
          <div className="p-5 bg-gradient-to-br from-slate-900 to-slate-950 rounded-xl border border-emerald-800/40 space-y-4 shadow-xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <div className="flex items-center space-x-2">
                <Award className="w-5 h-5 text-emerald-400" />
                <h3 className="text-sm font-bold text-white">
                  AI Evaluation Results
                </h3>
              </div>
              <div className="text-sm font-mono font-bold text-emerald-400 bg-emerald-950/60 border border-emerald-800/60 px-3 py-1 rounded-full">
                Score: {evaluation.total} / 40 pts
              </div>
            </div>

            {/* Rubrics Grid */}
            <div className="grid grid-cols-3 gap-2 text-center text-xs">
              <div className="p-2.5 bg-slate-950 rounded border border-slate-800">
                <span className="text-slate-400 text-[11px]">Grammar</span>
                <div className="text-sm font-bold font-mono text-blue-400 mt-0.5">{evaluation.grammar}/10</div>
              </div>
              <div className="p-2.5 bg-slate-950 rounded border border-slate-800">
                <span className="text-slate-400 text-[11px]">Vocabulary</span>
                <div className="text-sm font-bold font-mono text-purple-400 mt-0.5">{evaluation.vocabulary}/10</div>
              </div>
              <div className="p-2.5 bg-slate-950 rounded border border-slate-800">
                <span className="text-slate-400 text-[11px]">Fluency</span>
                <div className="text-sm font-bold font-mono text-emerald-400 mt-0.5">{evaluation.fluency}/10</div>
              </div>
              <div className="p-2.5 bg-slate-950 rounded border border-slate-800">
                <span className="text-slate-400 text-[11px]">Pronunciation</span>
                <div className="text-sm font-bold font-mono text-amber-400 mt-0.5">{evaluation.pronunciation}/10</div>
              </div>
              <div className="p-2.5 bg-slate-950 rounded border border-slate-800">
                <span className="text-slate-400 text-[11px]">Relevance</span>
                <div className="text-sm font-bold font-mono text-teal-400 mt-0.5">{evaluation.relevance}/10</div>
              </div>
              <div className="p-2.5 bg-slate-950 rounded border border-slate-800">
                <span className="text-slate-400 text-[11px]">Structure</span>
                <div className="text-sm font-bold font-mono text-indigo-400 mt-0.5">{evaluation.structure}/10</div>
              </div>
            </div>

            {/* AI Feedback */}
            <div className="space-y-1.5 pt-1">
              <span className="text-xs font-bold text-slate-300">Detailed AI Feedback:</span>
              <p className="text-xs text-slate-400 bg-slate-950 p-3 rounded border border-slate-800 leading-relaxed">
                {evaluation.feedback}
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
