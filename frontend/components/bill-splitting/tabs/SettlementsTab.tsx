import { useState } from 'react';
import { Users } from 'lucide-react';
import AddSettlementDialog from '@/components/bill-splitting/AddSettlementDialog';
import { Group } from '@/validation/groupSchema';
import { Trash2 } from 'lucide-react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';

export interface settlementsHistoryProp {
  id: number;
  groupId: number;
  paidByUserId: number;
  paidToUserId: number;
  amount: number;
  paymentDate: string;
}

export interface settlementProp {
  debtorId: number;
  creditorId: number;
  amount: number;
}

export default function SettlementsTab({
  settlements,
  trip,
  currentUserId,
  history,
  onSubmit,
  onDelete,
}: {
  settlements: settlementProp;
  trip: Group;
  currentUserId: number;
  history: settlementsProp[];
  onSubmit: (values: {
    groupId: number;
    debtorId: number;
    creditorId: number;
    amount: number;
  }) => void;
  onDelete: (values: number) => void;
}) {
  const [showDialog, setShowDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [selectedAmount, setSelectedAmount] = useState<number>(0);
  const [settlementType, setSettlementType] = useState<
    'youOwe' | 'theyOwe' | null
  >(null);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

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
      (s) => s.debtorId === currentUserId && s.creditorId === userId,
    );
    const theyOwe = settlements.find(
      (s) => s.debtorId === userId && s.creditorId === currentUserId,
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
          .filter((user) => user.userId !== currentUserId)
          .map((user) => {
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
        <h3 className="text-xl font-bold mb-4 text-primary-600">History</h3>
        <ul className="space-y-4">
          {history.length === 0 && (
            <li className="text-gray-500">No settlement history.</li>
          )}
          {history.map((item) => {
            const payer =
              trip?.memberships.find((u) => u.userId === item.paidByUserId)
                ?.userEmail || item.paidByUserId;
            const receiver =
              trip?.memberships.find((u) => u.userId === item.paidToUserId)
                ?.userEmail || item.paidToUserId;

            return (
              <li
                key={item.id}
                className="p-5 border rounded-md bg-white shadow-sm hover:shadow-md transition"
              >
                <div className="flex justify-between items-center mb-2">
                  <span className="text-xl font-bold text-primary-600">
                    {Number(item.amount).toFixed(2)} zł
                  </span>
                  <span className="text-xs text-muted-foreground">
                    {item.paymentDate
                      ? new Date(item.paymentDate).toLocaleDateString('pl-PL', {
                          day: '2-digit',
                          month: 'short',
                          year: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit',
                        })
                      : ''}
                  </span>
                </div>

                <div className="flex justify-between text-sm text-muted-foreground mb-1">
                  <div>
                    <span className="block">
                      <strong className="text-muted-foreground">{payer}</strong>{' '}
                      paid
                    </span>
                    <span className="block">
                      <strong className="text-muted-foreground">
                        {receiver}
                      </strong>
                    </span>
                  </div>
                  <div className="flex justify-end mt-2">
                    <button
                      className="text-red-500 hover:bg-red-50 p-2 rounded transition"
                      onClick={() => {
                        setDeleteId(item.id);
                        setShowDeleteDialog(true);
                      }}
                      title="Delete settlement"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </li>
            );
          })}
        </ul>

        <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Delete settlement</DialogTitle>
            </DialogHeader>
            <div className="mb-4">
              Are you sure you want to delete this settlement? This action
              cannot be undone.
            </div>
            <div className="flex justify-end gap-2">
              <button
                className="border px-4 py-2 rounded text-primary-500 border-primary-500 hover:bg-primary-50"
                onClick={() => setShowDeleteDialog(false)}
              >
                Cancel
              </button>
              <button
                className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                onClick={() => {
                  if (deleteId !== null) onDelete(deleteId);
                  setShowDeleteDialog(false);
                  setDeleteId(null);
                }}
              >
                Delete
              </button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      <AddSettlementDialog
        open={showDialog}
        onOpenChange={setShowDialog}
        userEmail={
          trip?.memberships.find((u) => u.userId === selectedUser)?.userEmail ||
          ''
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
