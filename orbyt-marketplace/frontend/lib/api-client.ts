import axios from "axios";
import { useAuthStore } from "@/store/auth-store";

export const apiClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api"
});

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  const tenantKey = process.env.NEXT_PUBLIC_TENANT_KEY ?? "orbyt-demo";

  config.headers = config.headers ?? {};
  config.headers["X-Tenant-Id"] = tenantKey;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
