import { z } from 'zod';

export const notificationTypeSchema = z.enum([
  'VOTE',
  'VOTE_CHANGE',
  'FRIEND_INVITATION',
  'GROUP_INVITATION',
  'FRIEND_INVITATION_ACCEPT',
  'FRIEND_INVITATION_REJECT',
  'GROUP_INVITATION_ACCEPT',
  'GROUP_INVITATION_REJECT',
]);

export type NotificationType = z.infer<typeof notificationTypeSchema>;

export const notificationSchema = z.object({
  type: notificationTypeSchema,
  senderId: z.number(),
  message: z.string(),
});

export type Notification = z.infer<typeof notificationSchema>;
