'use client';

import ActivityCard from '@/components/activity/ActivityCard';
import { Button } from '@/components/ui/button';
import { useTrip } from '@/context/TripContext';
import { formatDate } from '@/lib/utils';
import { ArrowLeft } from 'lucide-react';
import { useParams, useRouter } from 'next/navigation';

export default function ActivityDetailsPage() {
  const params = useParams<{ activityId: string }>();
  const router = useRouter();
  const { trip } = useTrip();

  const activity = trip?.activities.find(
    (activity) => activity.id === parseInt(params.activityId),
  );

  if (!activity && trip) {
    router.push(`/trips/${trip.id}`);
  }

  return (
    <>
      {activity && trip && (
        <div className="flex flex-col gap-2">
          <div className="flex items-center justify-between">
            <Button
              onClick={() => router.push(`/trips/${trip.id}`)}
              className="bg-white border text-primary-500 hover:bg-gray-100 shadow-sm  rounded-full mb-4"
            >
              <ArrowLeft />
              <span>Back to Trip Summary</span>
            </Button>

            <div className="text-md -mt-4 font-semibold text-gray-400">
              {trip.name}
              {trip.tripStartDate && trip.tripEndDate && (
                <>
                  {' | '}
                  {formatDate(trip.tripStartDate)} -{' '}
                  {formatDate(trip.tripEndDate)}
                </>
              )}
            </div>
          </div>
          <ActivityCard activity={activity} />
        </div>
      )}
    </>
  );
}
