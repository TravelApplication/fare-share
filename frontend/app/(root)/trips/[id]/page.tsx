'use client';
import { getToken } from '@/lib/auth';
import { Group, groupSchema } from '@/validation/groupSchema';
import axios from 'axios';
import { useParams } from 'next/navigation';
import { useEffect, useState } from 'react';

function Page() {
  const params = useParams<{ id: string }>();
  const [group, setGroup] = useState<Group | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!params.id) return;

    const fetchGroup = async () => {
      try {
        const token = getToken();
        const response = await axios.get(`/api/v1/groups/${params.id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        console.log(response.data);
        setGroup(groupSchema.parse(response.data));
      } catch (err) {
        console.error(err);
        setError('Failed to fetch group');
      }
    };

    fetchGroup();
  }, [params.id]);

  return <div>Details of trip with ID: {group?.id}</div>;
}

export default Page;
