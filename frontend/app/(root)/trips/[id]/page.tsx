'use client';

import { useTrip } from '@/context/TripContext';
import TripCard from '@/components/trip/TripCard';
import { Alert } from '@/components/ui/alert';
import { CirclePlus } from 'lucide-react';
import { redirect } from 'next/navigation';

export default function TripPage() {
  const { trip, loading, tripError } = useTrip();

  if (loading)
    return (
      <Alert variant="default" className="p-4">
        Loading...
      </Alert>
    );
  if (tripError)
    return (
      <Alert variant="destructive" className="p-4">
        {tripError}
      </Alert>
    );
  if (!trip) return null;

  return (
    <div>
      <TripCard trip={trip} />
      <button
        onClick={() => redirect(`/trips/${trip.id}/activities/create`)}
        className="mx-auto bg-white text-primary-500 hover:bg-gray-100 px-4 py-3 shadow-lg flex gap-2 items-center justify-center rounded-full mt-4"
      >
        <CirclePlus />
        <span className="text-base-semibold">Add an Activity</span>
      </button>
      {trip.activities.length > 0 ? <div>activities</div> : 'No activities yet'}
    </div>
  );
}
