import React, { useState } from 'react';
import { BurnEntry } from '../types';
import {
  Flame,
  Plus,
  Trash2,
  TrendingDown,
  Calendar,
  DollarSign,
  AlertCircle
} from 'lucide-react';

interface BurnWatchViewProps {
  entries: BurnEntry[];
  startingCash: number;
  onUpdateEntries: (entries: BurnEntry[]) => void;
  onUpdateStartingCash: (cash: number) => void;
}

export const BurnWatchView: React.FC<BurnWatchViewProps> = ({
  entries,
  startingCash,
  onUpdateEntries,
  onUpdateStartingCash
}) => {
  const [cashInput, setCashInput] = useState(startingCash.toString());

  // New entry form state
  const [monthInput, setMonthInput] = useState('Month 1');
  const [categoryInput, setCategoryInput] = useState('Engineering Salaries');
  const [typeInput, setTypeInput] = useState<'EXPENSE' | 'REVENUE'>('EXPENSE');
  const [amountInput, setAmountInput] = useState('12000');
  const [notesInput, setNotesInput] = useState('Dev core');

  const categories = [
    'Engineering Salaries',
    'Cloud Infrastructure (AWS/GCP)',
    'Marketing & Growth Ads',
    'SaaS Tools & Office',
    'Legal & Accounting',
    'B2B Pilot Contracts',
    'Enterprise Subscriptions',
    'Consulting & Services'
  ];

  const handleCashChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCashInput(e.target.value);
    const parsed = parseFloat(e.target.value);
    if (!isNaN(parsed) && parsed >= 0) {
      onUpdateStartingCash(parsed);
    }
  };

  const handleAddEntry = (e: React.FormEvent) => {
    e.preventDefault();
    const amount = parseFloat(amountInput);
    if (isNaN(amount) || amount <= 0) return;

    const newEntry: BurnEntry = {
      id: Date.now(),
      ventureId: 1,
      monthLabel: monthInput.trim() || 'Month 1',
      category: categoryInput,
      entryType: typeInput,
      amount,
      notes: notesInput.trim()
    };

    onUpdateEntries([...entries, newEntry]);
    setAmountInput('');
    setNotesInput('');
  };

  const handleDeleteEntry = (id: number) => {
    onUpdateEntries(entries.filter((item) => item.id !== id));
  };

  // Calculations
  const grossExpenses = entries
    .filter((e) => e.entryType === 'EXPENSE')
    .reduce((sum, e) => sum + e.amount, 0);

  const monthlyRevenue = entries
    .filter((e) => e.entryType === 'REVENUE')
    .reduce((sum, e) => sum + e.amount, 0);

  const netMonthlyBurn = grossExpenses - monthlyRevenue;

  const runwayMonths =
    netMonthlyBurn <= 0
      ? Infinity
      : Math.max(0, Math.round((startingCash / netMonthlyBurn) * 10) / 10);

  const zeroCashDate = () => {
    if (runwayMonths === Infinity) return 'Sustainable / Cash Positive';
    const now = new Date();
    const future = new Date(now);
    future.setDate(future.getDate() + Math.round(runwayMonths * 30.4));
    return future.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
  };

  const formatCurrency = (val: number) =>
    new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(val);

  // SVG Line Chart Simulation of Cash Depletion Curve
  const maxMonths = 18;
  const chartPoints: { month: number; balance: number }[] = [];
  for (let m = 0; m <= maxMonths; m++) {
    const bal = Math.max(0, startingCash - m * netMonthlyBurn);
    chartPoints.push({ month: m, balance: bal });
  }

  const svgWidth = 480;
  const svgHeight = 220;
  const padding = 36;
  const maxBal = startingCash > 0 ? startingCash : 100000;

  const pathD = chartPoints
    .map((p, idx) => {
      const x = padding + (p.month / maxMonths) * (svgWidth - padding * 2);
      const y = svgHeight - padding - (p.balance / maxBal) * (svgHeight - padding * 2);
      return `${idx === 0 ? 'M' : 'L'} ${x} ${y}`;
    })
    .join(' ');

  const areaD = `${pathD} L ${svgWidth - padding} ${svgHeight - padding} L ${padding} ${svgHeight - padding} Z`;

  return (
    <div id="burnwatch-screen" className="space-y-6">
      {/* Top Stats Banner */}
      <div className="grid grid-cols-2 sm:grid-cols-5 gap-3">
        <div className="bg-[#162235] border border-[#273852] rounded-xl p-3.5 col-span-2 sm:col-span-1">
          <label className="text-[10px] text-[#94A3B8] uppercase font-bold block mb-1">
            Starting Cash ($)
          </label>
          <input
            id="input-starting-cash"
            type="number"
            value={cashInput}
            onChange={handleCashChange}
            className="w-full text-base font-bold bg-[#0E1828] border border-[#273852] rounded-lg px-2.5 py-1 text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
          />
        </div>

        <div className="bg-[#162235] border border-[#273852] rounded-xl p-3.5">
          <span className="text-[10px] text-[#94A3B8] uppercase font-bold">Monthly Revenue</span>
          <p className="text-base font-bold text-[#10B981] mt-1">{formatCurrency(monthlyRevenue)}</p>
        </div>

        <div className="bg-[#162235] border border-[#273852] rounded-xl p-3.5">
          <span className="text-[10px] text-[#94A3B8] uppercase font-bold">Gross Expenses</span>
          <p className="text-base font-bold text-[#EF4444] mt-1">{formatCurrency(grossExpenses)}</p>
        </div>

        <div className="bg-[#162235] border border-[#273852] rounded-xl p-3.5">
          <span className="text-[10px] text-[#94A3B8] uppercase font-bold">Net Monthly Burn</span>
          <p className="text-base font-bold text-[#F59E0B] mt-1">{formatCurrency(netMonthlyBurn)}</p>
        </div>

        <div className="bg-[#162235] border border-[#273852] rounded-xl p-3.5">
          <span className="text-[10px] text-[#94A3B8] uppercase font-bold">Runway Horizon</span>
          <p className="text-base font-bold text-[#10B981] mt-1">
            {runwayMonths === Infinity ? 'Positive' : `${runwayMonths} Mo`}
          </p>
          <span className="text-[10px] text-[#64748B] block truncate">{zeroCashDate()}</span>
        </div>
      </div>

      {/* Main Split: Ledger Table vs Runway Depletion Curve */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Ledger Table: 7 Columns */}
        <div className="lg:col-span-7 bg-[#162235] border border-[#273852] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#273852] pb-3">
            <h3 className="text-sm font-bold text-[#F1F5F9] flex items-center gap-2">
              <Flame className="w-4 h-4 text-[#EF4444]" />
              <span>Operational Cash Ledger (JTable Model)</span>
            </h3>
            <span className="text-xs text-[#94A3B8]">{entries.length} line items</span>
          </div>

          {/* Add Entry Bar */}
          <form onSubmit={handleAddEntry} className="grid grid-cols-12 gap-2 bg-[#0E1828] p-3 rounded-lg border border-[#273852]">
            <input
              type="text"
              placeholder="Month"
              value={monthInput}
              onChange={(e) => setMonthInput(e.target.value)}
              className="col-span-2 px-2 py-1.5 text-xs bg-[#162235] border border-[#273852] rounded text-[#F1F5F9]"
            />
            <select
              value={categoryInput}
              onChange={(e) => setCategoryInput(e.target.value)}
              className="col-span-4 px-2 py-1.5 text-xs bg-[#162235] border border-[#273852] rounded text-[#F1F5F9]"
            >
              {categories.map((c) => (
                <option key={c} value={c}>{c}</option>
              ))}
            </select>
            <select
              value={typeInput}
              onChange={(e) => setTypeInput(e.target.value as 'EXPENSE' | 'REVENUE')}
              className="col-span-2 px-2 py-1.5 text-xs bg-[#162235] border border-[#273852] rounded text-[#F1F5F9]"
            >
              <option value="EXPENSE">Expense</option>
              <option value="REVENUE">Revenue</option>
            </select>
            <input
              type="number"
              placeholder="Amount"
              value={amountInput}
              onChange={(e) => setAmountInput(e.target.value)}
              className="col-span-3 px-2 py-1.5 text-xs bg-[#162235] border border-[#273852] rounded text-[#F1F5F9]"
            />
            <button
              type="submit"
              className="col-span-1 flex items-center justify-center bg-[#10B981] hover:bg-[#059669] text-white rounded cursor-pointer"
            >
              <Plus className="w-4 h-4" />
            </button>
          </form>

          {/* Table */}
          <div className="overflow-x-auto max-h-80 overflow-y-auto">
            <table className="w-full text-left text-xs border-collapse">
              <thead>
                <tr className="border-b border-[#273852] text-[#94A3B8]">
                  <th className="py-2 px-3 font-semibold">Month</th>
                  <th className="py-2 px-3 font-semibold">Category</th>
                  <th className="py-2 px-3 font-semibold">Type</th>
                  <th className="py-2 px-3 font-semibold text-right">Amount</th>
                  <th className="py-2 px-3 font-semibold text-center">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-[#273852]">
                {entries.map((entry) => (
                  <tr key={entry.id} className="hover:bg-[#1F2E47]/50 transition-colors">
                    <td className="py-2.5 px-3 text-[#F1F5F9] font-medium">{entry.monthLabel}</td>
                    <td className="py-2.5 px-3 text-[#CBD5E1]">{entry.category}</td>
                    <td className="py-2.5 px-3">
                      <span
                        className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                          entry.entryType === 'REVENUE'
                            ? 'bg-[#064E3B] text-[#10B981]'
                            : 'bg-[#7F1D1D]/50 text-[#EF4444]'
                        }`}
                      >
                        {entry.entryType}
                      </span>
                    </td>
                    <td
                      className={`py-2.5 px-3 text-right font-mono font-bold ${
                        entry.entryType === 'REVENUE' ? 'text-[#10B981]' : 'text-[#F1F5F9]'
                      }`}
                    >
                      {formatCurrency(entry.amount)}
                    </td>
                    <td className="py-2.5 px-3 text-center">
                      <button
                        onClick={() => handleDeleteEntry(entry.id)}
                        className="text-[#64748B] hover:text-[#EF4444] transition-colors p-1 cursor-pointer"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Depletion Curve Chart: 5 Columns */}
        <div className="lg:col-span-5 bg-[#162235] border border-[#273852] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#273852] pb-3">
            <h3 className="text-sm font-bold text-[#F1F5F9] flex items-center gap-2">
              <TrendingDown className="w-4 h-4 text-[#10B981]" />
              <span>Cash Depletion Curve (18 Months)</span>
            </h3>
            <span className="text-[10px] px-2 py-0.5 rounded bg-[#1F2E47] text-[#94A3B8]">
              Graphics2D Native
            </span>
          </div>

          <div className="bg-[#0E1828] border border-[#273852] rounded-xl p-3 flex flex-col items-center justify-center">
            <svg
              width="100%"
              height="220"
              viewBox={`0 0 ${svgWidth} ${svgHeight}`}
              className="overflow-visible"
            >
              <defs>
                <linearGradient id="curveGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#10B981" stopOpacity="0.3" />
                  <stop offset="100%" stopColor="#10B981" stopOpacity="0.0" />
                </linearGradient>
              </defs>

              {/* Grid Lines */}
              <line
                x1={padding}
                y1={padding}
                x2={svgWidth - padding}
                y2={padding}
                stroke="#273852"
                strokeDasharray="4 4"
              />
              <line
                x1={padding}
                y1={(svgHeight - padding * 2) / 2 + padding}
                x2={svgWidth - padding}
                y2={(svgHeight - padding * 2) / 2 + padding}
                stroke="#273852"
                strokeDasharray="4 4"
              />
              <line
                x1={padding}
                y1={svgHeight - padding}
                x2={svgWidth - padding}
                y2={svgHeight - padding}
                stroke="#475569"
                strokeWidth="1.5"
              />

              {/* Area Under Curve */}
              <path d={areaD} fill="url(#curveGradient)" />

              {/* Curve Line */}
              <path
                d={pathD}
                fill="none"
                stroke="#10B981"
                strokeWidth="2.5"
                strokeLinecap="round"
              />

              {/* Threshold Labels */}
              <text x={padding} y={padding - 10} fill="#94A3B8" fontSize="10">
                {formatCurrency(startingCash)}
              </text>
              <text x={padding} y={svgHeight - 12} fill="#64748B" fontSize="10">
                0 Mo
              </text>
              <text x={svgWidth - padding - 30} y={svgHeight - 12} fill="#64748B" fontSize="10">
                18 Mo
              </text>
            </svg>
          </div>

          <div className="bg-[#0E1828] border border-[#273852] rounded-lg p-3 text-xs text-[#94A3B8] space-y-1">
            <div className="flex justify-between">
              <span>Estimated Survival:</span>
              <span className="font-bold text-[#F1F5F9]">
                {runwayMonths === Infinity ? 'Infinite / Profitable' : `${runwayMonths} Months`}
              </span>
            </div>
            <div className="flex justify-between">
              <span>Zero-Cash Date:</span>
              <span className="font-bold text-[#F59E0B]">{zeroCashDate()}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
