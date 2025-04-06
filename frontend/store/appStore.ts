import { create } from 'zustand';
import { createUserSlice, UserSlice } from './slice/userSlice';
import {
  createNotificationSlice,
  NotificationSlice,
} from './slice/notificationSlice';

type StoreState = UserSlice & NotificationSlice;
export const appStore = create<StoreState>()((...a) => ({
  ...createUserSlice(...a),
  ...createNotificationSlice(...a),
}));
