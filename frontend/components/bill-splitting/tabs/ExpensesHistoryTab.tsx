import AddExpenseDialog from '@/components/bill-splitting/AddExpenseDialog';
import ExpensesHistory from '@/components/bill-splitting/ExpensesHistory';

export default function ExpensesHistoryTab({
  showDialog,
  setShowDialog,
  trip,
  initialValues,
  onSubmit,
  paginatedExpenses,
  expenses,
  ITEMS_PER_PAGE,
}: unknown) {
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
      />
    </div>
  );
}
