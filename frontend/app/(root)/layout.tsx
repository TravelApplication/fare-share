import React from 'react';
import { cookies } from 'next/headers';
import { isLoggedIn } from '@/lib/auth';
import { redirect } from 'next/navigation';
import { WebSocketProvider } from '@/providers/WebSocketProvider';

export default async function LoggedInLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;
  if (!token || !isLoggedIn(token)) {
    console.log('User is not logged in');
    redirect('/sign-in');
  }

  return <WebSocketProvider>{children}</WebSocketProvider>;
}
