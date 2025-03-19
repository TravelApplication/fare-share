'use client';
import { decodeToken, getToken } from '@/lib/auth';
import { Group, groupSchema } from '@/validation/groupSchema';
import axios from 'axios';
import { useParams } from 'next/navigation';
import { useEffect, useState } from 'react';
import TripCard from '@/components/cards/TripCard';
import { Alert } from '@/components/ui/alert';

function Page() {
  const params = useParams<{ id: string }>();
  const [group, setGroup] = useState<Group | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!params.id) return;

    const fetchGroup = async () => {
      try {
        const token = getToken();
        const userId = token ? decodeToken(token)?.sub : '';

        const response = await axios.get(`/api/v1/groups/${params.id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        const fetchedGroup = groupSchema.parse(response.data);
        console.log(fetchedGroup);

        if (
          !userId ||
          fetchedGroup.memberships.every(
            (membership) => membership.userId !== parseInt(userId),
          )
        ) {
          setError('User is not a member of this group');
          return;
        }

        setGroup(groupSchema.parse(response.data));
      } catch (err) {
        console.error(err);
        setError('Failed to fetch trip');
      }
    };

    fetchGroup();
  }, [params.id]);

  return (
    <>
      {group ? (
        <>
          <TripCard trip={group} />
        </>
      ) : (
        <Alert variant="default" className="p-4">
          {error || 'Loading...'}
        </Alert>
      )}
    </>
  );
}

export default Page;
