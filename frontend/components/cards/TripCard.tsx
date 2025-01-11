import Image from "next/image";
import Link from "next/link";
interface Props {
  id: string;
  name: string;
  startDate: string;
  endDate: string;
  location: string;
  imageUrl: string;
}

async function TripCard({
  id,
  name,
  startDate,
  endDate,
  location,
  imageUrl,
}: Props) {
  return (
    <div className="section p-0 flex items-center justify-between">
      <div className="py-6 px-10">
        <h4 className="text-heading4-semibold text-primary-500">{name}</h4>
        <p>{location}</p>
        <p className="text-small-regular text-gray-500">
          {startDate} - {endDate}
        </p>
        <Link href="/">
          <button className="p-0 text-small-regular text-primary-500">
            Manage Group Members
          </button>
        </Link>
        <div className="flex gap-4 mt-2 text-small-regular text-primary-500">
          <Link href={`/trips/${id}/edit`}>
            <button className="p-0">Edit</button>
          </Link>
          <button className="p-0">Delete</button>
        </div>
      </div>
      <div className="relative w-48 h-48">
        <Image
          className="rounded-r-lg"
          src={imageUrl}
          alt={name}
          layout="fill"
          objectFit="cover"
        />
      </div>
    </div>
  );
}

export default TripCard;
