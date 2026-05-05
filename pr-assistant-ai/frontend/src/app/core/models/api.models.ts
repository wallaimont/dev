export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  role: string;
}

export interface PullRequestResponse {
  id: string;
  externalPrId: number;
  repositoryName: string;
  repositoryOwner: string;
  title: string;
  description: string;
  author: string;
  sourceBranch: string;
  targetBranch: string;
  state: string;
  files: FileResponse[];
  latestRuleAnalysis: RuleAnalysisResponse | null;
  latestAiAnalysis: AiAnalysisResponse | null;
  latestFinalAnalysis: FinalAnalysisResponse | null;
  createdAt: string;
  updatedAt: string;
}

export interface FileResponse {
  id: string;
  filePath: string;
  fileStatus: string;
  additions: number;
  deletions: number;
  critical: boolean;
  criticalReason: string | null;
}

export interface RuleAnalysisResponse {
  id: string;
  riskLevel: string;
  score: number;
  findings: string;
  analyzedAt: string;
}

export interface AiAnalysisResponse {
  id: string;
  riskLevel: string;
  score: number;
  summary: string;
  analyzedAt: string;
}

export interface FinalAnalysisResponse {
  id: string;
  riskLevel: string;
  finalScore: number;
  decisionReason: string;
  analyzedAt: string;
}

export interface DashboardResponse {
  totalPrs: number;
  openPrs: number;
  analyzedPrs: number;
  approvedPrs: number;
  rejectedPrs: number;
  criticalPrs: number;
  avgQualityScore: number;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
