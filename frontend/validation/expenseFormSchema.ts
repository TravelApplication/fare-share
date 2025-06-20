import { z } from 'zod';

export const splitSchema = z.enum(['equally', 'percentage', 'amount', 'share']);

export const expenseFormSchema = z
  .object({
    description: z.string().min(1, 'Description is required'),
    totalAmount: z.number().min(0.01, 'Amount must be positive'),
    splitMethod: splitSchema,
    shares: z.array(
      z.object({
        userId: z.number(),
        included: z.boolean(),
        value: z
          .union([z.number(), z.nan(), z.undefined(), z.null()])
          .optional(),
      }),
    ),
  })
  .superRefine((data, ctx) => {
    const includedShares = data.shares.filter((s) => s.included);

    if (data.splitMethod === 'percentage') {
      const sum = includedShares.reduce(
        (acc, s) => acc + (typeof s.value === 'number' ? s.value : 0),
        0,
      );
      if (sum !== 100) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'Sum of percentages must be exactly 100%',
          path: ['shares'],
        });
      }
    }

    if (data.splitMethod === 'amount') {
      const sum = includedShares.reduce(
        (acc, s) => acc + (typeof s.value === 'number' ? s.value : 0),
        0,
      );
      if (Math.abs(sum - data.totalAmount) > 0.01) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'Sum of amounts must equal total amount',
          path: ['shares'],
        });
      }
    }

    if (data.splitMethod === 'share') {
      const sum = includedShares.reduce(
        (acc, s) => acc + (typeof s.value === 'number' ? s.value : 0),
        0,
      );
      if (includedShares.length < 2) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'At least two users must have shares',
          path: ['shares'],
        });
      }
      if (sum < 2) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: 'Sum of shares must be at least 2',
          path: ['shares'],
        });
      }
      includedShares.forEach((s, i) => {
        if (
          typeof s.value !== 'number' ||
          !Number.isInteger(s.value) ||
          s.value <= 0
        ) {
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: 'Each share must be a positive integer',
            path: ['shares', i, 'value'],
          });
        }
      });
    }
  });
