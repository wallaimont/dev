FROM node:20-alpine
WORKDIR /app

COPY package.json package.json
COPY tsconfig.base.json tsconfig.base.json
COPY apps/web/package.json apps/web/package.json
COPY packages/ui/package.json packages/ui/package.json
COPY packages/types/package.json packages/types/package.json
COPY packages/utils/package.json packages/utils/package.json
COPY packages/config/package.json packages/config/package.json

RUN npm install

COPY . .

EXPOSE 3000
CMD ["npm", "run", "dev", "--workspace", "@orbitcrm/web"]
