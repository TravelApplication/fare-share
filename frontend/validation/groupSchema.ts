import { z } from 'zod';
import { membershipSchema } from './membershipSchema';
import { activitySchema } from './activitySchema';

export const groupSchema = z
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
    links: z.array(z.string()),
    memberships: z.array(membershipSchema),
    activities: z.array(activitySchema)
  })
  .passthrough();

export type Group = z.infer<typeof groupSchema>;
