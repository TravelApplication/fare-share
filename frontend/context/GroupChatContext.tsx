'use client';

import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
  useCallback,
} from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { getToken } from '@/lib/auth';
import axiosInstance from '@/lib/axiosInstance';
import { ChatMessage } from '@/validation/chatMessageSchema';
import { useRouter } from 'next/navigation';

interface GroupChatContextType {
  messages: ChatMessage[];
  sendMessage: (content: string) => void;
  loadMoreMessages: () => Promise<void>;
  hasMore: boolean;
  isLoading: boolean;
}

const GroupChatContext = createContext<GroupChatContextType | undefined>(
  undefined,
);

export const GroupChatProvider = ({
  groupId,
  children,
}: {
  groupId: number;
  children: ReactNode;
}) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [client, setClient] = useState<Client | null>(null);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const pageSize = 10;

  const router = useRouter();
  const fetchMessages = useCallback(
    async (pageNumber: number) => {
      try {
        setIsLoading(true);
        const res = await axiosInstance.get(`/group/${groupId}/chat`, {
          params: {
            page: pageNumber,
            size: pageSize,
          },
        });

        const newMessages: ChatMessage[] = res.data.content;
        setMessages((prev) => {
          const combined = [...newMessages, ...prev];
          return combined.sort(
            (a, b) =>
              new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime(),
          );
        });
        setHasMore(!res.data.last);
      } catch (err) {
        console.error('Error fetching chat messages:', err);
        router.push('/trips');
      } finally {
        setIsLoading(false);
      }
    },
    [groupId],
  );

  useEffect(() => {
    const token = getToken();
    if (!token) return;
    fetchMessages(0);

    const stompClient = new Client({
      webSocketFactory: () => new SockJS('/ws/api/v1'),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      onConnect: () => {
        stompClient.subscribe(`/group/${groupId}/chat`, (message) => {
          const chatMessage: ChatMessage = JSON.parse(message.body);
          setMessages((prev) => {
            const combined = [chatMessage, ...prev];
            return combined.sort(
              (a, b) =>
                new Date(a.timestamp).getTime() -
                new Date(b.timestamp).getTime(),
            );
          });
        });
      },
    });

    stompClient.activate();
    setClient(stompClient);

    return () => {
      stompClient.deactivate();
    };
  }, [groupId, fetchMessages]);

  const sendMessage = (content: string) => {
    if (!client || !client.connected) return;

    client.publish({
      destination: `/app/group/${groupId}/chat.sendMessage`,
      body: content,
    });
  };

  const loadMoreMessages = useCallback(async () => {
    if (!hasMore || isLoading) return;
    const nextPage = page + 1;
    await fetchMessages(nextPage);
    setPage(nextPage);
  }, [hasMore, isLoading, page, fetchMessages]);

  return (
    <GroupChatContext.Provider
      value={{
        messages,
        sendMessage,
        loadMoreMessages,
        hasMore,
        isLoading,
      }}
    >
      {children}
    </GroupChatContext.Provider>
  );
};

export const useGroupChat = () => {
  const context = useContext(GroupChatContext);
  if (!context) {
    throw new Error('useGroupChat must be used within a GroupChatProvider');
  }
  return context;
};
