'use client';

import { useTrip } from '@/context/TripContext';
import TripCard from '@/components/trip/TripCard';
import { Alert } from '@/components/ui/alert';
import { ArrowLeft, CirclePlus, ThumbsDown, ThumbsUp } from 'lucide-react';
import { redirect, useRouter, useSearchParams } from 'next/navigation';
import { Button } from '@/components/ui/button';
import { useEffect, useState } from 'react';
import { appStore } from '@/store/appStore';
import CustomPagination from '@/components/shared/CustomPagination';

import {
  Select,
  SelectTrigger,
  SelectContent,
  SelectItem,
  SelectValue,
} from '@/components/ui/select';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import ActivityCard from '@/components/activity/ActivityCard';
const ITEMS_PER_PAGE = 3;

export default function TripPage() {
  const { trip, loading, tripError, refreshTrip, getVotes } = useTrip();
  const [votes, setVotes] = useState<
    Record<
      number,
      { for: number; against: number; userVote?: 'FOR' | 'AGAINST' }
    >
  >({});
  const searchParams = useSearchParams();
  const router = useRouter();
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState<'for' | 'against' | 'none'>('none');

  const toFetchGroup = appStore((s) => s.toFetchGroup);
  const setToFetchGroup = appStore((s) => s.setToFetchGroup);
  const user = appStore((state) => state.user);

  useEffect(() => {
    if (toFetchGroup) {
      refreshTrip();
      setToFetchGroup(false);
    }
    if (trip && user && user.id) {
      setVotes(getVotes(user.id));
    }
  }, [trip, toFetchGroup, refreshTrip, setToFetchGroup]);

  if (!trip) return null;

  if (loading) {
    return (
      <Alert variant="default" className="p-4">
        Loading...
      </Alert>
    );
  }

  if (tripError) {
    return (
      <Alert variant="destructive" className="p-4">
        {tripError}
      </Alert>
    );
  }

  const totalActivities = trip.activities.length;
  const currentPage = Number(searchParams.get('page') || 1);

  const filteredActivities = trip.activities.filter((activity) =>
    activity.name.toLowerCase().includes(searchQuery.toLowerCase()),
  );

  const sortedActivities = [...filteredActivities].sort((a, b) => {
    if (sortBy === 'for') {
      return (
        votes[b.id]?.for - votes[a.id]?.for ||
        votes[b.id]?.against - votes[a.id]?.against
      );
    }
    if (sortBy === 'against') {
      return (
        votes[b.id]?.against - votes[a.id]?.against ||
        votes[b.id]?.for - votes[a.id]?.for
      );
    }
    return 0;
  });

  const paginatedActivities = sortedActivities.slice(
    (currentPage - 1) * ITEMS_PER_PAGE,
    currentPage * ITEMS_PER_PAGE,
  );

  return (
    <div>
      <Button
        onClick={() => router.push('/trips')}
        className="bg-white border text-primary-500 hover:bg-gray-100 shadow-sm rounded-full my-4"
      >
        <ArrowLeft />
        <span>Back To Trips</span>
      </Button>

      <TripCard trip={trip} />

      <button
        onClick={() => redirect(`/trips/${trip.id}/activities/create`)}
        className="mx-auto relative -mb-6 z-50 bg-white border text-primary-500 hover:bg-gray-100 px-4 py-3 shadow-md flex gap-2 items-center justify-center rounded-full mt-4"
      >
        <CirclePlus />
        <span className="text-base-semibold">Add an Activity</span>
      </button>

      <div className="p-5 z-0 border rounded-lg shadow-md">
        <div>
          <h2 className="text-heading3-bold text-primary-500">Activities</h2>
          {trip.activities.length > 0 && (
            <div className="grid grid-cols-[2fr_1fr] flex gap-4 my-4">
              <div>
                <Label className="pl-2 text-gray-700">Search</Label>
                <Input
                  type="text"
                  placeholder="Search activities"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="border p-2 rounded-lg w-full"
                />
              </div>
              <div className="font-normal">
                <Label className="pl-2 text-gray-700">Sort By</Label>
                <Select
                  value={sortBy}
                  onValueChange={(value) =>
                    setSortBy(value as 'for' | 'against' | 'none')
                  }
                >
                  <SelectTrigger className="border p-2 rounded-lg w-full font-normal">
                    <SelectValue placeholder="Sort by" />
                  </SelectTrigger>
                  <SelectContent className="font-normal">
                    <SelectItem value="none">None</SelectItem>
                    <SelectItem value="for">
                      <div className="flex gap-2 items-center">
                        <div>Most votes for</div>
                        <ThumbsUp
                          className="text-primary-600"
                          width={16}
                          height={16}
                        />
                      </div>
                    </SelectItem>
                    <SelectItem value="against">
                      <div className="flex gap-2 items-center">
                        <div>Most votes against</div>
                        <ThumbsDown
                          className="text-red-800"
                          width={16}
                          height={16}
                        />
                      </div>
                    </SelectItem>
                  </SelectContent>
                </Select>
              </div>
            </div>
          )}
        </div>

        {paginatedActivities.length > 0 ? (
          <ul className="mt-2 space-y-4">
            {paginatedActivities.map((activity) => (
              <ActivityCard key={`a-${activity.id}`} activity={activity} />
            ))}
          </ul>
        ) : (
          <p className="text-gray-500">
            {trip.activities?.length > 0
              ? 'No activities matching search criteria.'
              : 'No activities yet.'}
          </p>
        )}

        <CustomPagination
          totalItems={totalActivities}
          itemsPerPage={ITEMS_PER_PAGE}
        />
      </div>
    </div>
  );
}
