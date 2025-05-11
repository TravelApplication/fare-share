'use client';

import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from 'react';
import axios from 'axios';
import { useParams } from 'next/navigation';
import { getToken, decodeToken } from '@/lib/auth';
import { Group, groupSchema } from '@/validation/groupSchema';

interface TripContextType {
  trip: Group | null;
  loading: boolean;
  tripError: string | null;
  refreshTrip: () => void;
  getVotes: (
    userId: number,
  ) => Record<
    number,
    { for: number; against: number; userVote?: 'FOR' | 'AGAINST' }
  >;
}

const TripContext = createContext<TripContextType | undefined>(undefined);

export function TripContextProvider({ children }: { children: ReactNode }) {
  const params = useParams<{ id: string }>();
  const [trip, setTrip] = useState<Group | null>(null);
  const [loading, setLoading] = useState(true);
  const [tripError, setTripError] = useState<string | null>(null);

  useEffect(() => {
    if (!params.id) return;

    fetchTrip();
  }, [params.id]);

  const fetchTrip = async () => {
    try {
      const token = getToken();
      const userId = token ? decodeToken(token)?.sub : '';

      const response = await axios.get(`/api/v1/groups/${params.id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const fetchedTrip = groupSchema.parse(response.data);

      if (
        !userId ||
        fetchedTrip.memberships.every(
          (membership) => membership.userId !== parseInt(userId),
        )
      ) {
        setTripError('User is not a member of this group');
        return;
      }

      setTrip(fetchedTrip);
      console.log('Fetched trip', fetchedTrip);
    } catch (err) {
      console.error(err);
      setTripError('Failed to fetch trip');
    } finally {
      setLoading(false);
    }
  };

  const getVotes = (userId: number) => {
    const voteData: Record<
      number,
      { for: number; against: number; userVote?: 'FOR' | 'AGAINST' }
    > = {};
    if (trip) {
      trip.activities.forEach((activity) => {
        const forVotes = activity.votes.filter((v) => v.voteType === 'FOR');
        const againstVotes = activity.votes.filter(
          (v) => v.voteType === 'AGAINST',
        );
        const userVote = activity.votes.find((v) => v.userId === userId);
        voteData[activity.id] = {
          for: forVotes.length,
          against: againstVotes.length,
          userVote:
            userVote?.voteType === 'FOR' || userVote?.voteType === 'AGAINST'
              ? userVote.voteType
              : undefined,
        };
      });
    }
    return voteData;
  };

  return (
    <TripContext.Provider
      value={{ trip, loading, tripError, refreshTrip: fetchTrip, getVotes }}
    >
      {children}
    </TripContext.Provider>
  );
}

export function useTrip() {
  const context = useContext(TripContext);
  if (!context) {
    throw new Error('useTrip must be used within a TripContextProvider');
  }
  return context;
}
