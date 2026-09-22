import React from 'react';
import { VentureIdea, CapTableState, BurnEntry, User } from '../types';
import {
  BrainCircuit,
  Flame,
  PieChart,
  FileSpreadsheet,
  ArrowRight,
  TrendingUp,
  AlertTriangle,
  CheckCircle2,
  Calendar,
  DollarSign
} from 'lucide-react';

interface OverviewViewProps {
  user: User | null;
  venture: VentureIdea;
  capTable: CapTableState;
  ledgerEntries: BurnEntry[];
  startingCash: number;
  onNavigate: (tab: string) => void;
}

export const OverviewView: React.FC<OverviewViewProps> = ({
  user,
  venture,
  capTable,
  ledgerEntries,
  startingCash,
  onNavigate
}) => {
  // Financial runway calculation
  const totalExpenses = ledgerEntries
    .filter((e) => e.entryType === 'EXPENSE')
    .reduce((sum, e) => sum + e.amount, 0);

  const totalRevenue = ledgerEntries
    .filter((e) => e.entryType === 'REVENUE')
    .reduce((sum, e) => sum + e.amount, 0);

  const netMonthlyBurn = totalExpenses - totalRevenue;
  const runwayMonths =
    netMonthlyBurn <= 0
      ? Infinity
      : Math.max(0, Math.round((startingCash / netMonthlyBurn) * 10) / 10);

  // Post-money calculation
  const postMoney = capTable.preMoneyValuation + capTable.investmentAmount;
  const investorPct =
    postMoney > 0 ? (capTable.investmentAmount / postMoney) * 100 : 0;
  const retentionFactor = postMoney > 0 ? capTable.preMoneyValuation / postMoney : 1;
  const founder1Diluted = capTable.founder1InitialPct * retentionFactor;

  const formatCurrency = (val: number) =>
    new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(val);

  return (
    <div id="overview-screen" className="space-y-6">
      {/* Welcome Hero Banner */}
      <div className="bg-[#162235] border border-[#273852] rounded-xl p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="text-xs font-semibold px-2 py-0.5 rounded bg-[#10B981]/20 text-[#10B981] border border-[#10B981]/30">
              Active Evaluation
            </span>
            <span className="text-xs text-[#94A3B8]">
              {user ? `Founder: ${user.username}` : 'Founder: founder_alex'}
            </span>
          </div>
          <h1 className="text-2xl font-bold text-[#F1F5F9] tracking-tight">
            {venture.startupName}
          </h1>
          <p className="text-sm text-[#94A3B8] max-w-2xl mt-1">
            {venture.proposedSolution}
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            id="btn-overview-validator"
            onClick={() => onNavigate('validator')}
            className="flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-semibold text-white bg-[#10B981] hover:bg-[#059669] transition-all cursor-pointer shadow-xs"
          >
            <span>Re-evaluate Idea</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* 4 Core Intelligence Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Card 1: Decision Intelligence */}
        <div className="bg-[#162235] border border-[#273852] rounded-xl p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between text-[#94A3B8] mb-3">
              <span className="text-xs uppercase font-bold tracking-wider">Validation Tier</span>
              <BrainCircuit className="w-4 h-4 text-[#10B981]" />
            </div>
            <div className="text-2xl font-bold text-[#10B981] tracking-tight">
              {venture.decisionTier.replace('_', ' ')}
            </div>
            <div className="text-sm font-semibold text-[#F1F5F9] mt-0.5">
              Score: {venture.overallScore} / 100
            </div>
            <p className="text-xs text-[#94A3B8] mt-2">
              Market: {venture.marketScore} &bull; Feasibility: {venture.feasibilityScore} &bull; Moat: {venture.competitionScore}
            </p>
          </div>
          <button
            onClick={() => onNavigate('validator')}
            className="mt-4 text-xs font-semibold text-[#10B981] hover:underline flex items-center gap-1 cursor-pointer"
          >
            <span>Inspect SWOT Matrix</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>

        {/* Card 2: BurnWatch Survival Runway */}
        <div className="bg-[#162235] border border-[#273852] rounded-xl p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between text-[#94A3B8] mb-3">
              <span className="text-xs uppercase font-bold tracking-wider">Survival Runway</span>
              <Flame className="w-4 h-4 text-[#EF4444]" />
            </div>
            <div className="text-2xl font-bold text-[#F1F5F9] tracking-tight">
              {runwayMonths === Infinity ? 'Infinite' : `${runwayMonths} Mo`}
            </div>
            <div className="text-sm font-semibold text-[#10B981] mt-0.5">
              Net Burn: {formatCurrency(netMonthlyBurn)} / mo
            </div>
            <p className="text-xs text-[#94A3B8] mt-2">
              Cash: {formatCurrency(startingCash)} &bull; Monthly Rev: {formatCurrency(totalRevenue)}
            </p>
          </div>
          <button
            onClick={() => onNavigate('burnwatch')}
            className="mt-4 text-xs font-semibold text-[#10B981] hover:underline flex items-center gap-1 cursor-pointer"
          >
            <span>Open Cash Ledger</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>

        {/* Card 3: CapTable Financing */}
        <div className="bg-[#162235] border border-[#273852] rounded-xl p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between text-[#94A3B8] mb-3">
              <span className="text-xs uppercase font-bold tracking-wider">Post-Money Valuation</span>
              <PieChart className="w-4 h-4 text-[#3B82F6]" />
            </div>
            <div className="text-2xl font-bold text-[#3B82F6] tracking-tight">
              {formatCurrency(postMoney)}
            </div>
            <div className="text-sm font-semibold text-[#F1F5F9] mt-0.5">
              Round: {formatCurrency(capTable.investmentAmount)} ({investorPct.toFixed(1)}%)
            </div>
            <p className="text-xs text-[#94A3B8] mt-2">
              Founder 1 Diluted: {founder1Diluted.toFixed(1)}% &bull; ESOP: {(capTable.esopInitialPct * retentionFactor).toFixed(1)}%
            </p>
          </div>
          <button
            onClick={() => onNavigate('captable')}
            className="mt-4 text-xs font-semibold text-[#10B981] hover:underline flex items-center gap-1 cursor-pointer"
          >
            <span>Simulate Dilution</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>

        {/* Card 4: PitchCraft Blueprint */}
        <div className="bg-[#162235] border border-[#273852] rounded-xl p-5 flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between text-[#94A3B8] mb-3">
              <span className="text-xs uppercase font-bold tracking-wider">Pitch Deck Exporter</span>
              <FileSpreadsheet className="w-4 h-4 text-[#8B5CF6]" />
            </div>
            <div className="text-2xl font-bold text-[#F1F5F9] tracking-tight">
              10 Slides
            </div>
            <div className="text-sm font-semibold text-[#10B981] mt-0.5">
              Ready for Export
            </div>
            <p className="text-xs text-[#94A3B8] mt-2">
              Markdown (.md) and Printable Browser PDF-ready HTML.
            </p>
          </div>
          <button
            onClick={() => onNavigate('pitchcraft')}
            className="mt-4 text-xs font-semibold text-[#10B981] hover:underline flex items-center gap-1 cursor-pointer"
          >
            <span>Preview & Export Deck</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </div>
      </div>

      {/* Strategic Summary Split */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Core Strengths & Moats */}
        <div className="bg-[#162235] border border-[#273852] rounded-xl p-5">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-bold text-[#F1F5F9] flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-[#10B981]" />
              <span>Architectural Strengths & Defensibility</span>
            </h3>
            <span className="text-xs text-[#94A3B8]">{venture.strengths.length} markers</span>
          </div>
          <ul className="space-y-2.5">
            {venture.strengths.map((s, idx) => (
              <li
                key={idx}
                className="text-xs text-[#CBD5E1] bg-[#0E1828] border border-[#273852] rounded-lg p-3 flex items-start gap-2.5"
              >
                <span className="w-1.5 h-1.5 rounded-full bg-[#10B981] mt-1.5 shrink-0" />
                <span>{s}</span>
              </li>
            ))}
          </ul>
        </div>

        {/* Critical Failure Risks */}
        <div className="bg-[#162235] border border-[#273852] rounded-xl p-5">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-bold text-[#F1F5F9] flex items-center gap-2">
              <AlertTriangle className="w-4 h-4 text-[#EF4444]" />
              <span>Critical Risks & Negation Flags</span>
            </h3>
            <span className="text-xs text-[#94A3B8]">{venture.risks.length} flagged</span>
          </div>
          <ul className="space-y-2.5">
            {venture.risks.map((r, idx) => (
              <li
                key={idx}
                className="text-xs text-[#CBD5E1] bg-[#0E1828] border border-[#273852] rounded-lg p-3 flex items-start gap-2.5"
              >
                <span className="w-1.5 h-1.5 rounded-full bg-[#EF4444] mt-1.5 shrink-0" />
                <span>{r}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};
