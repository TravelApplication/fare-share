import { StateCreator } from 'zustand';
import { User } from '@/validation/userProfileSchemas';

export type User = {
  id: number;
  email: string;
  createdAt: string;
  userInfo: {
    id: number;
    userId: number;
    firstName: string | null;
    lastName: string | null;
    phoneNumber: string | null;
    bio: string | null;
    dateOfBirth: string | null;
  };
  memberships: {
    groupId: number;
  }[];
};

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
