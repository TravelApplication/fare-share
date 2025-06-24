import React from 'react';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

export default async function LoggedUserLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const cookieStore = await cookies();
  const token = cookieStore.get('token')?.value;
  if (token) {
    redirect('/trips');
  }

  return <>{children}</>;
}
