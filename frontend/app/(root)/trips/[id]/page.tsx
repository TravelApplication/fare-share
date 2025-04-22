'use client';

import { useTrip } from '@/context/TripContext';
import TripCard from '@/components/trip/TripCard';
import { Alert } from '@/components/ui/alert';
import { ArrowLeft, CirclePlus, MapPinIcon, MapPinnedIcon } from 'lucide-react';
import { redirect, useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import Link from 'next/link';

export default function TripPage() {
  const { trip, loading, tripError } = useTrip();
  const router = useRouter();
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
      <Button
        onClick={() => router.push('/trips')}
        className="bg-white border text-primary-500 hover:bg-gray-100 shadow-sm  rounded-full my-4"
      >
        <ArrowLeft />
        <span>Back To Trips</span>
      </Button>
      <TripCard trip={trip} />
      <button
        onClick={() => redirect(`/trips/${trip.id}/activities/create`)}
        className="mx-auto relative -mb-6 z-50 bg-white border text-primary-500 hover:bg-gray-100 px-4 py-3 shadow-md flex gap-2 items-center justify-center rounded-full mt-4"
      >
        <CirclePlus />
        <span className="text-base-semibold">Add an Activity</span>
      </button>
      <div className="p-5 z-0 border rounded-lg shadow-md">
        <h2 className="text-heading3-bold ">Activities</h2>
        {trip.activities.length > 0 ? (
          <ul className="mt-2 space-y-4">
            {trip.activities.map((activity) => (
              <li
                key={activity.id}
                className="p-4 border rounded-lg shadow-md flex flex-col gap-1 hover:bg-gray-400/10 cursor-pointer transition-colors duration-300"
                onClick={() =>
                  router.push(`/trips/${trip.id}/activities/${activity.id}`)
                }
              >
                <h3 className="text-lg font-semibold">{activity.name}</h3>
                {activity.description && (
                  <p className="text-sm text-gray-600">
                    {activity.description}
                  </p>
                )}
                {activity.location && (
                  <p className="flex items-center font-semibold gap-1 ">
                    <MapPinIcon className="w-4 h-4 text-red-900" />
                    <span>{activity.location}</span>
                  </p>
                )}
                {activity.link && (
                  <Link
                    href={activity.link}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-primary-500 underline w-min text-nowrap"
                  >
                    More info
                  </Link>
                )}
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-gray-500">No activities yet</p>
        )}
      </div>
    </div>
  );
}
