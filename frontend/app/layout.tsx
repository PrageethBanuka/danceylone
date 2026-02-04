import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import GlobalNavbar from "@/components/GlobalNavbar";
import { ErrorBoundary } from "@/components/ErrorBoundary";
import { Toaster } from "sonner";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Danceylone - Dance & Fitness E-Commerce",
  description: "Premium dancewear and fitness equipment for passionate dancers and athletes",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <ErrorBoundary>
          <Toaster position="top-right" richColors closeButton expand={false} />
          <GlobalNavbar />
          {children}
        </ErrorBoundary>
      </body>
    </html>
  );
}
