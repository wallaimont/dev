import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./app/**/*.{ts,tsx}", "./components/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: {
          night: "#0f172a",
          ember: "#f59e0b",
          mist: "#e2e8f0",
          ocean: "#0891b2"
        }
      },
      boxShadow: {
        float: "0 20px 60px rgba(15, 23, 42, 0.16)"
      }
    }
  },
  plugins: []
};

export default config;
