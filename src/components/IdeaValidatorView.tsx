import React, { useState } from 'react';
import { VentureIdea } from '../types';
import { evaluateVentureNLP } from '../utils/nlpEngine';
import {
  BrainCircuit,
  Sparkles,
  Save,
  Check,
  AlertOctagon,
  Shield,
  Target,
  Zap,
  HelpCircle
} from 'lucide-react';

interface IdeaValidatorViewProps {
  currentVenture: VentureIdea;
  onUpdateVenture: (venture: VentureIdea) => void;
  onSaveVenture: (venture: VentureIdea) => void;
}

export const IdeaValidatorView: React.FC<IdeaValidatorViewProps> = ({
  currentVenture,
  onUpdateVenture,
  onSaveVenture
}) => {
  const [formData, setFormData] = useState({
    startupName: currentVenture.startupName,
    targetCustomer: currentVenture.targetCustomer,
    problemStatement: currentVenture.problemStatement,
    proposedSolution: currentVenture.proposedSolution,
    businessModel: currentVenture.businessModel
  });

  const [savedSuccess, setSavedSuccess] = useState(false);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleEvaluate = () => {
    const result = evaluateVentureNLP({
      ...currentVenture,
      ...formData
    });
    onUpdateVenture(result);
  };

  const handleSave = () => {
    const result = evaluateVentureNLP({
      ...currentVenture,
      ...formData
    });
    onSaveVenture(result);
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 2500);
  };

  const loadSamplePreset = (presetType: 'strong' | 'pivot') => {
    if (presetType === 'strong') {
      const data = {
        startupName: 'DataPulse Telemetry',
        targetCustomer: 'Enterprise CTOs and Platform Engineering Leaders managing Kubernetes clusters',
        problemStatement: 'Distributed cloud microservices suffer from silent data pipeline degradation and breaking API contract regressions causing millions in SLA breach penalties and customer churn.',
        proposedSolution: 'Automated real-time telemetry agent with proprietary causal graph anomaly detection algorithms and zero-config SDK integrations providing sub-second root-cause isolation.',
        businessModel: 'B2B Enterprise SaaS Subscription ($4,000/cluster/month) with annual upfront contracts'
      };
      setFormData(data);
      const res = evaluateVentureNLP({ ...currentVenture, ...data });
      onUpdateVenture(res);
    } else {
      const data = {
        startupName: 'Generic Notes App',
        targetCustomer: 'People who like writing',
        problemStatement: 'People forget ideas, but there is no demand for another notebook app and it lacks competition barriers.',
        proposedSolution: 'A simple notes app with standard layout, not feasible to build native sync yet.',
        businessModel: 'Free with occasional ads'
      };
      setFormData(data);
      const res = evaluateVentureNLP({ ...currentVenture, ...data });
      onUpdateVenture(res);
    }
  };

  const getTierColor = (tier: string) => {
    switch (tier) {
      case 'STRONG_GO':
        return 'text-[#10B981] bg-[#064E3B] border-[#10B981]';
      case 'GO':
        return 'text-[#3B82F6] bg-[#1E3A8A] border-[#3B82F6]';
      case 'CAUTION':
        return 'text-[#F59E0B] bg-[#78350F] border-[#F59E0B]';
      case 'PIVOT':
        return 'text-[#EF4444] bg-[#7F1D1D] border-[#EF4444]';
      default:
        return 'text-[#94A3B8] bg-[#1E293B] border-[#475569]';
    }
  };

  return (
    <div id="validator-screen" className="space-y-6">
      {/* Test presets bar */}
      <div className="bg-[#162235] border border-[#273852] rounded-xl px-5 py-3 flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2 text-xs text-[#94A3B8]">
          <Sparkles className="w-4 h-4 text-[#10B981]" />
          <span>Rule-Based NLP Engine with Lookbehind Negation Detection (e.g., &quot;no demand&quot;, &quot;lacks competition&quot;)</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-xs text-[#64748B]">Load Presets:</span>
          <button
            onClick={() => loadSamplePreset('strong')}
            className="px-2.5 py-1 text-xs rounded bg-[#1F2E47] hover:bg-[#2A3E5E] text-[#10B981] border border-[#273852] transition-colors cursor-pointer"
          >
            Strong Go (SaaS)
          </button>
          <button
            onClick={() => loadSamplePreset('pivot')}
            className="px-2.5 py-1 text-xs rounded bg-[#1F2E47] hover:bg-[#2A3E5E] text-[#EF4444] border border-[#273852] transition-colors cursor-pointer"
          >
            Negation / Pivot Sample
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Left Form: 5 Columns */}
        <div className="lg:col-span-5 bg-[#162235] border border-[#273852] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#273852] pb-3">
            <h3 className="text-sm font-bold text-[#F1F5F9] flex items-center gap-2">
              <Target className="w-4 h-4 text-[#10B981]" />
              <span>Venture Concept Definition</span>
            </h3>
            <span className="text-xs text-[#94A3B8]">5 Parameters</span>
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              Startup Name
            </label>
            <input
              id="input-startup-name"
              type="text"
              name="startupName"
              value={formData.startupName}
              onChange={handleInputChange}
              className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              Target Customer (ICP)
            </label>
            <input
              id="input-target-customer"
              type="text"
              name="targetCustomer"
              value={formData.targetCustomer}
              onChange={handleInputChange}
              className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              Problem Statement
            </label>
            <textarea
              id="input-problem-statement"
              name="problemStatement"
              rows={3}
              value={formData.problemStatement}
              onChange={handleInputChange}
              className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981] resize-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              Proposed Solution & Defensibility
            </label>
            <textarea
              id="input-proposed-solution"
              name="proposedSolution"
              rows={3}
              value={formData.proposedSolution}
              onChange={handleInputChange}
              className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981] resize-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              Business Model & Monetization
            </label>
            <input
              id="input-business-model"
              type="text"
              name="businessModel"
              value={formData.businessModel}
              onChange={handleInputChange}
              className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
            />
          </div>

          <div className="pt-2 flex flex-col gap-2">
            <button
              id="btn-run-nlp-evaluation"
              onClick={handleEvaluate}
              className="w-full flex items-center justify-center gap-2 px-4 py-2.5 rounded-lg text-xs font-bold text-white bg-[#10B981] hover:bg-[#059669] transition-all cursor-pointer shadow-xs"
            >
              <BrainCircuit className="w-4 h-4" />
              <span>Run Rule-Based NLP Pipeline</span>
            </button>

            <button
              id="btn-save-to-database"
              onClick={handleSave}
              className="w-full flex items-center justify-center gap-2 px-4 py-2 rounded-lg text-xs font-semibold text-[#CBD5E1] bg-[#1F2E47] hover:bg-[#2A3E5E] border border-[#273852] transition-colors cursor-pointer"
            >
              {savedSuccess ? (
                <>
                  <Check className="w-4 h-4 text-[#10B981]" />
                  <span className="text-[#10B981]">Saved to Database!</span>
                </>
              ) : (
                <>
                  <Save className="w-4 h-4" />
                  <span>Save Plan to Database</span>
                </>
              )}
            </button>
          </div>
        </div>

        {/* Right Output: 7 Columns */}
        <div className="lg:col-span-7 space-y-4">
          {/* Top Score Banner */}
          <div className="bg-[#162235] border border-[#273852] rounded-xl p-5">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
              <div>
                <span className="text-xs text-[#94A3B8] uppercase font-bold tracking-wider">
                  Overall Composite Decision Score
                </span>
                <div className="flex items-baseline gap-3 mt-1">
                  <span className="text-4xl font-extrabold text-[#F1F5F9] tracking-tight">
                    {currentVenture.overallScore}
                  </span>
                  <span className="text-sm font-semibold text-[#64748B]">/ 100</span>
                  <span
                    className={`text-xs px-3 py-1 rounded-full font-bold border ${getTierColor(
                      currentVenture.decisionTier
                    )}`}
                  >
                    {currentVenture.decisionTier.replace('_', ' ')}
                  </span>
                </div>
              </div>

              {/* Sub-scores */}
              <div className="grid grid-cols-2 gap-2 text-xs">
                <div className="bg-[#0E1828] border border-[#273852] rounded-lg p-2">
                  <span className="text-[#94A3B8] block text-[10px]">Market Demand (30%)</span>
                  <span className="text-sm font-bold text-[#F1F5F9]">{currentVenture.marketScore}</span>
                </div>
                <div className="bg-[#0E1828] border border-[#273852] rounded-lg p-2">
                  <span className="text-[#94A3B8] block text-[10px]">Feasibility (30%)</span>
                  <span className="text-sm font-bold text-[#F1F5F9]">{currentVenture.feasibilityScore}</span>
                </div>
                <div className="bg-[#0E1828] border border-[#273852] rounded-lg p-2">
                  <span className="text-[#94A3B8] block text-[10px]">Defensible Moat (20%)</span>
                  <span className="text-sm font-bold text-[#F1F5F9]">{currentVenture.competitionScore}</span>
                </div>
                <div className="bg-[#0E1828] border border-[#273852] rounded-lg p-2">
                  <span className="text-[#94A3B8] block text-[10px]">Concept Depth (20%)</span>
                  <span className="text-sm font-bold text-[#F1F5F9]">{currentVenture.depthScore}</span>
                </div>
              </div>
            </div>
          </div>

          {/* 4-Box SWOT Matrix */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {/* Strengths */}
            <div className="bg-[#162235] border border-[#273852] rounded-xl p-4">
              <div className="flex items-center gap-2 mb-2 text-[#10B981] font-bold text-xs">
                <Zap className="w-3.5 h-3.5" />
                <span>Strengths</span>
              </div>
              <ul className="space-y-1.5">
                {currentVenture.strengths.map((s, i) => (
                  <li key={i} className="text-xs text-[#CBD5E1] bg-[#0E1828] p-2 rounded-md border border-[#273852]">
                    {s}
                  </li>
                ))}
              </ul>
            </div>

            {/* Weaknesses */}
            <div className="bg-[#162235] border border-[#273852] rounded-xl p-4">
              <div className="flex items-center gap-2 mb-2 text-[#F59E0B] font-bold text-xs">
                <HelpCircle className="w-3.5 h-3.5" />
                <span>Weaknesses</span>
              </div>
              <ul className="space-y-1.5">
                {currentVenture.weaknesses.map((w, i) => (
                  <li key={i} className="text-xs text-[#CBD5E1] bg-[#0E1828] p-2 rounded-md border border-[#273852]">
                    {w}
                  </li>
                ))}
              </ul>
            </div>

            {/* Opportunities */}
            <div className="bg-[#162235] border border-[#273852] rounded-xl p-4">
              <div className="flex items-center gap-2 mb-2 text-[#3B82F6] font-bold text-xs">
                <Target className="w-3.5 h-3.5" />
                <span>Opportunities</span>
              </div>
              <ul className="space-y-1.5">
                {currentVenture.opportunities.map((o, i) => (
                  <li key={i} className="text-xs text-[#CBD5E1] bg-[#0E1828] p-2 rounded-md border border-[#273852]">
                    {o}
                  </li>
                ))}
              </ul>
            </div>

            {/* Threats */}
            <div className="bg-[#162235] border border-[#273852] rounded-xl p-4">
              <div className="flex items-center gap-2 mb-2 text-[#EF4444] font-bold text-xs">
                <AlertOctagon className="w-3.5 h-3.5" />
                <span>Threats</span>
              </div>
              <ul className="space-y-1.5">
                {currentVenture.threats.map((t, i) => (
                  <li key={i} className="text-xs text-[#CBD5E1] bg-[#0E1828] p-2 rounded-md border border-[#273852]">
                    {t}
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* Failure Risks Box */}
          <div className="bg-[#162235] border border-[#EF4444]/30 rounded-xl p-4">
            <div className="flex items-center gap-2 mb-2 text-[#EF4444] font-bold text-xs">
              <Shield className="w-4 h-4" />
              <span>Critical Failure Risks & Negation Alerts</span>
            </div>
            <div className="space-y-2">
              {currentVenture.risks.map((r, i) => (
                <div
                  key={i}
                  className="text-xs text-[#FCA5A5] bg-[#7F1D1D]/20 border border-[#7F1D1D]/50 p-2.5 rounded-lg flex items-start gap-2"
                >
                  <span className="w-1.5 h-1.5 rounded-full bg-[#EF4444] mt-1.5 shrink-0" />
                  <span>{r}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
