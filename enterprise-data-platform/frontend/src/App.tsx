import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AppLayout from './layouts/AppLayout';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import DataSourcesPage from './pages/DataSourcesPage';
import EtlMonitorPage from './pages/EtlMonitorPage';
import KpisPage from './pages/KpisPage';
import IntegrationsPage from './pages/IntegrationsPage';
import GovernancePage from './pages/GovernancePage';
import UsersPage from './pages/UsersPage';
import CompaniesPage from './pages/CompaniesPage';
import AuditPage from './pages/AuditPage';
import AlertsPage from './pages/AlertsPage';
import SettingsPage from './pages/SettingsPage';
import SubscriptionsPage from './pages/SubscriptionsPage';
import ContractsPage from './pages/ContractsPage';
import OnboardingPage from './pages/OnboardingPage';
import ProtectedRoute from './routes/ProtectedRoute';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />

        <Route path="/" element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />
          <Route path="data-sources" element={<DataSourcesPage />} />
          <Route path="integrations" element={<IntegrationsPage />} />
          <Route path="etl" element={<EtlMonitorPage />} />
          <Route path="governance" element={<GovernancePage />} />
          <Route path="kpis" element={<KpisPage />} />
          <Route path="users" element={<UsersPage />} />
          <Route path="companies" element={<CompaniesPage />} />
          <Route path="subscriptions" element={<SubscriptionsPage />} />
          <Route path="contracts" element={<ContractsPage />} />
          <Route path="onboarding" element={<OnboardingPage />} />
          <Route path="audit" element={<AuditPage />} />
          <Route path="alerts" element={<AlertsPage />} />
          <Route path="settings" element={<SettingsPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
