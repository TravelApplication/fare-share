'use client';

import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { UserSearch } from '@/validation/userProfileSchemas';
import UserTile from '@/components/shared/UserTile';
import { getToken, logout } from '@/lib/auth';
import axiosInstance from '@/lib/axiosInstance';

const SearchPage = () => {
  const searchParams = useSearchParams();
  const query = searchParams.get('name') || '';
  const [results, setResults] = useState<UserSearch[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchSearchResults = async () => {
      if (!query.trim()) return;

      setLoading(true);
      setError(null);
      const token = await getToken();
      if (!token) {
        logout();
        return;
      }
      try {
        const response = await axiosInstance.get(`user-info/search`, {
          params: { name: query, page: 0, size: 20 },
          headers: { Authorization: `Bearer ${token}` },
        });
        setResults(response.data.content || []);
      } catch (error) {
        console.error('Error fetching search results:', error);
        setError('Failed to fetch search results');
      } finally {
        setLoading(false);
      }
    };

    fetchSearchResults();
  }, [query]);

  return (
    <div className="max-w-4xl mx-auto p-4">
      <h1 className="text-heading1-bold mb-4">
        Search Results for &quot;{query}&quot;
      </h1>
      {loading ? (
        <p>Loading...</p>
      ) : error ? (
        <p className="text-red-500">{error}</p>
      ) : results.length > 0 ? (
        <ul className="grid grid-cols-1 gap-4">
          {results.map((user) => (
            <UserTile key={user.id} user={user} onClick={() => {}} />
          ))}
        </ul>
      ) : (
        <p>No results found for &quot;{query}&quot;.</p>
      )}
    </div>
  );
};

export default SearchPage;
