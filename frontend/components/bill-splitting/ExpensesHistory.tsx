import CustomPagination from '@/components/shared/CustomPagination';
import { Button } from '@/components/ui/button';
import { Pencil, Trash2 } from 'lucide-react';
import { Group } from '@/validation/groupSchema';
import { appStore } from '@/store/appStore';

export interface expenseProp {
  id: number;
  groupId: number;
  description: string;
  totalAmount: number;
  splitType: 'EQUALLY' | 'PERCENTAGE' | 'AMOUNT' | 'SHARES' | string;
  paidByUserId: number;
  createdAt: string;
  expenseDate: string;
  userShares: Record<number, number>;
}

interface ExpenseHistoryProps {
  paginatedExpenses: expenseProp[];
  expenses: expenseProp[];
  trip: Group;
  ITEMS_PER_PAGE: number;
  onDelete: (expenseId: number) => void;
  onEdit: (expense: expenseProp) => void;
}

export default function ExpenseHistory({
  paginatedExpenses,
  expenses,
  trip,
  ITEMS_PER_PAGE,
  onDelete,
  onEdit,
}: ExpenseHistoryProps) {
  const user = appStore((state) => state.user);
  return (
    <>
      {paginatedExpenses.length > 0 ? (
        <>
          <ul className="space-y-4">
            {paginatedExpenses.map((expense) => (
              <li
                key={expense.id}
                className="p-4 border rounded-md bg-light-1 shadow-sm"
              >
                <div className="flex justify-between items-center mb-1">
                  <span className="font-semibold text-primary-700">
                    {expense.description}
                  </span>
                  <span className="text-xs text-muted-foreground">
                    {new Date(expense.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <div className="text-sm text-muted-foreground mb-1">
                  Paid by{' '}
                  <strong>
                    {
                      trip.memberships.find(
                        (u) => u.userId === expense.paidByUserId,
                      )?.userEmail
                    }
                  </strong>{' '}
                  — Total:{' '}
                  <span className="font-semibold">
                    {expense.totalAmount.toFixed(2)} zł
                  </span>
                </div>
                <div className="text-sm mt-2 text-muted-foreground">
                  {Object.entries(expense.userShares).map(([uid, amount]) => (
                    <div key={uid}>
                      {trip.memberships.find((u) => u.userId == uid)?.userEmail}
                      : {Number(amount).toFixed(2)} zł
                    </div>
                  ))}
                </div>
                <div className="flex gap-2 mt-2 justify-end">
                  {expense.paidByUserId === user.id && (
                    <>
                      <Button
                        size="icon"
                        variant="ghost"
                        onClick={() => onEdit(expense)}
                        title="Edit"
                      >
                        <Pencil className="w-4 h-4" />
                      </Button>
                      <Button
                        size="icon"
                        variant="ghost"
                        onClick={() => onDelete(expense.id)}
                        title="Delete"
                      >
                        <Trash2 className="w-4 h-4 text-red-500" />
                      </Button>
                    </>
                  )}
                </div>
              </li>
            ))}
          </ul>
          <div className="mt-6">
            <CustomPagination
              totalItems={expenses.length}
              itemsPerPage={ITEMS_PER_PAGE}
            />
          </div>
        </>
      ) : (
        <div className="text-center text-gray-500 py-8">
          No expenses yet. Add your first group expense!
        </div>
      )}
    </>
  );
}
