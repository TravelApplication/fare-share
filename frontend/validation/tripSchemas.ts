import { z } from 'zod';

const membershipSchema = z
  .object({
    userId: z.number(),
    userEmail: z.string().email(),
    groupId: z.number(),
    role: z.string(z.enum(['MEMBER', 'OWNER', 'ADMIN'])),
    joinedAt: z.string().date(),
  })
  .passthrough();

export const tripSchema = z
  .object({
    id: z.number(),
    name: z.string().nonempty(),
    description: z.union([z.string(), z.null()]),
    createdByUserId: z.union([z.number(), z.null()]),
    createdAt: z.union([z.string(), z.null()]),
    tripStartDate: z.union([z.string().date(), z.null()]),
    tripEndDate: z.union([z.string().date(), z.null()]),
    tags: z.array(z.string()),
    groupImageUrl: z.union([z.string(), z.null()]),
    memberships: z.array(membershipSchema),
  })
  .passthrough();

export type Trip = z.infer<typeof tripSchema>;
