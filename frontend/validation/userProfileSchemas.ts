import { z } from 'zod';
import { membershipSchema } from './membershipSchema';

export const UserInfoSchema = z
  .object({
    id: z.number(),
    firstName: z.string(),
    lastName: z.string(),
    bio: z.string().nullable(),
    phoneNumber: z.string(),
    dateOfBirth: z.date(),
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

export const UserSearchSchema = z.object({
  id: z.number(),
  firstName: z.string(),
  lastName: z.string(),
  bio: z.string(),
});

export type User = z.infer<typeof UserSchema>;
export type UserInfo = z.infer<typeof UserInfoSchema>;
export type UserSearch = z.infer<typeof UserSearchSchema>;
