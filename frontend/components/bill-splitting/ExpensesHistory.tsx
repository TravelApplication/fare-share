import CustomPagination from '@/components/shared/CustomPagination';

export default function ExpenseHistory({
  paginatedExpenses,
  expenses,
  trip,
  ITEMS_PER_PAGE,
}: ExpenseHistoryProps) {
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

/* {paginatedExpenses.length > 0 ? (
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
                  <div className="text-sm mt-2 pl-2">
                    {Object.entries(expense.userShares).map(([uid, amount]) => (
                      <div key={uid}>
                        {
                          trip.memberships.find((u) => u.userId == uid)
                            ?.userEmail
                        }
                        : {Number(amount).toFixed(2)} zł
                      </div>
                    ))}
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
)} */
