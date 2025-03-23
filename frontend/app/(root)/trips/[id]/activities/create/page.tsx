'use client';
import { redirect, useParams } from 'next/navigation';
import ActivityForm from '@/components/activity/ActivityForm';
import { useState } from 'react';
import { activityFormSchema } from '@/validation/activityFormSchema';
import { z } from 'zod';
import { FormikHelpers } from 'formik';
import { getToken } from '@/lib/auth';
import axios from 'axios';
import { Alert } from '@/components/ui/alert';

export default function NewActivityPage() {
  const params = useParams<{ id: string }>();
  const [error, setError] = useState<string | null>(null);

  const handleCreateActivity = async (
    values: z.infer<typeof activityFormSchema>,
    actions: FormikHelpers<z.infer<typeof activityFormSchema>>,
  ) => {
    try {
      const token = getToken();
      const response = await axios.post(
        `/api/v1/groups/${params.id}/activites`,
        values,
        {
          headers: { Authorization: `Bearer ${token}` },
        },
      );
      console.log(response);
      actions.resetForm();
      redirect(`/trips/${params.id}`);
    } catch (err: unknown) {
      setError(err.message || 'An error occurred');
    }
  };
  return (
    <div className="section py-6 px-12">
      <h1 className="text-heading1-bold">Create Trip</h1>
      {error && (
        <Alert variant="destructive" className="my-4 p-4">
          {error}
        </Alert>
      )}
      <ActivityForm onSubmit={handleCreateActivity} />
    </div>
  );
}
