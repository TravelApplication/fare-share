import { StateCreator } from 'zustand';

export type InvitationSlice = {
  sentFriendInvitations: number[];
  addSentFriendInvitation: (userId: number) => void;
  hasSentFriendInbitation: (userId: number) => boolean;
};

export const createInvitationSlice: StateCreator<InvitationSlice> = (
  set,
  get,
) => ({
  sentFriendInvitations: [],
  addSentFriendInvitation: (userId: number) =>
    set((state) => {
      if (!state.sentFriendInvitations.includes(userId)) {
        const updatedArray = [...state.sentFriendInvitations, userId];
        return { sentFriendInvitations: updatedArray };
      }
      return state;
    }),
  hasSentFriendInvitation: (userId: number) => {
    return get().sentFriendInvitations.includes(Number(userId));
  },
});
