import AddExpenseDialog from '@/components/bill-splitting/AddExpenseDialog';
import ExpensesHistory from '@/components/bill-splitting/ExpensesHistory';
import { Group } from '@/validation/groupSchema';
import { AddExpenseInitialValues } from '@/components/bill-splitting/AddExpenseDialog.tsx';
import { useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';

export interface ExpenseShare {
  userId: number;
  included: boolean;
  value?: number;
}

export interface AddExpenseInitialValues {
  description: string;
  totalAmount: number;
  splitMethod: 'equally' | 'percentage' | 'amount' | 'share';
  shares: ExpenseShare[];
}

export default function ExpensesHistoryTab({
  showDialog,
  setShowDialog,
  trip,
  initialValues,
  onSubmit,
  paginatedExpenses,
  expenses,
  ITEMS_PER_PAGE,
  onEditExpense,
  onDelete,
}: {
  showDialog: boolean;
  setShowDialog: (v: boolean) => void;
  trip: Group;
  initialValues: AddExpenseInitialValues;
  onSubmit: (values: AddExpenseInitialValues) => Promise<void>;
  paginatedExpenses: expenseProp[];
  expenses: expenseProp[];
  ITEMS_PER_PAGE: number;
  onEditExpense: (id: number, values: AddExpenseInitialValues) => Promise<void>;
  onDelete: (id: number) => Promise<void>;
}) {
  const [editExpense, setEditExpense] = useState<expenseProp | null>(null);
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);

  function mapExpenseToInitialValues(
    expense: expenseProp,
    trip: Group,
  ): AddExpenseInitialValues {
    return {
      description: expense.description,
      totalAmount: expense.totalAmount,
      splitMethod: expense.splitType.toLowerCase() as
        | 'equally'
        | 'percentage'
        | 'amount'
        | 'share',
      shares: trip.memberships.map((member) => ({
        userId: member.userId,
        included: expense.userShares.hasOwnProperty(member.userId),
        value: expense.userShares[member.userId],
      })),
    };
  }

  const handleEdit = (expense: expenseProp) => {
    setEditExpense(expense);
    setEditDialogOpen(true);
  };

  const handleEditSubmit = async (values: AddExpenseInitialValues) => {
    await onEditExpense(editExpense!.id, values);
    setEditDialogOpen(false);
    setEditExpense(null);
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-4">
        <h4 className="text-heading3-bold text-primary-500">Expense History</h4>
        <AddExpenseDialog
          open={showDialog}
          setOpen={setShowDialog}
          trip={trip}
          initialValues={initialValues}
          onSubmit={onSubmit}
        />
      </div>
      <ExpensesHistory
        paginatedExpenses={paginatedExpenses}
        expenses={expenses}
        trip={trip}
        ITEMS_PER_PAGE={ITEMS_PER_PAGE}
        onDelete={(id) => {
          setDeleteId(id);
          setShowDeleteDialog(true);
        }}
        onEdit={handleEdit}
      />

      <Dialog open={showDeleteDialog} onOpenChange={setShowDeleteDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Delete expense</DialogTitle>
          </DialogHeader>
          <div className="mb-4">
            Are you sure you want to delete this expense? This action cannot be
            undone.
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

      {editExpense && (
        <AddExpenseDialog
          open={editDialogOpen}
          setOpen={(open) => {
            setEditDialogOpen(open);
            if (!open) setEditExpense(null);
          }}
          trip={trip}
          initialValues={mapExpenseToInitialValues(editExpense, trip)}
          onSubmit={handleEditSubmit}
        />
      )}
    </div>
  );
}
