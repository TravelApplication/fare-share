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
  error: string | null;
  refreshTrip: () => void;
}

const TripContext = createContext<TripContextType | undefined>(undefined);

export function TripContextProvider({ children }: { children: ReactNode }) {
  const params = useParams<{ id: string }>();
  const [trip, setTrip] = useState<Group | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

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
        setError('User is not a member of this group');
        return;
      }

      setTrip(fetchedTrip);
    } catch (err) {
      console.error(err);
      setError('Failed to fetch trip');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!params.id) return;

    fetchTrip();
  }, [params.id]);

  return (
    <TripContext.Provider
      value={{ trip, loading, error, refreshTrip: fetchTrip }}
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
