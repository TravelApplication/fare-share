import { z } from "zod";

export const createGroupFormSchema = z
  .object({
    name: z
      .string({ required_error: "Name is required" })
      .max(100, "Name must be less than 100 characters"),

    description: z
      .string({ required_error: "Description is required" })
      .max(500, "Description must be less than 500 characters"),

    tripStartDate: z.coerce
      .date({ required_error: "Start date is required" })
      .min(new Date(), "Start date must be in the future."),

    tripEndDate: z.coerce
      .date({ required_error: "End date is required" })
      .min(new Date(), "End date must be in the future."),

    tags: z.array(z.string()).max(10, "Cannot have more than 10 tags"),

    groupImageUrl: z.string().url({ message: "Invalid URL format" }).optional(),
  })
  .refine(
    (data) => {
      return data.tripEndDate > data.tripStartDate;
    },
    {
      message: "End date must be later than the start date.",
      path: ["tripEndDate"], // Określamy, że błąd ma być przypisany do pola tripEndDate
    }
  );

export const TripFormPropsSchema = z.object({
  onSubmit: z.function().args(
    z.object({
      name: z.string(),
      description: z.string(),
      tripStartDate: z.date(),
      tripEndDate: z.date(),
      tags: z.array(z.string()),
      groupImageUrl: z.string().optional(),
    }),
    z.any() // FormikHelpers
  ),
  initialValues: z
    .object({
      name: z.string(),
      description: z.string(),
      tripStartDate: z.date(),
      tripEndDate: z.date(),
      tags: z.array(z.string()),
      groupImageUrl: z.string().optional(),
    })
    .optional(),
  isSubmitting: z.boolean().optional(),
  error: z.string().nullable().optional(),
  setError: z
    .function()
    .args(z.string().nullable())
    .returns(z.void())
    .optional(),
  mode: z.enum(["create", "edit"]),
});
