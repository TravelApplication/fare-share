'use client';

import { Button } from '@/components/ui/button';
import { useTrip } from '@/context/TripContext';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import {
  ArrowLeft,
  ArrowDown,
  ArrowUp,
  Pencil,
  Trash,
  Search,
  LogOut,
  UserRoundPlus,
} from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useMemo, useState } from 'react';
import axiosInstance from '@/lib/axiosInstance';
import { MembershipRole } from '@/validation/membershipSchema';
import YesNoModal from '@/components/shared/YesNoModal';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { toast } from 'sonner';
import { appStore } from '@/store/appStore';
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog';
import SearchUsers from '@/components/shared/SearchUsers';

import { formatDate } from '@/lib/utils';
import Link from 'next/link';
import { UserSearch } from '@/validation/userProfileSchemas';

type SortKey = 'userEmail' | 'joinedAt' | 'role';

export default function TripMembersPage() {
  const router = useRouter();
  const { trip, refreshTrip } = useTrip();
  const user = appStore((s) => s.user);
  const memberships = trip?.memberships;
  const currentUserMembership = memberships?.find((m) => m.userId === user?.id);
  const currentUserRole = currentUserMembership?.role;

  const [sortKey, setSortKey] = useState<SortKey>('userEmail');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
  const [searchQuery, setSearchQuery] = useState('');
  const [editingUserId, setEditingUserId] = useState<number | null>(null);
  const [editedRole, setEditedRole] = useState<MembershipRole>('MEMBER');

  const filteredAndSortedMemberships = useMemo(() => {
    if (!memberships) return [];

    const filtered = memberships.filter((m) =>
      `${m.userEmail} ${m.role}`
        .toLowerCase()
        .includes(searchQuery.toLowerCase()),
    );

    return filtered.sort((a, b) => {
      const valA = a[sortKey].toLowerCase?.() ?? a[sortKey];
      const valB = b[sortKey].toLowerCase?.() ?? b[sortKey];
      if (valA < valB) return sortDirection === 'asc' ? -1 : 1;
      if (valA > valB) return sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
  }, [memberships, sortKey, sortDirection, searchQuery]);

  const toggleSort = (key: SortKey) => {
    if (sortKey === key) {
      setSortDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortKey(key);
      setSortDirection('asc');
    }
  };

  const SortIcon = (key: SortKey) => {
    if (sortKey !== key) return null;
    return sortDirection === 'asc' ? (
      <ArrowUp className="inline-block text-primary-600 w-4 h-4 ml-1" />
    ) : (
      <ArrowDown className="inline-block text-primary-600 w-4 h-4 ml-1" />
    );
  };

  const handleInviteUser = async (user: UserSearch) => {
    try {
      await axiosInstance.post('/group-invitations/send', null, {
        params: {
          receiverId: user.id,
          groupId: trip?.id,
        },
      });

      toast('Invitation sent.', {
        duration: 3000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
    } catch {
      toast('Error sending an invitation.', {
        duration: 3000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
    }
  };

  const updateMembership = async (memberId: number) => {
    if (!trip) {
      return;
    }
    try {
      await axiosInstance.put(
        `/groups/${trip.id}/members/${memberId}/role`,
        null,
        { params: { role: editedRole } },
      );
      setEditingUserId(null);
      refreshTrip();
      toast('Member role updated successfully.', {
        duration: 3000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
    } catch {
      toast('Error updating member role.', {
        duration: 3000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
    }
  };

  const deleteMembership = async (memberId: number) => {
    if (!trip) return;
    let msg = 'Left group successfully.';
    if (memberId !== user!.id) {
      msg = 'Member removed successfully.';
    }

    try {
      await axiosInstance.delete(`/groups/${trip.id}/members/${memberId}`);
      refreshTrip();
      toast(msg, {
        duration: 3000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
      if (memberId === user!.id) {
        router.push('/trips');
      }
    } catch {
      toast('Failed to remove a group member.', {
        duration: 3000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
    }
  };

  if (!trip) return <div>Loading...</div>;

  return (
    <>
      <div className="flex justify-between gap-3 md:items-center md:flex-row flex-col mb-4">
        <Button
          onClick={() => router.push(`/trips/${trip.id}`)}
          className="bg-white w-min border text-primary-500 hover:bg-gray-100 shadow-sm rounded-full"
        >
          <ArrowLeft />
          <span>Back To Trip Summary</span>
        </Button>
        <div className="text-md  font-semibold text-gray-400">
          {trip.name}
          {trip.tripStartDate && trip.tripEndDate && (
            <>
              {' | '}
              {formatDate(trip.tripStartDate)} - {formatDate(trip.tripEndDate)}
            </>
          )}
        </div>
      </div>

      <div className="section p-6 flex flex-col gap-8 border bg-white shadow-md rounded-lg">
        <div className="flex justify-between md:flex-row flex-col gap-4">
          <h2 className="text-heading3-bold text-primary-500">Group Members</h2>
          <div className="flex gap-4">
            <Dialog>
              <DialogTrigger asChild>
                <button className="flex items-center gap-2 text-primary-500 cursor-pointer hover:bg-gray-100 rounded-full px-3 py-1.5 border">
                  <span>Invite</span>
                  <UserRoundPlus width={20} height={20} />
                </button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-[500px]">
                <DialogHeader>
                  <DialogTitle className="!font-bold !text-xl text-primary-500">
                    Invite Members
                  </DialogTitle>
                  <DialogDescription>
                    Search users to invite them to the trip.
                  </DialogDescription>
                </DialogHeader>
                <SearchUsers
                  onUserClick="select"
                  onSelectUser={handleInviteUser}
                  userIdsToFilterOut={memberships?.map((el) => el.userId)}
                />
              </DialogContent>
            </Dialog>

            {currentUserRole === 'MEMBER' && (
              <YesNoModal
                title="Leave Group"
                description={`Are you sure you want to leave group ${trip.name}?`}
                actionName="Confirm"
                cancelName="Cancel"
                onConfirm={() => deleteMembership(user!.id)}
                trigger={
                  <button className="flex items-center gap-2 text-primary-500 cursor-pointer hover:bg-gray-100 rounded-full px-3 py-1.5 border">
                    <span>Leave</span>
                    <LogOut width={20} height={20} />
                  </button>
                }
              />
            )}
          </div>
        </div>
        <div>
          <Label className="pl-2 text-gray-700">Search</Label>
          <div className="relative">
            <Input
              type="text"
              placeholder="Search among group members..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="border p-2 rounded-lg w-full"
            />
            <div className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700">
              <Search className="w-5 h-5 text-primary-500" />
            </div>
          </div>
        </div>

        <div className="w-full overflow-x-auto">
          <table className="min-w-full table-auto md:table-fixed text-left">
            <thead className="text-gray-700 hidden md:table-header-group">
              <tr>
                <th
                  onClick={() => toggleSort('userEmail')}
                  className="w-[220px] bg-gray-100 rounded-tl-lg font-semibold cursor-pointer py-2 px-4 border-b"
                >
                  Email {SortIcon('userEmail')}
                </th>
                <th
                  onClick={() => toggleSort('joinedAt')}
                  className="w-[110px] bg-gray-100 font-semibold cursor-pointer py-2 px-4 border-b"
                >
                  Joined At {SortIcon('joinedAt')}
                </th>
                <th
                  onClick={() => toggleSort('role')}
                  className={`w-[120px] bg-gray-100 font-semibold cursor-pointer py-2 px-4 border-b ${currentUserRole === 'MEMBER' && 'rounded-tr-lg'}`}
                >
                  Role {SortIcon('role')}
                </th>
                {currentUserRole === 'OWNER' && (
                  <>
                    <th className="w-[140px] bg-gray-100 font-semibold py-2 px-4 border-b text-center">
                      Edit
                    </th>
                    <th className="w-[64px] bg-gray-100 rounded-tr-lg font-semibold py-2 px-4 border-b text-center">
                      Delete
                    </th>
                  </>
                )}
              </tr>
            </thead>
            <tbody className="flex flex-col gap-4 md:table-row-group">
              {filteredAndSortedMemberships.map((membership) => {
                const isOwner = membership.role === 'OWNER';

                return (
                  <tr
                    key={membership.userId}
                    className="flex flex-col w-full border rounded-lg md:h-[64px] md:table-row md:rounded-none md:border-0 md:border-b bg-white"
                  >
                    <td className="px-4 py-2 md:table-cell">
                      <span className="font-semibold md:hidden block mb-1">
                        Email
                      </span>
                      <Link
                        href={`/account/${membership.userId}`}
                        className="hover:text-primary-500 underline"
                      >
                        {membership.userEmail}
                      </Link>
                    </td>

                    <td className="px-4 py-2 md:table-cell">
                      <span className="font-semibold md:hidden block mb-1">
                        Joined At
                      </span>
                      {new Date(membership.joinedAt).toLocaleDateString()}
                    </td>

                    <td className="px-4 py-2 md:table-cell">
                      <span className="font-semibold md:hidden block mb-1">
                        Role
                      </span>
                      {editingUserId === membership.userId ? (
                        <Select
                          value={editedRole}
                          onValueChange={(value) =>
                            setEditedRole(value as MembershipRole)
                          }
                        >
                          <SelectTrigger className="w-[120px]">
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="OWNER">OWNER</SelectItem>
                            <SelectItem value="MEMBER">MEMBER</SelectItem>
                          </SelectContent>
                        </Select>
                      ) : (
                        <span>{membership.role}</span>
                      )}
                    </td>

                    <td className="px-4 py-2 md:hidden">
                      <div className="flex justify-end gap-2">
                        {currentUserRole === 'OWNER' ? (
                          editingUserId === membership.userId ? (
                            <>
                              <button
                                onClick={() =>
                                  updateMembership(membership.userId)
                                }
                                className="text-green-600 hover:bg-green-100 px-4 py-1.5 rounded-full border"
                              >
                                Save
                              </button>
                              <button
                                onClick={() => setEditingUserId(null)}
                                className="text-gray-900 hover:bg-gray-100 px-4 py-1.5 rounded-full border"
                              >
                                Cancel
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                onClick={() => {
                                  setEditingUserId(membership.userId);
                                  setEditedRole(
                                    membership.role as MembershipRole,
                                  );
                                }}
                                disabled={editingUserId === null}
                                className="flex items-center gap-1 px-4 py-1.5 rounded-full border 
                                  disabled:cursor-not-allowed disabled:opacity-50 cursor-pointer text-primary-500 hover:bg-primary-500/10"
                              >
                                <Pencil width={18} height={18} />
                                <span>Edit</span>
                              </button>
                              <YesNoModal
                                title="Remove Member"
                                description={`Are you sure you want to remove ${membership.userEmail} from the group?`}
                                actionName="Remove"
                                cancelName="Cancel"
                                onConfirm={() =>
                                  deleteMembership(membership.userId)
                                }
                                trigger={
                                  <button
                                    disabled={isOwner || editingUserId !== null}
                                    className="flex items-center gap-1 px-4 py-1.5 rounded-full border text-red-800 hover:bg-red-500/10 disabled:text-gray-300 disabled:hover:bg-transparent cursor-pointer disabled:cursor-not-allowed"
                                  >
                                    <Trash width={18} height={18} />
                                    <span>Delete</span>
                                  </button>
                                }
                              />
                            </>
                          )
                        ) : null}
                      </div>
                    </td>

                    {currentUserRole === 'OWNER' && (
                      <td className="hidden md:table-cell align-middle">
                        <div className="flex justify-center items-center h-full">
                          {editingUserId === membership.userId ? (
                            <div className="flex gap-2">
                              <button
                                onClick={() =>
                                  updateMembership(membership.userId)
                                }
                                className="text-green-600 hover:bg-green-100 px-2 py-1 rounded"
                              >
                                Save
                              </button>
                              <button
                                onClick={() => setEditingUserId(null)}
                                className="text-gray-500 hover:bg-gray-100 px-2 py-1 rounded"
                              >
                                Cancel
                              </button>
                            </div>
                          ) : (
                            <button
                              onClick={() => {
                                setEditingUserId(membership.userId);
                                setEditedRole(
                                  membership.role as MembershipRole,
                                );
                              }}
                              disabled={
                                editingUserId !== null ||
                                (membership.role === 'OWNER' &&
                                  memberships &&
                                  memberships.filter(
                                    (el) => el.role === 'OWNER',
                                  ).length <= 1)
                              }
                              className="flex items-center gap-1 px-2 py-1 rounded 
                              cursor-pointer text-primary-500 hover:bg-primary-500/10
                              disabled:cursor-not-allowed disabled:text-gray-300 disabled:hover:bg-transparent
                              "
                            >
                              <Pencil width={18} height={18} />
                            </button>
                          )}
                        </div>
                      </td>
                    )}

                    {currentUserRole === 'OWNER' && (
                      <td className="hidden md:table-cell align-middle">
                        <div className="flex justify-center items-center h-full">
                          <YesNoModal
                            title="Remove Member"
                            description={`Are you sure you want to remove ${membership.userEmail} from the group?`}
                            actionName="Remove"
                            cancelName="Cancel"
                            onConfirm={() =>
                              deleteMembership(membership.userId)
                            }
                            trigger={
                              <button
                                disabled={isOwner || editingUserId !== null}
                                className="flex items-center gap-1 px-2 py-1 rounded text-red-800 hover:bg-red-500/10 disabled:text-gray-300 disabled:hover:bg-transparent cursor-pointer disabled:cursor-not-allowed"
                              >
                                <Trash width={18} height={18} />
                              </button>
                            }
                          />
                        </div>
                      </td>
                    )}
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}
