import { z } from 'zod';
import { voteSchema } from './voteSchema';

export const activitySchema = z.object({
  id: z.number(),
  name: z.string().nonempty(),
  description: z.union([z.string(), z.null()]),
  location: z.union([z.string(), z.null()]),
  link: z.union([z.string(), z.null()]),
  groupId: z.number(),
  createdAt: z.union([z.string(), z.null()]),
  votes: z.array(voteSchema),
});

export type Activity = z.infer<typeof activitySchema>;
