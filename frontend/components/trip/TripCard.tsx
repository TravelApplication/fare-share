'use client';
import React from 'react';
import Link from 'next/link';
import { Group } from '@/validation/groupSchema';
import { useState } from 'react';
import { formatDate } from '@/lib/utils';
import {
  Ellipsis,
  MessageCircle,
  Pencil,
  Trash,
  UsersRound,
} from 'lucide-react';
import YesNoModal from '../shared/YesNoModal';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';
import { useRouter } from 'next/navigation';
import axiosInstance from '@/lib/axiosInstance';
import { toast } from 'sonner';

interface TripCardProps {
  trip: Group;
  showDropdownOptions?: boolean;
}
function TripCard({ trip, showDropdownOptions = false }: TripCardProps) {
  const {
    id,
    name,
    description,
    tripStartDate,
    tripEndDate,
    tags,
    groupImageUrl,
  } = trip;
  const [imageSrc, setImageSrc] = useState(
    groupImageUrl || '/assets/trip-placeholder.png',
  );

  const router = useRouter();

  const handleDelete = async () => {
    try {
      if (trip) {
        await axiosInstance.delete(`groups/${trip.id}`);
        toast(`Trip ${name} deleted!`, {
          duration: 7000,
          action: {
            label: 'Close',
            onClick: () => {
              toast.dismiss();
            },
          },
        });

        router.push('/trips');
      }
    } catch {
      toast(`Error deleting the trip`, {
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

  const renderDropdownMenu = (className?: string) => (
    <div className={className}>
      <DropdownMenu>
        <DropdownMenuTrigger className="text-primary-500 border bg-white shadow-md rounded-full hover:bg-gray-100 p-1.5 h-min w-min">
          <Ellipsis width={20} height={20} />
        </DropdownMenuTrigger>
        <DropdownMenuContent>
          <DropdownMenuItem
            onClick={() => router.push(`/trips/${id}/edit`)}
            className="cursor-pointer !text-primary-500 hover:!bg-primary-500/10 flex items-center gap-2"
          >
            <Pencil width={16} height={16} />
            <span>Edit</span>
          </DropdownMenuItem>
          <DropdownMenuItem
            className="cursor-pointer !text-red-800 hover:!bg-red-500/10"
            onSelect={(e) => e.preventDefault()}
          >
            <YesNoModal
              title="Are you sure you want to delete this activity?"
              description={`Group "${name}" will be permanently removed.`}
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
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );

  return (
    <div className="section p-0 flex flex-col md:flex-row md:items-start justify-start md:justify-between border bg-white shadow-md rounded-lg">
      <div className="p-6 w-full md:w-2/3">
        <div className="flex justify-between">
          <Link href={`/trips/${id}`}>
            <h4 className="text-heading3-bold text-primary-500 hover:underline">
              {name}
            </h4>
          </Link>
          {showDropdownOptions && (
            <div className="md:hidden block -mr-3 -mt-3">
              {renderDropdownMenu()}
            </div>
          )}
        </div>

        <p className="text-small-regular text-gray-500 mb-2">
          {tripStartDate ? formatDate(tripStartDate) : 'N/A'} -{' '}
          {tripEndDate ? formatDate(tripEndDate) : 'N/A'}
        </p>
        <p className="text-gray-700">{description}</p>

        {tags.length > 0 && (
          <div className="flex flex-wrap gap-2 mt-3">
            {tags.map((tag, index) => (
              <span
                key={index}
                className="px-3 py-1 text-small-medium text-white bg-primary-500 rounded-lg"
              >
                #{tag}
              </span>
            ))}
          </div>
        )}
        <div className="flex gap-3 mt-6">
          <button
            className="text-primary-500 text-sm border shadow-sm flex gap-2 py-2 px-4 rounded-full items-center hover:bg-gray-100"
            onClick={() => router.push(`/trips/${id}/members`)}
          >
            <UsersRound size={20} />
            <div>Members</div>
          </button>
          <button
            className="text-primary-500 text-sm border shadow-sm flex gap-2 py-2 px-4 rounded-full items-center hover:bg-gray-100"
            onClick={() => router.push(`/trips/${id}/chat`)}
          >
            <MessageCircle size={20} />
            <div>Chat</div>
          </button>
        </div>
      </div>
      <div className="relative w-48 h-48 md:w-60 md:h-60 hidden md:block overflow-hidden rounded-r-lg">
        {showDropdownOptions && (
          <div className="absolute top-2 right-2 hidden md:block">
            {renderDropdownMenu()}
          </div>
        )}

        <Link href={`/trips/${id}`}>
          <img
            src={imageSrc}
            alt={name}
            className="w-full h-full object-cover"
            onError={() => setImageSrc('/assets/trip-placeholder.png')}
          />
        </Link>
      </div>
    </div>
  );
}

export default TripCard;
