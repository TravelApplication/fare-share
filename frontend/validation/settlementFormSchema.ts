import { z } from 'zod';

export const settlementFormSchema = (maxAmount: number) =>
  z.object({
    amount: z
      .number({
        required_error: 'Amount is required',
        invalid_type_error: 'Amount must be a number',
      })
      .min(0.01, 'Amount must be greater than 0')
      .max(
        maxAmount,
        `Amount cannot be greater than debtor's debt (${maxAmount} zł)`,
      ),
  });

export type SettlementFormValues = z.infer<
  ReturnType<typeof settlementFormSchema>
>;
