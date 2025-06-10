'use client';

import { useEffect, useState } from 'react';
import axiosInstance from '@/lib/axiosInstance';
import { Tabs, TabsList, TabsTrigger, TabsContent } from '@/components/ui/tabs';
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { appStore } from '@/store/appStore';
import YesNoModal from '@/components/shared/YesNoModal';
import { toast } from 'sonner';
import { Invitation, GroupInvitation } from '@/validation/invitationsSchema';

export default function InvitationsPage() {
  const [friendInvitesReceived, setFriendInvitesReceived] = useState<
    Invitation[]
  >([]);
  const [friendInvitesSent, setFriendInvitesSent] = useState<Invitation[]>([]);
  const [groupInvitesReceived, setGroupInvitesReceived] = useState<
    GroupInvitation[]
  >([]);
  const [groupInvitesSent, setGroupInvitesSent] = useState<GroupInvitation[]>(
    [],
  );
  const [currentAction, setCurrentAction] = useState<{
    id: number;
    type: 'friend' | 'group';
    action: 'accept' | 'reject';
  } | null>(null);

  const toFetchFriendInvitations = appStore((s) => s.toFetchFriendInvitations);
  const setToFetchFriendInvitations = appStore(
    (s) => s.setToFetchFriendInvitations,
  );
  const toFetchGroupInvitations = appStore((s) => s.toFetchGroupInvitations);
  const setToFetchGroupInvitations = appStore(
    (s) => s.setToFetchGroupInvitations,
  );

  const fetchInvitations = async () => {
    try {
      const [friendsReceived, friendsSent, groupsReceived, groupsSent] =
        await Promise.all([
          axiosInstance.get('friend-invitations/received'),
          axiosInstance.get('friend-invitations/sent'),
          axiosInstance.get('group-invitations/received'),
          axiosInstance.get('group-invitations/sent'),
        ]);

      setFriendInvitesReceived(friendsReceived.data);
      setFriendInvitesSent(friendsSent.data);
      setGroupInvitesReceived(groupsReceived.data);
      setGroupInvitesSent(groupsSent.data);
    } catch {
      toast('Failed to load invitations', {
        description: 'Please try again later',
        duration: 5000,
        action: {
          label: 'Close',
          onClick: () => toast.dismiss(),
        },
      });
    }
  };

  useEffect(() => {
    fetchInvitations();
  }, []);

  useEffect(() => {
    if (toFetchFriendInvitations || toFetchGroupInvitations) {
      fetchInvitations();
      if (toFetchFriendInvitations) setToFetchFriendInvitations(false);
      if (toFetchGroupInvitations) setToFetchGroupInvitations(false);
    }
  }, [
    toFetchFriendInvitations,
    toFetchGroupInvitations,
    setToFetchFriendInvitations,
    setToFetchGroupInvitations,
  ]);

  const handleActionConfirmation = async () => {
    if (!currentAction) return;

    const { id, type, action } = currentAction;
    const base = type === 'group' ? 'group-invitations' : 'friend-invitations';
    const url = `${base}/${action}/${id}`;

    const toastId = toast.loading(
      `${action === 'accept' ? 'Accepting' : 'Rejecting'} invitation...`,
    );

    try {
      if (action === 'accept') {
        await axiosInstance.post(url, {});
      } else {
        await axiosInstance.delete(url);
      }

      toast.success(
        `Invitation ${action === 'accept' ? 'accepted' : 'rejected'} successfully`,
        {
          id: toastId,
          duration: 5000,
          action: {
            label: 'Close',
            onClick: () => toast.dismiss(toastId),
          },
        },
      );

      if (type === 'group') {
        setToFetchGroupInvitations(true);
      } else {
        setToFetchFriendInvitations(true);
      }
    } catch (err) {
      console.error(`Failed to ${action} invitation`, err);
      toast.error(`Failed to ${action} invitation`, {
        id: toastId,
        description: 'Please try again later',
        duration: 5000,
        action: {
          label: 'Close',
          onClick: () => toast.dismiss(toastId),
        },
      });
    } finally {
      setCurrentAction(null);
    }
  };

  const renderInvitationCard = (
    inv: Invitation | GroupInvitation,
    type: 'received' | 'sent',
  ) => {
    const isGroup = 'groupId' in inv;

    return (
      <Card key={inv.id} className="mb-4">
        <CardContent className="p-4 flex justify-between flex-col gap-2 sm:flex-row sm:items-center ">
          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-1">
              <div className="text-gray-700 text-sm flex flex-col gap-1 sm:gap-2 sm:flex-row sm:items-center">
                {type === 'received' ? 'From user' : 'Sent to '}
                <div className="text-primary-500 text-base font-bold">
                  {type === 'received'
                    ? `${inv.sender.firstName} ${inv.sender.lastName}`
                    : `${inv.receiver.firstName} ${inv.receiver.lastName}`}
                </div>
                {'groupId' in inv && (
                  <>
                    to the trip
                    <div className="text-primary-500 text-base font-bold">
                      {inv.groupName}
                    </div>
                  </>
                )}
              </div>
              <div className="text-sm text-gray-800 font-semibold">
                {type === 'received'
                  ? `${inv.sender.email}`
                  : `${inv.receiver.email}`}
              </div>
            </div>
            <div className="flex gap-2 text-xs font-semibold text-gray-400">
              <div>Sent at:</div>
              <div>{new Date(inv.createdAt).toLocaleString()}</div>
            </div>
          </div>
          {type === 'received' && (
            <div className="flex gap-2 mt-2">
              <YesNoModal
                title={`Accept ${isGroup ? 'trip' : 'friend'} invitation`}
                description={`Are you sure you want to accept this ${
                  isGroup ? 'trip' : 'friend'
                } invitation?`}
                actionName="Accept"
                onConfirm={handleActionConfirmation}
                trigger={
                  <Button
                    className="bg-primary-500 hover:bg-primary-600 w-full rounded-xl"
                    onClick={() =>
                      setCurrentAction({
                        id: inv.id,
                        type: isGroup ? 'group' : 'friend',
                        action: 'accept',
                      })
                    }
                  >
                    Accept
                  </Button>
                }
              />
              <YesNoModal
                title={`Reject ${isGroup ? 'trip' : 'friend'} invitation`}
                description={`Are you sure you want to reject this ${
                  isGroup ? 'trip' : 'friend'
                } invitation?`}
                actionName="Reject"
                onConfirm={handleActionConfirmation}
                trigger={
                  <Button
                    className="border w-full border-primary-500 text-primary-500 bg-white hover:text-white hover:bg-primary-600 hover:border-primary-600 rounded-xl"
                    onClick={() =>
                      setCurrentAction({
                        id: inv.id,
                        type: isGroup ? 'group' : 'friend',
                        action: 'reject',
                      })
                    }
                  >
                    Reject
                  </Button>
                }
              />
            </div>
          )}
        </CardContent>
      </Card>
    );
  };

  const renderInvitationSection = (
    invitations: Invitation[] | GroupInvitation[],
    label: string,
    type: 'received' | 'sent',
  ) => (
    <>
      <h2 className="text-lg text-primary-500 font-semibold mt-4 mb-2">
        {label}
      </h2>
      {invitations.length === 0 ? (
        <Card className="mb-4">
          <CardContent className="p-4 text-muted-foreground">
            No invitations found.
          </CardContent>
        </Card>
      ) : (
        invitations.map((inv) => renderInvitationCard(inv, type))
      )}
    </>
  );

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-2xl font-bold text-primary-500">
          Invitations
        </CardTitle>
        <CardDescription>
          Manage your friend and trip invitations.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Tabs defaultValue="friends" className="w-full">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="friends">Friends</TabsTrigger>
            <TabsTrigger value="groups">Trips</TabsTrigger>
          </TabsList>

          <TabsContent value="friends">
            {renderInvitationSection(
              friendInvitesReceived,
              'Received',
              'received',
            )}
            {renderInvitationSection(friendInvitesSent, 'Sent', 'sent')}
          </TabsContent>

          <TabsContent value="groups">
            {renderInvitationSection(
              groupInvitesReceived,
              'Received',
              'received',
            )}
            {renderInvitationSection(groupInvitesSent, 'Sent', 'sent')}
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  );
}
