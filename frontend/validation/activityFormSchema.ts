import { z } from 'zod';

export const activityFormSchema = z.object({
  name: z
    .string({ required_error: 'Name is required' })
    .max(100, 'Name must be less than 100 characters'),
  description: z
    .string()
    .max(200, 'Description must be less than 500 characters')
    .optional(),
  location: z
    .string()
    .max(100, 'Location must be less than 100 characters')
    .optional(),
  link: z.string().url({ message: 'Invalid URL format' }).optional(),
});
