import { z } from "zod";

export const loginFormSchema = z.object({
  email: z.string().email(),
  password: z.string().min(8, "Password must have 8 characters."),
});

export const registerFormSchema = loginFormSchema.extend({
  firstName: z.string(),
  lastName: z.string(),
  dateOfBirth: z.coerce
    .date()
    .max(new Date(), "Date birth cannot be in the future."),
  phoneNumber: z
    .string()
    .min(9, "Phone number must have 9 digits")
    .max(9, "Phone number must have 9 digits"),
});

export const authApiSchema = z.object({
  token: z.string(),
  userId: z.number(),
});
