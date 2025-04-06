'use client';
import { useEffect } from 'react';
import { appStore } from '@/store/appStore';
import { getToken } from '@/lib/auth';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';

export const WebSocketProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const user = appStore((state) => state.user);
  const setUser = appStore((state) => state.setUser);
  const addNotfication = appStore((state) => state.addNotification);

  useEffect(() => {
    const token = getToken();
    if (!token) return;

    const fetchUserData = async () => {
      if (!user) {
        try {
          const response = await axios.get('/api/v1/users', {
            headers: { Authorization: `Bearer ${token}` },
          });
          const userData = response.data;
          setUser(userData);
          console.log('user data websocket:', userData);
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
      console.log('connected websocket');
      if (user) {
        stompClient.subscribe(`/user/${user.id}/notifications`, (message) => {
          const notification = JSON.parse(message.body);
          console.log('Otrzymano powiadomienie:', notification);
          addNotfication(notification);
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
