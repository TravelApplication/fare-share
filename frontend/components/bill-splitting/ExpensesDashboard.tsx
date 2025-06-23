'use client';
import { useEffect, useState } from 'react';
import { getToken, logout } from '@/lib/auth';
import { Alert } from '@/components/ui/alert';
import { Banknote } from 'lucide-react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { useRouter } from 'next/navigation';
import { Group } from '@/validation/groupSchema';
import axiosInstance from '@/lib/axiosInstance';

export default function ExpansesDashboard({ trip }: { trip: Group }) {
  const [error, setError] = useState<string | null>(null);
  const [groupBalance, setGroupBalance] = useState<
    { userId: number; balance: number }[]
  >([]);
  const router = useRouter();

  const fetchGroupExpenses = async () => {
    try {
      const token = getToken();
      if (!token) {
        logout();
        return;
      }
      const response = await axiosInstance.get(
        `/groups/${trip.id}/balance/balances`,
      );
      setGroupBalance(response.data);
    } catch {
      setError('An error occurred');
    }
  };

  useEffect(() => {
    fetchGroupExpenses();
  }, []);

  if (error) {
    return (
      <Alert variant="destructive" className="p-4">
        {error}
      </Alert>
    );
  }

  const balanceMap: Map<number, number> = new Map(
    groupBalance.map((b) => [b.userId, b.balance]),
  );

  return (
    <div className="section mt-4 flex flex-col justify-start border bg-white shadow-md rounded-lg p-6">
      <h4 className="text-heading3-bold text-primary-500 mb-4">
        Expenses Dashboard
      </h4>
      <div className="flex flex-col gap-4">
        <ul className="space-y-4">
          {trip.memberships.map((user) => {
            const userInfo = trip.memberships.find(
              (u) => u.userId === user.userId,
            );
            const balance = balanceMap.get(user.userId) ?? 0;
            const balanceClass =
              balance > 0
                ? 'text-green-500'
                : balance < 0
                  ? 'text-red-500'
                  : 'text-gray-500';

            return (
              <li
                key={user.userId}
                className="flex items-center justify-between p-3 border rounded-lg shadow-sm bg-light-1"
              >
                <div className="flex items-center gap-4">
                  <Link
                    href={`/account/${userInfo?.userId || user.userId}`}
                    className="text-primary-500 font-semibold hover:underline"
                  >
                    {userInfo?.userEmail || 'User'}
                  </Link>
                </div>
                <span
                  className={`text-xl font-bold text-base-semibold ${balanceClass}`}
                >
                  {balance > 0 ? '+' : ''}
                  {Number(balance).toFixed(2)} zł
                </span>
              </li>
            );
          })}
        </ul>
        <Button
          onClick={() => router.push(`/trips/${trip.id}/bill-splitting`)}
          className="bg-white border text-primary-500 hover:bg-gray-100 shadow-sm rounded-full"
        >
          <Banknote />
          <span className="ml-2">Manage Trip Expenses</span>
        </Button>
      </div>
    </div>
  );
}
