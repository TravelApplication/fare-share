import { z } from "zod";

const membershipSchema = z.object({
    userId: z.number(),
    userEmail: z.string().email(),
    groupId: z.number(),
    role: z.string(
        z.enum(["MEMBER", "OWNER", "ADMIN"])
    ),
    joinedAt: z.string().date(),
}).passthrough();


export const tripSchema = z.object({
    id: z.number(),
    name: z.string().nonempty(),
    description: z.string().nonempty(),
    createdByUserId: z.number(),
    createdAt: z.string(),
    tripStartDate: z.string().date(),
    tripEndDate: z.string().date(),
    tags: z.array(z.string()),
    groupImageUrl: z.string().url(),
    memberships: z.array(membershipSchema),
}).passthrough();

export type Trip = z.infer<typeof tripSchema>;