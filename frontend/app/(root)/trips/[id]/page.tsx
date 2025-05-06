'use client';

import { useTrip } from '@/context/TripContext';
import TripCard from '@/components/trip/TripCard';
import { Alert } from '@/components/ui/alert';
import { ArrowLeft, CirclePlus, EllipsisVertical, MapPinIcon, ThumbsDown, ThumbsUp } from 'lucide-react';
import { redirect, useRouter } from 'next/navigation';
import { Button } from '@/components/ui/button';
import Link from 'next/link';
import { getToken } from '@/lib/auth';
import axios from 'axios';
import { useEffect, useState } from 'react';
import { Vote } from '@/validation/voteSchema';
import { appStore } from '@/store/appStore';
import { motion } from 'framer-motion';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';


export default function TripPage() {
  const { trip, loading, tripError, refreshTrip } = useTrip();
  const router = useRouter();
  const toFetchGroup = appStore((s) => s.toFetchGroup);
  const setToFetchGroup = appStore((s) => s.setToFetchGroup)

  const [votes, setVotes] = useState<Record<number, { for: number; against: number; userVote?: 'FOR' | 'AGAINST' }>>({});

  const user = appStore((state) => state.user);
  useEffect(() => {
    if (toFetchGroup) {
      refreshTrip();
      setToFetchGroup(false);
    }
    if (trip) {
    const voteData: typeof votes = {};
    trip.activities.forEach((activity) => {
      const forVotes = activity.votes.filter((v: Vote) => v.voteType === 'FOR');
      const againstVotes = activity.votes.filter((v: Vote) => v.voteType === 'AGAINST');
      const userVote = activity.votes.find((v: Vote) => v.userId === user?.id);

      const voteType = userVote?.voteType;
      const isValidVoteType = voteType === 'FOR' || voteType === 'AGAINST';
      
      voteData[activity.id] = {
        for: forVotes.length,
        against: againstVotes.length,
        userVote: isValidVoteType ? voteType : undefined,
      };
      });
      setVotes(voteData);
    }

    
  }, [trip, toFetchGroup]);



  const castVote = async (activityId: number, type: 'FOR' | 'AGAINST') => {
    const token = getToken();
    const currentActivity = trip?.activities.find((a) => a.id === activityId);
    const existingVote = currentActivity?.votes.find((v: any) => v.userId === user?.id);

    try {
      if (existingVote && existingVote.voteType === type) {
        await axios.delete(
          `/api/v1/groups/${trip?.id}/activities/${activityId}/votes/${existingVote.id}`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
      } else if (existingVote) {
        await axios.put(
          `/api/v1/groups/${trip?.id}/activities/${activityId}/votes/${existingVote.id}`,
          { voteType: type },
          { headers: { Authorization: `Bearer ${token}` } }
        );
      } else {
        await axios.post(
          `/api/v1/groups/${trip?.id}/activities/${activityId}/votes`,
          { voteType: type },
          { headers: { Authorization: `Bearer ${token}` } }
        );
      }

      await refreshTrip();
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) {
    return <Alert variant="default" className="p-4">Loading...</Alert>;
  }

  if (tripError) {
    return <Alert variant="destructive" className="p-4">{tripError}</Alert>;
  }

  if (!trip) return null;

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
        <h2 className="text-heading3-bold text-primary-500">Activities</h2>

        {trip.activities.length > 0 ? (
          <ul className="mt-2 space-y-4">
            {trip.activities.map((activity) => (
              <li
                key={activity.id}
                className="p-4 border rounded-lg shadow-md flex justify-between"
              >
                <div className="flex flex-col gap-2">
                  <h3
                    className="text-lg font-semibold cursor-pointer"
                    onClick={() => router.push(`/trips/${trip.id}/activities/${activity.id}`)}
                  >
                    {activity.name}
                  </h3>
                  {activity.description && (
                    <p className="text-sm text-gray-600">{activity.description}</p>
                  )}
                  {activity.location && (
                    <p className="flex items-center font-semibold gap-1">
                      <MapPinIcon className="w-4 h-4 text-red-900" />
                      <span>{activity.location}</span>
                    </p>
                  )}
                  {activity.link && (
                    <Link
                      href={activity.link}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-primary-500 underline w-min text-nowrap"
                    >
                      More info
                    </Link>
                  )}
                </div>
                <div className='flex flex-col justify-between items-end'>
                  <DropdownMenu>
                    <DropdownMenuTrigger className='text-gray-400 rounded-full hover:bg-gray-100 p-1.5 h-min w-min'>
                      <EllipsisVertical width={20} height={20}/>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent>
                      <DropdownMenuItem className="cursor-pointer">
                        <Link href={`/trips/${trip.id}/activities/${activity.id}/edit`}>
                          Edit
                        </Link>
                      </DropdownMenuItem>
                      <DropdownMenuItem className="cursor-pointer">Delete</DropdownMenuItem>
                      <DropdownMenuItem className="cursor-pointer">
                        <Link href={`/trips/${trip.id}/activities/${activity.id}`}>
                          Details
                        </Link>
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                  <div className="flex h-min">
                  <button
                    onClick={() => castVote(activity.id, 'FOR')}
                    className={`pl-4 pr-3 py-3 shadow-md flex items-center justify-center gap-2 rounded-l-full border
                      ${votes[activity.id]?.userVote === 'FOR'
                        ? 'bg-primary-500/15 text-primary-600'
                        : 'bg-white text-primary-500 border hover:bg-primary-600/15'
                      }`}
                  >
                        <motion.div
                          key={votes[activity.id]?.userVote}
                          animate={votes[activity.id]?.userVote === 'FOR' ? { rotate: [0, -15, 15, -10, 10, 0] } : {}}
                          transition={{ duration: 0.6 }}
                        >
                          <ThumbsUp width={20} height={20} />
                        </motion.div>
                      <span>{votes[activity.id]?.for || 0}</span>
                    </button>
                    <button
                      onClick={() => castVote(activity.id, 'AGAINST')}
                      className={`pr-4 pl-3 py-3 shadow-md flex items-center justify-center gap-2 rounded-r-full border
                        ${votes[activity.id]?.userVote === 'AGAINST'
                          ? 'bg-red-300/25 text-red-700'
                          : 'bg-white text-red-800 border hover:bg-red-300/20'
                        }`}
                    >
                      <span>{votes[activity.id]?.against || 0}</span>
                      <motion.div
                        key={votes[activity.id]?.userVote}
                        animate={votes[activity.id]?.userVote === 'AGAINST' ? { rotate: [0, -15, 15, -10, 10, 0] } : {}}
                        transition={{ duration: 0.6 }}
                      >
                        <ThumbsDown width={20} height={20} />
                      </motion.div>
                    </button>
                  </div>

                </div>
              </li>
            ))}
          </ul>
        ) : (
          <p className="text-gray-500">No activities yet</p>
        )}
      </div>
    </div>
  );
}
