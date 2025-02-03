import { z } from "zod";

export const updateEmailSchema = z.object({
    currentPassword: z
        .string()
        .min(8, "Password must be at least 8 characters long"),
    newEmail: z
        .string()
        .email("Invalid email format")
        .nonempty("Email is required"),
});

export const updatePasswordSchema = z.object({
    currentPassword: z
        .string()
        .min(8, "Password must be at least 8 characters long"),
    newPassword: z.string().min(8, "Password must be at least 8 characters long"),
});