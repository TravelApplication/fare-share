import { z } from 'zod';

export const membershipSchema = z
    .object({
        userId: z.number(),
        userEmail: z.string().email(),
        groupId: z.number(),
        role: z.string(z.enum(['MEMBER', 'OWNER', 'ADMIN'])),
        joinedAt: z.string().date(),
    })
    .passthrough();

export type MembershipSchema = z.infer<typeof membershipSchema>;