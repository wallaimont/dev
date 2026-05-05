'use client';

import { monthlySales, opportunities } from '../lib/demo';
import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis, Area, AreaChart, CartesianGrid } from 'recharts';

const stageData = [
  { name: 'Qualification', value: opportunities.filter((item) => item.stage === 'qualification').length, color: '#0ea5e9' },
  { name: 'Proposal', value: opportunities.filter((item) => item.stage === 'proposal').length, color: '#22c55e' },
  { name: 'Negotiation', value: opportunities.filter((item) => item.stage === 'negotiation').length, color: '#f59e0b' },
  { name: 'Won', value: opportunities.filter((item) => item.stage === 'won').length, color: '#8b5cf6' },
];

export function SalesChart() {
  return (
    <div className="h-72 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={monthlySales}>
          <defs>
            <linearGradient id="sales" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#0ea5e9" stopOpacity={0.35} />
              <stop offset="95%" stopColor="#0ea5e9" stopOpacity={0.03} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
          <XAxis dataKey="month" stroke="#64748b" />
          <YAxis stroke="#64748b" />
          <Tooltip />
          <Area type="monotone" dataKey="value" stroke="#0ea5e9" fill="url(#sales)" />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}

export function StageChart() {
  return (
    <div className="h-72 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <PieChart>
          <Pie data={stageData} dataKey="value" nameKey="name" outerRadius={96} innerRadius={60}>
            {stageData.map((entry) => (
              <Cell key={entry.name} fill={entry.color} />
            ))}
          </Pie>
          <Tooltip />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}
