import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      { protocol: "https", hostname: "via.placeholder.com", },
      { protocol: "https", hostname: "placecats.com", },
    ]
  }
};

export default nextConfig;
