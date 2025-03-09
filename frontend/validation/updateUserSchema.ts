import { z } from 'zod';

export const updateEmailSchema = z.object({
  currentEmail: z
    .string()
    .nonempty('e-mail is required')
    .email('Invalid email format'),
  newEmail: z
    .string({
      required_error: 'New e-mail is required',
    })
    .email('Invalid e-mail format'),
  currentPassword: z
    .string({
      required_error: 'Password is required',
    })
    .nonempty('Current password is required')
    .min(8, 'Password must be at least 8 characters long'),
});

export const updatePasswordSchema = z.object({
  currentPassword: z
    .string({
      required_error: 'Current password is required',
    })
    .min(8, 'Password must be at least 8 characters long'),
  newPassword: z
    .string({
      required_error: 'New password is required',
    })
    .min(8, 'Password must be at least 8 characters long'),
});
