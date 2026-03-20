"use client";

import { usePathname } from 'next/navigation';
import Header from '@/components/header/Header';

export default function ClientLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  const pathname = usePathname();

  // Hide Header on signin and signup pages
  const hideHeader = pathname === '/signin' || pathname === '/signup';

  return (
    <>
      {!hideHeader && <Header />}
      {children}
    </>
  );
}