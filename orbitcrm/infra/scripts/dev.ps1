Copy-Item ..\..\.env.example ..\..\.env -ErrorAction SilentlyContinue
Set-Location ..\..
npm install
npm run db:generate
npm run db:push
npm run db:seed
npm run dev
