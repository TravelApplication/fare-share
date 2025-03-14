import { z } from 'zod';

export const UserInfoSchema = z
  .object({
    id: z.number(),
    firstName: z.string(),
    lastName: z.string(),
    phoneNumber: z.string(),
    dateOfBirth: z.string().date(),
  })
  .passthrough();

export const UserSchema = z
  .object({
    id: z.number(),
    email: z.string().email(),
    userInfo: UserInfoSchema,
  })
  .passthrough();

export type User = z.infer<typeof UserSchema>;
