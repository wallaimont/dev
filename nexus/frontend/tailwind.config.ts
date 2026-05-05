import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        // Nexus primary palette
        nexus: {
          50:  '#eef0fd',
          100: '#dce2fb',
          200: '#b9c4f7',
          300: '#8fa2f2',
          400: '#5b76eb',
          500: '#1A2FE8', // primary
          600: '#1525c8',
          700: '#101ca6',
          800: '#0d1584',
          900: '#0B0F1A', // ink
        },
        accent: {
          50:  '#fff1ee',
          100: '#ffddd7',
          200: '#ffb8ab',
          300: '#ff8a74',
          400: '#ff6546',
          500: '#FF4B26', // accent
          600: '#e53410',
          700: '#c0270c',
          800: '#9a2010',
          900: '#7d1d10',
        },
        success: {
          50:  '#e8fdf4',
          500: '#00D68F',
          700: '#00916a',
        },
        ink: '#0B0F1A',
        surface: '#F5F4F0',
      },
      fontFamily: {
        sans: ['var(--font-dm-sans)', 'system-ui', 'sans-serif'],
        display: ['var(--font-syne)', 'system-ui', 'sans-serif'],
        mono: ['var(--font-jetbrains-mono)', 'monospace'],
      },
      fontSize: {
        '2xs': ['0.625rem', { lineHeight: '0.875rem' }],
      },
      boxShadow: {
        'nexus': '0 1px 3px 0 rgba(11, 15, 26, 0.08), 0 1px 2px -1px rgba(11, 15, 26, 0.06)',
        'nexus-md': '0 4px 6px -1px rgba(11, 15, 26, 0.08), 0 2px 4px -2px rgba(11, 15, 26, 0.06)',
        'nexus-lg': '0 10px 15px -3px rgba(11, 15, 26, 0.08), 0 4px 6px -4px rgba(11, 15, 26, 0.06)',
        'card': '0 0 0 1px rgba(11, 15, 26, 0.06), 0 1px 3px 0 rgba(11, 15, 26, 0.08)',
      },
      borderRadius: {
        '4xl': '2rem',
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'shimmer': 'shimmer 2s linear infinite',
        'pulse-soft': 'pulseSoft 2s ease-in-out infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
        pulseSoft: {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.6' },
        },
      },
    },
  },
  plugins: [],
}

export default config
