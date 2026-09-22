import React, { useState } from 'react';
import { VentureIdea } from '../types';
import {
  History,
  CheckCircle2,
  Calendar,
  Layers,
  ArrowRight,
  Database,
  Search
} from 'lucide-react';

interface HistoryViewProps {
  historyVentures: VentureIdea[];
  activeVentureId: number;
  onSelectVenture: (venture: VentureIdea) => void;
  onNavigate: (tab: string) => void;
}

export const HistoryView: React.FC<HistoryViewProps> = ({
  historyVentures,
  activeVentureId,
  onSelectVenture,
  onNavigate
}) => {
  const [selectedId, setSelectedId] = useState<number>(activeVentureId || (historyVentures[0]?.id ?? 1));
  const [searchTerm, setSearchTerm] = useState('');

  const filtered = historyVentures.filter(
    (v) =>
      v.startupName.toLowerCase().includes(searchTerm.toLowerCase()) ||
      v.targetCustomer.toLowerCase().includes(searchTerm.toLowerCase()) ||
      v.businessModel.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const selectedVenture = historyVentures.find((v) => v.id === selectedId) || historyVentures[0];

  const getTierBadgeStyle = (tier: string) => {
    switch (tier) {
      case 'STRONG_GO':
        return 'bg-[#064E3B] text-[#10B981] border-[#10B981]';
      case 'GO':
        return 'bg-[#1E3A8A] text-[#3B82F6] border-[#3B82F6]';
      case 'CAUTION':
        return 'bg-[#78350F] text-[#F59E0B] border-[#F59E0B]';
      case 'PIVOT':
        return 'bg-[#7F1D1D] text-[#EF4444] border-[#EF4444]';
      default:
        return 'bg-[#1E293B] text-[#94A3B8] border-[#475569]';
    }
  };

  const handleRowClick = (venture: VentureIdea) => {
    setSelectedId(venture.id);
  };

  const handleLoadActive = () => {
    if (selectedVenture) {
      onSelectVenture(selectedVenture);
      onNavigate('overview');
    }
  };

  return (
    <div id="history-screen" className="space-y-6">
      {/* Top Bar */}
      <div className="bg-[#162235] border border-[#273852] rounded-xl p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 className="text-base font-bold text-[#F1F5F9] flex items-center gap-2">
            <History className="w-5 h-5 text-[#10B981]" />
            <span>Saved Plans & Venture Evaluation History</span>
          </h3>
          <p className="text-xs text-[#94A3B8] mt-1">
            Persisted MySQL ventures with custom high-contrast DecisionBadgeRenderer pills.
          </p>
        </div>

        <div className="relative">
          <Search className="w-4 h-4 text-[#64748B] absolute left-3 top-2.5" />
          <input
            id="input-search-history"
            type="text"
            placeholder="Search saved ventures..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-9 pr-3 py-1.5 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981] w-64"
          />
        </div>
      </div>

      {/* Main Table Card */}
      <div className="bg-[#162235] border border-[#273852] rounded-xl p-5 space-y-4">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="border-b border-[#273852] text-[#94A3B8]">
                <th className="py-2.5 px-3 font-semibold">ID</th>
                <th className="py-2.5 px-3 font-semibold">Startup Name</th>
                <th className="py-2.5 px-3 font-semibold text-center">Decision Tier</th>
                <th className="py-2.5 px-3 font-semibold text-right">Composite Score</th>
                <th className="py-2.5 px-3 font-semibold">Business Model</th>
                <th className="py-2.5 px-3 font-semibold">Created Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#273852]">
              {filtered.map((venture) => {
                const isSelected = venture.id === selectedId;
                return (
                  <tr
                    key={venture.id}
                    onClick={() => handleRowClick(venture)}
                    className={`cursor-pointer transition-colors ${
                      isSelected
                        ? 'bg-[#1F2E47] border-l-4 border-l-[#10B981]'
                        : 'hover:bg-[#1E2C44]'
                    }`}
                  >
                    <td className="py-3 px-3 text-[#64748B] font-mono">#{venture.id}</td>
                    <td className="py-3 px-3 text-[#F1F5F9] font-bold">{venture.startupName}</td>
                    <td className="py-3 px-3 text-center">
                      {/* Custom Rounded Colored Pill Badge */}
                      <span
                        className={`inline-block px-3 py-1 rounded-full text-[11px] font-bold border tracking-wide ${getTierBadgeStyle(
                          venture.decisionTier
                        )}`}
                      >
                        {venture.decisionTier.replace('_', ' ')}
                      </span>
                    </td>
                    <td className="py-3 px-3 text-right font-mono font-bold text-[#F1F5F9]">
                      {venture.overallScore} / 100
                    </td>
                    <td className="py-3 px-3 text-[#CBD5E1] truncate max-w-xs">{venture.businessModel}</td>
                    <td className="py-3 px-3 text-[#64748B]">
                      {new Date(venture.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Dynamic Bottom Inspection Card */}
      {selectedVenture && (
        <div className="bg-[#162235] border border-[#273852] rounded-xl p-6 space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-[#273852] pb-4">
            <div className="flex items-center gap-3">
              <h4 className="text-lg font-bold text-[#F1F5F9]">
                {selectedVenture.startupName}
              </h4>
              <span
                className={`px-3 py-0.5 rounded-full text-xs font-bold border ${getTierBadgeStyle(
                  selectedVenture.decisionTier
                )}`}
              >
                {selectedVenture.decisionTier.replace('_', ' ')} &bull; {selectedVenture.overallScore}/100
              </span>
            </div>

            <button
              id="btn-load-into-workspace"
              onClick={handleLoadActive}
              className="flex items-center gap-2 px-4 py-2 rounded-lg text-xs font-bold text-white bg-[#10B981] hover:bg-[#059669] transition-all cursor-pointer shadow-xs"
            >
              <span>Load into Active Workspace</span>
              <ArrowRight className="w-4 h-4" />
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-[#0E1828] border border-[#273852] rounded-lg p-4 space-y-1.5">
              <span className="text-[10px] uppercase font-bold text-[#94A3B8]">Problem Statement</span>
              <p className="text-xs text-[#CBD5E1] leading-relaxed">
                {selectedVenture.problemStatement}
              </p>
            </div>

            <div className="bg-[#0E1828] border border-[#273852] rounded-lg p-4 space-y-1.5">
              <span className="text-[10px] uppercase font-bold text-[#94A3B8]">Proposed Solution</span>
              <p className="text-xs text-[#CBD5E1] leading-relaxed">
                {selectedVenture.proposedSolution}
              </p>
            </div>
          </div>

          <div className="text-xs text-[#94A3B8] flex flex-wrap gap-4 pt-2">
            <span>Market Demand: <strong className="text-[#F1F5F9]">{selectedVenture.marketScore}</strong></span>
            <span>Technical Feasibility: <strong className="text-[#F1F5F9]">{selectedVenture.feasibilityScore}</strong></span>
            <span>Competitive Moat: <strong className="text-[#F1F5F9]">{selectedVenture.competitionScore}</strong></span>
            <span>Concept Depth: <strong className="text-[#F1F5F9]">{selectedVenture.depthScore}</strong></span>
          </div>
        </div>
      )}
    </div>
  );
};
