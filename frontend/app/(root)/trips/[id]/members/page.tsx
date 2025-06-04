'use client';

import { Button } from '@/components/ui/button';
import { useTrip } from '@/context/TripContext';
import { ArrowLeft, MessageCircle } from 'lucide-react';
import { useRouter } from 'next/navigation';

export default function TripMembersPage() {
  const router = useRouter();
  const { trip } = useTrip();

  if (!trip) return <div>zero</div>;

  return (
    <>
      <Button
        onClick={() => router.push(`/trips/${trip.id}`)}
        className="bg-white border text-primary-500 hover:bg-gray-100 shadow-sm rounded-full mb-4"
      >
        <ArrowLeft />
        <span>Back To Trip Summary</span>
      </Button>
      <div className="section p-6 flex flex-col border bg-white shadow-md rounded-lg">
        content
      </div>
    </>
  );
}
