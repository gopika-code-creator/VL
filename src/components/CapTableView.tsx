import React, { useState } from 'react';
import { CapTableState } from '../types';
import { PieChart, Calculator, RefreshCw, Layers } from 'lucide-react';

interface CapTableViewProps {
  capTable: CapTableState;
  onUpdateCapTable: (capTable: CapTableState) => void;
}

export const CapTableView: React.FC<CapTableViewProps> = ({
  capTable,
  onUpdateCapTable
}) => {
  const [inputs, setInputs] = useState({
    founder1: capTable.founder1InitialPct.toString(),
    founder2: capTable.founder2InitialPct.toString(),
    esop: capTable.esopInitialPct.toString(),
    preMoney: capTable.preMoneyValuation.toString(),
    investment: capTable.investmentAmount.toString()
  });

  const f1 = parseFloat(inputs.founder1) || 0;
  const f2 = parseFloat(inputs.founder2) || 0;
  const esop = parseFloat(inputs.esop) || 0;
  const preMoney = parseFloat(inputs.preMoney) || 0;
  const investment = parseFloat(inputs.investment) || 0;

  const postMoney = preMoney + investment;
  const investorPct = postMoney > 0 ? (investment / postMoney) * 100 : 0;
  const retentionFactor = postMoney > 0 ? preMoney / postMoney : 1;

  const f1Diluted = f1 * retentionFactor;
  const f2Diluted = f2 * retentionFactor;
  const esopDiluted = esop * retentionFactor;

  const formatCurrency = (val: number) =>
    new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 0
    }).format(val);

  const handleApply = () => {
    onUpdateCapTable({
      founder1InitialPct: f1,
      founder2InitialPct: f2,
      esopInitialPct: esop,
      preMoneyValuation: preMoney,
      investmentAmount: investment
    });
  };

  // Donut chart math for SVG
  const slices = [
    { label: 'Founder 1', pct: f1Diluted, color: '#10B981' },
    { label: 'Founder 2', pct: f2Diluted, color: '#3B82F6' },
    { label: 'ESOP Pool', pct: esopDiluted, color: '#8B5CF6' },
    { label: 'New Investors', pct: investorPct, color: '#F59E0B' }
  ];

  let cumulativeAngle = 0;
  const renderDonutSlices = () => {
    const size = 260;
    const center = size / 2;
    const radius = 95;
    const innerRadius = 55;

    return slices.map((slice, i) => {
      const angle = (slice.pct / 100) * 360;
      if (angle <= 0) return null;

      const startAngle = cumulativeAngle;
      const endAngle = cumulativeAngle + angle;
      cumulativeAngle = endAngle;

      const startRad = (startAngle - 90) * (Math.PI / 180);
      const endRad = (endAngle - 90) * (Math.PI / 180);

      const x1 = center + radius * Math.cos(startRad);
      const y1 = center + radius * Math.sin(startRad);
      const x2 = center + radius * Math.cos(endRad);
      const y2 = center + radius * Math.sin(endRad);

      const ix1 = center + innerRadius * Math.cos(startRad);
      const iy1 = center + innerRadius * Math.sin(startRad);
      const ix2 = center + innerRadius * Math.cos(endRad);
      const iy2 = center + innerRadius * Math.sin(endRad);

      const largeArc = angle > 180 ? 1 : 0;

      const d = `
        M ${x1} ${y1}
        A ${radius} ${radius} 0 ${largeArc} 1 ${x2} ${y2}
        L ${ix2} ${iy2}
        A ${innerRadius} ${innerRadius} 0 ${largeArc} 0 ${ix1} ${iy1}
        Z
      `;

      return (
        <path
          key={i}
          d={d}
          fill={slice.color}
          className="transition-all duration-300 hover:opacity-90"
        />
      );
    });
  };

  return (
    <div id="captable-screen" className="space-y-6">
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Controls: 5 Columns */}
        <div className="lg:col-span-5 bg-[#162235] border border-[#273852] rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between border-b border-[#273852] pb-3">
            <h3 className="text-sm font-bold text-[#F1F5F9] flex items-center gap-2">
              <Calculator className="w-4 h-4 text-[#10B981]" />
              <span>Equity Terms & Valuation</span>
            </h3>
            <span className="text-xs text-[#94A3B8]">Round Modeling</span>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
                Founder 1 Initial (%)
              </label>
              <input
                id="input-f1-pct"
                type="number"
                value={inputs.founder1}
                onChange={(e) => setInputs({ ...inputs, founder1: e.target.value })}
                className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
                Founder 2 Initial (%)
              </label>
              <input
                id="input-f2-pct"
                type="number"
                value={inputs.founder2}
                onChange={(e) => setInputs({ ...inputs, founder2: e.target.value })}
                className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              ESOP Employee Option Pool (%)
            </label>
            <input
              id="input-esop-pct"
              type="number"
              value={inputs.esop}
              onChange={(e) => setInputs({ ...inputs, esop: e.target.value })}
              className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
            />
          </div>

          <div className="pt-2 border-t border-[#273852]">
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              Pre-Money Valuation ($)
            </label>
            <input
              id="input-pre-money"
              type="number"
              value={inputs.preMoney}
              onChange={(e) => setInputs({ ...inputs, preMoney: e.target.value })}
              className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-[#CBD5E1] mb-1">
              Simulated Investment Amount ($)
            </label>
            <input
              id="input-investment-amount"
              type="number"
              value={inputs.investment}
              onChange={(e) => setInputs({ ...inputs, investment: e.target.value })}
              className="w-full px-3 py-2 text-xs bg-[#0E1828] border border-[#273852] rounded-lg text-[#F1F5F9] focus:outline-hidden focus:border-[#10B981]"
            />
          </div>

          <button
            id="btn-recalculate-captable"
            onClick={handleApply}
            className="w-full flex items-center justify-center gap-2 px-4 py-2.5 rounded-lg text-xs font-bold text-white bg-[#10B981] hover:bg-[#059669] transition-all cursor-pointer shadow-xs mt-2"
          >
            <RefreshCw className="w-4 h-4" />
            <span>Recalculate Post-Round Dilution</span>
          </button>
        </div>

        {/* Outputs & Donut Chart: 7 Columns */}
        <div className="lg:col-span-7 space-y-4">
          {/* Summary Stat Grid */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <div className="bg-[#162235] border border-[#273852] rounded-xl p-3.5">
              <span className="text-[10px] text-[#94A3B8] uppercase font-bold">Post-Money</span>
              <p className="text-base font-bold text-[#F1F5F9] mt-1">{formatCurrency(postMoney)}</p>
            </div>
            <div className="bg-[#162235] border border-[#273852] rounded-xl p-3.5">
              <span className="text-[10px] text-[#94A3B8] uppercase font-bold">Investors</span>
              <p className="text-base font-bold text-[#F59E0B] mt-1">{investorPct.toFixed(2)}%</p>
            </div>
            <div className="bg-[#162235] border border-[#273852] rounded-xl p-3.5">
              <span className="text-[10px] text-[#94A3B8] uppercase font-bold">Founder 1 Diluted</span>
              <p className="text-base font-bold text-[#10B981] mt-1">{f1Diluted.toFixed(2)}%</p>
            </div>
            <div className="bg-[#162235] border border-[#273852] rounded-xl p-3.5">
              <span className="text-[10px] text-[#94A3B8] uppercase font-bold">ESOP Diluted</span>
              <p className="text-base font-bold text-[#8B5CF6] mt-1">{esopDiluted.toFixed(2)}%</p>
            </div>
          </div>

          {/* Donut Chart and Legend Card */}
          <div className="bg-[#162235] border border-[#273852] rounded-xl p-5">
            <div className="flex items-center justify-between mb-4 border-b border-[#273852] pb-3">
              <h4 className="text-sm font-bold text-[#F1F5F9] flex items-center gap-2">
                <PieChart className="w-4 h-4 text-[#3B82F6]" />
                <span>Simulated Post-Round Ownership Distribution</span>
              </h4>
              <span className="text-[10px] px-2 py-0.5 rounded bg-[#1F2E47] text-[#94A3B8]">
                Graphics2D fillArc Equivalent
              </span>
            </div>

            <div className="flex flex-col md:flex-row items-center justify-around gap-6">
              {/* SVG Donut */}
              <div className="relative w-[260px] h-[260px]">
                <svg width="260" height="260" viewBox="0 0 260 260">
                  {renderDonutSlices()}
                </svg>
                <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                  <span className="text-[11px] text-[#94A3B8] font-semibold">Post-Money</span>
                  <span className="text-sm font-bold text-[#F1F5F9]">{formatCurrency(postMoney)}</span>
                </div>
              </div>

              {/* Legend with initial vs diluted breakdown */}
              <div className="space-y-3 w-full max-w-xs">
                {slices.map((item, idx) => (
                  <div
                    key={idx}
                    className="flex items-center justify-between p-2.5 rounded-lg bg-[#0E1828] border border-[#273852]"
                  >
                    <div className="flex items-center gap-2.5">
                      <span
                        className="w-3 h-3 rounded-full shrink-0"
                        style={{ backgroundColor: item.color }}
                      />
                      <span className="text-xs font-semibold text-[#F1F5F9]">
                        {item.label}
                      </span>
                    </div>
                    <span className="text-xs font-bold text-[#F1F5F9]">
                      {item.pct.toFixed(2)}%
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
