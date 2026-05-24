import { defineConfig } from "vite";
import { resolve } from "node:path";

export default defineConfig({
  root: ".",
  publicDir: false,

  build: {
    outDir: "../resources/static",
    emptyOutDir: false,

    rollupOptions: {
      input: {
        main: resolve(__dirname, "main.js"),
        notifications: resolve(__dirname, "notifications.js")
      },
      output: {
        entryFileNames: "assets/js/[name].js",
        chunkFileNames: "assets/js/[name].js",
        assetFileNames: (assetInfo) => {
          const name = assetInfo.name || "";

          if (name.endsWith(".css")) {
            return "assets/css/[name][extname]";
          }

          if (/\.(woff2?|ttf|eot|svg)$/.test(name)) {
            return "assets/fonts/[name][extname]";
          }

          return "assets/[name][extname]";
        }
      }
    }
  }
});
