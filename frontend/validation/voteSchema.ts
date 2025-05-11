import { z } from 'zod';

export const voteSchema = z.object({
  id: z.number(),
  userId: z.number(),
  userEmail: z.string().email(),
  activityId: z.number(),
  voteType: z.string(z.enum(['FOR', 'AGAINST'])),
});

export type Vote = z.infer<typeof voteSchema>;
