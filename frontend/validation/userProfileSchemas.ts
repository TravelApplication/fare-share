import { z } from 'zod';
import { membershipSchema } from './membershipSchema';

export const UserInfoSchema = z
  .object({
    id: z.number(),
    firstName: z.union([z.string(), z.null()]),
    lastName: z.union([z.string(), z.null()]),
    phoneNumber: z.union([z.string(), z.null()]),
    dateOfBirth: z.union([z.string().date(), z.null()]),
  })
  .passthrough();

export const UserSchema = z
  .object({
    id: z.number(),
    email: z.string().email(),
    userInfo: UserInfoSchema,
    memberships: z.array(membershipSchema),
  })
  .passthrough();

export type User = z.infer<typeof UserSchema>;
