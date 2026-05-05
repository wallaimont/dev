import type { Config } from 'tailwindcss';
import preset from '../../packages/config/tailwind-preset';

const config: Config = {
  content: [
    './app/**/*.{ts,tsx}',
    './components/**/*.{ts,tsx}',
    '../../packages/ui/src/**/*.{ts,tsx}',
  ],
  presets: [preset as unknown as Config],
  theme: {
    extend: {},
  },
  plugins: [],
};

export default config;
