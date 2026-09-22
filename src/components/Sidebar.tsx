import React from 'react';
import {
  LayoutDashboard,
  BrainCircuit,
  PieChart,
  Flame,
  FileSpreadsheet,
  History,
  LogOut,
  Code2,
  ShieldCheck
} from 'lucide-react';
import { User } from '../types';

interface SidebarProps {
  currentTab: string;
  setCurrentTab: (tab: string) => void;
  user: User | null;
  onLogout: () => void;
  onOpenCodeHub: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({
  currentTab,
  setCurrentTab,
  user,
  onLogout,
  onOpenCodeHub
}) => {
  const navItems = [
    { id: 'overview', label: 'Overview / Snapshot', icon: LayoutDashboard },
    { id: 'validator', label: 'Idea Validator', icon: BrainCircuit },
    { id: 'captable', label: 'CapTable Simulator', icon: PieChart },
    { id: 'burnwatch', label: 'BurnWatch (Cash Flow)', icon: Flame },
    { id: 'pitchcraft', label: 'PitchCraft (Deck Exporter)', icon: FileSpreadsheet },
    { id: 'history', label: 'Saved Plans & History', icon: History }
  ];

  return (
    <aside
      id="venturelens-sidebar"
      className="w-64 bg-[#162235] border-r border-[#273852] flex flex-col justify-between shrink-0 h-screen select-none"
    >
      {/* Top Branding */}
      <div>
        <div className="p-5 border-b border-[#273852]">
          <div className="flex items-center gap-2.5">
            <div className="w-8 h-8 rounded-lg bg-[#10B981]/15 border border-[#10B981]/30 flex items-center justify-center text-[#10B981]">
              <BrainCircuit className="w-5 h-5" />
            </div>
            <div>
              <h1 className="text-lg font-bold text-[#F1F5F9] tracking-tight leading-none">
                VentureLens
              </h1>
              <span className="text-[10px] uppercase font-semibold tracking-wider text-[#94A3B8]">
                Startup Operating System
              </span>
            </div>
          </div>
        </div>

        {/* Navigation Items */}
        <nav className="p-3 space-y-1">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = currentTab === item.id;
            return (
              <button
                key={item.id}
                id={`nav-${item.id}`}
                onClick={() => setCurrentTab(item.id)}
                className={`w-full flex items-center gap-3 px-3.5 py-2.5 rounded-lg text-sm font-medium transition-all duration-150 text-left ${
                  isActive
                    ? 'bg-[#1F2E47] text-[#10B981] shadow-xs'
                    : 'text-[#94A3B8] hover:text-[#F1F5F9] hover:bg-[#1E2C44]'
                }`}
              >
                <Icon className={`w-4 h-4 shrink-0 ${isActive ? 'text-[#10B981]' : 'text-[#64748B]'}`} />
                <span className="truncate">{item.label}</span>
              </button>
            );
          })}
        </nav>
      </div>

      {/* Bottom Session & Architecture Button */}
      <div className="p-3 border-t border-[#273852] space-y-2">
        {/* Java Source Code & Architecture Hub Button */}
        <button
          id="btn-open-code-hub"
          onClick={onOpenCodeHub}
          className="w-full flex items-center justify-between px-3 py-2 rounded-lg text-xs font-semibold text-[#10B981] bg-[#10B981]/10 hover:bg-[#10B981]/20 border border-[#10B981]/30 transition-all cursor-pointer"
        >
          <div className="flex items-center gap-2">
            <Code2 className="w-4 h-4" />
            <span>Java Source & Architecture</span>
          </div>
          <span className="text-[10px] px-1.5 py-0.5 rounded bg-[#10B981]/20 font-mono">JDK 21</span>
        </button>

        {/* User Session Pill */}
        <div className="bg-[#0E1828] border border-[#273852] rounded-lg p-2.5 flex items-center justify-between">
          <div className="flex items-center gap-2 overflow-hidden">
            <div className="w-2.5 h-2.5 rounded-full bg-[#10B981] shrink-0 animate-pulse" />
            <div className="overflow-hidden">
              <p className="text-xs font-semibold text-[#F1F5F9] truncate">
                {user ? user.username : 'founder_alex'}
              </p>
              <p className="text-[10px] text-[#64748B] truncate flex items-center gap-1">
                <ShieldCheck className="w-3 h-3 text-[#10B981]" /> Authenticated
              </p>
            </div>
          </div>
          <button
            id="btn-logout"
            onClick={onLogout}
            title="Log Out"
            className="text-[#64748B] hover:text-[#EF4444] transition-colors p-1"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </div>
    </aside>
  );
};
