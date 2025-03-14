'use client';
import React from 'react';
import TripForm from '@/components/trip/TripForm';
import { Alert } from '@/components/ui/alert';
import { createGroupFormSchema } from '@/validation/groupSchemas';
import { useState } from 'react';
import { FormikHelpers } from 'formik';
import { z } from 'zod';
import { getToken } from '@/lib/auth';
import axios from 'axios';

const NewTripPage = () => {
  const [error, setError] = useState<string | null>(null);

  const handleCreateTrip = async (
    values: z.infer<typeof createGroupFormSchema>,
    actions: FormikHelpers<z.infer<typeof createGroupFormSchema>>,
  ) => {
    try {
      const token = getToken();
      const response = await axios.post('/api/v1/groups', values, {
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log(response);
      actions.resetForm();
      window.location.href = '/trips';
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
      <TripForm onSubmit={handleCreateTrip} mode="create" />
    </div>
  );
};

export default NewTripPage;
