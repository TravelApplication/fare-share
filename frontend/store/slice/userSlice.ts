import { StateCreator } from "zustand";

export type User = {
    id: number;
    email: string;
    userInfo: {
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
    addMembership: (groupId: number) => void;
    removeMembership: (groupId: number) => void;
};

export const createUserSlice: StateCreator<UserSlice, [], [], UserSlice> = (set) => ({
    user: null,
    setUser: (user) => set({ user }),
    addMembership: (groupId) =>
        set((state) =>
            state.user
                ? {
                    user: {
                        ...state.user,
                        memberships: [...state.user.memberships, { groupId }],
                    },
                }
                : state
        ),
    removeMembership: (groupId) =>
        set((state) =>
            state.user
                ? {
                    user: {
                        ...state.user,
                        memberships: state.user.memberships.filter(
                            (membership) => membership.groupId !== groupId
                        ),
                    },
                }
                : state
        ),
});
