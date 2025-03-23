'use client';
import ActivityForm from '@/components/activity/ActivityForm';
import { useState } from 'react';
import { activityFormSchema } from '@/validation/activityFormSchema';
import { z } from 'zod';
import { FormikHelpers } from 'formik';
import { getToken } from '@/lib/auth';
import axios from 'axios';
import { Alert } from '@/components/ui/alert';
import { useTrip } from '@/context/TripContext';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';

export default function NewActivityPage() {
  const { trip, tripError } = useTrip();
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  const handleCreateActivity = async (
    values: z.infer<typeof activityFormSchema>,
    actions: FormikHelpers<z.infer<typeof activityFormSchema>>,
  ) => {
    if (!trip) return;
    try {
      console.log('Creating activity', values);
      const token = getToken();
      const response = await axios.post(
        `/api/v1/groups/${trip.id}/activities`,
        values,
        {
          headers: { Authorization: `Bearer ${token}` },
        },
      );
      console.log(response);
      actions.resetForm();
      router.push(`/trips/${trip.id}`);
    } catch (err: unknown) {
      setError(err.message || 'An error occurred');
    }
  };

  return (
    <>
      {trip && (
        <Button
          onClick={() => router.push(`/trips/${trip.id}`)}
          className="bg-white text-primary-500 hover:bg-gray-100 shadow-sm  rounded-full my-4"
        >
          <ArrowLeft />
          <span>Back To Trip Summary</span>
        </Button>
      )}
      <div className="section px-12">
        {tripError && (
          <Alert variant="destructive" className="my-4 p-4">
            {error}
          </Alert>
        )}
        {error && (
          <Alert variant="destructive" className="my-4 p-4">
            {error}
          </Alert>
        )}
        {trip && (
          <>
            <h1 className="text-heading1-bold">Add an Activity</h1>
            <ActivityForm onSubmit={handleCreateActivity} trip={trip} />
          </>
        )}
      </div>
    </>
  );
}
