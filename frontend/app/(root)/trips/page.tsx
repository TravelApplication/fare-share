'use client';
import React from 'react';
import TripCard from '@/components/trip/TripCard';
import { useEffect, useState } from 'react';
import { Group, groupSchema } from '@/validation/groupSchema';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import CustomPagination from '@/components/shared/CustomPagination';
import {
  CirclePlus,
  ArrowDownZA,
  ArrowUpAZ,
  ArrowDown,
  ArrowUp,
  MapPlus,
} from 'lucide-react';
import { Alert } from '@/components/ui/alert';
import axiosInstance from '@/lib/axiosInstance';

function Page() {
  const [error, setError] = useState<string | null>(null);
  const [trips, setTrips] = useState<Group[]>([]);
  const [totalTrips, setTotalTrips] = useState(0);
  const tripsPerPage = 3;

  const router = useRouter();
  const searchParams = useSearchParams();

  const page = Number(searchParams.get('page')) || 1;
  const sortBy = searchParams.get('sort') || 'createdAt';
  const sortDirection = searchParams.get('direction') || 'DESC';

  useEffect(() => {
    const fetchTrips = async () => {
      try {
        const response = await axiosInstance.get('groups/user-groups', {
          params: {
            page: page - 1,
            size: tripsPerPage,
            sort: `${sortBy},${sortDirection}`,
          },
        });
        const parsedTrips = response.data.content
          .map((trip: unknown) => {
            try {
              return groupSchema.parse(trip);
            } catch {
              return null;
            }
          })
          .filter((trip: Group | null) => trip !== null);

        setTrips(parsedTrips);
        setTotalTrips(response.data.totalElements);
      } catch (error) {
        console.error(error);
        setError('An error occurred while loading trips. Please try again.');
      }
    };

    fetchTrips();
  }, [page, sortBy, sortDirection]);

  const handleSortChange = (newSortBy: string) => {
    let newDirection = sortDirection;
    if (sortBy === newSortBy && sortDirection === 'ASC') {
      newDirection = 'DESC';
    } else if (sortBy === newSortBy && sortDirection === 'DESC') {
      newDirection = 'ASC';
    } else {
      newDirection = 'DESC';
    }

    router.push(`?page=1&sort=${newSortBy}&direction=${newDirection}`);
  };

  return (
    <div>
      <div className="flex justify-between gap-4 sm:p-0 p-2 sm:flex-row flex-col ">
        <Link
          href="/trips/create"
          className="flex items-center gap-1.5 text-body-semibold text-primary-500 hover:underline"
        >
          <CirclePlus />
          <p>Add a Trip</p>
        </Link>
        {trips && trips.length > 0 && (
          <div className="flex items-center text-gray-700 gap-4">
            <div className="font-semibold ">Sort By</div>
            <button
              className={`flex items-center gap-2 px-3 py-2  shadow-md ${
                sortBy === 'createdAt' ? 'bg-gray-100' : ''
              }`}
              onClick={() => handleSortChange('createdAt')}
            >
              Date{' '}
              {sortBy === 'createdAt' &&
                (sortDirection === 'ASC' ? (
                  <>
                    <ArrowDown />
                  </>
                ) : (
                  <>
                    <ArrowUp />
                  </>
                ))}
            </button>
            <button
              className={`flex items-center gap-2 px-3 py-2 shadow-md ${
                sortBy === 'name' ? 'bg-gray-100' : ''
              }`}
              onClick={() => handleSortChange('name')}
            >
              Name
              {sortBy === 'name' && (
                <span>
                  {sortDirection === 'ASC' ? <ArrowUpAZ /> : <ArrowDownZA />}
                </span>
              )}
            </button>
          </div>
        )}
      </div>

      {trips.length > 0 ? (
        <>
          <div className="flex flex-col gap-4 my-4">
            {trips.map((trip) => (
              <TripCard key={trip.id} trip={trip} />
            ))}
          </div>
          <CustomPagination
            totalItems={totalTrips}
            itemsPerPage={tripsPerPage}
          />
        </>
      ) : (
        <div className="mt-20 flex flex-col items-center justify-center gap-3">
          <MapPlus size={220} className="text-gray-200" />
          <p className="text-gray-300 font-semibold text-center">
            No trips yet. Create a trip to get started!
          </p>
        </div>
      )}
      {error && <Alert className="mt-4">{error}</Alert>}
    </div>
  );
}

export default Page;
