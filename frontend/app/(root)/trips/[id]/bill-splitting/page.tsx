'use client';

import { useEffect, useState } from 'react';
import { getToken, logout } from '@/lib/auth';
import { Alert } from '@/components/ui/alert';
import { Banknote, Users, ListOrdered } from 'lucide-react';
import { useTrip } from '@/context/TripContext';
import { useSearchParams } from 'next/navigation';
import ExpensesHistoryTab from '@/components/bill-splitting/tabs/ExpensesHistoryTab';
import BalancesTab from '@/components/bill-splitting/tabs/BalancesTab';
import SettlementsTab from '@/components/bill-splitting/tabs/SettlementsTab';
import { appStore } from '@/store/appStore';
import { toast } from 'sonner';
import axiosInstance from '@/lib/axiosInstance';

const ITEMS_PER_PAGE = 5;

export default function GroupExpensesPage() {
  const { trip, refreshTrip } = useTrip();
  const [error, setError] = useState<string | null>(null);
  const [groupBalance, setGroupBalance] = useState([]);
  const [expenses, setExpenses] = useState([]);
  const [settlements, setSettlements] = useState([]);
  const [settlementsHistory, setSettlementsHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showDialog, setShowDialog] = useState(false);
  const [activeTab, setActiveTab] = useState<
    'history' | 'balances' | 'settlements'
  >('history');
  const searchParams = useSearchParams();
  const user = appStore((state) => state.user);

  useEffect(() => {
    if (!trip || !trip.id) return;
    fetchData();
  }, [trip]);

  const fetchData = async () => {
    try {
      const [balanceRes, expensesRes, settlementsRes, settlementsHistoryRes] =
        await Promise.all([
          axiosInstance.get(`/groups/${trip.id}/balance/balances`),
          axiosInstance.get(`/groups/${trip.id}/expenses`),
          axiosInstance.get(`/groups/${trip.id}/balance/min`),
          axiosInstance.get(`/groups/${trip.id}/settlements`),
        ]);

      setGroupBalance(balanceRes.data);
      setExpenses(expensesRes.data.content);
      setSettlements(settlementsRes.data);
      setSettlementsHistory(settlementsHistoryRes.data);
    } catch (err) {
      setError(err.message || 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  const upsertExpense = async (values, id?: number) => {
    try {
      const splitTypeMap = {
        amount: 'AMOUNT',
        share: 'SHARES',
        percentage: 'PERCENTAGE',
        equal: 'EQUALLY',
      };
      const userShares = {};
      if (values.splitMethod === 'equal') {
        trip.memberships.forEach((u) => {
          userShares[u.userId] = 0;
        });
      } else {
        values.shares.forEach((share) => {
          if (
            share.included &&
            share.value !== undefined &&
            share.value !== null &&
            share.value !== ''
          ) {
            userShares[share.userId] = Number(share.value);
          }
        });
      }
      const payload = {
        groupId: trip.id,
        paidByUserId: user.id,
        description: values.description,
        totalAmount: Number(values.totalAmount),
        splitType: splitTypeMap[values.splitMethod],
        userShares,
        expenseDate: values.expenseDate || new Date().toISOString(),
      };
      if (id) {
        await axiosInstance.put(`/groups/${trip.id}/expenses/${id}`, payload);
        toast('Expense updated!');
      } else {
        await axiosInstance.post(`/groups/${trip.id}/expenses`, payload);
        toast('Expense added successfully!');
      }
      setShowDialog(false);
      refreshTrip();
    } catch (err) {
      setError(err.message || 'An error occurred while adding the expense');
    }
  };

  const upsertSettlement = async (values, id?: number) => {
    try {
      const token = getToken();
      if (!token) {
        logout();
        return;
      }
      const payload = {
        groupId: trip.id,
        debtorId: String(values.debtorId),
        creditorId: String(values.creditorId),
        amount: Number(values.amount),
      };

      if (id) {
        await axiosInstance.put(
          `/groups/${trip.id}/settlements/${id}`,
          payload,
        );
        toast('Settlement updated!');
      } else {
        await axiosInstance.post(`/groups/${trip.id}/settlements`, payload);
        toast('Settlement added successfully!');
      }
      setShowDialog(false);
      refreshTrip();
    } catch (err) {
      setError(err.message || 'An error occurred while saving the settlement');
    }
  };

  const handleSettlementDelete = async (settlementId: number) => {
    try {
      await axiosInstance.delete(
        `/groups/${trip.id}/settlements/${settlementId}`,
      );
      toast('Settlement deleted!');
      refreshTrip();
    } catch (err) {
      toast.error(err.message || 'Failed to delete settlement');
    }
  };

  const handleExpenseDelete = async (expenseId: number) => {
    try {
      await axiosInstance.delete(`/groups/${trip.id}/expenses`, {
        params: { expenseId },
      });
      toast('Expense deleted!');
      refreshTrip();
    } catch (err) {
      toast.error(err.message || 'Failed to delete expense');
    }
  };

  const balanceMap = new Map(groupBalance.map((b) => [b.userId, b.balance]));

  const currentPage = Number(searchParams.get('page') || 1);
  const paginatedExpenses = expenses.slice(
    (currentPage - 1) * ITEMS_PER_PAGE,
    currentPage * ITEMS_PER_PAGE,
  );

  const initialValues = {
    description: '',
    totalAmount: 0,
    splitMethod: 'amount',
    shares:
      trip?.memberships.map((u) => ({
        userId: u.userId,
        included: true,
        value: undefined,
      })) || [],
  };

  if (loading) {
    return (
      <Alert variant="default" className="p-4">
        Loading...
      </Alert>
    );
  }

  return (
    <div className="section mt-4 flex flex-col gap-10 border bg-white shadow-md p-6 max-w-3xl mx-auto">
      {error && <Alert variant="destructive">{error}</Alert>}

      <div className="flex w-full mb-6">
        <button
          className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 border-b-2 transition-all rounded-none ${
            activeTab === 'history'
              ? 'border-primary-500 text-primary-600 bg-gray-50 font-semibold'
              : 'border-transparent text-gray-500 hover:text-primary-500'
          }`}
          onClick={() => setActiveTab('history')}
        >
          <ListOrdered className="w-4 h-4" />
          Expense history
        </button>
        <button
          className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 border-b-2 transition-all rounded-none ${
            activeTab === 'balances'
              ? 'border-primary-500 text-primary-600 bg-gray-50 font-semibold'
              : 'border-transparent text-gray-500 hover:text-primary-500'
          }`}
          onClick={() => setActiveTab('balances')}
        >
          <Banknote className="w-4 h-4" />
          Users balances
        </button>
        <button
          className={`flex-1 flex items-center justify-center gap-2 px-4 py-2 border-b-2 transition-all rounded-none ${
            activeTab === 'settlements'
              ? 'border-primary-500 text-primary-600 bg-gray-50 font-semibold'
              : 'border-transparent text-gray-500 hover:text-primary-500'
          }`}
          onClick={() => setActiveTab('settlements')}
        >
          <Users className="w-4 h-4" />
          Settlements
        </button>
      </div>

      {activeTab === 'history' && (
        <ExpensesHistoryTab
          showDialog={showDialog}
          setShowDialog={setShowDialog}
          trip={trip}
          initialValues={initialValues}
          onSubmit={async (values) => upsertExpense(values)}
          onEditExpense={async (id, values) => upsertExpense(values, id)}
          onDelete={async (id) => handleExpenseDelete(id)}
          paginatedExpenses={paginatedExpenses}
          expenses={expenses}
          ITEMS_PER_PAGE={ITEMS_PER_PAGE}
        />
      )}

      {activeTab === 'balances' && (
        <BalancesTab trip={trip} balanceMap={balanceMap} />
      )}

      {activeTab === 'settlements' && (
        <SettlementsTab
          settlements={settlements}
          trip={trip}
          currentUserId={user.id}
          history={settlementsHistory}
          onSubmit={async (values, id) => upsertSettlement(values, id)}
          onDelete={async (id) => handleSettlementDelete(id)}
        />
      )}
    </div>
  );
}
