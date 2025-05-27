'use client';
import React, { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import TripForm from '@/components/trip/TripForm';
import { Alert } from '@/components/ui/alert';
import axiosInstance from '@/lib/axiosInstance';
import { z } from 'zod';
import { createGroupFormSchema } from '@/validation/groupFormSchemas';
import { FormikHelpers } from 'formik';
import { useTrip } from '@/context/TripContext';
import { Group } from '@/validation/groupSchema';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import { formatDate } from '@/lib/utils';

const page = () => {
  const router = useRouter();
  const { trip } = useTrip();
  const [error, setError] = useState<string | null>(null);

  const mapTripToInitialValues = (
    trip: Group,
  ): z.infer<typeof createGroupFormSchema> => {
    return {
      name: trip.name || '',
      description: trip.description || '',
      tripStartDate: trip.tripStartDate
        ? new Date(trip.tripStartDate)
        : new Date(),
      tripEndDate: trip.tripEndDate ? new Date(trip.tripEndDate) : new Date(),
      tags: Array.isArray(trip.tags) ? trip.tags : [],
      groupImageUrl: trip.groupImageUrl || '',
    };
  };

  const handleUpdateTrip = async (
    values: z.infer<typeof createGroupFormSchema>,
    actions: FormikHelpers<z.infer<typeof createGroupFormSchema>>,
  ) => {
    if (trip) {
      try {
        await axiosInstance.put(`/groups/${trip.id}`, values);
        actions.setSubmitting(false);
        router.push('/trips');
      } catch (err: unknown) {
        setError('Failed to update the trip.');
        actions.setSubmitting(false);
      }
    } else {
      router.push('/trips');
    }
  };

  if (!trip) {
    return <div>Loading trip data...</div>;
  }

  return (
    <>
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
              {formatDate(trip.tripStartDate)} - {formatDate(trip.tripEndDate)}
            </>
          )}
        </div>
      </div>
      <div className="section py-6 px-12 border">
        <h1 className="text-heading1-bold">Edit Trip</h1>
        {error && (
          <Alert variant="destructive" className="my-4 p-4">
            {error}
          </Alert>
        )}
        <TripForm
          initialValues={mapTripToInitialValues(trip)}
          onSubmit={handleUpdateTrip}
          mode="edit"
        />
      </div>
    </>
  );
};

export default page;
