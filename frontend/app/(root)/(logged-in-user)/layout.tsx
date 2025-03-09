import React from 'react';
import { redirect } from 'next/navigation';

export default async function LoggedInLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  // redirect to login page if user is not logged in
  // TODO: replace with actual values
  const isLoggedIn = true;
  if (!isLoggedIn) {
    redirect('/sign-in');
  }
  return <>{children}</>;
}
