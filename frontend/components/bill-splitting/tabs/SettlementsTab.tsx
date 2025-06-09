import { useState } from 'react';
import { Users } from 'lucide-react';
import AddSettlementDialog from '@/components/bill-splitting/AddSettlementDialog';

export default function SettlementsTab({
  settlements,
  trip,
  currentUserId,
  history,
  onSubmit,
}: unknown) {
  const [showDialog, setShowDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState<unknown>(null);
  const [selectedAmount, setSelectedAmount] = useState<number>(0);
  const [settlementType, setSettlementType] = useState<
    'youOwe' | 'theyOwe' | null
  >(null);

  const handleOpenSettlement = (
    userId: number,
    amount: number,
    type: 'youOwe' | 'theyOwe',
  ) => {
    setSelectedUser(userId);
    setSelectedAmount(amount);
    setSettlementType(type);
    setShowDialog(true);
  };

  const findSettlement = (userId: number) => {
    const youOwe = settlements.find(
      (s: unknown) => s.debtorId === currentUserId && s.creditorId === userId,
    );
    const theyOwe = settlements.find(
      (s: unknown) => s.debtorId === userId && s.creditorId === currentUserId,
    );
    return { youOwe, theyOwe };
  };

  return (
    <div>
      <h4 className="text-heading3-bold text-primary-500 mb-4 flex items-center gap-2">
        <Users className="w-6 h-6" />
        User settlements
      </h4>
      <ul className="space-y-2">
        {trip?.memberships
          .filter((user: unknown) => user.userId !== currentUserId)
          .map((user: unknown) => {
            const { youOwe, theyOwe } = findSettlement(user.userId);
            const info = youOwe
              ? {
                  text: `You owe ${Number(youOwe.amount).toFixed(2)} zł`,
                  color: 'text-red-600',
                  type: 'youOwe',
                  amount: youOwe.amount,
                }
              : theyOwe
                ? {
                    text: `They owe ${Number(theyOwe.amount).toFixed(2)} zł`,
                    color: 'text-green-600',
                    type: 'theyOwe',
                    amount: theyOwe.amount,
                  }
                : {
                    text: 'No settlement',
                    color: 'text-gray-500',
                    type: null,
                    amount: 0,
                  };

            return (
              <li
                key={user.userId}
                className="flex items-center justify-between p-3 border rounded-lg bg-light-1 shadow-sm cursor-pointer transition hover:bg-gray-100"
                onClick={() => {
                  if (info.type) {
                    handleOpenSettlement(
                      user.userId,
                      info.amount,
                      info.type as 'youOwe' | 'theyOwe',
                    );
                  }
                }}
              >
                <span className="font-semibold text-primary-600">
                  {user.userEmail}
                </span>
                <span className={`ml-2 font-medium ${info.color}`}>
                  {info.text}
                </span>
              </li>
            );
          })}
      </ul>

      <div className="mt-8">
        <h5 className="text-heading4-bold mb-2">History</h5>
        <ul className="space-y-2">
          {history.length === 0 && (
            <li className="text-gray-500">No settlement history.</li>
          )}
          {history.map((item) => (
            <li
              key={item.id}
              className="flex items-center justify-between p-2 border rounded bg-white"
            >
              <span>
                <span className="font-semibold text-primary-600">
                  {trip?.memberships.find(
                    (u: unknown) => u.userId === item.paidByUserId,
                  )?.userEmail || item.paidByUserId}
                </span>{' '}
                paid{' '}
                <span className="font-semibold text-primary-600">
                  {trip?.memberships.find(
                    (u: unknown) => u.userId === item.paidToUserId,
                  )?.userEmail || item.paidToUserId}
                </span>
              </span>
              <span className="text-primary-500 font-bold">
                {Number(item.amount).toFixed(2)} zł
              </span>
              <span className="text-xs text-gray-400 ml-2">
                {item.paymentDate
                  ? new Date(item.paymentDate).toLocaleString()
                  : ''}
              </span>
            </li>
          ))}
        </ul>
      </div>

      <AddSettlementDialog
        open={showDialog}
        onOpenChange={setShowDialog}
        userEmail={
          trip?.memberships.find((u: unknown) => u.userId === selectedUser)
            ?.userEmail || ''
        }
        settlementType={settlementType}
        maxAmount={selectedAmount}
        amount={selectedAmount}
        debtorId={settlementType === 'youOwe' ? currentUserId : selectedUser}
        creditorId={settlementType === 'youOwe' ? selectedUser : currentUserId}
        groupId={trip?.id}
        onSubmit={(values) => onSubmit(values)}
      />
    </div>
  );
}
