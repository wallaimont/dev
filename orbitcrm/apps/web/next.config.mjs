/** @type {import('next').NextConfig} */
const nextConfig = {
  transpilePackages: ['@orbitcrm/ui', '@orbitcrm/types', '@orbitcrm/utils'],
  experimental: {
    typedRoutes: false,
  },
};

export default nextConfig;
