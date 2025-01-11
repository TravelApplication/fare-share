import TripCard from "@/components/cards/TripCard";
import Pagination from "@/components/shared/Pagination";
import Image from "next/image";
import Link from "next/link";
function Page() {
  // mock of trips data
  // TODO: fetch user's trips from the backend
  const trips = [
    {
      id: "1",
      name: "Hawaii Vacation",
      startDate: "2022-01-01",
      endDate: "2022-01-10",
      location: "Hawaii",
      imageUrl: "https://placecats.com/600/400",
    },
    {
      id: "2",
      name: "Cultural Tour of Paris",
      startDate: "2022-02-01",
      endDate: "2022-02-10",
      location: "Paris",
      imageUrl: "https://placecats.com/400/600",
    },
    {
      id: "3",
      name: "Sępólno Krajeńskie Gateway",
      startDate: "2022-03-01",
      endDate: "2022-03-03",
      location: "Sępólno Krajeńskie",
      imageUrl: "https://placecats.com/400/400",
    },
  ];

  return (
    <div>
      <div className="flex">
        <Link href="/trips/new" className="flex items-center gap-1.5">
          <div className="border border-2 border-primary-500 rounded-full p-0.5">
            <Image
              src="/assets/plus-blue.svg"
              alt="plus"
              width={18}
              height={18}
            />
          </div>
          <p className="text-base-semibold text-primary-500">Add a Trip</p>
        </Link>
      </div>
      {trips ? (
        <>
          <div className="flex flex-col gap-4 mt-4">
            {trips.map((trip) => (
              <TripCard
                key={trip.id}
                id={trip.id}
                name={trip.name}
                startDate={trip.startDate}
                endDate={trip.endDate}
                location={trip.location}
                imageUrl={trip.imageUrl}
              />
            ))}
          </div>
          <Pagination />
        </>
      ) : (
        <div>No trips yet. Add a trip to get started!</div>
      )}
    </div>
  );
}

export default Page;
