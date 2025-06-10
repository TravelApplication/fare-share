import { create } from 'zustand';
import { createUserSlice, UserSlice } from './slice/userSlice';
import {
  createNotificationSlice,
  NotificationSlice,
} from './slice/notificationSlice';
import {
  createInvitationSlice,
  InvitationSlice,
} from './slice/invitationSlice';
import { createFriendSlice, FriendSlice } from './slice/friendSlice';

type StoreState = UserSlice & NotificationSlice & InvitationSlice & FriendSlice;

export const appStore = create<StoreState>()((...a) => ({
  ...createUserSlice(...a),
  ...createNotificationSlice(...a),
  ...createInvitationSlice(...a),
  ...createFriendSlice(...a),
}));
