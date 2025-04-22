'use client';
import React, { useState } from 'react';
import ActivityForm from '@/components/activity/ActivityForm';
import { useTrip } from '@/context/TripContext';
import { useRouter } from 'next/navigation';
import { z } from 'zod';
import { FormikHelpers } from 'formik';
import { activityFormSchema } from '@/validation/activityFormSchema';
import { getToken } from '@/lib/auth';
import axios from 'axios';
import { useParams } from 'next/navigation';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import { Alert } from '@/components/ui/alert';

export default function EditActivityPage() {
  const params = useParams<{ activityId: string }>();
  const { trip, refreshTrip } = useTrip();
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  const activity = trip?.activities.find(
    (activity) => activity.id === parseInt(params.activityId),
  );

  if (!activity && trip) {
    router.push(`/trips/${trip.id}`);
  }

  const initialValues = activity
    ? {
        name: activity.name,
        description: activity.description ?? '',
        location: activity.location ?? '',
        link: activity.link ?? '',
      }
    : {
        name: '',
        description: '',
        location: '',
        link: '',
      };

  const handleEditActivity = async (
    values: z.infer<typeof activityFormSchema>,
    actions: FormikHelpers<z.infer<typeof activityFormSchema>>,
  ) => {
    if (!trip) return;
    try {
      if (activity && trip) {
        const token = getToken();
        const response = await axios.put(
          `/api/v1/groups/${trip.id}/activities/${params.activityId}`,
          values,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        );

        toast('Activity updated!', {
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
    } catch {
      setError('An error occurred');
    }
  };
  return (
    <>
      {trip && activity && (
        <>
          <Button
            onClick={() => router.push(`/trips/${trip.id}`)}
            className="bg-white border text-primary-500 hover:bg-gray-100 shadow-sm  rounded-full my-4"
          >
            <ArrowLeft />
            <span>Back To Activity Summary</span>
          </Button>
          <div className="section px-12">
            {error && (
              <Alert variant="destructive" className="my-4 p-4">
                {error}
              </Alert>
            )}

            <h1 className="text-heading2-bold">Edit the Activity</h1>
            <ActivityForm
              onSubmit={handleEditActivity}
              trip={trip}
              initialValues={initialValues}
            />
          </div>
        </>
      )}
    </>
  );
}
