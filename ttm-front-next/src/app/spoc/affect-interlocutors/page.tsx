'use client';

import React from 'react';
import dynamic from 'next/dynamic';
import SpocLayout from '../SpocLayout';

// Dynamically import the AffectInterlocutors component with SSR disabled
const AffectInterlocutors = dynamic(
  () => import('./AffectInterlocutors'),
  { ssr: false }
);

export default function Page() {
  return (
    <div>
      <SpocLayout>
        <AffectInterlocutors />
      </SpocLayout>
      
    </div>
  );
}
