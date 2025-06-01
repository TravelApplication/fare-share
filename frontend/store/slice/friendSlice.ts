import { StateCreator } from 'zustand';

export type FriendSlice = {
  friends: number[];
  setFriends: (friends: number[]) => void;
  addFriend: (userId: number) => void;
  removeFriend: (userId: number) => void;
  hasFriend: (userId: number) => boolean;
};

export const createFriendSlice: StateCreator<FriendSlice> = (set, get) => ({
  friends: [],
  setFriends: (friends: number[]) => set({ friends }),
  addFriend: (userId: number) =>
    set((state) => {
      if (!state.friends.includes(userId)) {
        const updatedArray = [...state.friends, userId];
        return { friends: updatedArray };
      }
      return state;
    }),
  removeFriend: (userId: number) =>
    set((state) => {
      const updatedArray = state.friends.filter((friend) => friend !== userId);
      return { friends: updatedArray };
    }),
  hasFriend: (userId: number) => {
    const friends = get().friends;
    const res = friends.filter((u) => u.id == userId);
    return res.length > 0;
  },
});
