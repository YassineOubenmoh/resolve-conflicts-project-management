'use client';

import React from 'react';
import { usePathname, useRouter } from 'next/navigation';

type SpocLayoutProps = {
  children: React.ReactNode;
};

const SpocLayout = ({ children }: SpocLayoutProps) => {
  const pathname = usePathname();
  const router = useRouter();

  const navLinks = [
    { label: 'Interlocutors', path: '/spoc/interlocutors-affected' },
    { label: 'Dashboard', path: '/spoc/dashboard' },
    { label: 'Assignments', path: '/spoc/projects-affected' },
    { label: 'Required Actions', path: '/spoc/projects-requiredactions' },
    { label: 'Feedbacks On Impacts', path: '/spoc/returns-impacts' },
    { label: 'Documents', path: '/spoc/impact-documents' },
  ];

  const handleNavClick = (path: string) => {
    router.push(path);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation Header */}
      <nav className="bg-white shadow p-4 flex justify-center gap-6">
        {navLinks.map((link) => (
          <div
            key={link.path}
            className={`cursor-pointer font-medium transition-colors ${
              pathname === link.path
                ? 'text-[#B12B89]'
                : 'text-gray-700 hover:text-[#B12B89]'
            }`}
            onClick={() => handleNavClick(link.path)}
          >
            {link.label}
          </div>
        ))}
      </nav>

      {/* Page Content */}
      <main className="p-6">{children}</main>
    </div>
  );
};

export default SpocLayout;
