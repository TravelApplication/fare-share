"use client";
import Image from "next/image";
import Link from "next/link";
import { Trip } from "@/validation/tripSchemas";
import { useState } from "react";

interface TripCardProps {
  trip: Trip;
}
function TripCard({ trip }: TripCardProps) {
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
    groupImageUrl || "/assets/trip-placeholder.png"
  );

  return (
    <div className="section p-0 flex flex-col md:flex-row items-center justify-between">
      <div className="py-6 px-10">
        <Link href={`/trips/${id}`}>
          <h4 className="text-heading3-bold text-primary-500 hover:underline">
            {name}
          </h4>
        </Link>
        <p className="text-small-regular text-gray-500 mb-2 ">
          {tripStartDate} - {tripEndDate}
        </p>
        <p>{description}</p>
        {tags.length > 0 && (
          <div className="flex flex-wrap gap-2 mt-2">
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
        <Link href="/">
          <button className="p-0 text-small-regular text-primary-500 hover:underline mt-2">
            Manage Group Members
          </button>
        </Link>
      </div>
      <div className="relative w-48 h-48">
        <Link href={`/trips/${id}`}>
          <Image
            className="rounded-r-lg"
            src={imageSrc}
            onError={() => setImageSrc("/assets/trip-placeholder.png")}
            alt={name}
            fill
            objectFit="cover"
          />
        </Link>
      </div>
    </div>
  );
}

export default TripCard;
