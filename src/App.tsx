import React, { useState } from 'react';
import { VentureIdea, CapTableState, BurnEntry, User } from './types';
import { Sidebar } from './components/Sidebar';
import { TopHeader } from './components/TopHeader';
import { OverviewView } from './components/OverviewView';
import { IdeaValidatorView } from './components/IdeaValidatorView';
import { CapTableView } from './components/CapTableView';
import { BurnWatchView } from './components/BurnWatchView';
import { PitchCraftView } from './components/PitchCraftView';
import { HistoryView } from './components/HistoryView';
import { JavaCodeHubModal } from './components/JavaCodeHubModal';
import { AuthModal } from './components/AuthModal';

const INITIAL_VENTURE: VentureIdea = {
  id: 1,
  userId: 1,
  startupName: 'DataPulse AI',
  targetCustomer: 'Enterprise CTOs and B2B SaaS engineering leaders',
  problemStatement: 'Modern distributed data pipelines fail silently in production, causing millions in undetected revenue leakage and SLA contract breaches.',
  proposedSolution: 'Automated, cloud-native real-time telemetry agent with proprietary anomaly detection algorithms and zero-config SDK integrations.',
  businessModel: 'B2B Enterprise SaaS Subscription ($3,500/cluster/month)',
  overallScore: 84.5,
  marketScore: 88.0,
  feasibilityScore: 85.0,
  competitionScore: 80.0,
  depthScore: 85.0,
  decisionTier: 'STRONG_GO',
  strengths: [
    'Engineered technical architecture with concrete feasibility markers.',
    'Predictable recurring revenue model with compounding SaaS dynamics.',
    'Clear core problem articulation addressing enterprise data loss.'
  ],
  weaknesses: [
    'Enterprise sales cycle velocity requires dedicated deployment specialists.'
  ],
  opportunities: [
    'Expanding addressable market demand across cloud data engineering teams.',
    'Favorable tailwinds for automated reliability and telemetry tooling.'
  ],
  threats: [
    'Aggressive feature creep from established APM and observability suites.'
  ],
  risks: [
    'Execution timeline risk: Enterprise procurement security reviews (SOC2/FedRAMP).'
  ],
  createdAt: new Date().toISOString()
};

const SEED_HISTORY: VentureIdea[] = [
  INITIAL_VENTURE,
  {
    id: 2,
    userId: 1,
    startupName: 'CloudShield Mesh',
    targetCustomer: 'DevSecOps teams in regulated financial services',
    problemStatement: 'Developers struggle with compliance drift and misconfigured microservice access keys across hybrid clouds.',
    proposedSolution: 'Automated zero-trust infrastructure identity broker with cryptographic ephemeral credentials.',
    businessModel: 'Usage-based SaaS per active node',
    overallScore: 74.2,
    marketScore: 76.0,
    feasibilityScore: 78.0,
    competitionScore: 68.0,
    depthScore: 72.0,
    decisionTier: 'GO',
    strengths: ['Strong regulatory tailwinds', 'Clear cryptographic defensibility'],
    weaknesses: ['Complex initial configuration for legacy servers'],
    opportunities: ['Banking compliance mandates driving budget allocation'],
    threats: ['Cloud hyperscalers integrating native IAM tools'],
    risks: ['High customer support overhead during initial pilot onboarding'],
    createdAt: new Date(Date.now() - 86400000 * 3).toISOString()
  },
  {
    id: 3,
    userId: 1,
    startupName: 'ClinicFlow Portal',
    targetCustomer: 'Independent physical therapy practices',
    problemStatement: 'Private clinics waste 20 hours a week on manual patient scheduling and insurance claims.',
    proposedSolution: 'Simple web scheduling with automated SMS reminders and basic claim filing.',
    businessModel: '$149/clinic flat monthly fee',
    overallScore: 58.0,
    marketScore: 60.0,
    feasibilityScore: 65.0,
    competitionScore: 45.0,
    depthScore: 55.0,
    decisionTier: 'CAUTION',
    strengths: ['Low technical hurdle', 'Immediate time savings for clinic staff'],
    weaknesses: ['Low defensibility; easily replicated by horizontal booking tools'],
    opportunities: ['Bundle with patient billing processing for interchange revenue'],
    threats: ['Intense price pressure from legacy EHR incumbents'],
    risks: ['High customer churn rate and low willingness to pay'],
    createdAt: new Date(Date.now() - 86400000 * 7).toISOString()
  },
  {
    id: 4,
    userId: 1,
    startupName: 'CampusBites Delivery',
    targetCustomer: 'College students studying late',
    problemStatement: 'Food delivery apps charge high delivery fees on campuses, but there is no demand during daytime and no competition moat.',
    proposedSolution: 'Peer-to-peer student delivery in dorms, not feasible with current campus building security rules.',
    businessModel: 'Small tip fee per delivery',
    overallScore: 42.5,
    marketScore: 35.0,
    feasibilityScore: 40.0,
    competitionScore: 30.0,
    depthScore: 65.0,
    decisionTier: 'PIVOT',
    strengths: ['High student density'],
    weaknesses: ['Severe lack of competitive moat; unit economics are unviable'],
    opportunities: ['Pivot to bulk scheduled catering for campus student clubs'],
    threats: ['Campus dining hall expansions and DoorDash campus passes'],
    risks: ["CRITICAL: Negative demand indicator detected ('no demand' / 'lacks moat'). Severe validation deficit."],
    createdAt: new Date(Date.now() - 86400000 * 14).toISOString()
  }
];

const INITIAL_CAPTABLE: CapTableState = {
  founder1InitialPct: 50.0,
  founder2InitialPct: 40.0,
  esopInitialPct: 10.0,
  preMoneyValuation: 6000000.0,
  investmentAmount: 1500000.0
};

const INITIAL_BURN_ENTRIES: BurnEntry[] = [
  { id: 1, ventureId: 1, monthLabel: 'Month 1', category: 'Engineering Salaries', entryType: 'EXPENSE', amount: 22000, notes: 'Lead architect and 2 senior devs' },
  { id: 2, ventureId: 1, monthLabel: 'Month 1', category: 'Cloud Infrastructure (AWS/GCP)', entryType: 'EXPENSE', amount: 4500, notes: 'Telemetry ingestion clusters' },
  { id: 3, ventureId: 1, monthLabel: 'Month 1', category: 'Marketing & Growth Ads', entryType: 'EXPENSE', amount: 3000, notes: 'Targeted developer community outreach' },
  { id: 4, ventureId: 1, monthLabel: 'Month 1', category: 'SaaS Tools & Office', entryType: 'EXPENSE', amount: 1500, notes: 'GitHub, Datadog, Slack, Notion' },
  { id: 5, ventureId: 1, monthLabel: 'Month 1', category: 'B2B Pilot Contracts', entryType: 'REVENUE', amount: 8000, notes: 'Design partner pilot milestone' }
];

export function App() {
  const [currentTab, setCurrentTab] = useState<string>('overview');
  const [user, setUser] = useState<User | null>({
    id: 1,
    username: 'alex_founder',
    email: 'alex@venturelens.io',
    createdAt: new Date().toISOString()
  });

  const [activeVenture, setActiveVenture] = useState<VentureIdea>(INITIAL_VENTURE);
  const [historyVentures, setHistoryVentures] = useState<VentureIdea[]>(SEED_HISTORY);
  const [capTable, setCapTable] = useState<CapTableState>(INITIAL_CAPTABLE);
  const [ledgerEntries, setLedgerEntries] = useState<BurnEntry[]>(INITIAL_BURN_ENTRIES);
  const [startingCash, setStartingCash] = useState<number>(250000);

  const [isCodeHubOpen, setIsCodeHubOpen] = useState(false);
  const [isAuthOpen, setIsAuthOpen] = useState(false);

  const handleSaveVenture = (venture: VentureIdea) => {
    setActiveVenture(venture);
    const existingIdx = historyVentures.findIndex((v) => v.id === venture.id);
    if (existingIdx >= 0) {
      const updated = [...historyVentures];
      updated[existingIdx] = venture;
      setHistoryVentures(updated);
    } else {
      setHistoryVentures([venture, ...historyVentures]);
    }
  };

  const handleSelectVenture = (venture: VentureIdea) => {
    setActiveVenture(venture);
  };

  const handleLogout = () => {
    setUser(null);
    setIsAuthOpen(true);
  };

  return (
    <div className="flex h-screen bg-[#0B1320] text-[#F1F5F9] font-sans antialiased overflow-hidden">
      {/* Navigation Sidebar */}
      <Sidebar
        currentTab={currentTab}
        setCurrentTab={setCurrentTab}
        user={user}
        onLogout={handleLogout}
        onOpenCodeHub={() => setIsCodeHubOpen(true)}
      />

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 h-screen overflow-hidden">
        {/* Top Header */}
        <TopHeader
          currentTab={currentTab}
          activeVenture={activeVenture}
          onOpenCodeHub={() => setIsCodeHubOpen(true)}
          onQuickEvaluate={() => setCurrentTab('validator')}
        />

        {/* View Cards Container */}
        <main className="flex-1 overflow-y-auto p-6 bg-[#0B1320]">
          <div className="max-w-7xl mx-auto pb-10">
            {currentTab === 'overview' && (
              <OverviewView
                user={user}
                venture={activeVenture}
                capTable={capTable}
                ledgerEntries={ledgerEntries}
                startingCash={startingCash}
                onNavigate={setCurrentTab}
              />
            )}

            {currentTab === 'validator' && (
              <IdeaValidatorView
                currentVenture={activeVenture}
                onUpdateVenture={setActiveVenture}
                onSaveVenture={handleSaveVenture}
              />
            )}

            {currentTab === 'captable' && (
              <CapTableView
                capTable={capTable}
                onUpdateCapTable={setCapTable}
              />
            )}

            {currentTab === 'burnwatch' && (
              <BurnWatchView
                entries={ledgerEntries}
                startingCash={startingCash}
                onUpdateEntries={setLedgerEntries}
                onUpdateStartingCash={setStartingCash}
              />
            )}

            {currentTab === 'pitchcraft' && (
              <PitchCraftView
                venture={activeVenture}
                capTable={capTable}
                ledgerEntries={ledgerEntries}
                startingCash={startingCash}
              />
            )}

            {currentTab === 'history' && (
              <HistoryView
                historyVentures={historyVentures}
                activeVentureId={activeVenture.id}
                onSelectVenture={handleSelectVenture}
                onNavigate={setCurrentTab}
              />
            )}
          </div>
        </main>
      </div>

      {/* Modals */}
      <JavaCodeHubModal
        isOpen={isCodeHubOpen}
        onClose={() => setIsCodeHubOpen(false)}
      />

      <AuthModal
        isOpen={isAuthOpen}
        onClose={() => setIsAuthOpen(false)}
        onLogin={(u) => setUser(u)}
      />
    </div>
  );
}

export default App;
