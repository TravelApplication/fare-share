'use client';
import { useEffect } from 'react';
import { appStore } from '@/store/appStore';
import { getToken } from '@/lib/auth';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axiosInstance from '@/lib/axiosInstance';
import { toast } from 'sonner';
import { Membership } from '@/validation/membershipSchema';

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
  const sentFriendInvitations = appStore((s) => s.sentFriendInvitations);
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
          invitationsData.forEach((invitation) => {
            console.log(invitation.receiverId);
            addSentFriendInvitation(invitation.receiverId);
          });
        } catch (err) {
          console.error('error websockeet', err);
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
      debug: (str) => console.log(str),
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
          stompClient.subscribe(
            `/group/${memb.groupId}/notifications`,
            (vote) => {
              setToFetchGroup(true);
            },
          );
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
