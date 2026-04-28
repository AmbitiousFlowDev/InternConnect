import { defineConfig } from "vite";

export default defineConfig({
  build: {
    outDir: "../../resources/static",
    emptyOutDir: false,
    rollupOptions: {
      input: {
        main: "./main.js",
        styles: "./styles.css",
      },

      output: {
        entryFileNames: "js/[name].js",
        assetFileNames: (assetInfo) => {
          if (assetInfo.name?.endsWith(".css")) {
            return "css/[name][extname]";
          }
          return "assets/[name][extname]";
        },
      },
    },
  },
});