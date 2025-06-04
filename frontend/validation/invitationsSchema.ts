import { z } from 'zod';

export const userGeneralSchema = z.object({
  id: z.number(),
  firstName: z.string(),
  lastName: z.string(),
  email: z.string().email(),
});

export const invitationSchema = z.object({
  id: z.number(),
  sender: userGeneralSchema,
  receiver: userGeneralSchema,
  createdAt: z.string(),
});

export const groupInvitationSchema = invitationSchema.extend({
  groupId: z.number(),
  groupName: z.string(),
});

export type UserGeneral = z.infer<typeof userGeneralSchema>;
export type Invitation = z.infer<typeof invitationSchema>;
export type GroupInvitation = z.infer<typeof groupInvitationSchema>;
