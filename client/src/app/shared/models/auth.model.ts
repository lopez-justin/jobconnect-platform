export type Role = 'CLIENT' | 'PROFESSIONAL';

export interface AuthResponse {
  userId: string;
  email: string;
  fullName: string;
  roles: Role[];
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  fullName: string;
  password: string;
  confirmPassword: string;
  phone?: string;
  role: Role;
}
