import { StateCreator } from 'zustand';
import { User } from '@/validation/userProfileSchemas';

export type UserSlice = {
  user: User | null;
  setUser: (user: User) => void;
};

export const createUserSlice: StateCreator<UserSlice, [], [], UserSlice> = (
  set,
) => ({
  user: null,
  setUser: (user) => set({ user }),
});
