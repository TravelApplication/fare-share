import { z } from 'zod';

export const chatMessageSchema = z.object({
  id: z.number(),
  content: z.string(),
  senderId: z.number(),
  senderEmail: z.string(),
  groupId: z.number(),
  timestamp: z.string(),
});

export type ChatMessage = z.infer<typeof chatMessageSchema>;
