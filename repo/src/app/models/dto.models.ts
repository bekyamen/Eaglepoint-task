export type Role = 'PASSENGER' | 'DISPATCHER' | 'ADMIN';

export interface User {
  id: string;
  username: string;
  role: Role;
}

export interface Route {
  id: string;
  name: string;
  frequencyScore: number;
}

export interface Stop {
  id: string;
  name: string;
  popularityScore: number;
}

export interface Task {
  id: string;
  type: string;
  status: string;
  timeoutAt: string | null;
}

export interface Notification {
  id: string;
  type: string;
  content: string;
  status: 'PENDING' | 'SENT' | 'READ';
  scheduledTime: string | null;
  createdAt: string;
}

export interface WorkflowState {
  id: string;
  workflowName: string;
  currentState: string;
}

export interface QueueMessage {
  id: string;
  type: string;
  status: string;
  retryCount: number;
  nextRetryAt: string | null;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface ApiResponse<T> {
  success: boolean;
  data: T;
}
