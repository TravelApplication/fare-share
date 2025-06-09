import { Formik, Form, Field, ErrorMessage } from 'formik';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { CircleAlert } from 'lucide-react';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import { settlementFormSchema } from '@/validation/settlementFormSchema';

export default function AddSettlementDialog({
  open,
  onOpenChange,
  userEmail,
  settlementType,
  amount,
  maxAmount,
  debtorId,
  creditorId,
  groupId,
  onSubmit,
}: {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  userEmail: string;
  settlementType: 'youOwe' | 'theyOwe' | null;
  amount: number;
  maxAmount: number;
  debtorId: number;
  creditorId: number;
  groupId: number;
  onSubmit: (values: {
    groupId: number;
    debtorId: number;
    creditorId: number;
    amount: number;
  }) => void;
}) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Confirm settlement</DialogTitle>
        </DialogHeader>
        <Formik
          initialValues={{ amount }}
          enableReinitialize
          validationSchema={toFormikValidationSchema(
            settlementFormSchema(maxAmount),
          )}
          // validate={(values) => {
          //   const errors: { amount?: string } = {};
          //   if (!values.amount || values.amount <= 0) {
          //     errors.amount = 'Amount must be greater than 0';
          //   }
          //   return errors;
          // }}
          onSubmit={(values, { setSubmitting }) => {
            onSubmit({
              groupId,
              debtorId,
              creditorId,
              amount: Number(values.amount),
            });
            setSubmitting(false);
            onOpenChange(false);
          }}
        >
          {({ isSubmitting }) => (
            <Form>
              <div className="mb-2">
                {settlementType === 'theyOwe'
                  ? `Confirm that ${userEmail} paid you:`
                  : settlementType === 'youOwe'
                    ? `Confirm that you paid to ${userEmail}:`
                    : null}
              </div>
              <Field
                as={Input}
                name="amount"
                type="number"
                min={0.01}
                step={0.01}
                className="mb-2"
              />
              <ErrorMessage
                name="amount"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
              <div className="flex gap-2 justify-end mt-4">
                <Button
                  variant="outline"
                  type="button"
                  onClick={() => onOpenChange(false)}
                >
                  Cancel
                </Button>
                <Button type="submit" disabled={isSubmitting}>
                  Confirm
                </Button>
              </div>
            </Form>
          )}
        </Formik>
      </DialogContent>
    </Dialog>
  );
}
