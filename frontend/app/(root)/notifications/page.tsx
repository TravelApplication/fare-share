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

interface InvitationResponse {
  id: number;
  senderId: number;
  receiverId: number;
  createdAt: string;
}

interface GroupInvitationResponse extends InvitationResponse {
  groupId: number;
}

export default function InvitationsPage() {
  const [friendInvitesReceived, setFriendInvitesReceived] = useState<
    InvitationResponse[]
  >([]);
  const [friendInvitesSent, setFriendInvitesSent] = useState<
    InvitationResponse[]
  >([]);
  const [groupInvitesReceived, setGroupInvitesReceived] = useState<
    GroupInvitationResponse[]
  >([]);
  const [groupInvitesSent, setGroupInvitesSent] = useState<
    GroupInvitationResponse[]
  >([]);

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
    } catch (err) {
      console.error('error with invitations', err);
    }
  };

  useEffect(() => {
    fetchInvitations();
  });

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

  const renderInvitationCard = (
    inv: InvitationResponse | GroupInvitationResponse,
    type: 'received' | 'sent',
  ) => {
    const isGroup = 'groupId' in inv;

    const handleAction = async (action: 'accept' | 'reject') => {
      const base = isGroup ? 'group-invitations' : 'friend-invitations';
      const url = `${base}/${action}/${inv.id}`;

      try {
        if (action === 'accept') {
          await axiosInstance.post(url, {});
        } else {
          await axiosInstance.delete(url);
        }

        if (isGroup) {
          setToFetchGroupInvitations(true);
        } else {
          setToFetchFriendInvitations(true);
        }
      } catch (err) {
        console.error(`Failed to ${action} invitation`, err);
      }
    };

    return (
      <Card key={inv.id} className="mb-4">
        <CardContent className="p-4 flex flex-col gap-2">
          <p>
            <strong>From:</strong> {inv.senderId}
          </p>
          {'groupId' in inv && (
            <p>
              <strong>Trip:</strong> {inv.groupId}
            </p>
          )}
          <p>
            <strong>Sent At:</strong> {new Date(inv.createdAt).toLocaleString()}
          </p>
          {type === 'received' && (
            <div className="flex gap-2 mt-2">
              <Button variant="default" onClick={() => handleAction('accept')}>
                Accept
              </Button>
              <Button
                variant="secondary"
                onClick={() => handleAction('reject')}
              >
                Reject
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    );
  };

  const renderInvitationSection = (
    invitations: InvitationResponse[] | GroupInvitationResponse[],
    label: string,
    type: 'received' | 'sent',
  ) => (
    <>
      <h2 className="text-xl font-semibold mt-4 mb-2">{label}</h2>
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
            <TabsTrigger value="friends">Friend Invitations</TabsTrigger>
            <TabsTrigger value="groups">Trip Invitations</TabsTrigger>
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
