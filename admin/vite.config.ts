import path from "node:path";
import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

const rootDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");

export default defineConfig({
  plugins: [react()],
  server: { port: 5173 },
  // Читает CodeApp/.env (SUPABASE_* или VITE_SUPABASE_*)
  envDir: rootDir,
  envPrefix: ["VITE_", "SUPABASE_"],
});
