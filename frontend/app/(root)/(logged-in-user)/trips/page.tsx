"use client";
import TripCard from "@/components/cards/TripCard";
import { getToken, isAuthenticated, logout } from "@/lib/auth";
import axios from "axios";
import { useEffect, useState } from "react";
import { Trip, tripSchema } from "@/validation/tripSchemas";
import { useRouter, useSearchParams } from "next/navigation";
import Link from "next/link";
import CustomPagination from "@/components/shared/CustomPagination";
import {
  CirclePlus,
  ArrowDownZA,
  ArrowUpAZ,
  ArrowDown,
  ArrowUp,
} from "lucide-react";
import { Alert } from "@/components/ui/alert";

function Page() {
  const [error, setError] = useState<string | null>(null);
  const [trips, setTrips] = useState<Trip[]>([]);
  const [totalTrips, setTotalTrips] = useState(0);
  const tripsPerPage = 3;

  const router = useRouter();
  const searchParams = useSearchParams();

  const page = Number(searchParams.get("page")) || 1;
  const sortBy = searchParams.get("sort") || "createdAt";
  const sortDirection = searchParams.get("direction") || "DESC";

  useEffect(() => {
    if (!isAuthenticated()) {
      logout();
      return;
    }

    const fetchTrips = async () => {
      try {
        const token = getToken();
        if (!token) {
          logout();
          return;
        }

        const response = await axios.get("/api/v1/groups/user-groups", {
          headers: { Authorization: `Bearer ${token}` },
          params: {
            page: page - 1,
            size: tripsPerPage,
            sort: `${sortBy},${sortDirection}`,
          },
        });
        const parsedTrips = response.data.content
          .map((trip: any) => {
            try {
              return tripSchema.parse(trip);
            } catch (error) {
              return null;
            }
          })
          .filter((trip: Trip | null) => trip !== null);

        setTrips(parsedTrips);
        setTotalTrips(response.data.totalElements);
      } catch (error) {
        console.error(error);
        setError("An error occurred while loading trips. Please try again.");
      }
    };

    fetchTrips();
  }, [page, sortBy, sortDirection]);

  const handleSortChange = (newSortBy: string) => {
    let newDirection = sortDirection;
    if (sortBy === newSortBy && sortDirection === "ASC") {
      newDirection = "DESC";
    } else if (sortBy === newSortBy && sortDirection === "DESC") {
      newDirection = "ASC";
    } else {
      newDirection = "DESC";
    }

    router.push(`?page=1&sort=${newSortBy}&direction=${newDirection}`);
  };

  return (
    <div>
      <div className="flex justify-between">
        <Link
          href="/trips/create"
          className="flex items-center gap-1.5 text-body-semibold text-primary-500 hover:underline"
        >
          <CirclePlus />
          <p>Add a Trip</p>
        </Link>

        <div className="flex flex-col md:flex-row gap-4">
          <button
            className={`flex items-center gap-2 px-3 py-2  shadow-md ${
              sortBy === "createdAt" ? "bg-primary-100" : ""
            }`}
            onClick={() => handleSortChange("createdAt")}
          >
            Sort by {sortBy !== "createdAt" ? "Newest" : ""}
            {sortBy === "createdAt" &&
              (sortDirection === "ASC" ? (
                <>
                  Oldest <ArrowDown />
                </>
              ) : (
                <>
                  Newest <ArrowUp />
                </>
              ))}
          </button>
          <button
            className={`flex items-center gap-2 px-3 py-2 shadow-md ${
              sortBy === "name" ? "bg-primary-100" : ""
            }`}
            onClick={() => handleSortChange("name")}
          >
            Sort by Name
            {sortBy === "name" && (
              <span>
                {sortDirection === "ASC" ? <ArrowUpAZ /> : <ArrowDownZA />}
              </span>
            )}
          </button>
        </div>
      </div>

      {trips.length > 0 ? (
        <>
          <div className="flex flex-col gap-4 my-4">
            {trips.map((trip) => (
              <TripCard key={trip.id} trip={trip} />
            ))}
          </div>
          <CustomPagination
            totalTrips={totalTrips}
            tripsPerPage={tripsPerPage}
          />
        </>
      ) : (
        <div>No trips yet. Add a trip to get started!</div>
      )}
      {error && <Alert className="mt-4">{error}</Alert>}
    </div>
  );
}

export default Page;
