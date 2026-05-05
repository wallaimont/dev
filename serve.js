const h = require('http');
const fs = require('fs');
const p = 'C:\\Users\\wallace\\Desktop\\Nova pasta (3)\\pac-man.html';
h.createServer((q, r) => {
  r.setHeader('Access-Control-Allow-Origin', '*');
  r.end(fs.readFileSync(p, 'utf8'));
}).listen(8765, () => console.log('servidor ok'));
