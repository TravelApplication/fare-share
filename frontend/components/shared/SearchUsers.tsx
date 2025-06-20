import { UserSearch } from '@/validation/userProfileSchemas';
import { useEffect, useState, useRef } from 'react';
import axiosInstance from '@/lib/axiosInstance';
import { Input } from '@/components/ui/input';
import UserTile from './UserTile';
import { Search } from 'lucide-react';
import { useRouter } from 'next/navigation';

interface SearchUsersProps {
  userIdsToFilterOut?: number[];
  onUserClick?: 'redirect' | 'select';
  onSelectUser?: (user: UserSearch) => void;
}

function SearchUsers({
  onUserClick = 'redirect',
  onSelectUser,
  userIdsToFilterOut,
}: SearchUsersProps) {
  const [query, setQuery] = useState<string>('');
  const [debouncedQuery, setDebouncedQuery] = useState<string>('');
  const [results, setResults] = useState<UserSearch[]>([]);
  const [isDropdownVisible, setIsDropdownVisible] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const router = useRouter();

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedQuery(query);
    }, 300);

    return () => {
      clearTimeout(handler);
    };
  }, [query]);

  useEffect(() => {
    const fetchUsers = async () => {
      if (debouncedQuery.length > 0) {
        try {
          const response = await axiosInstance.get(
            `user-info/search/top8?name=${debouncedQuery}`,
          );
          let filteredResults = response.data;
          if (userIdsToFilterOut) {
            filteredResults = filteredResults.filter(
              (user: UserSearch) => !userIdsToFilterOut.includes(user.id),
            );
          }
          setResults(filteredResults);
        } catch (error) {
          console.error('Error fetching users:', error);
        }
      } else {
        setResults([]);
      }
    };

    fetchUsers();
  }, [debouncedQuery, userIdsToFilterOut]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsDropdownVisible(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && query.trim() !== '') {
      router.push(`/search?name=${query}`);
    }
  };

  return (
    <div className="relative w-full max-w-md">
      <div className="relative">
        <Input
          placeholder="Search users..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => setIsDropdownVisible(true)}
          onKeyDown={handleKeyDown}
          className="pr-10"
        />
        <div className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700">
          <Search className="w-5 h-5 text-primary-500" />
        </div>
      </div>

      {isDropdownVisible && (results.length > 0 || query.trim().length > 0) && (
        <div
          ref={dropdownRef}
          className="absolute top-full left-0 w-full p-2 bg-light-1 shadow-lg rounded-lg z-50 max-h-60 overflow-y-auto mt-2"
        >
          {results.length > 0 ? (
            <ul>
              {results.map((user) => (
                <UserTile
                  key={user.id}
                  user={user}
                  onClick={() => {
                    if (onUserClick === 'redirect') {
                      router.push(`/account/${user.id}`);
                    }
                  }}
                  showInviteButton={onUserClick === 'select'}
                  onInvite={() => {
                    setIsDropdownVisible(false);
                    setQuery('');
                    onSelectUser?.(user);
                  }}
                />
              ))}
            </ul>
          ) : (
            <ul>
              <li className="p-3 font-normal text-gray-600">No users found.</li>
            </ul>
          )}
        </div>
      )}
    </div>
  );
}

export default SearchUsers;
