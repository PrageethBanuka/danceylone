import axios from "axios";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Add auth token to requests if available
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("authToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  message?: string;
}

export const register = async (data: RegisterRequest): Promise<void> => {
  await api.post("/api/auth/register", data);
};

export const login = async (data: LoginRequest): Promise<AuthResponse> => {
  const response = await api.post("/api/auth/login", data);
  return response.data;
};

export const getCurrentUser = async () => {
  const response = await api.get("/api/users/me");
  return response.data;
};

export default api;
