import { DecisionTierType, VentureIdea } from '../types';

const STOP_WORDS = new Set([
  'a', 'about', 'above', 'after', 'again', 'against', 'all', 'am', 'an', 'and', 'any', 'are',
  'as', 'at', 'be', 'because', 'been', 'before', 'being', 'below', 'between', 'both', 'but',
  'by', 'can', 'cannot', 'could', 'did', 'do', 'does', 'doing', 'down', 'during', 'each', 'few',
  'for', 'from', 'further', 'had', 'has', 'have', 'having', 'he', 'her', 'here', 'hers', 'herself',
  'him', 'himself', 'his', 'how', 'i', 'if', 'in', 'into', 'is', 'it', 'its', 'itself', 'just',
  'me', 'more', 'most', 'my', 'myself', 'no', 'nor', 'not', 'now', 'of', 'off', 'on', 'once',
  'only', 'or', 'other', 'our', 'ours', 'ourselves', 'out', 'over', 'own', 'same', 'she', 'should',
  'so', 'some', 'such', 'than', 'that', 'the', 'their', 'theirs', 'them', 'themselves', 'then',
  'there', 'these', 'they', 'this', 'those', 'through', 'to', 'too', 'under', 'until', 'up',
  'very', 'was', 'we', 'were', 'what', 'when', 'where', 'which', 'while', 'who', 'whom', 'why',
  'with', 'would', 'you', 'your', 'yours', 'yourself', 'yourselves'
]);

const NEGATION_PATTERN = /(?:\b(?:no|not|never|without|lacks?|lacking)\s+|\black\s+of\s+)([a-z0-9_-]+)/gi;

const MARKET_TERMS = new Set([
  'market', 'b2b', 'enterprise', 'smb', 'consumer', 'tam', 'sam', 'som', 'demand', 'arr', 'mrr',
  'retention', 'churn', 'recurring', 'subscription', 'expansion', 'global', 'segment', 'volume',
  'cac', 'ltv', 'pipeline', 'revenue', 'contracts', 'buyers', 'growth', 'scale', 'customers',
  'monetization', 'paying', 'pricing', 'marketplace', 'vertical'
]);

const COMPETITION_TERMS = new Set([
  'moat', 'proprietary', 'patent', 'patented', 'defensible', 'differentiation', 'network',
  'switching', 'barrier', 'first-mover', 'monopoly', 'ip', 'lock-in', 'ecosystem', 'niche',
  'specialized', 'exclusive', 'advantage', 'unmatched', 'unique', 'defensibility'
]);

const FEASIBILITY_TERMS = new Set([
  'api', 'automated', 'cloud', 'mvp', 'prototype', 'architecture', 'algorithm', 'algorithms',
  'devops', 'infrastructure', 'latency', 'throughput', 'integration', 'compliance', 'security',
  'pipeline', 'scalable', 'microservices', 'database', 'sdk', 'modular', 'engine', 'tested',
  'framework', 'deploy', 'distributed', 'serverless', 'analytics', 'ai', 'ml', 'telemetry'
]);

const RISK_TERMS = new Set([
  'unproven', 'commoditized', 'crowded', 'regulation', 'regulatory', 'liability', 'expensive',
  'slow', 'fragile', 'dependency', 'churn', 'friction', 'burn', 'unclear', 'doubtful', 'risky'
]);

export function evaluateVentureNLP(input: Partial<VentureIdea>): VentureIdea {
  const name = input.startupName || 'Untitled Venture';
  const customer = input.targetCustomer || '';
  const problem = input.problemStatement || '';
  const solution = input.proposedSolution || '';
  const model = input.businessModel || '';

  const combined = `${name} ${customer} ${problem} ${solution} ${model}`.toLowerCase();

  // 1. Detect Negated Concepts using regex
  const negatedConcepts = new Set<string>();
  let match: RegExpExecArray | null;
  const regex = new RegExp(NEGATION_PATTERN.source, 'gi');
  while ((match = regex.exec(combined)) !== null) {
    if (match[1]) {
      negatedConcepts.add(match[1].toLowerCase());
    }
  }

  // 2. Tokenize and filter
  const rawTokens = combined.split(/[^a-zA-Z0-9_-]+/);
  const tokens: string[] = [];
  for (const rt of rawTokens) {
    const token = rt.trim().toLowerCase();
    if (token.length > 1 && !STOP_WORDS.has(token)) {
      tokens.push(token);
    }
  }

  // 3. Count hits
  let marketHits = 0;
  let competitionHits = 0;
  let feasibilityHits = 0;
  let riskHits = 0;

  for (const t of tokens) {
    if (MARKET_TERMS.has(t)) {
      if (negatedConcepts.has(t)) {
        riskHits += 2;
      } else {
        marketHits++;
      }
    }
    if (COMPETITION_TERMS.has(t)) {
      if (negatedConcepts.has(t)) {
        riskHits += 2;
      } else {
        competitionHits++;
      }
    }
    if (FEASIBILITY_TERMS.has(t)) {
      if (negatedConcepts.has(t)) {
        riskHits += 2;
      } else {
        feasibilityHits++;
      }
    }
    if (RISK_TERMS.has(t)) {
      riskHits++;
    }
  }

  // 4. Calculate Sub-Scores
  const severeDemandNegation = negatedConcepts.has('demand');
  const severeFeasibleNegation = negatedConcepts.has('feasible');
  const severeMoatNegation = negatedConcepts.has('moat') || negatedConcepts.has('competition');

  let marketScore = severeDemandNegation ? 25.0 : Math.min(95.0, Math.max(20.0, 50.0 + (marketHits / 3) * 35.0));
  let feasibilityScore = severeFeasibleNegation ? 25.0 : Math.min(95.0, Math.max(20.0, 50.0 + (feasibilityHits / 4) * 35.0));
  let competitionScore = severeMoatNegation ? 25.0 : Math.min(95.0, Math.max(20.0, 50.0 + (competitionHits / 2) * 35.0));

  const totalLength = problem.length + solution.length;
  let depthScore = 40.0;
  if (totalLength >= 250) depthScore = 92.0;
  else if (totalLength >= 120) depthScore = 75.0;
  else if (totalLength >= 50) depthScore = 58.0;

  const riskPenalty = Math.min(30.0, riskHits * 5.0 + negatedConcepts.size * 6.0);
  if (severeDemandNegation) marketScore = Math.max(15.0, marketScore - 35.0);
  if (severeMoatNegation) competitionScore = Math.max(15.0, competitionScore - 25.0);
  if (severeFeasibleNegation) feasibilityScore = Math.max(15.0, feasibilityScore - 30.0);

  const rawComposite = (marketScore * 0.30) + (feasibilityScore * 0.30) + (competitionScore * 0.20) + (depthScore * 0.20) - (riskPenalty * 0.25);
  const overallScore = Math.round(Math.min(100.0, Math.max(10.0, rawComposite)) * 10) / 10;

  marketScore = Math.round(marketScore * 10) / 10;
  feasibilityScore = Math.round(feasibilityScore * 10) / 10;
  competitionScore = Math.round(competitionScore * 10) / 10;
  depthScore = Math.round(depthScore * 10) / 10;

  let decisionTier: DecisionTierType = 'CAUTION';
  if (overallScore >= 80.0) decisionTier = 'STRONG_GO';
  else if (overallScore >= 65.0) decisionTier = 'GO';
  else if (overallScore >= 50.0) decisionTier = 'CAUTION';
  else decisionTier = 'PIVOT';

  // SWOT and risks
  const strengths: string[] = [];
  const weaknesses: string[] = [];
  const opportunities: string[] = [];
  const threats: string[] = [];
  const risks: string[] = [];

  if (feasibilityHits >= 2) {
    strengths.push('Engineered technical architecture with concrete feasibility markers.');
  }
  if (competitionHits >= 1) {
    strengths.push('Articulated competitive moat and proprietary differentiation mechanisms.');
  }
  if (model.toLowerCase().includes('subscription') || model.toLowerCase().includes('saas')) {
    strengths.push('Predictable recurring revenue model with compounding SaaS dynamics.');
  }
  if (strengths.length === 0) {
    strengths.push('Clear core problem articulation addressing identified customer friction.');
  }

  if (competitionHits === 0 || severeMoatNegation) {
    weaknesses.push('Limited structural defensibility; vulnerable to rapid incumbent copying.');
  }
  if (feasibilityHits < 2) {
    weaknesses.push('Technical execution details require specification and validation.');
  }
  if (customer.length < 20) {
    weaknesses.push('Target customer segment lacks granular ICP definition.');
  }
  if (weaknesses.length === 0) {
    weaknesses.push('Early-stage brand presence requiring accelerated enterprise proof points.');
  }

  if (marketHits >= 2) {
    opportunities.push('Expanding addressable market demand across specified customer segments.');
  }
  opportunities.push('Favorable tailwinds for modern cloud-native automated tooling.');
  opportunities.push('Potential for land-and-expand monetization via enterprise tiered rollouts.');

  if (riskHits > 0 || severeDemandNegation) {
    threats.push('Negative market sentiment flags detected regarding market demand or feasibility.');
  }
  threats.push('Aggressive feature expansion by established market incumbents.');
  threats.push('Potential enterprise sales cycle friction and procurement latency.');

  if (severeDemandNegation) {
    risks.push("CRITICAL: Negative demand indicator detected ('no demand' / 'lacks demand'). Severe market validation deficit.");
  }
  if (severeMoatNegation) {
    risks.push('HIGH: Lack of defensible moat exposes margins to price erosion.');
  }
  if (severeFeasibleNegation) {
    risks.push('HIGH: Technical feasibility flagged as questionable or constrained.');
  }
  if (risks.length === 0) {
    risks.push('Execution timeline risk: Slower than expected enterprise contract conversion.');
    risks.push('Go-to-market risk: High outbound sales customer acquisition cost (CAC).');
  }

  return {
    id: input.id || Date.now(),
    userId: input.userId || 1,
    startupName: name,
    targetCustomer: customer,
    problemStatement: problem,
    proposedSolution: solution,
    businessModel: model,
    overallScore,
    marketScore,
    feasibilityScore,
    competitionScore,
    depthScore,
    decisionTier,
    strengths,
    weaknesses,
    opportunities,
    threats,
    risks,
    createdAt: input.createdAt || new Date().toISOString()
  };
}
