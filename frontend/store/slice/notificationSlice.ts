import { StateCreator } from 'zustand';

export type NotificationType =
  | 'VOTE'
  | 'VOTE_CHANGE'
  | 'FRIEND_INVITATION'
  | 'GROUP_INVITATION'
  | 'FRIEND_INVITATION_ACCEPT'
  | 'FRIEND_INVITATION_REJECT'
  | 'GROUP_INVITATION_ACCEPT'
  | 'GROUP_INVITATION_REJECT';

export type Notification = {
  type: NotificationType;
  senderId: number;
  message: string;
};

export type NotificationSlice = {
  notifications: Notification[];
  addNotification: (notification: Notification) => void;
  toFetchFriendInvitations: boolean;
  toFetchGroupInvitations: boolean;
  setToFetchFriendInvitations: (value: boolean) => void;
  setToFetchGroupInvitations: (value: boolean) => void;
};

export const createNotificationSlice: StateCreator<
  NotificationSlice,
  [],
  [],
  NotificationSlice
> = (set) => ({
  notifications: [],
  addNotification: (notification) =>
    set((state) => {
      if (
        notification.type === 'FRIEND_INVITATION' ||
        notification.type === 'FRIEND_INVITATION_ACCEPT' ||
        notification.type === 'FRIEND_INVITATION_REJECT'
      ) {
        set({ toFetchFriendInvitations: true });
      }

      if (
        notification.type === 'GROUP_INVITATION' ||
        notification.type === 'GROUP_INVITATION_ACCEPT' ||
        notification.type === 'GROUP_INVITATION_REJECT'
      ) {
        set({ toFetchGroupInvitations: true });
      }

      return { notifications: [...state.notifications, notification] };
    }),
  toFetchFriendInvitations: false,
  toFetchGroupInvitations: false,
  setToFetchFriendInvitations: (value) =>
    set({ toFetchFriendInvitations: value }),
  setToFetchGroupInvitations: (value) =>
    set({ toFetchGroupInvitations: value }),
});
