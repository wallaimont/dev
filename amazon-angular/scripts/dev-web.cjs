const net = require('net');
const { spawn } = require('child_process');

const candidatePorts = [4200, 4201, 4202, 4300];

function isPortFree(port) {
  return new Promise((resolve) => {
    const server = net.createServer();
    server.once('error', () => resolve(false));
    server.once('listening', () => {
      server.close(() => resolve(true));
    });
    server.listen(port, '127.0.0.1');
  });
}

async function pickPort() {
  for (const port of candidatePorts) {
    const free = await isPortFree(port);
    if (free) {
      return port;
    }
  }
  return null;
}

async function main() {
  const port = await pickPort();

  if (!port) {
    console.error('Nenhuma porta livre encontrada entre 4200, 4201, 4202 e 4300.');
    process.exit(1);
  }

  console.log(`Iniciando frontend na porta ${port}...`);

  const child = spawn('npx', ['ng', 'serve', '--port', String(port), '--proxy-config', 'proxy.conf.json'], {
    stdio: 'inherit',
    shell: true,
  });

  child.on('exit', (code) => {
    process.exit(code ?? 0);
  });
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
