// spoc/layout.tsx
import React from 'react';
import SpocLayout from '../SpocLayout';
const Layout = ({ children }: { children: React.ReactNode }) => {
  return <SpocLayout>{children}</SpocLayout>;
};

export default Layout;
