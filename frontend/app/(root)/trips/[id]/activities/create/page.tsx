'use client';
import ActivityForm from '@/components/activity/ActivityForm';
import { useState } from 'react';
import { activityFormSchema } from '@/validation/activityFormSchema';
import { z } from 'zod';
import { FormikHelpers } from 'formik';
import axiosInstance from '@/lib/axiosInstance';
import { Alert } from '@/components/ui/alert';
import { useTrip } from '@/context/TripContext';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import { toast } from 'sonner';

export default function NewActivityPage() {
  const { trip, refreshTrip } = useTrip();
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  const handleCreateActivity = async (
    values: z.infer<typeof activityFormSchema>,
    actions: FormikHelpers<z.infer<typeof activityFormSchema>>,
  ) => {
    if (!trip) return;
    try {
      const response = await axiosInstance.post(
        `groups/${trip.id}/activities`,
        values,
      );
      console.log(response.data);
      toast(`Activity ${values.name} added!`, {
        duration: 7000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
      actions.resetForm();
      refreshTrip();

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
          className="bg-white border text-primary-500 hover:bg-gray-100 shadow-sm  rounded-full my-4"
        >
          <ArrowLeft />
          <span>Back To Trip Summary</span>
        </Button>
      )}
      <div className="section px-12">
        {error && (
          <Alert variant="destructive" className="my-4 p-4">
            {error}
          </Alert>
        )}
        {trip && (
          <>
            <h1 className="text-heading2-bold">Add an Activity</h1>
            <ActivityForm onSubmit={handleCreateActivity} trip={trip} />
          </>
        )}
      </div>
    </>
  );
}
