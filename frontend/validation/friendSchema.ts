import { z } from 'zod';

export const FriendSchema = z.object({
  id: z.number(),
  firstName: z.string(),
  lastName: z.string(),
  email: z.string().email(),
});

export type Friend = z.infer<typeof FriendSchema>;
