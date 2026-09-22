import React from 'react';
import { VentureIdea } from '../types';
import { Code2, Sparkles, FolderArchive } from 'lucide-react';

interface TopHeaderProps {
  currentTab: string;
  activeVenture: VentureIdea;
  onOpenCodeHub: () => void;
  onQuickEvaluate: () => void;
}

export const TopHeader: React.FC<TopHeaderProps> = ({
  currentTab,
  activeVenture,
  onOpenCodeHub,
  onQuickEvaluate
}) => {
  const titles: Record<string, { title: string; subtitle: string }> = {
    overview: {
      title: 'Executive Snapshot & Health Cockpit',
      subtitle: 'Holistic intelligence combining NLP validation, equity dilution, and cash survival.'
    },
    validator: {
      title: 'Idea Validation & Rule-Based NLP Pipeline',
      subtitle: 'Deterministic concept scoring with lookbehind negation detection and 4-box SWOT.'
    },
    captable: {
      title: 'CapTable & Equity Dilution Simulator',
      subtitle: 'Financial math using java.math.BigDecimal precision with live Graphics2D donut visualization.'
    },
    burnwatch: {
      title: 'BurnWatch — Cash Flow & Survival Tracker',
      subtitle: 'Monthly financial ledger tracking net burn, cash survival runway, and zero-cash horizon.'
    },
    pitchcraft: {
      title: 'PitchCraft — Pitch Deck & Blueprint Exporter',
      subtitle: 'Generates structured Markdown (.md) and printable browser PDF-ready HTML documents.'
    },
    history: {
      title: 'Saved Plans & Evaluated Ventures History',
      subtitle: 'Direct MySQL database integration with asynchronous SwingWorker retrieval.'
    }
  };

  const currentInfo = titles[currentTab] || {
    title: 'VentureLens Desktop Suite',
    subtitle: 'Decision intelligence platform for startup founders.'
  };

  const getTierStyles = (tier: string) => {
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

  return (
    <header
      id="venturelens-top-header"
      className="h-18 px-6 bg-[#162235]/90 backdrop-blur-xs border-b border-[#273852] flex items-center justify-between shrink-0"
    >
      <div>
        <div className="flex items-center gap-3">
          <h2 className="text-base font-bold text-[#F1F5F9]">{currentInfo.title}</h2>
          <span
            id="active-venture-badge"
            className={`text-xs px-2.5 py-0.5 rounded-full font-bold border ${getTierStyles(
              activeVenture.decisionTier
            )}`}
          >
            {activeVenture.decisionTier.replace('_', ' ')} &bull; {activeVenture.overallScore}/100
          </span>
        </div>
        <p className="text-xs text-[#94A3B8]">{currentInfo.subtitle}</p>
      </div>

      <div className="flex items-center gap-3">
        <button
          id="btn-top-quick-evaluate"
          onClick={onQuickEvaluate}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold text-[#F1F5F9] bg-[#1F2E47] hover:bg-[#273854] border border-[#273852] transition-colors cursor-pointer"
        >
          <Sparkles className="w-3.5 h-3.5 text-[#10B981]" />
          <span>Active: {activeVenture.startupName}</span>
        </button>

        <button
          id="btn-top-open-code-hub"
          onClick={onOpenCodeHub}
          className="flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-xs font-semibold text-white bg-[#10B981] hover:bg-[#059669] transition-colors shadow-xs cursor-pointer"
        >
          <Code2 className="w-3.5 h-3.5" />
          <span>Java Code & Schema Hub</span>
        </button>
      </div>
    </header>
  );
};
