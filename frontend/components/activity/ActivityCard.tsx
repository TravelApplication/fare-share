'use client';
import { ActivitySchema } from '@/validation/activitySchema';
import YesNoModal from '../shared/YesNoModal';
import {
  EllipsisVertical,
  MapPinIcon,
  Pencil,
  ThumbsDown,
  ThumbsUp,
  Trash,
} from 'lucide-react';
import Link from 'next/link';
import { motion } from 'framer-motion';
import { useRouter } from 'next/navigation';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import axiosInstance from '@/lib/axiosInstance';
import { useTrip } from '@/context/TripContext';
import { appStore } from '@/store/appStore';
import { useEffect, useState } from 'react';
import { Vote } from '@/validation/voteSchema';
import { toast } from 'sonner';

export default function ActivityCard({
  activity,
}: {
  activity: ActivitySchema;
}) {
  const router = useRouter();
  const { trip, refreshTrip, getVotes } = useTrip();
  const user = appStore((state) => state.user);
  const [votes, setVotes] = useState<
    Record<
      number,
      { for: number; against: number; userVote?: 'FOR' | 'AGAINST' }
    >
  >({});

  useEffect(() => {
    if (trip && user && user.id) {
      setVotes(getVotes(user.id));
    }
  }, [trip, user?.id, getVotes, user]);

  const castVote = async (activityId: number, type: 'FOR' | 'AGAINST') => {
    const currentActivity = trip?.activities.find((a) => a.id === activityId);
    const existingVote = currentActivity?.votes.find(
      (v: Vote) => v.userId === user?.id,
    );

    try {
      if (existingVote && existingVote.voteType === type) {
        await axiosInstance.delete(
          `groups/${trip!.id}/activities/${activityId}/votes/${existingVote.id}`,
        );
      } else if (existingVote) {
        await axiosInstance.put(
          `groups/${trip!.id}/activities/${activityId}/votes/${existingVote.id}`,
          { voteType: type },
        );
      } else {
        await axiosInstance.post(
          `groups/${trip!.id}/activities/${activityId}/votes`,
          { voteType: type },
        );
      }

      await refreshTrip();
    } catch (err) {
      console.error(err);
    }
  };

  const handleDelete = async () => {
    try {
      if (activity && trip) {
        await axiosInstance.delete(
          `groups/${trip.id}/activities/${activity.id}`,
        );
        toast(`Activity ${activity.name} deleted!`, {
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
      toast(`Error deleting an activity`, {
        duration: 7000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
    }
  };
  return (
    <li
      key={activity.id}
      className="p-4 border rounded-lg shadow-md flex justify-between"
    >
      <div className="flex flex-col gap-2">
        <h3
          className="text-lg font-semibold cursor-pointer"
          onClick={() =>
            router.push(`/trips/${trip!.id}/activities/${activity.id}`)
          }
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
      <div className="flex flex-col justify-between items-end">
        <DropdownMenu>
          <DropdownMenuTrigger className="text-gray-400 rounded-full hover:bg-gray-100 p-1.5 h-min w-min">
            <EllipsisVertical width={20} height={20} />
          </DropdownMenuTrigger>
          <DropdownMenuContent>
            <DropdownMenuItem
              onClick={() =>
                router.push(`/trips/${trip!.id}/activities/${activity.id}/edit`)
              }
              className="cursor-pointer !text-primary-500 hover:!bg-primary-500/10 flex items-center gap-2"
            >
              <Pencil width={16} height={16} />
              <span>Edit</span>
            </DropdownMenuItem>
            <DropdownMenuItem
              className="cursor-pointer !text-red-800 hover:!bg-red-500/10"
              onSelect={(e) => e.preventDefault()}
            >
              <div>
                <YesNoModal
                  title="Are you sure you want to delete this activity?"
                  description={`Activity "${activity.name}" will be permanently removed.`}
                  cancelName="Cancel"
                  actionName="Delete"
                  onConfirm={handleDelete}
                  trigger={
                    <div className="flex items-center gap-2">
                      <Trash width={16} height={16} />
                      <span>Delete</span>
                    </div>
                  }
                />
              </div>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
        <div className="flex h-min">
          <Tooltip>
            <TooltipTrigger asChild>
              <button
                onClick={() => castVote(activity.id, 'FOR')}
                className={`pl-4 pr-3 py-3 shadow-md flex items-center justify-center gap-2 rounded-l-full border border-r-transparent text-primary-600 hover:bg-primary-500/20
              ${
                votes[activity.id]?.userVote === 'FOR'
                  ? 'bg-primary-500/10'
                  : 'bg-white'
              }`}
              >
                <motion.div
                  key={votes[activity.id]?.userVote}
                  animate={
                    votes[activity.id]?.userVote === 'FOR'
                      ? { rotate: [0, -15, 15, -10, 10, 0] }
                      : {}
                  }
                  transition={{ duration: 0.6 }}
                >
                  <ThumbsUp width={20} height={20} />
                </motion.div>
                <span>{votes[activity.id]?.for || 0}</span>
              </button>
            </TooltipTrigger>
            <TooltipContent>
              {activity.votes.some((v) => v.voteType === 'FOR') ? (
                activity.votes
                  .filter((v) => v.voteType === 'FOR')
                  .map((v, idx) => <p key={`vf-${idx}`}>{v.userEmail}</p>)
              ) : (
                <p>No votes yet</p>
              )}
            </TooltipContent>
          </Tooltip>
          <Tooltip>
            <TooltipTrigger asChild>
              <button
                onClick={() => castVote(activity.id, 'AGAINST')}
                className={`pr-4 pl-3 py-3 shadow-md flex items-center justify-center gap-2 text-red-800 rounded-r-full border hover:bg-red-400/20
            ${
              votes[activity.id]?.userVote === 'AGAINST'
                ? 'bg-red-400/10'
                : 'bg-white'
            }`}
              >
                <span>{votes[activity.id]?.against || 0}</span>
                <motion.div
                  key={votes[activity.id]?.userVote}
                  animate={
                    votes[activity.id]?.userVote === 'AGAINST'
                      ? { rotate: [0, -15, 15, -10, 10, 0] }
                      : {}
                  }
                  transition={{ duration: 0.6 }}
                >
                  <ThumbsDown width={20} height={20} />
                </motion.div>
              </button>
            </TooltipTrigger>
            <TooltipContent>
              {activity.votes.some((v) => v.voteType === 'AGAINST') ? (
                activity.votes
                  .filter((v) => v.voteType === 'AGAINST')
                  .map((v, idx) => <p key={`va-${idx}`}>{v.userEmail}</p>)
              ) : (
                <p>No votes yet</p>
              )}
            </TooltipContent>
          </Tooltip>
        </div>
      </div>
    </li>
  );
}
