import { z } from 'zod';

export const loginFormSchema = z.object({
  email: z
    .string({
      required_error: 'Email is required',
    })
    .email(),
  password: z
    .string({
      required_error: 'Password is required',
    })
    .min(8, 'Password must have 8 characters'),
});

export const registerFormSchema = loginFormSchema.extend({
  firstName: z.string({
    required_error: 'First name is required',
    invalid_type_error: 'First name must be a string',
  }),
  lastName: z.string({
    required_error: 'Last name is required',
    invalid_type_error: 'Last name must be a string',
  }),
  dateOfBirth: z.coerce
    .date({
      required_error: 'Date of Birth is required',
    })
    .max(new Date(), 'Date birth cannot be in the future.'),
  phoneNumber: z
    .string({
      required_error: 'Phone number is required',
    })
    .min(9, 'Phone number must have 9 digits')
    .max(9, 'Phone number must have 9 digits'),
});

export const authApiSchema = z.object({
  token: z.string(),
  userId: z.number(),
});
