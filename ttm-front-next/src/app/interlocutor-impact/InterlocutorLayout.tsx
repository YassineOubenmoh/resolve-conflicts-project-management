'use client';

import React from 'react';
import { usePathname, useRouter } from 'next/navigation';

type InterlocutorLayoutProps = {
  children: React.ReactNode;
};

const InterlocutorLayout = ({ children }: InterlocutorLayoutProps) => {
  const pathname = usePathname();
  const router = useRouter();

  const navLinks = [
    { label: 'Dashboard', path: '/interlocutor-impact/dashboard' },
    { label: 'Required Actions', path: '/interlocutor-impact/projects-requiredactions' },
    { label: 'Feedbacks On Impacts', path: '/interlocutor-impact/returns-impacts' },
    { label: 'Documents', path: '/interlocutor-impact/impact-documents' }
  ];

  const handleNavClick = (path: string) => {
    router.push(path);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation Header */}
      <nav className="bg-white shadow p-4">
        <ul className="flex justify-center gap-6 list-none m-0 p-0">
          {navLinks.map((link) => (
            <li
              key={link.path}
              className={`cursor-pointer font-medium transition-colors ${
                pathname === link.path
                  ? 'text-[#B12B89]'
                  : 'text-gray-700 hover:text-[#B12B89]'
              }`}
              onClick={() => handleNavClick(link.path)}
            >
              {link.label}
            </li>
          ))}
        </ul>
      </nav>

      {/* Page Content */}
      <main className="p-6">{children}</main>
    </div>
  );
};

export default InterlocutorLayout;
