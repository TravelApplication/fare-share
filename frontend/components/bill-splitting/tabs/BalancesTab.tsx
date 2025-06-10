import Link from 'next/link';
import { Banknote } from 'lucide-react';
import { groupSchema } from '@/validation/groupSchema';

export default function BalancesTab({
  trip,
  balanceMap,
}: {
  trip: groupSchema;
  balanceMap: Map<number, number>;
}) {
  return (
    <div>
      <h4 className="text-heading3-bold text-primary-500 mb-4 flex items-center gap-2">
        <Banknote className="w-6 h-6" />
        Group Balances
      </h4>
      <ul className="space-y-2">
        {trip?.memberships.map((user) => {
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
              className="flex items-center justify-between p-3 border rounded-lg bg-light-1 shadow-sm"
            >
              <Link
                href={`/account/${user.userId}`}
                className="text-primary-600 font-semibold hover:underline"
              >
                {user.userEmail || 'User'}
              </Link>
              <span className={`text-lg font-bold ${balanceClass}`}>
                {balance > 0 ? '+' : ''}
                {Number(balance).toFixed(2)} zł
              </span>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
