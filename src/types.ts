export type DecisionTierType = 'STRONG_GO' | 'GO' | 'CAUTION' | 'PIVOT';

export interface User {
  id: number;
  username: string;
  email: string;
  createdAt: string;
}

export interface VentureIdea {
  id: number;
  userId: number;
  startupName: string;
  targetCustomer: string;
  problemStatement: string;
  proposedSolution: string;
  businessModel: string;
  overallScore: number;
  marketScore: number;
  feasibilityScore: number;
  competitionScore: number;
  depthScore: number;
  decisionTier: DecisionTierType;
  strengths: string[];
  weaknesses: string[];
  opportunities: string[];
  threats: string[];
  risks: string[];
  createdAt: string;
}

export interface CapTableState {
  founder1InitialPct: number;
  founder2InitialPct: number;
  esopInitialPct: number;
  preMoneyValuation: number;
  investmentAmount: number;
}

export interface BurnEntry {
  id: number;
  ventureId: number;
  monthLabel: string;
  category: string;
  entryType: 'EXPENSE' | 'REVENUE';
  amount: number;
  notes: string;
}

export interface JavaFileSource {
  path: string;
  name: string;
  pkg: string;
  category: 'Entry' | 'Model' | 'Analysis' | 'DAO' | 'UI Component' | 'UI View' | 'Utils' | 'SQL';
  description: string;
  code: string;
}
