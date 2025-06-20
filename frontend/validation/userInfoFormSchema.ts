import { z } from 'zod';
export const userInfoFormSchema = z.object({
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  bio: z.string().nullable().optional(),
  phoneNumber: z.string().nullable().optional().or(z.literal('')),
  dateOfBirth: z
    .string()
    .nullable()
    .optional()
    .refine((val) => !val || !isNaN(Date.parse(val)), 'Invalid date format'),
});

export type UserInfoForm = z.infer<typeof userInfoFormSchema>;
