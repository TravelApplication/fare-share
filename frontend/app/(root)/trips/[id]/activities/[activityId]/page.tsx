'use client';

import { Button } from '@/components/ui/button';
import { useTrip } from '@/context/TripContext';
import { getToken } from '@/lib/auth';
import { formatDate } from '@/lib/utils';
import axios from 'axios';
import { ArrowLeft, MapPinIcon, Pencil, Trash } from 'lucide-react';
import Link from 'next/link';
import { useParams, useRouter } from 'next/navigation';
import { toast } from 'sonner';

export default function ActivityDetailsPage() {
  const params = useParams<{ activityId: string }>();
  const router = useRouter();
  const { trip, refreshTrip } = useTrip();

  const activity = trip?.activities.find(
    (activity) => activity.id === parseInt(params.activityId),
  );

  if (!activity && trip) {
    router.push(`/trips/${trip.id}`);
  }

  const handleDelete = async () => {
    console.log(`Usuwam aktywność: ${activity?.id}`);
    try {
      if (activity && trip) {
        const token = getToken();
        const response = await axios.delete(
          `/api/v1/groups/${trip.id}/activities/${activity.id}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        );
        toast(`Activity ${activity.name} deleted!`, {
          duration: 7000,
          action: {
            label: 'Close',
            onClick: () => {
              toast.dismiss();
            },
          },
        });
        router.push(`/trips/${trip.id}`);
        refreshTrip();
      }
    } catch {}
  };

  return (
    <>
      {activity && trip && (
        <>
          <Button
            onClick={() => router.push(`/trips/${trip.id}`)}
            className="bg-white border text-primary-500 hover:bg-gray-100 shadow-sm  rounded-full mb-4"
          >
            <ArrowLeft />
            <span>Back To Trip Summary</span>
          </Button>

          <div className="section p-6 bg-white border flex flex-col gap-3">
            <div className="flex justify-between items-center">
              <h1 className="text-2xl font-bold text-primary-500">{activity.name}</h1>
              <div className="flex gap-4">
                <Button
                  onClick={() =>
                    router.push(
                      `/trips/${trip.id}/activities/${activity.id}/edit`,
                    )
                  }
                  className="bg-white border border-primary-500/10 text-primary-500 hover:bg-primary-500/10 shadow-sm  rounded-full "
                >
                  <Pencil />
                  <span>Edit</span>
                </Button>

                <Button
                  onClick={handleDelete}
                  className="bg-white border border-red-800/10 text-red-800 hover:bg-red-500/10 shadow-sm  rounded-full "
                >
                  <Trash />
                  <span>Delete</span>
                </Button>
              </div>
            </div>
            <h1 className="text-md -mt-4 font-semibold text-gray-400">
              {trip.name}
              {trip.tripStartDate && trip.tripEndDate && (
                <>
                  {' | '}
                  {formatDate(trip.tripStartDate)} -{' '}
                  {formatDate(trip.tripEndDate)}
                </>
              )}
            </h1>
            <p className="text-gray-600">
              {activity.description || 'No description available'}
            </p>
            <p className="flex items-center font-semibold gap-1 ">
              <MapPinIcon className="w-4 h-4 text-red-900" />
              <span>{activity.location || 'No location available'}</span>
            </p>

            {activity.link && (
              <Link
                href={activity.link}
                target="_blank"
                rel="noopener noreferrer"
                className="block text-primary-500 underline hover:text-blue-700"
              >
                More info
              </Link>
            )}
          </div>
        </>
      )}
    </>
  );
}
