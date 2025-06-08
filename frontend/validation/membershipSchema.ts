import { z } from 'zod';

export const membershipRoleSchema = z.string(
  z.enum(['MEMBER', 'OWNER', 'ADMIN']),
);
export const membershipSchema = z
  .object({
    userId: z.number(),
    userEmail: z.string().email(),
    groupId: z.number(),
    role: membershipRoleSchema,
    joinedAt: z.string().date(),
  })
  .passthrough();

export type MembershipRole = z.infer<typeof membershipRoleSchema>;
export type Membership = z.infer<typeof membershipSchema>;
