'use client';
import { TripContextProvider } from '@/context/TripContext';
import { GroupChatProvider } from '@/context/GroupChatContext';
import { useParams } from 'next/navigation';

export default function TripLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const params = useParams<{ id: string }>();
  const groupId = Number(params.id);

  return (
    <TripContextProvider>
      <GroupChatProvider groupId={groupId}>{children}</GroupChatProvider>
    </TripContextProvider>
  );
}
