import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const PORT = 3000;

function getKotlinFiles() {
  const kotlinFiles = [];
  const baseDir = path.join(__dirname, 'app/src/main/java/com/example');

  function scan(dir) {
    if (!fs.existsSync(dir)) return;
    const entries = fs.readdirSync(dir, { withFileTypes: true });
    for (const entry of entries) {
      const fullPath = path.join(dir, entry.name);
      if (entry.isDirectory()) {
        scan(fullPath);
      } else if (entry.name.endsWith('.kt')) {
        const relPath = path.relative(__dirname, fullPath);
        const content = fs.readFileSync(fullPath, 'utf8');
        kotlinFiles.push({ path: relPath, name: entry.name, content });
      }
    }
  }

  scan(baseDir);
  return kotlinFiles;
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, `http://${req.headers.host}`);

  // Health check endpoint
  if (url.pathname === '/health' || url.pathname === '/healthz') {
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end('OK');
    return;
  }

  // APK Download endpoint
  if (url.pathname === '/app-debug.apk' || url.pathname === '/download-apk') {
    const apkPaths = [
      path.join(__dirname, 'app/build/outputs/apk/debug/app-debug.apk'),
      path.join(__dirname, '.build-outputs/app-debug.apk')
    ];
    const foundPath = apkPaths.find(p => fs.existsSync(p));
    if (foundPath) {
      const stat = fs.statSync(foundPath);
      res.writeHead(200, {
        'Content-Type': 'application/vnd.android.package-archive',
        'Content-Disposition': 'attachment; filename="aura-fashion-debug.apk"',
        'Content-Length': stat.size
      });
      fs.createReadStream(foundPath).pipe(res);
      return;
    } else {
      res.writeHead(404, { 'Content-Type': 'text/plain' });
      res.end('APK not found. Build is in progress.');
      return;
    }
  }

  // Kotlin sources endpoint
  if (url.pathname === '/api/kotlin-files') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify(getKotlinFiles()));
    return;
  }

  // App script endpoint
  if (url.pathname === '/app.js') {
    const jsPath = path.join(__dirname, 'public/app.js');
    if (fs.existsSync(jsPath)) {
      res.writeHead(200, { 'Content-Type': 'application/javascript; charset=utf-8' });
      fs.createReadStream(jsPath).pipe(res);
      return;
    }
  }

  // Serve the index.html
  const indexPath = path.join(__dirname, 'public/index.html');
  if (fs.existsSync(indexPath)) {
    const html = fs.readFileSync(indexPath, 'utf8');
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(html);
  } else {
    res.writeHead(200, { 'Content-Type': 'text/plain' });
    res.end('Aura Fashion E-Commerce Server Running');
  }
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Aura Fashion dev server listening on 0.0.0.0:${PORT}`);
});
