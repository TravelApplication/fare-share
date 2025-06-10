import { z } from 'zod';

export const chatMessageFormSchema = z.object({
  message: z
    .string()
    .min(1, 'Message cannot be empty')
    .max(500, 'Message cannot exceed 500 characters'),
});
