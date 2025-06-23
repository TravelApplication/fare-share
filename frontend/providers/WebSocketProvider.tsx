'use client';
import { useEffect } from 'react';
import { appStore } from '@/store/appStore';
import { getToken } from '@/lib/auth';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axiosInstance from '@/lib/axiosInstance';
import { toast } from 'sonner';
import { Membership } from '@/validation/membershipSchema';
import { Invitation } from '@/validation/invitationsSchema';

export const WebSocketProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const user = appStore((state) => state.user);
  const setUser = appStore((state) => state.setUser);
  const addNotfication = appStore((state) => state.addNotification);
  const addSentFriendInvitation = appStore(
    (state) => state.addSentFriendInvitation,
  );
  const setToFetchGroup = appStore((state) => state.setToFetchGroup);

  useEffect(() => {
    const token = getToken();
    if (!token) return;

    const fetchUserData = async () => {
      if (!user) {
        try {
          const userResponse = await axiosInstance.get('users');
          const userData = userResponse.data;
          setUser(userData);

          const invitationsResponse = await axiosInstance.get(
            'friend-invitations/sent',
          );
          const invitationsData = invitationsResponse.data;
          invitationsData.forEach((invitation: Invitation) => {
            addSentFriendInvitation(invitation.receiver.id);
          });
        } catch {
          console.error('Error fetching user data.');
        }
      }
    };

    fetchUserData();

    const socketUrl = '/ws/api/v1';
    const stompClient = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
    });

    stompClient.onConnect = () => {
      if (user) {
        stompClient.subscribe(`/user/${user.id}/notifications`, (message) => {
          const notification = JSON.parse(message.body);
          toast(`${notification.message}`, {
            duration: 7000,
            action: {
              label: 'Close',
              onClick: () => {
                toast.dismiss();
              },
            },
          });
          addNotfication(notification);
        });

        user.memberships.forEach((memb: Membership) => {
          stompClient.subscribe(`/group/${memb.groupId}/notifications`, () => {
            setToFetchGroup(true);
          });
        });
      }
    };

    stompClient.activate();
    return () => {
      stompClient.deactivate();
    };
  }, [user]);

  return <>{children}</>;
};
