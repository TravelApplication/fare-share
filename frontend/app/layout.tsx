import React from 'react';
import type { Metadata } from 'next';
import { Geist, Geist_Mono } from 'next/font/google';
import './globals.css';
import Navbar from '@/components/shared/Navbar';
import { Suspense } from 'react';
import { Toaster } from '@/components/ui/sonner';
import { TooltipProvider } from '@/components/ui/tooltip';

const geistSans = Geist({
  variable: '--font-geist-sans',
  subsets: ['latin'],
});

const geistMono = Geist_Mono({
  variable: '--font-geist-mono',
  subsets: ['latin'],
});

export const metadata: Metadata = {
  title: 'FareShare',
  description: 'Travel app',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <Navbar />
        <Suspense>
          <section className="main-container">
            <div className="w-full max-w-4xl">
              <TooltipProvider>{children}</TooltipProvider>
            </div>
            <Toaster />
          </section>
        </Suspense>
        <footer className="w-full text-center text-xs text-gray-500 py-4 border-t">
          Illustration by{' '}
          <a
            href="https://storyset.com/city"
            target="_blank"
            rel="noopener noreferrer"
            className="underline hover:text-blue-600"
          >
            Storyset
          </a>
        </footer>
      </body>
    </html>
  );
}
