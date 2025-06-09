'use client';
import React from 'react';
import TripForm from '@/components/trip/TripForm';
import { Alert } from '@/components/ui/alert';
import { createGroupFormSchema } from '@/validation/groupFormSchemas';
import { useState } from 'react';
import { FormikHelpers } from 'formik';
import { z } from 'zod';
import axiosInstance from '@/lib/axiosInstance';
import { useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';

const NewTripPage = () => {
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();
  const handleCreateTrip = async (
    values: z.infer<typeof createGroupFormSchema>,
    actions: FormikHelpers<z.infer<typeof createGroupFormSchema>>,
  ) => {
    try {
      await axiosInstance.post('groups', values);
      actions.resetForm();
      router.push('/trips');
    } catch {
      setError('An error occurred');
    }
  };

  return (
    <>
      <Button
        onClick={() => router.push(`/trips`)}
        className="bg-white border text-primary-500 hover:bg-gray-100 shadow-sm  rounded-full mb-4"
      >
        <ArrowLeft />
        <span>Back to Trips</span>
      </Button>
      <div className="section py-6 px-12 border">
        <h1 className="text-heading1-bold">Create Trip</h1>
        {error && (
          <Alert variant="destructive" className="my-4 p-4">
            {error}
          </Alert>
        )}
        <TripForm onSubmit={handleCreateTrip} mode="create" />
      </div>
    </>
  );
};

export default NewTripPage;
